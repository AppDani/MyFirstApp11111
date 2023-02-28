package com.danielarog.myfirstapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielarog.myfirstapp.models.ProductRequest
import com.danielarog.myfirstapp.repositories.ProductRequestRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProductRequestsViewModel : ViewModel() {
    private val repository: ProductRequestRepository = ProductRequestRepository()

    private val _incomingRequestsLiveData: MutableLiveData<List<ProductRequest>> = MutableLiveData()
    val incomingRequestsLiveData: LiveData<List<ProductRequest>> get() = _incomingRequestsLiveData

    private val _outgoingRequestsLiveData: MutableLiveData<List<ProductRequest>> = MutableLiveData()
    val outgoingRequestsLiveData: LiveData<List<ProductRequest>> get() = _outgoingRequestsLiveData

    private val _exceptionsLiveData: MutableLiveData<Exception> = MutableLiveData()
    val exceptionsLiveData: LiveData<Exception> get() = _exceptionsLiveData


    private val _requestStatusLiveData: MutableLiveData<ProductRequestRepository.RequestStatus> =
        MutableLiveData()
    val requestStatusLiveData: LiveData<ProductRequestRepository.RequestStatus> get() = _requestStatusLiveData

    fun startListeningRequests() {
        repository.getIncomingRequests(_incomingRequestsLiveData, _exceptionsLiveData)
        repository.getOutgoingRequests(_outgoingRequestsLiveData, _exceptionsLiveData)
    }

    suspend fun canMakeRequest(
        productId: String
    ): Boolean {
        return FirebaseAuth.getInstance().currentUser?.let { requesteeUser ->
            repository.canMakeRequest(requesteeUser.uid, productId)
        } ?: false
    }

    fun makeRequest(
        toUserId: String,
        productId: String
    ) {
        viewModelScope.launch {
            val status = repository.makeRequest(toUserId, productId)
            _requestStatusLiveData.postValue(status)
        }
    }
}