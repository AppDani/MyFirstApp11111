package com.danielarog.myfirstapp.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielarog.myfirstapp.models.AppUser
import com.danielarog.myfirstapp.repositories.UserRepository
import com.danielarog.myfirstapp.repositories.tryAwait
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class ImageUploader(
    val imageByteArray: ByteArray?,
    val imageUri: Uri?
) {

    suspend fun upload(path: String): String? { // userImages/uid/image.jpg
        if (imageByteArray == null && imageUri == null) return null
        val storageRef = FirebaseStorage.getInstance().getReference(path)
        return imageByteArray?.let {
            storageRef.putBytes(it).await()
            return storageRef.downloadUrl.tryAwait().toString()
        } ?: run {
            storageRef.putFile(imageUri!!).await()
            return storageRef.downloadUrl.tryAwait().toString()
        }
    }
}

class AuthViewModel : ViewModel() {

    private val _currentUser: MutableLiveData<AppUser> = MutableLiveData()
    val currentUser: LiveData<AppUser> = _currentUser

    init {
        viewModelScope.launch {
            if (FirebaseAuth.getInstance().uid != null) {
                UserRepository.getUser().let { user ->
                    _currentUser.postValue(user)
                }
            }
        }
    }

    suspend fun getUser(): AppUser? {
        return UserRepository.getUser()
    }

    suspend fun saveUser(
        appUser: AppUser,
        imageByteArray: ByteArray? = null,
        imageUri: Uri? = null
    ) {

        val uploadResult = ImageUploader(imageByteArray, imageUri)
            .upload("/images/users/${appUser.uid}.jpg")

        uploadResult?.let { imageUrl ->
            appUser.image = imageUrl
        }
        UserRepository.saveProfile(appUser)
    }
}