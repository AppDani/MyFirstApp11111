package com.danielarog.myfirstapp.models

data class Comment(
    val commenterId:String,
    val commenterName:String,
    val commenterImage:String,
    val content:String,
    val rating:Long
) {
    constructor() : this("","","","",0)
}