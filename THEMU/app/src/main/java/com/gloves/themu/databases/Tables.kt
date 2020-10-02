package com.gloves.themu.databases

class Tables {
    abstract class Gestures{
        companion object {
            val TABLE_GESTURES = "GESTURES"

            val FIELD_GESTURE_PK = "GESTURE_PK"
            val FIELD_GESTURE_USERSNAME = "USERSNAME"
            val FIELD_GESTURE_LITTLE = "LITTLE_FINGER"
            val FIELD_GESTURE_RING = "RING_FINGER"
            val FIELD_GESTURE_MIDDLE = "MIDDLE_FINGER"
            val FIELD_GESTURE_INDEX = "INDEX_FINGER"
            val FIELD_GESTURE_THUMB = "THUMB_FINGER"
            val FIELD_GESTURE_XAXIS = "XAXIS"
            val FIELD_GESTURE_YAXIS = "YAXIS"
            val FIELD_GESTURE_ZAXIS = "ZAXIS"


            val CREATE_TABLE_GESTURE = ("CREATE TABLE " + TABLE_GESTURES + "("
                    + FIELD_GESTURE_PK + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + FIELD_GESTURE_USERSNAME + " TEXT,"
                    + FIELD_GESTURE_LITTLE + " INTEGER,"
                    + FIELD_GESTURE_RING + " INTEGER,"
                    + FIELD_GESTURE_MIDDLE + " INTEGER,"
                    + FIELD_GESTURE_INDEX + " INTEGER,"
                    + FIELD_GESTURE_THUMB + " INTEGER,"
                    + FIELD_GESTURE_XAXIS + " REAL,"
                    + FIELD_GESTURE_YAXIS + " REAL,"
                    + FIELD_GESTURE_ZAXIS + " REAL)")

            val UPDATE_TABLE_GESTURE = "DROP TABLE IF EXISTS $TABLE_GESTURES"
        }
    }
    abstract class Effects{
        companion object {
            val TABLE_EFFECTS = "EFFECTS"

            val FIELD_EFFECT_PK = "EFFECT_PK"
            val FIELD_EFFECT_NAME = "NAME"
            val FIELD_EFFECT_USERSNAME = "USERSNAME"

            val CREATE_TABLE_EFFECT = ("CREATE TABLE " + TABLE_EFFECTS + "("
                    + FIELD_EFFECT_PK + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + FIELD_EFFECT_NAME + " TEXT,"
                    + FIELD_EFFECT_USERSNAME + " TEXT)")

            val UPDATE_TABLE_EFFECT = "DROP TABLE IF EXISTS $TABLE_EFFECTS"
        }
    }
    abstract class Parameters{
        companion object {
            val TABLE_PARAMETRES = "PARAMETRES"

            val FIELD_PARAMETRES_PK = "PARAMETRE_PK"
            val FIELD_PARAMETRES_FK = "PARAMETRE_FK"
            val FIELD_PARAMETRES_VALUE = "VALUE"

            val CREATE_TABLE_PARAMETRE = ("CREATE TABLE " + TABLE_PARAMETRES + "("
                    + FIELD_PARAMETRES_PK + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + FIELD_PARAMETRES_FK + " INTEGER,"
                    + FIELD_PARAMETRES_VALUE + " REAL)")

            val UPDATE_TABLE_PARAMETRE = "DROP TABLE IF EXISTS $TABLE_PARAMETRES"
        }
    }
}