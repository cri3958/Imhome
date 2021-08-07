package com.hojin.imhome.map

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class Map_DBHelper(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    private val TAG = Map_DBHelper::class.java.simpleName

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
        private val AREA_ENTER = "Enter"
        private val AREA_ENTER_WIFI = "EnWifi"
        private val AREA_ENTER_DATA = "EnData"
        private val AREA_ENTER_SOUND = "EnSound"
        private val AREA_EXIT = "Exit"
        private val AREA_EXIT_WIFI = "ExWifi"
        private val AREA_EXIT_DATA = "ExData"
        private val AREA_EXIT_SOUND = "ExSound"
        private val AREA_REQUESTID = "RequestID"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_AREA =
            ("CREATE TABLE " + AREALIST + "("
                    + AREA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + AREA_NAME + " TEXT,"
                    + AREA_ADDRESS + " TEXT,"
                    + AREA_LATITUDE + " TEXT,"
                    + AREA_LONGITUDE + " TEXT,"
                    + AREA_RADIUS + " TEXT,"
                    + AREA_ENTER + " TEXT,"
                    + AREA_ENTER_WIFI + " TEXT,"
                    + AREA_ENTER_DATA + " TEXT,"
                    + AREA_ENTER_SOUND + " TEXT,"
                    + AREA_EXIT + " TEXT,"
                    + AREA_EXIT_WIFI + " TEXT,"
                    + AREA_EXIT_DATA + " TEXT,"
                    + AREA_EXIT_SOUND + " TEXT,"
                    + AREA_REQUESTID + " TEXT)")
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
        contentValues.put(AREA_ENTER,area.getEnter())
        contentValues.put(AREA_ENTER_WIFI,area.getEnwifi())
        contentValues.put(AREA_ENTER_DATA,area.getEndata())
        contentValues.put(AREA_ENTER_SOUND,area.getEnsound())
        contentValues.put(AREA_EXIT,area.getExit())
        contentValues.put(AREA_EXIT_WIFI,area.getExwifi())
        contentValues.put(AREA_EXIT_DATA,area.getExdata())
        contentValues.put(AREA_EXIT_SOUND,area.getExsound())
        contentValues.put(AREA_REQUESTID,area.getRequestId())
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
            area.setEnter(cursor.getString(cursor.getColumnIndex(AREA_ENTER)))
            area.setEnwifi(cursor.getString(cursor.getColumnIndex(AREA_ENTER_WIFI)))
            area.setEndata(cursor.getString(cursor.getColumnIndex(AREA_ENTER_DATA)))
            area.setEnsound(cursor.getString(cursor.getColumnIndex(AREA_ENTER_SOUND)))
            area.setExit(cursor.getString(cursor.getColumnIndex(AREA_EXIT)))
            area.setExwifi(cursor.getString(cursor.getColumnIndex(AREA_EXIT_WIFI)))
            area.setExdata(cursor.getString(cursor.getColumnIndex(AREA_EXIT_DATA)))
            area.setExsound(cursor.getString(cursor.getColumnIndex(AREA_EXIT_SOUND)))
            area.setRequestId(cursor.getString(cursor.getColumnIndex(AREA_REQUESTID)))
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
        contentValues.put(AREA_ENTER,area.getEnter())
        contentValues.put(AREA_ENTER_WIFI,area.getEnwifi())
        contentValues.put(AREA_ENTER_DATA,area.getEndata())
        contentValues.put(AREA_ENTER_SOUND,area.getEnsound())
        contentValues.put(AREA_EXIT,area.getExit())
        contentValues.put(AREA_EXIT_WIFI,area.getExwifi())
        contentValues.put(AREA_EXIT_DATA,area.getExdata())
        contentValues.put(AREA_EXIT_SOUND,area.getExsound())
        contentValues.put(AREA_REQUESTID,area.getRequestId())
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

    fun deleteArea(address:String){
        val db = this.writableDatabase
        db.delete(AREALIST,"$AREA_ADDRESS = ? ",arrayOf(address))
        db.close()
    }

    fun findArea(requestId:String):AREA{
        Log.d(TAG, "findArea: $requestId")
        val db = this.readableDatabase
        val area = AREA()
        val cursor = db.rawQuery("SELECT * FROM $AREALIST WHERE $AREA_REQUESTID LIKE '$requestId'",null)
        while(cursor.moveToNext()){
            area.setName(cursor.getString(cursor.getColumnIndex(AREA_NAME)))
            area.setAddress(cursor.getString(cursor.getColumnIndex(AREA_ADDRESS)))
            area.setLatitude(cursor.getString(cursor.getColumnIndex(AREA_LATITUDE)))
            area.setLongitude(cursor.getString(cursor.getColumnIndex(AREA_LONGITUDE)))
            area.setRadius(cursor.getString(cursor.getColumnIndex(AREA_RADIUS)))
            area.setEnter(cursor.getString(cursor.getColumnIndex(AREA_ENTER)))
            area.setEnwifi(cursor.getString(cursor.getColumnIndex(AREA_ENTER_WIFI)))
            area.setEndata(cursor.getString(cursor.getColumnIndex(AREA_ENTER_DATA)))
            area.setEnsound(cursor.getString(cursor.getColumnIndex(AREA_ENTER_SOUND)))
            area.setExit(cursor.getString(cursor.getColumnIndex(AREA_EXIT)))
            area.setExwifi(cursor.getString(cursor.getColumnIndex(AREA_EXIT_WIFI)))
            area.setExdata(cursor.getString(cursor.getColumnIndex(AREA_EXIT_DATA)))
            area.setExsound(cursor.getString(cursor.getColumnIndex(AREA_EXIT_SOUND)))
            area.setRequestId(cursor.getString(cursor.getColumnIndex(AREA_REQUESTID)))
        }
        return area
    }
}