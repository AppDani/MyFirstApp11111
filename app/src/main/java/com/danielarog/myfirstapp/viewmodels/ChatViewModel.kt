package com.danielarog.myfirstapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielarog.myfirstapp.models.AppUser
import com.danielarog.myfirstapp.models.ChatRoom
import com.danielarog.myfirstapp.models.ChatRoomReference
import com.danielarog.myfirstapp.models.ShoppingItem
import com.danielarog.myfirstapp.repositories.ChatRepository
import com.danielarog.myfirstapp.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _chatRoomsLiveData: MutableLiveData<List<ChatRoomReference>> = MutableLiveData()
    val chatRoomsLiveData: LiveData<List<ChatRoomReference>> = _chatRoomsLiveData

    private val _chatRoomLiveData: MutableLiveData<ChatRoom?> = MutableLiveData(null)
    val chatRoomLiveData: LiveData<ChatRoom?> = _chatRoomLiveData


    private val _exceptionsLiveData: MutableLiveData<Exception> = MutableLiveData()
    val exceptionsLiveData: LiveData<Exception> = _exceptionsLiveData

    var chatRoomsListener: ListenerRegistration? = null
    var chatRoomListener: ListenerRegistration? = null


    init {
        chatRoomsListener =
            ChatRepository.listenForChatRooms(_chatRoomsLiveData, _exceptionsLiveData)
    }


    fun enterChatRoom(roomId: String) {
        chatRoomListener?.remove()
        chatRoomListener =
            ChatRepository.listenForChatMessages(
                roomId,
                _chatRoomLiveData,
                _exceptionsLiveData
            )
    }

    fun sendMessage(room: ChatRoom, user: AppUser, message: String) {
        viewModelScope.launch {
            ChatRepository.sendChatMessage(room, user, message)
        }
    }


    fun startChatWithForItem(
        customer: AppUser,
        shoppingItem: ShoppingItem,
        onChatStarted: () -> Unit
    ) {
        val pubId = shoppingItem.publisherId ?: return
        // if the pub is us, don't allow chat
        if (FirebaseAuth.getInstance().currentUser?.uid == pubId)
            return run {
                _exceptionsLiveData.postValue(java.lang.Exception("You can not start a chat with your self!"))
            }
        viewModelScope.launch {
            UserRepository.getUserById(pubId)?.let { seller ->
                ChatRepository.chatWithSeller(
                    customer,
                    seller,
                    shoppingItem,
                    viewModelScope,
                    onChatStarted
                )
            } ?: run {
                onChatStarted()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (chatRoomListener != null) {
            chatRoomListener!!.remove()
        }
        if (chatRoomsListener != null) {
            chatRoomsListener!!.remove()
        }
    }


}