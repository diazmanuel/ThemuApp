package com.gloves.themu.databases

import android.app.ActionBar
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getIntOrNull
import com.gloves.themu.classes.Effect
import com.gloves.themu.classes.Gesture


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
        db!!.execSQL(Tables.Gestures.CREATE_TABLE_GESTURE)
        db.execSQL(Tables.Effects.CREATE_TABLE_EFFECT)
        db.execSQL(Tables.Parameters.CREATE_TABLE_PARAMETRE)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL(Tables.Gestures.UPDATE_TABLE_GESTURE)
        db.execSQL(Tables.Effects.UPDATE_TABLE_EFFECT)
        db.execSQL(Tables.Parameters.UPDATE_TABLE_PARAMETRE)
        onCreate(db)
    }
    fun insertGesture(gesture : Gesture){
        values.put(Tables.Gestures.FIELD_GESTURE_USERSNAME,gesture.gestureUsersName)
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
        effect.effectPK = cursor.getIntOrNull(0)
        cursor.close()
        for (param in effect.paramValues){
            values.put(Tables.Parameters.FIELD_PARAMETRES_FK,effect.effectPK)
            values.put(Tables.Parameters.FIELD_PARAMETRES_VALUE,param)
            db.insert(Tables.Parameters.TABLE_PARAMETRES,null,values)
            values.clear()
        }
    }
    fun readGestures() : List<Gesture>  {
        val gestures: MutableList<Gesture> = mutableListOf()
        val cursor = db.rawQuery(
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

}