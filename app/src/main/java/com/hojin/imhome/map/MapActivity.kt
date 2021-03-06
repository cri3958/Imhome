package com.hojin.imhome.map

import android.Manifest
import android.app.NotificationManager
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
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
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
    private val TAG = MapActivity::class.java.simpleName

    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    private val dbHelper = Map_DBHelper(this)
    private val util:util = util()

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager:LocationManager
    private lateinit var location:String

    private lateinit var mname:String
    private var mlatitude:Double = 0.0
    private var mlongitude:Double = 0.0


    lateinit var geofencingClient : GeofencingClient
    private val geofenceList = ArrayList<Geofence>()

    val REQUEST_ID_EXTRA = "geofenceRequestId"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    override fun onMapReady(gMap: GoogleMap) {

            mMap = gMap

            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            mMap.setOnMarkerClickListener(this)

            refreshMap()
            UIIntraction()
            drawAreas()

    }

    fun geofencing(area: AREA){
        Log.d(TAG, "geofencing: ENTER!")
        geofencingClient = LocationServices.getGeofencingClient(this)

        val idCount = getPreferences(MODE_PRIVATE).getInt(REQUEST_ID_EXTRA,0)


        geofenceList.add(Geofence.Builder()
                //37.5548414,126.8573177 ????????? ??????
            .setCircularRegion(area.getLatitude().toDouble(),area.getLongitude().toDouble(), area.getRadius().toFloat())
            .setRequestId(REQUEST_ID_EXTRA+idCount)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()
        )
        area.setRequestId(REQUEST_ID_EXTRA+idCount)
        Log.d(TAG, "geofencing REQUEST_ID: ${REQUEST_ID_EXTRA+idCount}")

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent).run {
                addOnSuccessListener {
                    Toast.makeText(applicationContext, "????????? ?????? ??????", Toast.LENGTH_SHORT).show()
                }
                addOnFailureListener {
                    Toast.makeText(applicationContext, "????????? ?????? ?????? ??????", Toast.LENGTH_SHORT).show()
                }
            }

            geofencingClient.removeGeofences(geofencePendingIntent).run {
                addOnSuccessListener {
                    Toast.makeText(applicationContext, "?????? ??????", Toast.LENGTH_SHORT).show()
                }
                addOnFailureListener {
                    Toast.makeText(applicationContext, "?????? ?????? ??????", Toast.LENGTH_SHORT).show()
                }
            }

            return
        }


        val request = GeofencingRequest.Builder()
            .addGeofences(geofenceList)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()


        dbHelper.insertAREALIST(area)
        geofencingClient.addGeofences(request, getPendingIntent())//geofence ????????????
        return

    }

    private val geofencePendingIntent: PendingIntent by lazy {
        Log.d(TAG, "geofencePendingIntent : Add GeofenceReceiver")
        val intent = Intent(this, GeofenceReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()
    }

    fun getPendingIntent():PendingIntent{
        val intent = Intent(this,GeofenceReceiver::class.java)
        return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
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

        mMap.clear()
        mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15F))
        drawAreas()
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
            val address = Geocoder(this, Locale.getDefault()).getFromLocation(latitude,longitude,10).toString().split("\u0022")[1]
            Log.d("@@@@",address)

            location= "${address}@${latitude}@${longitude}"
        }
        return location
    }

    private fun UIIntraction(){
        map_btn_refresh.setOnClickListener { refreshMap() }
        map_search_btn.setOnClickListener {//????????????
            val geocoder = Geocoder(this)
            val addressname = map_search_text.text.toString()
            if(addressname.isEmpty()){
                Toast.makeText(applicationContext,"????????? ????????? ??? ????????? ???????????????!",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val addressList = geocoder.getFromLocationName(addressname,5)
            if(addressList.isEmpty()){
                Toast.makeText(applicationContext,"????????? ?????? ????????? ??? ????????? ???????????????!",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val temp = addressList[0].toString().split(",")
            mname = temp[0].split("\u0022")[1]
            mlatitude = temp[10].split("=")[1].toDouble()
            mlongitude = temp[12].split("=")[1].toDouble()

            val position = LatLng(mlatitude,mlongitude)
            val markerOptions = MarkerOptions()

            markerOptions.position(position)

            mMap.clear()
            mMap.addMarker(markerOptions)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15F))
            drawAreas()

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
        if(dbHelper.checkArea(mlatitude,mlongitude))    // ?????? ????????? ???????????? dialog?????? ???????????????
            return true

        val area: AREA = AREA()

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

        area.setEnsound(dialogview.map_dialog_radio_enter_silence.text.toString())
        dialogview.map_dialog_radio_enter_group.setOnCheckedChangeListener {  radioGroup, i ->
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


        val builder = AlertDialog.Builder(this)
        builder.setTitle("????????? ????????? ??????????????????????")
            .setView(dialogview)
            .setPositiveButton("???",null)
            .setNegativeButton("?????????",null)
            .setCancelable(false)

        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if(dialogview.map_dialog_name.text.isNullOrEmpty() ||dialogview.map_dialog_address.text.isNullOrEmpty() ||dialogview.map_dialog_latitude.text.isNullOrEmpty() ||dialogview.map_dialog_longitude.text.isNullOrEmpty()){
                Toast.makeText(applicationContext,"????????? ?????? ??????????????????.",Toast.LENGTH_SHORT).show()
            }else if(dialogview.map_dialog_longitude.text.toString().toDouble()>180||dialogview.map_dialog_longitude.text.toString().toDouble()<-180){
                Toast.makeText(applicationContext,"???????????? -180 ~ 180??? ?????? ????????? ??? ????????????.",Toast.LENGTH_SHORT).show()
            } else if(dialogview.map_dialog_latitude.text.toString().toDouble()>90||dialogview.map_dialog_latitude.text.toString().toDouble()<-90){
                Toast.makeText(applicationContext,"???????????? -90 ~ 90??? ?????? ????????? ??? ????????????.",Toast.LENGTH_SHORT).show()
            } else if(dialogview.map_dialog_radius.text.toString().toInt()>5000||dialogview.map_dialog_radius.text.toString().toDouble()<0){
                Toast.makeText(applicationContext,"???????????? 0 ~ 5000??? ?????? ????????? ??? ????????????.",Toast.LENGTH_SHORT).show()
            } else{
                area.setName(dialogview.map_dialog_name.text.toString())
                area.setAddress(dialogview.map_dialog_address.text.toString())
                area.setLatitude(dialogview.map_dialog_latitude.text.toString())
                area.setLongitude(dialogview.map_dialog_longitude.text.toString())
                area.setRadius(dialogview.map_dialog_radius.text.toString())

                area.setEnter(dialogview.map_dialog_switch_enter.isChecked.toString())
                area.setEnwifi(dialogview.map_dialog_switch_enter_wifi.isChecked.toString())
                area.setEndata(dialogview.map_dialog_switch_enter_data.isChecked.toString())

                if(!dialogview.map_dialog_switch_enter.isChecked) {
                    area.setEnsound("false")
                }

                area.setExit(dialogview.map_dialog_switch_exit.isChecked.toString())
                area.setExwifi(dialogview.map_dialog_switch_exit_wifi.isChecked.toString())
                area.setExdata(dialogview.map_dialog_switch_exit_data.isChecked.toString())
                if(!dialogview.map_dialog_switch_exit.isChecked) {
                    area.setExsound("false")
                }

                Log.d(TAG, "onMarkerClick: Complete")
                geofencing(area)
                drawAreas()

                //Toast.makeText(applicationContext,"????????? ????????? ?????????????????????.",Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        return true
    }

    private fun drawAreas(){
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

