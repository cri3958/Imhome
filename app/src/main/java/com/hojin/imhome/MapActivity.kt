package com.hojin.imhome

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private val multiplePermissionsCode = 100
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    var rejectedPermissionList = ArrayList<String>()

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager:LocationManager
    private lateinit var location:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(gMap: GoogleMap) {
        mMap=gMap

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        settingPermission()
        refreshMap()
        UIIntraction()
    }

    fun settingPermission(){
        checkPermissions()
        requestPermissions()
    }

    private fun checkPermissions():Boolean {//https://m.blog.naver.com/PostView.naver?blogId=chandong83&logNo=221616557088&proxyReferer=https:%2F%2Fwww.google.com%2F
        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
        var isClear: Boolean = true//권한이 전부다 있음
        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                rejectedPermissionList.add(permission)
                isClear = false
            }
        }
        return isClear
    }

    private fun requestPermissions(){
        //거절된 퍼미션이 있다면...
        if(rejectedPermissionList.isNotEmpty()){
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), multiplePermissionsCode)
        }
    }

    fun refreshMap(){
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val mylocation = getLocation()
        val mlatitude = mylocation.split("@")[0].toDouble()
        val mlongitude = mylocation.split("@")[1].toDouble()


        val position = LatLng(mlatitude,mlongitude)

        val markerOptions = MarkerOptions()
        markerOptions.position(position)
            .title("현위치")

        mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15F))
    }

    fun getLocation() : String {
        var temp: Location? = null
        var bestLocation: Location? = null
        val providers: List<String> = locationManager.getProviders(true)

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, requiredPermissions, 100);
        }else{
            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if(isGPSEnabled) {
                for (provider in providers) {
                    temp = locationManager.getLastKnownLocation(provider)
                    if (temp == null)
                        continue
                    if (bestLocation == null || temp.accuracy < bestLocation.accuracy) {
                        bestLocation = temp;
                    }
                }
            }
            else if(isNetworkEnabled)
                temp = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
        }

        location= temp!!.latitude.toString()+"@"+temp!!.longitude.toString()
        return location
    }

    fun UIIntraction(){
        btn_add.setOnClickListener {
            //여기서 AlertDialogView로 만들어서 값 받아와서 db에 저장~
        }
    }
}