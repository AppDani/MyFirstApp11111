package com.danielarog.myfirstapp.models

data class AppUser(
    val uid:String,
    val email:String,
    val name:String,
    val gender:String,
    val address_city:String,
    val address:String,
    var rating:Long
) {

    constructor() : this("","","","","","",0)
}