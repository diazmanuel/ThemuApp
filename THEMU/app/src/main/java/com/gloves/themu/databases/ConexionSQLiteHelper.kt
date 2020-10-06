package com.gloves.themu.databases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getIntOrNull
import com.gloves.themu.classes.*


class ConexionSQLiteHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME,null,DATABASE_VERSION) {
    private val db: SQLiteDatabase
    private val values : ContentValues
    companion object{
        private val DATABASE_NAME = "THEMU"
        private val DATABASE_VERSION = 1
    }
    init {
        db = this.writableDatabase
        values = ContentValues()
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(Tables.Gestures.CREATE_TABLE_GESTURES)
        db.execSQL(Tables.Effects.CREATE_TABLE_EFFECTS)
        db.execSQL(Tables.Parameters.CREATE_TABLE_PARAMETRES)
        db.execSQL(Tables.Profile.CREATE_TABLE_PROFILES)
        db.execSQL(Tables.Links.CREATE_TABLE_LINKS)


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL(Tables.Gestures.UPDATE_TABLE_GESTURES)
        db.execSQL(Tables.Effects.UPDATE_TABLE_EFFECTS)
        db.execSQL(Tables.Parameters.UPDATE_TABLE_PARAMETRES)
        db.execSQL(Tables.Profile.UPDATE_TABLE_PROFILES)
        db.execSQL(Tables.Links.UPDATE_TABLE_LINKS)
        onCreate(db)
    }
    fun insertGesture(gesture : Gesture){
        values.put(Tables.Gestures.FIELD_GESTURE_USERSNAME,gesture.usersName)
        values.put(Tables.Gestures.FIELD_GESTURE_LITTLE,gesture.littleFinger)
        values.put(Tables.Gestures.FIELD_GESTURE_RING,gesture.ringFinger)
        values.put(Tables.Gestures.FIELD_GESTURE_MIDDLE,gesture.middleFinger)
        values.put(Tables.Gestures.FIELD_GESTURE_INDEX,gesture.indexFinger)
        values.put(Tables.Gestures.FIELD_GESTURE_THUMB,gesture.thumbFinger)
        values.put(Tables.Gestures.FIELD_GESTURE_XAXIS,gesture.xAxis)
        values.put(Tables.Gestures.FIELD_GESTURE_YAXIS,gesture.yAxis)
        values.put(Tables.Gestures.FIELD_GESTURE_ZAXIS,gesture.zAxis)
        db.insert(Tables.Gestures.TABLE_GESTURES,null,values)
        values.clear()
    }
    fun insertEffect(effect : Effect){
        //para crear el efecto tomo el nombre del effecto de la base de datos
        //y obtengo un effect descritor del native interface //effectDescriptor =  NativeInterface.effectDescriptionMap[effectName]
        //y luego creo el effecto effect = Effect (effectDescriptor)
        values.put(Tables.Effects.FIELD_EFFECT_USERSNAME,effect.usersName)
        values.put(Tables.Effects.FIELD_EFFECT_NAME,effect.name)
        db.insert(Tables.Effects.TABLE_EFFECTS, null,values)
        values.clear()

        val cursor = db.rawQuery(
            "SELECT MAX("+
                Tables.Effects.FIELD_EFFECT_PK +
                 ") FROM " +
                 Tables.Effects.TABLE_EFFECTS
        ,null)

        cursor.moveToFirst()
        effect.effectPK = cursor.getInt(0)
        cursor.close()
        for (param in effect.paramValues){
            values.put(Tables.Parameters.FIELD_EFFECT_FK,effect.effectPK)
            values.put(Tables.Parameters.FIELD_PARAMETRES_VALUE,param)
            db.insert(Tables.Parameters.TABLE_PARAMETRES,null,values)
            values.clear()
        }
    }
    fun insertProfile(profile :Profile){
        values.put(Tables.Profile.FIELD_PROFILE_USERSNAME,profile.usersName)
        db.insert(Tables.Profile.TABLE_PROFILES,null,values)
        values.clear()
        val cursor = db.rawQuery(
            "SELECT MAX("+
                    Tables.Profile.FIELD_PROFILE_PK +
                    ") FROM " +
                    Tables.Profile.TABLE_PROFILES
            ,null)
        cursor.moveToFirst()
        profile.profilePK = cursor.getInt(0)
        for (link in profile.links){
            values.put(Tables.Links.FIELD_PROFILE_FK,profile.profilePK)
            values.put(Tables.Links.FIELD_EFFECT_FK,link.effect.effectPK)
            values.put(Tables.Links.FIELD_GESTURE_FK,link.gesture.gesturePK)
            values.put(Tables.Links.FIELD_LINKS_LED,link.led)
            db.insert(Tables.Links.TABLE_LINKS,null,values)
            values.clear()
        }
    }


