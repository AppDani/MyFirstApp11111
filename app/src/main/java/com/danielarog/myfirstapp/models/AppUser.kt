package com.danielarog.myfirstapp.models

data class AppUser(
    val uid:String,
    val email:String,
    var name:String,
    var gender:String,
    var address_city:String,
    var image: String,
    var address:String,
    var rating:Long
) {

    constructor() : this("","","","","","","",0)
}