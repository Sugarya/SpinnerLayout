package com.sugarya.footer

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import com.sugarya.footer.adapter.LinearFooterAdapter
import com.sugarya.footer.base.BaseSpinnerFooter
import com.sugarya.footer.interfaces.FooterMode
import com.sugarya.footer.interfaces.IFooterItem
import com.sugarya.footer.interfaces.OnFooterItemClickListener
import com.sugarya.footer.interfaces.OnItemContainerClickListener
import com.sugarya.utils.FOOTER_MODE_SPARSE
import com.sugarya.spinnerlibrary.R


/**
 * Created by Ethan Ruan 2018/06/14
 * FilterLayout 的下拉视图  垂直列表
 * 支持FooterView优先控制下拉动画的模式
 */
class SpinnerLinearFooter : BaseSpinnerFooter {

    private val FOOTER_ITEM_HEIGHT: Float = 50f

    var mOnFooterItemClickListener: OnFooterItemClickListener? = null

    override var mFooterMode: FooterMode? = null
    private var mAdapter: LinearFooterAdapter? = null
    private var mItemHeight = FOOTER_ITEM_HEIGHT

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SpinnerLinearFooter)
        mFooterMode = FOOTER_MODE_SPARSE[typedArray.getInt(R.styleable.SpinnerLinearFooter_linearFooterMode, -1)]
        mItemHeight = typedArray.getDimension(R.styleable.SpinnerLinearFooter_linearItemHeight, FOOTER_ITEM_HEIGHT)
        typedArray.recycle()

        init(context)
    }


    private fun init(context: Context) {
        if (background == null) {
            setBackgroundColor(resources.getColor(android.R.color.white))
        }

        val recyclerView = RecyclerView(context)
        recyclerView.layoutParams = RecyclerView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        removeAllViews()
        addView(recyclerView)

        mAdapter = LinearFooterAdapter(mItemHeight, recyclerView)
        recyclerView.adapter = mAdapter
        mAdapter?.mOnItemContainerClickListener = object : OnItemContainerClickListener {
            override fun onItemClick(list: MutableList<IFooterItem>, position: Int) {
                for(item in list){
                    item.isSelected = false
                }
                val footerItem = list[position]
                footerItem.isSelected = true
                mAdapter?.notifyDataSetChanged()

                mOnFooterItemClickListener?.onClick(list[position])
            }
        }

    }


    fun setNewData(sourceList: MutableList<out IFooterItem>) {
        val size = sourceList.size
        val needHeight = mItemHeight * size
        setupFooterHeight(needHeight.toInt())

//        val filterContainerLayout = (filterLayout.getChildAt(0) as ViewGroup)
//        val filterBarLayout = filterContainerLayout.getChildAt(0)
        //bringToFront方法并不会真实改变视图在父容器的位置，只是展示上看起来像
//        filterBarLayout.bringToFront()
        mAdapter?.setNewData(sourceList)
    }

    fun setOnFooterItemClickListener(onFooterItemClickListener: OnFooterItemClickListener){
        this.mOnFooterItemClickListener = onFooterItemClickListener
    }

}