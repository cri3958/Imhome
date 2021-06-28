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

    fun insertAREALIST(area:AREA) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(AREA_NAME,area.getName())
        contentValues.put(AREA_ADDRESS,area.getAddress())
        contentValues.put(AREA_LATITUDE,area.getLatitude())
        contentValues.put(AREA_LONGITUDE,area.getLongitude())
        contentValues.put(AREA_RADIUS,area.getRadius())
        db.insert(AREALIST, null, contentValues)
        db.close()
    }

    fun getAREALIST():ArrayList<AREA> {
        val db = this.readableDatabase
        val arealist = ArrayList<AREA>()
        var area:AREA
        val cursor = db.rawQuery("SELECT * FROM $AREALIST",null)
        while(cursor.moveToNext()){
            area = AREA()
            area.setName(cursor.getString(cursor.getColumnIndex(AREA_NAME)))
            area.setAddress(cursor.getString(cursor.getColumnIndex(AREA_ADDRESS)))
            area.setLatitude(cursor.getString(cursor.getColumnIndex(AREA_LATITUDE)))
            area.setLongitude(cursor.getString(cursor.getColumnIndex(AREA_LONGITUDE)))
            area.setRadius(cursor.getString(cursor.getColumnIndex(AREA_RADIUS)))
            arealist.add(area)
        }
        cursor.close()
        db.close()
        return arealist
    }

    fun updateAREA(area:AREA,latitude:Double,longitude:Double){
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(AREA_NAME,area.getName())
        contentValues.put(AREA_ADDRESS,area.getAddress())
        contentValues.put(AREA_LATITUDE,area.getLatitude())
        contentValues.put(AREA_LONGITUDE,area.getLongitude())
        contentValues.put(AREA_RADIUS,area.getRadius())
        db.update(AREALIST, contentValues,"$AREA_LATITUDE = $latitude AND $AREA_LONGITUDE = $longitude",null)
    }

    fun checkArea(latitude: Double,longitude: Double):Boolean{
        val db = this.readableDatabase
        var isExist:Boolean = false

        val cursor = db.rawQuery("SELECT * FROM $AREALIST",null)
        while (cursor.moveToNext()){
            if(latitude.toString() == cursor.getString(cursor.getColumnIndex(AREA_LATITUDE))
                && longitude.toString() == cursor.getString(cursor.getColumnIndex(AREA_LONGITUDE))){
                isExist = true
                cursor.close()
                db.close()
                return isExist
            }
        }
        cursor.close()
        db.close()
        return isExist
    }
}