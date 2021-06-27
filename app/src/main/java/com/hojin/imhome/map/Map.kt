package com.hojin.imhome.map

class Map {
    private lateinit var Map_Name:String
    private lateinit var Map_Address:String
    private lateinit var Map_Latitude:String
    private lateinit var Map_Longitude:String
    private lateinit var Map_Radius:String

    fun Map(Name:String,Address:String,Latitude:String,Longitude:String,Radius:String){
        this.Map_Name = Name
        this.Map_Address = Address
        this.Map_Latitude = Latitude
        this.Map_Longitude = Longitude
        this.Map_Radius = Radius
    }

    fun getName(): String { return Map_Name}
    fun setName(text:String){this.Map_Name=text}

    fun getAddress(): String { return Map_Address}
    fun setAddress(text:String){this.Map_Address=text}

    fun getLatitude():String{return Map_Latitude}
    fun setLatitude(text:String){this.Map_Latitude = text}

    fun getLongitude():String{return Map_Longitude}
    fun setLongitude(text:String){this.Map_Longitude = text}

    fun getRadius():String{return Map_Radius}
    fun setRadius(text:String){this.Map_Radius = text}
}