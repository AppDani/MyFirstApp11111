package com.danielarog.myfirstapp.repositories

import com.danielarog.myfirstapp.models.AppUser
import com.danielarog.myfirstapp.tryAwait
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object UserRepository {

    private val usersCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection("users")

    suspend fun getUser(): AppUser? {
        return usersCollection.document(FirebaseAuth.getInstance().uid!!)
            .get()
            .tryAwait()?.toObject(AppUser::class.java)
    }

    suspend fun saveProfile(user: AppUser) {
        usersCollection.document(user.uid).set(user).tryAwait()
    }
}