package com.sugarya.footer

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.sugarya.footer.adapter.GridFooterAdapter
import com.sugarya.footer.base.BaseSpinnerFooter
import com.sugarya.interfaces.FooterMode
import com.sugarya.interfaces.IFooterItem
import com.sugarya.interfaces.OnFooterItemClickListener
import com.sugarya.interfaces.OnItemContainerClickListener
import com.sugarya.utils.FOOTER_MODE_SPARSE
import com.sugarya.spinnerlibrary.R

/**
 * 多列下拉列表
 * Create by Ethan Ruan 2018/07/02
 */
class SpinnerGridFooter : BaseSpinnerFooter {

    var mOnFooterItemClickListener: OnFooterItemClickListener? = null

    companion object {
        const val TAG: String = "SpinnerGridFooter"
        private const val DEFAULT_SPAN_COUNT = 4
    }

    override var mFooterMode: FooterMode? = null
    private var mSpanCount = DEFAULT_SPAN_COUNT
    private var mItemHeight: Float = 0f
    private var mAdapter: GridFooterAdapter? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpinnerGridFooter)
        mFooterMode = FOOTER_MODE_SPARSE[typedArray.getInt(R.styleable.SpinnerGridFooter_gridFooterMode, -1)]
        mSpanCount = typedArray.getInt(R.styleable.SpinnerGridFooter_gridSpanCount, DEFAULT_SPAN_COUNT)
        mItemHeight = typedArray.getDimension(R.styleable.SpinnerGridFooter_gridItemHeight, 0f)
        typedArray.recycle()

        init()
    }

    private fun init() {
        if (background == null) {
            setBackgroundColor(resources.getColor(android.R.color.white))
        }

        val recyclerView = RecyclerView(context)
        recyclerView.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                .apply {
                    addRule(RelativeLayout.CENTER_VERTICAL)
                }

        recyclerView.layoutManager = GridLayoutManager(context, mSpanCount, GridLayoutManager.VERTICAL, false)
        removeAllViews()
        addView(recyclerView)

        mAdapter = GridFooterAdapter(mItemHeight)
        recyclerView.adapter = mAdapter

        mAdapter?.mOnItemContainerClickListener = object : OnItemContainerClickListener {
            override fun onItemClick(list: MutableList<IFooterItem>, position: Int) {
                for (footerItem in list) {
                    footerItem.isSelected = false
                }

                val currentFooterItem = list[position]
                currentFooterItem.isSelected = true
                mAdapter?.notifyDataSetChanged()

                mOnFooterItemClickListener?.onClick(currentFooterItem)
            }
        }


    }

    /**
     * 设置数据源
     */
    fun setNewData(dataList: MutableList<out IFooterItem>) {
        val size = dataList.size
        val rowCount = size / mSpanCount + 1
        val needHeight = rowCount * mItemHeight
        setupFooterHeight(needHeight.toInt())

        mAdapter?.setNewData(dataList)
    }

    fun setOnFooterItemClickListener(onFooterItemClickListener: OnFooterItemClickListener){
        this.mOnFooterItemClickListener = onFooterItemClickListener
    }
}