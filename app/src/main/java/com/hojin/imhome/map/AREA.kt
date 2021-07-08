package com.hojin.imhome.map

class AREA {
    private lateinit var AREA_Name:String
    private lateinit var AREA_Address:String
    private lateinit var AREA_Latitude:String
    private lateinit var AREA_Longitude:String
    private lateinit var AREA_Radius:String
    private lateinit var AREA_Enter:String
    private lateinit var AREA_Enter_wifi:String
    private lateinit var AREA_Enter_data:String
    private lateinit var AREA_Enter_sound:String
    private lateinit var AREA_Exit:String
    private lateinit var AREA_Exit_wifi:String
    private lateinit var AREA_Exit_data:String
    private lateinit var AREA_Exit_sound:String
    
    

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

    fun getEnter():String{return AREA_Enter}
    fun setEnter(text:String){this.AREA_Enter = text}

    fun getEnwifi():String{return AREA_Enter_wifi}
    fun setEnwifi(text:String){this.AREA_Enter_wifi = text}

    fun getEndata():String{return AREA_Enter_data}
    fun setEndata(text:String){this.AREA_Enter_data = text}

    fun getEnsound():String{return AREA_Enter_sound}
    fun setEnsound(text:String){this.AREA_Enter_sound = text}

    fun getExit():String{return AREA_Exit}
    fun setExit(text:String){this.AREA_Exit = text}

    fun getExwifi():String{return AREA_Exit_wifi}
    fun setExwifi(text:String){this.AREA_Exit_wifi = text}

    fun getExdata():String{return AREA_Exit_data}
    fun setExdata(text:String){this.AREA_Exit_data = text}

    fun getExsound():String{return AREA_Exit_sound}
    fun setExsound(text:String){this.AREA_Exit_sound = text}
}