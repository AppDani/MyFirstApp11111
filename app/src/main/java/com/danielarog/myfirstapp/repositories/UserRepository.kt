package com.danielarog.myfirstapp.repositories

import androidx.lifecycle.MutableLiveData
import com.danielarog.myfirstapp.models.AppUser
import com.danielarog.myfirstapp.models.FavoriteItem
import com.danielarog.myfirstapp.models.ShoppingItem
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

object UserRepository {

    val usersCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection("users")
    val userId: String by lazy {
        FirebaseAuth.getInstance().uid!!
    }

    fun listenFavorites(favorites: MutableLiveData<HashMap<String, FavoriteItem>>): ListenerRegistration? {
        val hashMap = HashMap<String, FavoriteItem>()
        return FirebaseAuth.getInstance().uid?.let {
            usersCollection.document(it)
                .collection("favorites")
                .addSnapshotListener { value, _ ->
                    value?.forEach { snapshot ->
                        val favItem = snapshot.toObject(FavoriteItem::class.java)
                        hashMap[snapshot.id] = favItem
                    }
                    favorites.postValue(hashMap)
                }
        }
    }

    fun listenTopSellers(liveData: MutableLiveData<List<AppUser>>) {
        usersCollection.orderBy("rating").limit(10)
            .addSnapshotListener { value, _ ->
                val users = value?.toObjects(AppUser::class.java)
                users?.let {
                    liveData.postValue(it)
                }
            }
    }

    suspend fun getUser(): AppUser? {
        return usersCollection.document(userId)
            .get()
            .tryAwait()?.toObject(AppUser::class.java)
    }

    fun listenToUser(
        userLiveData: MutableLiveData<AppUser>,
        exceptionLiveData: MutableLiveData<Exception>
    ): ListenerRegistration {
        return usersCollection.document(userId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    exceptionLiveData.postValue(error)
                    return@addSnapshotListener
                }
                value?.toObject(AppUser::class.java)?.let { user ->
                    userLiveData.postValue(user)
                } ?: run {
                    exceptionLiveData.postValue(
                        java.lang.Exception("Unknown error occurred..")
                    )
                }
            }
    }

    suspend fun saveItem(item: ShoppingItem) {
        val newRef = usersCollection.document(userId)
            .collection("items")
            .add(item)
            .tryAwait()
        newRef?.update("id", newRef.id)
    }

    suspend fun editItem(item: ShoppingItem) {
        usersCollection.document(userId)
            .collection("items")
            .document(item.id!!)
            .set(item)
            .tryAwait()
    }

    suspend fun deleteItem(shoppingItem: ShoppingItem) {
        usersCollection.document(userId)
            .collection("items")
            .document(shoppingItem.id!!)
            .delete()
            .tryAwait()
    }

    suspend fun getProducts(): List<ShoppingItem> {
        val result = usersCollection.document(FirebaseAuth.getInstance().uid!!)
            .collection("items")
            .get()
            .tryAwait()
        return result?.toObjects(ShoppingItem::class.java) ?: listOf()
    }

    fun listenToUserProducts(
        liveData: MutableLiveData<List<ShoppingItem>>,
        exceptionsLiveData: MutableLiveData<java.lang.Exception>
    ): ListenerRegistration {
        return usersCollection.document(FirebaseAuth.getInstance().uid!!)
            .collection("items")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    exceptionsLiveData.postValue(error)
                    return@addSnapshotListener
                }
                liveData.postValue(value?.toObjects(ShoppingItem::class.java) ?: listOf())
            }

    }

    suspend fun saveProfile(user: AppUser) {
        usersCollection.document(user.uid).set(user).tryAwait()
    }

}