package com.danielarog.myfirstapp.models

data class ChatRoomRequest(
    val roomId: String = "",
    val buyerId: String = "",
    val sellerId: String = "",
)