    fun readGestures() : List<Gesture>  {
        val gestures: MutableList<Gesture> = mutableListOf()
        val cursor  = db.rawQuery(
            "SELECT * FROM "+
                    Tables.Gestures.TABLE_GESTURES
        ,null)
        if(cursor.moveToFirst()) {
            do {
                gestures.add(
                    Gesture(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getInt(4),
                        cursor.getInt(5),
                        cursor.getInt(6),
                        cursor.getFloat(7),
                        cursor.getFloat(8),
                        cursor.getFloat(9)
                    )
                )
                cursor.getString(0)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return gestures
    }
    fun readEffects(): List<Effect>{
        val effects: MutableList<Effect> = mutableListOf()
        var effectAux : Effect
        var paramCount = 0
        var pk = 0
        //SELECT EFFECTS.* , PARAMETRES.VALUE from EFFECTS JOIN PARAMETRES ON EFFECTS.EFFECT_PK = PARAMETRES.PARAMETRE_FK ORDER BY PARAMETRES.PARAMETRE_PK ASC
        val cursor = db.rawQuery(
            "SELECT "+
                    Tables.Effects.TABLE_EFFECTS+".* , "+
                    Tables.Parameters.TABLE_PARAMETRES+"."+
                    Tables.Parameters.FIELD_PARAMETRES_VALUE+
                    " FROM "+
                    Tables.Effects.TABLE_EFFECTS+
                    " JOIN "+
                    Tables.Parameters.TABLE_PARAMETRES+
                    " ON "+
                    Tables.Effects.TABLE_EFFECTS+
                    "."+
                    Tables.Effects.FIELD_EFFECT_PK +
                    " = "+
                    Tables.Parameters.TABLE_PARAMETRES+
                    "."+
                    Tables.Parameters.FIELD_EFFECT_FK+
                    " ORDER BY "+
                    Tables.Parameters.TABLE_PARAMETRES+
                    "."+
                    Tables.Parameters.FIELD_PARAMETRES_PK+
                    " ASC"
        ,null)
        if (cursor.moveToFirst()){
            do{
                if(cursor.getInt(0)!=pk){
                    pk=cursor.getInt((0))
                    effectAux = NativeInterface.effectDescriptionMap[cursor.getString(1)]?.let {
                        Effect(
                            it
                        )
                    }!!
                    effectAux.usersName = cursor.getString(2)
                    effectAux.effectPK = pk
                    effects.add(effectAux)
                    paramCount = 0
                }
                effects[effects.size - 1].paramValues[paramCount]=cursor.getFloat(3)
                paramCount++
            }while (cursor.moveToNext())
        }
        cursor.close()
        return  effects
    }

    fun readProfiles(): List<Profile>{
        val profiles = mutableListOf<Profile>()
        val links = mutableListOf<Link>()
        var gestures = emptyList<Gesture>()
        var effects = emptyList<Effect>()

        gestures = readGestures()
        effects = readEffects()
        //SELECT PROFILES.*,LINKS.* FROM LINKS JOIN PROFILES ON LINKS.PROFILE_FK = PROFILES.PROFILE_PK ORDER BY LINKS.LINKS_PK ASC
        val cursor = db.rawQuery(
            "SELECT "+
                    Tables.Profile.TABLE_PROFILES+".*,"+
                    Tables.Links.TABLE_LINKS+".*"+
                    " FROM "+
                    Tables.Links.TABLE_LINKS+
                    " JOIN "+
                    Tables.Profile.TABLE_PROFILES+
                    " ON "+
                    Tables.Links.TABLE_LINKS+
                    "."+
                    Tables.Links.FIELD_PROFILE_FK +
                    " = "+
                    Tables.Profile.TABLE_PROFILES+
                    "."+
                    Tables.Profile.FIELD_PROFILE_PK+
                    " ORDER BY "+
                    Tables.Links.TABLE_LINKS+
                    "."+
                    Tables.Links.FIELD_LINKS_PK+
                    " ASC"
        ,null)
        if(cursor.moveToFirst()){
            var auxProfilePK = cursor.getInt(0)
            do{

                links.add(
                    Link(
                        gestures.find { it.gesturePK == cursor.getInt(4) }!!,
                        effects.find { it.effectPK == cursor.getInt(5)}!!,
                        cursor.getInt(6),
                        cursor.getInt(2)
                    )
                )
                if(auxProfilePK != cursor.getInt(0)){
                    profiles.add(
                        Profile(
                            links,
                            cursor.getString(1),
                            auxProfilePK
                        )
                    )
                    links.clear()
                    auxProfilePK = cursor.getInt(0)
                }
            }while (cursor.moveToNext())
        }
        cursor.close()
        return profiles
    }

}