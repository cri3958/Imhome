package com.hojin.imhome.map

class AREA {
    private lateinit var AREA_Name:String
    private lateinit var AREA_Address:String
    private lateinit var AREA_Latitude:String
    private lateinit var AREA_Longitude:String
    private lateinit var AREA_Radius:String

    fun AREA(Name:String,Address:String,Latitude:String,Longitude:String,Radius:String){
        this.AREA_Name = Name
        this.AREA_Address = Address
        this.AREA_Latitude = Latitude
        this.AREA_Longitude = Longitude
        this.AREA_Radius = Radius
    }

    fun getName(): String { return AREA_Name}
    fun setName(text:String){this.AREA_Name=text}

    fun getAddress(): String { return AREA_Address}
    fun setAddress(text:String){this.AREA_Address=text}

    fun getLatitude():String{return AREA_Latitude}
    fun setLatitude(text:String){this.AREA_Latitude = text}

    fun getLongitude():String{return AREA_Longitude}
    fun setLongitude(text:String){this.AREA_Longitude = text}

    fun getRadius():String{return AREA_Radius}
    fun setRadius(text:String){this.AREA_Radius = text}
}