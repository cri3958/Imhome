package com.hojin.imhome.map

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hojin.imhome.MainActivity
import com.hojin.imhome.R
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.dialog_add_area.view.*

class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        UIInteraction()
        setAdapter(this)
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
    fun setAdapter(context: Context){
        val dbHelper = Map_DBHelper(context)
        val adapter = ListActivityAdapter()

        adapter.setContext(context)
        adapter.listData.addAll(dbHelper.getAREALIST())

        map_list_recyclerView.adapter = adapter
        map_list_recyclerView.layoutManager = LinearLayoutManager(context)

        val simpleCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {//4 : 왼쪽으로
                dbHelper.deleteArea(viewHolder.itemView.map_dialog_address.text.toString())
                val adapter1 = ListActivityAdapter()
                adapter1.listData.addAll(dbHelper.getAREALIST())
                map_list_recyclerView.adapter = adapter1
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(map_list_recyclerView)

    }
}