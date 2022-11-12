package com.danielarog.myfirstapp.models

data class ChatMessage(
    val content:String,
    val date:Long,
    val senderId:String,
    val senderName:String
) {
    constructor() : this("",0,"","")
}