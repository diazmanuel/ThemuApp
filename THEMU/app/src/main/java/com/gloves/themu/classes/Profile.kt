package com.gloves.themu.classes

data class Profile(
    val links: MutableList<Link>,
    val usersName : String,
    var profilePK : Int
)