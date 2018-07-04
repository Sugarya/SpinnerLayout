package com.sugarya.footer.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.sugarya.interfaces.IFooterItem
import com.sugarya.interfaces.OnItemContainerClickListener
import com.sugarya.spinnerlibrary.R

/**
 * Created by Ethan Ruan 2018/06/14
 * 垂直列表适配器
 */
class LinearFooterAdapter(private val itemHeight: Float, val recyclerView: RecyclerView) : RecyclerView.Adapter<LinearFooterAdapter.LinearViewHolder>() {

    var mDataList: MutableList<IFooterItem> = arrayListOf()

    var mOnItemContainerClickListener: OnItemContainerClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinearViewHolder {
        val inflateView = LayoutInflater.from(parent.context).inflate(R.layout.item_filter_footer_linear, null)
        inflateView.findViewById<RelativeLayout>(R.id.container_item_filter_footer_linear).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, itemHeight.toInt())
        }
        return LinearViewHolder(inflateView)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(holder: LinearViewHolder, position: Int) {
        val item: IFooterItem? = mDataList[position]
        item?.let {
            holder.onBindView(it)
            holder.setOnItemClickListener(mOnItemContainerClickListener, mDataList, position)
        }
    }

    fun setNewData(sourceList: MutableList<out IFooterItem>?){
        val list = sourceList?: arrayListOf()
        mDataList.clear()
        mDataList.addAll(list)
        notifyDataSetChanged()
    }






    class LinearViewHolder(private val inflateView: View): RecyclerView.ViewHolder(inflateView){

        fun onBindView(item: IFooterItem){
            val tvTitle = inflateView.findViewById<TextView>(R.id.tv_item_footer_linear_title)
            val ivIcon = inflateView.findViewById<ImageView>(R.id.img_item_footer_linear_icon)


            val context = tvTitle.context
            tvTitle.text = item.titleName
            if(item.isSelected){
                tvTitle.setTextColor(context.resources.getColor(R.color.font_blue))
                ivIcon.visibility = View.VISIBLE
            }else{
                tvTitle.setTextColor(context.resources.getColor(R.color.font_black_light))
                ivIcon.visibility = View.GONE
            }

        }

        fun setOnItemClickListener(onFooterItemClickLister: OnItemContainerClickListener?, list: MutableList<IFooterItem>, position: Int){
            val itemContainer = inflateView.findViewById<RelativeLayout>(R.id.container_item_filter_footer_linear)
            itemContainer.setOnClickListener {
                onFooterItemClickLister?.onItemClick(list, position)
            }
        }

    }



}