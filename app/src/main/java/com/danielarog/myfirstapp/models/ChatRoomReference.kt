package com.danielarog.myfirstapp.models

data class ChatRoomReference(
    val roomId:String,
    val userName1:String,
    val userName2:String,
    val userId1:String,
    val userId2:String,
    val userImage1:String,
    val userImage2:String
) {
    constructor() : this("","","","","","","")
}