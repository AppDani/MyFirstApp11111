package com.danielarog.myfirstapp.models

class ChatRoom(
    val roomId:String,
    val userId1:String,
    val userId2:String,
    val messages:List<ChatMessage>
) {
    constructor() : this("","","", listOf())
}