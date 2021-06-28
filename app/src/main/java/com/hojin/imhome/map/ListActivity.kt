package com.hojin.imhome.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hojin.imhome.MainActivity
import com.hojin.imhome.R
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        UIInteraction()
        setAdapter(context)
    }
    fun UIInteraction(){
        map_btn_back_list.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        map_btn_view_map.setOnClickListener {
            val intent = Intent(this,MapActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    fun setAdapter(context){
        val dbHelper = Map_DBHelper(context)

    }
}