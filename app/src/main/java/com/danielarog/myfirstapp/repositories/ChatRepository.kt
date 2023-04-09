package com.danielarog.myfirstapp.repositories

import androidx.lifecycle.MutableLiveData
import com.danielarog.myfirstapp.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

object ChatRepository {

    suspend fun chatWithSeller(
        customer: AppUser,
        seller: AppUser,
        product: ShoppingItem,
        scope: CoroutineScope,
        onChatStarted: () -> Unit
    ) {
        val exists_1 = scope.async {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(customer.uid)
                .collection("chats")
                .whereEqualTo("userId1", seller.uid)
                .get()
                .tryAwait()
        }.await()

        val exists_2 = scope.async {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(customer.uid)
                .collection("chats")
                .whereEqualTo("userId2", seller.uid)
                .get()
                .tryAwait()
        }.await()


        val defaultMessage =
            "Hello!, i am interested in hearing more details about ${product.itemName} which is on sale on your page!"
        val doesAlreadyExist = !(exists_1?.isEmpty ?: true) || !(exists_2?.isEmpty ?: true)
        if (!doesAlreadyExist) {
            val room = ChatRoom(
                roomId = "",
                userId1 = customer.uid,
                userId2 = seller.uid,
                messages = mutableListOf(
                    ChatMessage(
                        content = defaultMessage,
                        senderId = customer.uid,
                        senderName = customer.name,
                        date = System.currentTimeMillis()
                    )
                )
            )

            // Add the new chat to chats collection
            val newChatReference = FirebaseFirestore.getInstance()
                .collection("chats")
                .add(room)
                .tryAwait()


            newChatReference?.let { newChatRef ->
                newChatReference
                    .update("roomId", newChatReference.id)
                    .tryAwait()

                val chatRoomReference = ChatRoomReference(
                    roomId = newChatRef.id,
                    userId1 = customer.uid,
                    userId2 = seller.uid,
                    userName1 = customer.name,
                    userName2 = seller.name,
                    userImage1 = customer.image,
                    userImage2 = seller.image
                )

                // Add the new chat to the customer
                val customerChatReference = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(customer.uid)
                    .collection("chats")
                    .document(newChatRef.id)
                    .set(chatRoomReference)
                    .tryAwait()

                // Add the new chat to the seller
                val sellerChatReference = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(seller.uid)
                    .collection("chats")
                    .document(newChatRef.id)
                    .set(chatRoomReference)
                    .tryAwait()


                onChatStarted.invoke()
            } ?: run {
                onChatStarted.invoke()
            }

        }
    }


    fun listenForChatRooms(
        liveDataChats: MutableLiveData<List<ChatRoomReference>>,
        liveDataExceptions: MutableLiveData<Exception>
    ): ListenerRegistration? {

        return FirebaseAuth.getInstance().uid?.let { uid ->
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("chats")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        liveDataExceptions.postValue(error)
                        return@addSnapshotListener
                    }
                    val roomRefs = value?.toObjects(ChatRoomReference::class.java)
                    roomRefs?.let {
                        liveDataChats.postValue(it)
                    } ?: run {
                        liveDataExceptions.postValue(java.lang.Exception("Chat not found"))
                    }
                }
        }
    }

    fun listenForChatMessages(
        roomId: String,
        chatRoom: MutableLiveData<ChatRoom?>,
        exception: MutableLiveData<Exception>
    ): ListenerRegistration {
        return FirebaseFirestore.getInstance()
            .collection("chats")
            .whereEqualTo("roomId", roomId)
            .limit(1)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    exception.postValue(error)
                    return@addSnapshotListener
                }
                val rooms = (value?.toObjects(ChatRoom::class.java) ?: listOf())
                if (rooms.isEmpty()) {
                    exception.postValue(java.lang.Exception("Unknown error occured"))
                    return@addSnapshotListener
                }
                chatRoom.postValue(
                    rooms[0]
                )
            }
    }

    suspend fun sendChatMessage(room: ChatRoom, sender: AppUser, message: String) {
        val chatMessage = ChatMessage(
            message,
            System.currentTimeMillis(),
            senderId = sender.uid,
            senderName = sender.name
        )

        room.messages.add(chatMessage)
        FirebaseFirestore.getInstance()
            .collection("chats")
            .document(room.roomId)
            .update("messages", room.messages)
            .tryAwait()
    }


}