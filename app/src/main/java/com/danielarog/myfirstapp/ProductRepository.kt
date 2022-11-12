package com.danielarog.myfirstapp

import com.danielarog.myfirstapp.models.ShoppingItem
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.Exception

suspend fun <T> Task<T>.tryAwait(exceptionCallback: ((e:Exception) -> Unit)? = null ): T? {
    val task = this
    return withContext(Dispatchers.IO) {
        try {
            val result = task.await()
            return@withContext withContext(Dispatchers.Main) {
                result
            }
        } catch (e: Exception) {
            // handle exception -> toast
            print(e.message)
            exceptionCallback?.invoke(e)
            return@withContext withContext(Dispatchers.Main) {
                null
            }
        }
    }
}

object ProductRepository {

    private val productCategoriesCollectionReference: CollectionReference =
        FirebaseFirestore.getInstance()
            .collection("productCategories")

    private var listener: ListenerRegistration? = null


    private fun categoryDocumentRef(
        category: ProductCategory
    ): DocumentReference {
        return productCategoriesCollectionReference
            .document(category.value)
    }

    private fun subCategoryCollectionRef(
        category: ProductCategory,
        subCategory: ProductCategory.SubCategory
    ): CollectionReference {
        return categoryDocumentRef(category)
            .collection(subCategory.value)
    }


    suspend fun addItem(shoppingItem: ShoppingItem) {
        val category = ProductCategory.valueOf(shoppingItem.category)
        val subCategory = ProductCategory.SubCategory.valueOf(shoppingItem.subCategory)
        val collectionRef = subCategoryCollectionRef(category, subCategory)
        val newRef = collectionRef
            .add(shoppingItem)
            .tryAwait()
        newRef?.update("id", newRef.id)
    }

    suspend fun removeItem(shoppingItem: ShoppingItem) {
        val category = ProductCategory.valueOf(shoppingItem.category)
        val subCategory = ProductCategory.SubCategory.valueOf(shoppingItem.subCategory)
        val collectionRef = subCategoryCollectionRef(category, subCategory)
        collectionRef.document(shoppingItem.id)
            .delete()
            .tryAwait()
    }

    suspend fun updateItem(shoppingItem: ShoppingItem) {
        val category = ProductCategory.valueOf(shoppingItem.category)
        val subCategory = ProductCategory.SubCategory.valueOf(shoppingItem.subCategory)
        val collectionRef = subCategoryCollectionRef(category, subCategory)
        collectionRef.document(shoppingItem.id)
            .set(shoppingItem, SetOptions.merge())
            .tryAwait()
    }


    fun listenAllSubCategories(
        category: ProductCategory,
        callback: (list: List<Pair<ProductCategory, ProductCategory.SubCategory>>?, e: Exception?) -> Unit
    ) {
        clear()
        val collectionRef = categoryDocumentRef(category)
        listener = collectionRef.addSnapshotListener { value, error ->
            if (value == null) return@addSnapshotListener
            if (error != null) {
                callback.invoke(null, error)
            } else {
                val subCategories = value.data?.keys?.map {
                    Pair(category, ProductCategory.SubCategory.valueOf(it))
                }
                callback.invoke(subCategories, null)
            }
        }
    }

    fun listenAllCategoryProducts(
        category: ProductCategory,
        subCategory: ProductCategory.SubCategory,
        callback: (list: List<ShoppingItem>?, e: Exception?) -> Unit
    ) {
        clear()
        val collectionRef = subCategoryCollectionRef(category, subCategory)
        listener = collectionRef.addSnapshotListener { value, error ->
            if (error != null) {
                callback.invoke(null, error)
            } else {
                val products = value?.documents?.mapNotNull {
                    it.toObject(ShoppingItem::class.java)
                }
                callback.invoke(products, null)
            }
        }
    }

    private fun clear() {
        listener?.remove()
    }

}