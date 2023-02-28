package com.danielarog.myfirstapp.repositories

import androidx.lifecycle.MutableLiveData
import com.danielarog.myfirstapp.models.ProductRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

const val REQUEST_EXISTS = 0
const val SUCCESS = 1
const val USER_NOT_LOGGED = 2

class ProductRequestRepository {


    data class RequestStatus(
        val status: Int,
        val success: Boolean
    )

    private val usersCollection: CollectionReference =
        FirebaseFirestore.getInstance()
            .collection("users")

    suspend fun makeRequest(
        toUserId: String,
        productId: String
    ): RequestStatus {
        FirebaseAuth.getInstance().currentUser?.let { requesteeUser ->

            val request = ProductRequest(
                requestId = "",
                requesteeId = requesteeUser.uid,
                productId = productId,
                date = System.currentTimeMillis()
            )
            if (!canMakeRequest(requesteeUser.uid, productId)) {
                return RequestStatus(REQUEST_EXISTS, false)
            }

            val newRequestDocument = usersCollection
                .document(toUserId)
                .collection("incomingRequests")
                .add(request)
                .tryAwait()

            newRequestDocument?.let { incomingRequest ->
                request.requestId = incomingRequest.id
                usersCollection
                    .document(requesteeUser.uid)
                    .collection("outgoingRequests")
                    .document(incomingRequest.id)
                    .set(request)
                    .tryAwait()
                incomingRequest.update("requestId", incomingRequest.id)
            }
            return RequestStatus(SUCCESS, true)
        }
        return RequestStatus(USER_NOT_LOGGED, false)
    }

    suspend fun canMakeRequest(
        fromUserId: String,
        productId: String
    ): Boolean {
        return usersCollection
            .document(fromUserId)
            .collection("outgoingRequests")
            .whereEqualTo("productId", productId)
            .get()
            .tryAwait()
            ?.isEmpty ?: true
    }

    fun getOutgoingRequests(
        liveData: MutableLiveData<List<ProductRequest>>,
        exceptions: MutableLiveData<Exception>
    ): ListenerRegistration? {
        return FirebaseAuth.getInstance().currentUser?.let { requesteeUser ->
            return usersCollection
                .document(requesteeUser.uid)
                .collection("outgoingRequests")
                .addSnapshotListener { result, error ->
                    error?.let { err ->
                        liveData.postValue(listOf())
                        exceptions.postValue(err)
                    } ?: run {
                        result?.toObjects(ProductRequest::class.java)?.let { requests ->
                            liveData.postValue(requests)
                        } ?: run { liveData.postValue(listOf()) }
                    }
                }
        }
    }

    fun getIncomingRequests(
        liveData: MutableLiveData<List<ProductRequest>>,
        exceptions: MutableLiveData<Exception>
    ): ListenerRegistration? {
        return FirebaseAuth.getInstance().currentUser?.let { requesteeUser ->
            return usersCollection
                .document(requesteeUser.uid)
                .collection("incomingRequests")
                .addSnapshotListener { result, error ->
                    error?.let { err ->
                        liveData.postValue(listOf())
                        exceptions.postValue(err)
                    } ?: run {
                        result?.toObjects(ProductRequest::class.java)?.let { requests ->
                            liveData.postValue(requests)
                        } ?: run { liveData.postValue(listOf()) }
                    }
                }
        }
    }


}