package com.hojin.imhome.map

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        val geofenceTransition = geofencingEvent.geofenceTransition
        val dbHelper = Map_DBHelper(context)
        if( geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            Toast.makeText(context,"ENTER AREA",Toast.LENGTH_SHORT).show()

        }else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            Toast.makeText(context,"EXIT AREA",Toast.LENGTH_SHORT).show()

        }
    }
}
