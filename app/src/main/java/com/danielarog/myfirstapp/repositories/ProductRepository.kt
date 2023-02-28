package com.danielarog.myfirstapp.repositories

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.danielarog.myfirstapp.Utils
import com.danielarog.myfirstapp.dialogs.ProductImageSelection
import com.danielarog.myfirstapp.models.Category
import com.danielarog.myfirstapp.models.ShoppingItem
import com.danielarog.myfirstapp.models.ProductCategory
import com.danielarog.myfirstapp.viewmodels.ImageUploader
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.Exception
import kotlin.collections.HashMap
import kotlin.math.max
import kotlin.math.min


typealias CategoryPair = Pair<ProductCategory, ProductCategory.SubCategory>
typealias CategoryMapping = List<CategoryPair>

suspend fun <T> Task<T>.tryAwait(exceptionCallback: ((e: Exception) -> Unit)? = null): T? {
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


    suspend fun addItem(
        shoppingItem: ShoppingItem,
        imageUri: Uri?,
        imageByteArray: ByteArray?,
        additionalImages: MutableList<ProductImageSelection>
    ) {
        val category = Utils.getCategoryEnumName(shoppingItem.category!!)
        val subCategory = Utils.getSubCategoryEnumName(shoppingItem.subCategory!!)
        val collectionRef = subCategoryCollectionRef(category, subCategory)

        val newRef = collectionRef
            .add(shoppingItem)
            .tryAwait()
        var currentUploadingIndex = 1
        val imageUrl = ImageUploader(imageByteArray, imageUri)
            .upload("/images/items/${newRef!!.id}.jpg")
        newRef.update("id", newRef.id).tryAwait()
        newRef.update("image", imageUrl).tryAwait()
        var additionalImagesUrls = listOf<String>()
        if (additionalImages.isNotEmpty()) {
            additionalImagesUrls = additionalImages.mapNotNull {
                ImageUploader(it.imageByteArray, it.imageUri)
                    .upload("/images/items/${newRef.id}_${currentUploadingIndex++}.jpg")
            }
            newRef.update("additionalImages", additionalImagesUrls)
        }
        imageUrl?.let {
            shoppingItem.image = it
            shoppingItem.additionalImages = additionalImagesUrls
        }
        UserRepository.saveItem(shoppingItem)
    }

    suspend fun editItem(
        shoppingItem: ShoppingItem,
        imageUri: Uri?,
        imageByteArray: ByteArray?
    ) {
        val category = Utils.getCategoryEnumName(shoppingItem.category!!)
        val subCategory = Utils.getSubCategoryEnumName(shoppingItem.subCategory!!)
        val collectionRef = subCategoryCollectionRef(category, subCategory)

        collectionRef
            .document(shoppingItem.id!!)
            .set(shoppingItem)
            .tryAwait()

        val imageUrl = ImageUploader(imageByteArray, imageUri)
            .upload("/images/items/${shoppingItem.id}.jpg")
        collectionRef
            .document(shoppingItem.id)
            .update("image", imageUrl).tryAwait()
        imageUrl?.let { shoppingItem.image = it }
        UserRepository.editItem(shoppingItem)
    }


    //@TODO : Remove storage images upon deletion
    suspend fun deleteItem(shoppingItem: ShoppingItem) {
        val category = Utils.getCategoryEnumName(shoppingItem.category!!)
        val subCategory = Utils.getSubCategoryEnumName(shoppingItem.subCategory!!)
        val collectionRef = subCategoryCollectionRef(category, subCategory)
        collectionRef.document(shoppingItem.id!!)
            .delete()
            .tryAwait()
        UserRepository.deleteItem(shoppingItem)
    }

    suspend fun updateItem(shoppingItem: ShoppingItem) {
        val category = ProductCategory.valueOf(shoppingItem.category!!)
        val subCategory = ProductCategory.SubCategory.valueOf(shoppingItem.subCategory!!)
        val collectionRef = subCategoryCollectionRef(category, subCategory)
        collectionRef.document(shoppingItem.id!!)
            .set(shoppingItem, SetOptions.merge())
            .tryAwait()
    }


    fun addProductProperty() { // @TODO: Remove on production
        ProductCategory.values()
            .forEach { category ->
                val categorySubs = FirebaseFirestore.getInstance()
                    .collection("productCategories")
                    .document(category.value)
                category.getSubCategories().forEach { sub ->
                    categorySubs.collection(sub.value)
                        .get()
                        .addOnSuccessListener {
                            it.documents.forEach { snap ->
                                snap.reference.update("additionalImages", listOf<String>())
                            }
                        }

                }
            }
    }

    suspend fun getAllSubCategories(category: ProductCategory): CategoryMapping {
        clear()
        val categoryDocument = categoryDocumentRef(category)
        val docs = categoryDocument.get().tryAwait()

        val subCategories = docs?.data?.keys?.map {
            Pair(category, ProductCategory.SubCategory.valueOf(it))
        }
        return subCategories ?: listOf()
    }

    suspend fun getAllCategoryProducts(
        category: ProductCategory,
        gender: String,
        limit: Long? = null,
        liveData: MutableLiveData<List<ShoppingItem>>,
    ) {
        val subCategories = category.getSubCategories()
        val output = mutableListOf<ShoppingItem>()
        for (subCategory in subCategories) {
            val productCollection = subCategoryCollectionRef(category, subCategory)
            // @TODO
            // query default items here can be done
            // with conditions on item rating,date etc..
            val resource = limit?.let {
                productCollection.orderBy("publisherRating")
                    .whereEqualTo("gender", gender)
                    .get()
                    .tryAwait()
            } ?: productCollection.get()
                .tryAwait()
            resource?.let {
                it.toObjects(ShoppingItem::class.java).let { list ->
                    output.addAll(list)
                }
            }
        }
        liveData.postValue(output)
    }

    suspend fun getAllCategoryProductsToList(
        category: ProductCategory,
        gender: String,
        list: MutableList<ShoppingItem>
    ) {
        val subCategories = category.getSubCategories()
        val output = mutableListOf<ShoppingItem>()
        for (subCategory in subCategories) {
            val productCollection = subCategoryCollectionRef(category, subCategory)
            val items = productCollection.get()
                .tryAwait()

            items?.let {
                it.toObjects(ShoppingItem::class.java).let { list ->
                    output.addAll(list.filter { item -> item.gender == gender })
                }
            }
        }
        list.addAll(output)
    }


    suspend fun getAllCategoryProductsAll(
        gender: String = "female",
        liveData: MutableLiveData<List<ShoppingItem>>
    ) {
        val output = mutableListOf<ShoppingItem>()
        Category.pick3Categories().forEach {
            getAllCategoryProductsToList(it.category, gender = gender, list = output)
        }
        liveData.postValue(output)
    }


    suspend fun getAllCategoryProductsAll2() {
        var categoryNames = Category.categoryList().map { it.category.value.lowercase() }
        val rand = max(1, Random().nextInt(categoryNames.size))
        categoryNames = categoryNames.slice(0..rand)
        val categories = productCategoriesCollectionReference
            .get()
            .await()
            ?.documents
            ?.filter {
                println("IDS")
                println(it.id)
                categoryNames.contains(it.id)
            }
            ?.forEach {
                val hash = it.data!!
                val subcategories = hash[it.id]
                println(subcategories)
            }
    }

    suspend fun putSellerRating() {
        Category.categoryList()
            .forEach { category ->
                category.category.getSubCategories().forEach { subcategory ->
                    val productCollection =
                        subCategoryCollectionRef(category.category, subcategory)
                    productCollection.get()
                        .await()
                        .forEach {
                            it.reference.update("publisherRating", 0)
                        }
                }
            }
    }


    fun listenAllCategoryProductsBySubCategory(
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

    suspend fun getSingle(
        id: String,
        category: ProductCategory,
        subCategory: ProductCategory.SubCategory,
    ): ShoppingItem? {
        val collectionRef = subCategoryCollectionRef(category, subCategory)
        val product = collectionRef.document(id).get().tryAwait {
            println(it)
        }?.toObject(ShoppingItem::class.java)
        return product
    }


    private fun clear() {
        listener?.remove()
    }

}