package com.gloves.themu.classes

data class Gesture(
    val gesturePK : Int,
    val gestureUsersName : String,
    val littleFinger : Int,
    val ringFinger : Int,
    val middleFinger : Int,
    val indexFinger : Int,
    val thumbFinger : Int,
    val xAxis : Float,
    val yAxis : Float,
    val zAxis : Float
    ) {

}