package com.hojin.imhome.map

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.hojin.imhome.MainActivity
import com.hojin.imhome.R
import com.hojin.imhome.util.util
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.dialog_add_area.*
import kotlinx.android.synthetic.main.dialog_add_area.view.*
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private val multiplePermissionsCode = 100
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )
    private var rejectedPermissionList = ArrayList<String>()

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager:LocationManager
    private lateinit var location:String

    private lateinit var mname:String
    private var mlatitude:Double = 0.0
    private var mlongitude:Double = 0.0

    private val util:util = util()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        settingPermission()

        val geofencePendingIntent: PendingIntent by lazy {
            val intent = Intent(this, GeofenceBroadcastReceiver::class.java)

            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

    }

    override fun onMapReady(gMap: GoogleMap) {
        mMap=gMap

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.setOnMarkerClickListener(this)
        if(settingPermission()){
            refreshMap()
            UIIntraction()
            drawAreas()
        }
    }

    private fun settingPermission():Boolean{
        return if(checkPermissions())
            true
        else {
            if (requestPermissions())
                true
            else
                settingPermission()
        }
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

    private fun requestPermissions():Boolean{
        //거절된 퍼미션이 있다면...
        if(rejectedPermissionList.isNotEmpty()){
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), multiplePermissionsCode)
        }
        return checkPermissions()
    }

    private fun refreshMap(){
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val mylocation = getLocation()

        mname = mylocation.split("@")[0]
        mlatitude = mylocation.split("@")[1].toDouble()
        mlongitude = mylocation.split("@")[2].toDouble()

        val position = LatLng(mlatitude,mlongitude)

        val markerOptions = MarkerOptions()
        markerOptions.position(position)

        mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15F))
    }

    private fun getLocation() : String {
        var temp: Location? = null
        var bestLocation: Location? = null
        val providers: List<String> = locationManager.getProviders(true)

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, requiredPermissions, 100)
        }else{
            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if(isGPSEnabled) {
                for (provider in providers) {
                    temp = locationManager.getLastKnownLocation(provider)
                    if (temp == null)
                        continue
                    if (bestLocation == null || temp.accuracy < bestLocation.accuracy) {
                        bestLocation = temp
                    }
                    temp = bestLocation
                }
            }
            else if(isNetworkEnabled)
                temp = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
            val latitude = temp!!.latitude
            val longitude = temp.longitude
            val address = Geocoder(this, Locale.getDefault()).getFromLocation(latitude,longitude,1).toString().split("\u0022")[1]
            Log.d("@@@@",address)

            location= "${address}@${latitude}@${longitude}"
        }
        return location
    }

    private fun UIIntraction(){
        map_btn_refresh.setOnClickListener { refreshMap() }
        map_search_btn.setOnClickListener {//검색버튼
            val geocoder = Geocoder(this)
            val addressname = map_search_text.text.toString()
            if(addressname.isEmpty()){
                Toast.makeText(applicationContext,"장소를 입력한 후 검색을 눌러주세요!",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val addressList = geocoder.getFromLocationName(addressname,5)
            if(addressList.isEmpty()){
                Toast.makeText(applicationContext,"장소를 다시 입력한 후 검색을 눌러주세요!",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val temp = addressList[0].toString().split(",")
            mname = temp[0].split("\u0022")[1]
            mlatitude = temp[10].split("=")[1].toDouble()
            mlongitude = temp[12].split("=")[1].toDouble()

            val position = LatLng(mlatitude,mlongitude)
            val markerOptions = MarkerOptions()

            markerOptions.position(position)

            mMap.addMarker(markerOptions)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15F))

            util.keyboard_down(applicationContext,map_search_text)
            map_search_text.text=null
        }
        map_btn_back_map.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        map_btn_view_list.setOnClickListener {
            val intent = Intent(this,ListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val dbHelper = Map_DBHelper(this)
        if(dbHelper.checkArea(mlatitude,mlongitude))    // 이미 저장된 좌표이면 dialog발생 안하게하기
            return true


        val dialogview: View = layoutInflater.inflate(R.layout.dialog_add_area,null)

        dialogview.map_dialog_address.setText(mname)
        dialogview.map_dialog_latitude.setText(mlatitude.toString())
        dialogview.map_dialog_longitude.setText(mlongitude.toString())
        dialogview.map_dialog_switch_enter.setOnCheckedChangeListener { _, isCheckeed ->
            if(isCheckeed)
                dialogview.map_dialog_switch_enter_layout.visibility = View.VISIBLE
            else
                dialogview.map_dialog_switch_enter_layout.visibility = View.GONE
        }
        dialogview.map_dialog_switch_exit.setOnCheckedChangeListener { _, isCheckeed ->
            if(isCheckeed)
                dialogview.map_dialog_switch_exit_layout.visibility = View.VISIBLE
            else
                dialogview.map_dialog_switch_exit_layout.visibility = View.GONE
        }


        val builder = AlertDialog.Builder(this)
        builder.setTitle("새로운 지역을 만드시겠습니까?")
            .setView(dialogview)
            .setPositiveButton("네",null)
            .setNegativeButton("아니오",null)
            .setCancelable(false)

        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if(dialogview.map_dialog_name.text.isNullOrEmpty() ||dialogview.map_dialog_address.text.isNullOrEmpty() ||dialogview.map_dialog_latitude.text.isNullOrEmpty() ||dialogview.map_dialog_longitude.text.isNullOrEmpty()){
                Toast.makeText(applicationContext,"내용을 전부 입력해주세요.",Toast.LENGTH_SHORT).show()
            }else if(dialogview.map_dialog_longitude.text.toString().toDouble()>180||dialogview.map_dialog_longitude.text.toString().toDouble()<-180){
                Toast.makeText(applicationContext,"경도에는 -180 ~ 180의 값만 입력할 수 있습니다.",Toast.LENGTH_SHORT).show()
            } else if(dialogview.map_dialog_latitude.text.toString().toDouble()>90||dialogview.map_dialog_latitude.text.toString().toDouble()<-90){
                Toast.makeText(applicationContext,"위도에는 -90 ~ 90의 값만 입력할 수 있습니다.",Toast.LENGTH_SHORT).show()
            } else if(dialogview.map_dialog_radius.text.toString().toInt()>5000||dialogview.map_dialog_radius.text.toString().toDouble()<0){
                Toast.makeText(applicationContext,"반경에는 0 ~ 5000의 값만 입력할 수 있습니다.",Toast.LENGTH_SHORT).show()
            } else{
                val area: AREA = AREA()
                area.setName(dialogview.map_dialog_name.text.toString())
                area.setAddress(dialogview.map_dialog_address.text.toString())
                area.setLatitude(dialogview.map_dialog_latitude.text.toString())
                area.setLongitude(dialogview.map_dialog_longitude.text.toString())
                area.setRadius(dialogview.map_dialog_radius.text.toString())

                area.setEnter(dialogview.map_dialog_switch_enter.isChecked.toString())
                area.setEnwifi(dialogview.map_dialog_switch_enter_wifi.isChecked.toString())
                area.setEndata(dialogview.map_dialog_switch_enter_data.isChecked.toString())
                if(dialogview.map_dialog_switch_enter.isChecked) {
                    area.setEnsound(dialogview.map_dialog_radio_enter_silence.text.toString())
                    dialogview.map_dialog_radio_enter_group.setOnCheckedChangeListener { radioGroup, i ->
                        when (i) {
                            R.id.map_dialog_radio_enter_silence -> {
                                area.setEnsound(dialogview.map_dialog_radio_enter_silence.text.toString())
                            }
                            R.id.map_dialog_radio_enter_vibrate -> {
                                area.setEnsound(dialogview.map_dialog_radio_enter_vibrate.text.toString())
                            }
                            R.id.map_dialog_radio_enter_sound -> {
                                area.setEnsound(dialogview.map_dialog_radio_enter_sound.text.toString())
                            }
                        }
                    }
                }else {
                    area.setEnsound("false")
                }

                area.setExit(dialogview.map_dialog_switch_exit.isChecked.toString())
                area.setExwifi(dialogview.map_dialog_switch_exit_wifi.isChecked.toString())
                area.setExdata(dialogview.map_dialog_switch_exit_data.isChecked.toString())
                if(dialogview.map_dialog_switch_exit.isChecked) {
                    area.setExsound(dialogview.map_dialog_radio_exit_silence.text.toString())
                    dialogview.map_dialog_radio_exit_group.setOnCheckedChangeListener { radioGroup, i ->
                        when (i) {
                            R.id.map_dialog_radio_exit_silence -> {
                                area.setExsound(dialogview.map_dialog_radio_exit_silence.text.toString())
                            }
                            R.id.map_dialog_radio_exit_vibrate -> {
                                area.setExsound(dialogview.map_dialog_radio_exit_vibrate.text.toString())
                            }
                            R.id.map_dialog_radio_exit_sound -> {
                                area.setExsound(dialogview.map_dialog_radio_exit_sound.text.toString())
                            }
                        }
                    }
                }else{
                    area.setExsound("false")
                }

                dbHelper.insertAREALIST(area)

                drawAreas()

                Toast.makeText(applicationContext,"새로운 지역이 추가되었습니다.",Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        return true
    }

    private fun drawAreas(){
        val dbHelper = Map_DBHelper(this)
        val locations = dbHelper.getAREALIST()
        var area:AREA
        for(i in 0 until locations.size){
            area = locations[i]
            val circleOptions = CircleOptions()
                .center(LatLng(area.getLatitude().toDouble(),area.getLongitude().toDouble()))
                .clickable(false)
                .radius(area.getRadius().toDouble())
                .fillColor(Color.argb(100, 200, 200, 200))
                .strokeColor(Color.RED)
                .strokeWidth(2F)
            val markerOptions = MarkerOptions()
                .title(area.getName())
                .draggable(false)
                .position(LatLng(area.getLatitude().toDouble(),area.getLongitude().toDouble()))
                .snippet(area.getAddress())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

            mMap.addMarker(markerOptions)
            mMap.addCircle(circleOptions)
        }
    }
}

