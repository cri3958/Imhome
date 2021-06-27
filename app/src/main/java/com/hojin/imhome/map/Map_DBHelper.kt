package com.hojin.imhome.map

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Map_DBHelper(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object{
        private val DB_VERSION = 1
        private val DB_NAME = "ImHome.db"

        private val AREALIST = "AREA"
        private val AREA_ID = "Id"
        private val AREA_NAME = "Name"
        private val AREA_ADDRESS = "Address"
        private val AREA_LATITUDE = "Latitude"
        private val AREA_LONGITUDE = "Longitude"
        private val AREA_RADIUS = "Radius"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_AREA =
            ("CREATE TABLE " + AREALIST + "("
                    + AREA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + AREA_NAME + " TEXT,"
                    + AREA_ADDRESS + " TEXT,"
                    + AREA_LATITUDE + " TEXT,"
                    + AREA_LONGITUDE + " TEXT,"
                    + AREA_RADIUS + " TEXT)")
        db!!.execSQL(CREATE_TABLE_AREA)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newViersion: Int) {
        when (oldVersion) {
            1 -> {
                db!!.execSQL("DROP TABLE IF EXISTS $AREALIST")
            }
        }
        onCreate(db)
    }

    fun insertAREALIST(map:Map) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(AREA_NAME,map.getName())
        contentValues.put(AREA_ADDRESS,map.getAddress())
        contentValues.put(AREA_LATITUDE,map.getLatitude())
        contentValues.put(AREA_LONGITUDE,map.getLongitude())
        contentValues.put(AREA_RADIUS,map.getRadius())
        db.insert(AREALIST, null, contentValues)
        db.close()
    }

    fun getAREALIST():MutableList<Map> {
        val db = this.readableDatabase
        val maplist = mutableListOf<Map>()
        var map:Map
        val cursor = db.rawQuery("SELECT * FROM $AREALIST",null)
        while(cursor.moveToNext()){
            map = Map()
            map.setName(cursor.getString(cursor.getColumnIndex(AREA_NAME)))
            map.setAddress(cursor.getString(cursor.getColumnIndex(AREA_ADDRESS)))
            map.setLatitude(cursor.getString(cursor.getColumnIndex(AREA_LATITUDE)))
            map.setLongitude(cursor.getString(cursor.getColumnIndex(AREA_LONGITUDE)))
            map.setRadius(cursor.getString(cursor.getColumnIndex(AREA_RADIUS)))
            maplist.add(map)
        }
        cursor.close()
        db.close()
        return maplist
    }

    fun updateAREA(map:Map,latitude:Double,longitude:Double){
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(AREA_NAME,map.getName())
        contentValues.put(AREA_ADDRESS,map.getAddress())
        contentValues.put(AREA_LATITUDE,map.getLatitude())
        contentValues.put(AREA_LONGITUDE,map.getLongitude())
        contentValues.put(AREA_RADIUS,map.getRadius())
        db.update(AREALIST, contentValues,"$AREA_LATITUDE = $latitude AND $AREA_LONGITUDE = $longitude",null)
    }
}