package com.gloves.themu.databases

class Tables {
    abstract class Effects{
        companion object {
            val TABLE_EFFECTS = "EFFECTS"

            val FIELD_EFFECT_PK = "EFFECT_PK"
            val FIELD_EFFECT_NAME = "NAME"
            val FIELD_EFFECT_USERSNAME = "USERSNAME"

            val CREATE_TABLE_EFFECTS = ("CREATE TABLE " + TABLE_EFFECTS + "("
                    + FIELD_EFFECT_PK + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + FIELD_EFFECT_NAME + " TEXT,"
                    + FIELD_EFFECT_USERSNAME + " TEXT)")

            val UPDATE_TABLE_EFFECTS = "DROP TABLE IF EXISTS $TABLE_EFFECTS"
        }
    }
    abstract class Parameters{
        companion object {
            val TABLE_PARAMETRES = "PARAMETRES"

            val FIELD_PARAMETRES_PK = "PARAMETRE_PK"
            val FIELD_EFFECT_FK = "EFFECT_FK"
            val FIELD_PARAMETRES_VALUE = "VALUE"

            val CREATE_TABLE_PARAMETRES = ("CREATE TABLE " + TABLE_PARAMETRES + "("
                    + FIELD_PARAMETRES_PK + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + FIELD_EFFECT_FK + " INTEGER,"
                    + FIELD_PARAMETRES_VALUE + " REAL)")

            val UPDATE_TABLE_PARAMETRES = "DROP TABLE IF EXISTS $TABLE_PARAMETRES"
        }
    }

    abstract class Profile{
        companion object {
            val TABLE_PROFILES = "PROFILES"

            val FIELD_PROFILE_PK = "PROFILE_PK"
            val FIELD_PROFILE_USERSNAME = "USERSNAME"

            val CREATE_TABLE_PROFILES = ("CREATE TABLE " + TABLE_PROFILES + "("
                    + FIELD_PROFILE_PK + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + FIELD_PROFILE_USERSNAME + " TEXT)")

            val UPDATE_TABLE_PROFILES = "DROP TABLE IF EXISTS $TABLE_PROFILES"
        }
    }
    abstract class Links{
        companion object {
            val TABLE_LINKS = "LINKS"

            val FIELD_LINKS_PK = "LINKS_PK"
            val FIELD_GESTURE = "GESTURE"
            val FIELD_EFFECT_FK = "EFFECT_FK"
            val FIELD_PROFILE_FK = "PROFILE_FK"
            val FIELD_LINKS_LED = "LED"
            val FIELD_DINAMIC_EFFECT = "DIN_EFFECT"



            val CREATE_TABLE_LINKS = ("CREATE TABLE " + TABLE_LINKS + "("
                    + FIELD_LINKS_PK + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + FIELD_PROFILE_FK + " INTEGER,"
                    + FIELD_GESTURE + " INTEGER,"
                    + FIELD_EFFECT_FK + " INTEGER,"
                    + FIELD_DINAMIC_EFFECT+ " INTEGER,"
                    + FIELD_LINKS_LED+ " INTEGER)")

            val UPDATE_TABLE_LINKS = "DROP TABLE IF EXISTS $TABLE_LINKS"
        }
    }
}