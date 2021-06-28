package com.hojin.imhome.map

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hojin.imhome.R
import kotlinx.android.synthetic.main.item_maplist.view.*

class ListActivityAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mcontext: Context? = null
    var listData = ArrayList<AREA>()

    fun setContext(context: Context){
        this.mcontext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_maplist,parent,false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return listData.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val area = listData[position]
        holder.itemView.item_name.text = area.getName()
        holder.itemView.item_latitude.text = area.getLatitude()
        holder.itemView.item_longitude.text = area.getLongitude()

    }
    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){}
}