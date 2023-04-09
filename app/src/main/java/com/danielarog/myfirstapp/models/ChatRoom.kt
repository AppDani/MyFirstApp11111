package com.danielarog.myfirstapp.models

data class ChatRoom(
    val roomId:String,
    val userId1:String,
    val userId2:String,
    val messages:MutableList<ChatMessage>
) {
    constructor() : this("","","", mutableListOf())
}