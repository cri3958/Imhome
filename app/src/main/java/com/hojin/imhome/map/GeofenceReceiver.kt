package com.hojin.imhome.map

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver : BroadcastReceiver() {
    private val TAG = GeofenceReceiver::class.java.simpleName
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        val triggeringGeofences = geofencingEvent.triggeringGeofences

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ) {
            Toast.makeText(context, "Enter Geofence : ${triggeringGeofences[0].requestId}", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "onReceive Enter : ${triggeringGeofences[0].requestId}")
        } else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            Toast.makeText(context, "Exit Geofence : ${triggeringGeofences[0].requestId}", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "onReceive Exit : ${triggeringGeofences[0].requestId}")
        } else{
            Log.e(TAG, "GeofenceReceiver에 에러발생")
        }
    }
}
