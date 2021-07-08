package com.hojin.imhome.map

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
        holder.itemView.item_latitude.text = "%.4f".format(area.getLatitude().toDouble())
        holder.itemView.item_longitude.text = "%.4f".format(area.getLongitude().toDouble())
        
        if(area.getEnter()=="true")
            holder.itemView.item_enter.visibility = View.VISIBLE
        else
            holder.itemView.item_enter.visibility = View.GONE
        if(area.getEnwifi()=="true")
            holder.itemView.item_enter_function_wifi.setTextColor(ContextCompat.getColor(mcontext!!, R.color.green))
        else
            holder.itemView.item_enter_function_wifi.setTextColor(ContextCompat.getColor(mcontext!!, R.color.red))

        if(area.getEndata()=="true")
            holder.itemView.item_enter_function_data.setTextColor(ContextCompat.getColor(mcontext!!, R.color.green))
        else
            holder.itemView.item_enter_function_data.setTextColor(ContextCompat.getColor(mcontext!!, R.color.red))

        if(area.getEnsound()!="false"){
            holder.itemView.item_enter_function_sound.setTextColor(ContextCompat.getColor(mcontext!!, R.color.green))
            holder.itemView.item_enter_function_sound.text = area.getEnsound()
        }

        if(area.getExit()=="true")
            holder.itemView.item_exit.visibility = View.VISIBLE
        else
            holder.itemView.item_exit.visibility = View.GONE
        if(area.getExwifi()=="true")
            holder.itemView.item_exit_function_wifi.setTextColor(ContextCompat.getColor(mcontext!!, R.color.green))
        else
            holder.itemView.item_exit_function_wifi.setTextColor(ContextCompat.getColor(mcontext!!, R.color.red))

        if(area.getExdata()=="true")
            holder.itemView.item_exit_function_data.setTextColor(ContextCompat.getColor(mcontext!!, R.color.green))
        else
            holder.itemView.item_exit_function_data.setTextColor(ContextCompat.getColor(mcontext!!, R.color.red))

        if(area.getExsound()!="false"){
            holder.itemView.item_exit_function_sound.setTextColor(ContextCompat.getColor(mcontext!!, R.color.green))
            holder.itemView.item_exit_function_sound.text = area.getExsound()
        }


    }
    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){}
}