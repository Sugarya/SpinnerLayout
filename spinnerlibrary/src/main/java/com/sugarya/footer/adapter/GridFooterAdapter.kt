package com.sugarya.footer.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.sugarya.footer.interfaces.IFooterItem
import com.sugarya.footer.interfaces.OnItemContainerClickListener
import com.sugarya.spinnerlibrary.R

class GridFooterAdapter(private val itemHeight: Float) : RecyclerView.Adapter<GridFooterAdapter.GridViewHolder>() {

    private val mDataList: MutableList<IFooterItem> = arrayListOf()
    var mOnItemContainerClickListener: OnItemContainerClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val inflateView = View.inflate(parent.context, R.layout.item_filter_footer_grid, null)
        inflateView.findViewById<RelativeLayout>(R.id.containerItemGridFooter).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, itemHeight.toInt())
        }
        return GridViewHolder(inflateView)
    }

    override fun getItemCount(): Int = mDataList.size

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val item = mDataList[position]
        holder.onBindView(item)
        holder.setOnItemClickListener(mOnItemContainerClickListener, mDataList, position)
    }

    fun setNewData(sourceList: MutableList<out IFooterItem>){
        mDataList.clear()
        mDataList.addAll(sourceList)
        notifyDataSetChanged()
    }


    class GridViewHolder(val inflateView: View) : RecyclerView.ViewHolder(inflateView) {

        fun onBindView(item: IFooterItem) {
            val tvLabel = inflateView.findViewById<TextView>(R.id.tvItemFooterGrid)
            tvLabel.text = item.titleName
            if (item.isSelected) {
                tvLabel.setBackgroundResource(R.drawable.bg_footer_item_grid_blue)
                tvLabel.setTextColor(inflateView.context.resources.getColor(android.R.color.white))
            } else {
                tvLabel.setBackgroundResource(R.drawable.bg_footer_item_grid_gray)
                tvLabel.setTextColor(inflateView.context.resources.getColor(R.color.font_black))
            }
        }

        fun setOnItemClickListener(onFooterItemClickListener: OnItemContainerClickListener?, list: MutableList<IFooterItem>, position: Int){
            val itemContainer = inflateView.findViewById<RelativeLayout>(R.id.containerItemGridFooter)
            itemContainer.setOnClickListener {
                onFooterItemClickListener?.onItemClick(list, position)
            }
        }

    }

}

