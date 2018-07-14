package com.sugarya

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sugarya.spinnerlayout.R

class RecyclerViewAdapter: RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>(){

    val dataList = arrayListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = LayoutInflater.from(parent.context).inflate(R.layout.item_animation_test, parent, false)
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBindView(dataList[position])
    }




    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun onBindView(s: String){
            val tvTitle = itemView.findViewById<TextView>(R.id.tvItemTitle)
            tvTitle.text = s
        }
    }

    fun setNewData(list: MutableList<String>){
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }
}