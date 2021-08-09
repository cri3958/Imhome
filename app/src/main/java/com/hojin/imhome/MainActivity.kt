package com.hojin.imhome

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.hojin.imhome.map.ManifestActivity
import com.hojin.imhome.map.MapActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_map.setOnClickListener {
            startActivity(Intent(this, ManifestActivity::class.java))
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (!notificationManager.isNotificationPolicyAccessGranted) {
            Toast.makeText(this, "권한을 허용해주세요", Toast.LENGTH_LONG).show();
            startActivity(Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
        }
    }
}