package com.hojin.imhome.map

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.hojin.imhome.R

class GeofenceReceiver : BroadcastReceiver() {
    private val TAG = GeofenceReceiver::class.java.simpleName
    private lateinit var dbHelper : Map_DBHelper
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive: receive Event")
        dbHelper = Map_DBHelper(context)

        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        val triggeringGeofences = geofencingEvent.triggeringGeofences
        val audioManager:AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager


        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ) {
            val triggerarea = dbHelper.findArea(triggeringGeofences[0].requestId)
            Toast.makeText(context,"${triggerarea.getName()}에 Enter",Toast.LENGTH_SHORT).show()

            if(triggerarea.getEnwifi() == "true"){
                //와이파이 on
            }
            if(triggerarea.getEndata() == "true"){
                //데이터 on
            }
            when(triggerarea.getEnsound()){
                context.getString(R.string.dialog_area_sound_silence)->{
                    audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                }
                context.getString(R.string.dialog_area_sound_vibrate)->{
                    audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                }
                context.getString(R.string.dialog_area_sound_sound)->{
                    audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                }
            }

        } else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            val triggerarea = dbHelper.findArea(triggeringGeofences[0].requestId)
            Toast.makeText(context,"${triggerarea.getName()}에 Exit",Toast.LENGTH_SHORT).show()


        } else{
            Log.e(TAG, "GeofenceReceiver에 에러발생")
        }
    }
}
