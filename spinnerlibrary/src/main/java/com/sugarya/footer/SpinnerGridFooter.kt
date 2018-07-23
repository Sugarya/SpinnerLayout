package com.sugarya.footer

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.widget.RelativeLayout
import com.sugarya.SpinnerConfig
import com.sugarya.footer.adapter.GridFooterAdapter
import com.sugarya.footer.base.BaseSpinnerFooter
import com.sugarya.footer.interfaces.FooterMode
import com.sugarya.footer.interfaces.IFooterItem
import com.sugarya.footer.interfaces.OnFooterItemClickListener
import com.sugarya.footer.interfaces.OnFooterItemContainerClickListener
import com.sugarya.footer.model.GridFooterProperty
import com.sugarya.utils.FOOTER_MODE_SPARSE
import com.sugarya.spinnerlibrary.R

/**
 * 多列下拉列表
 * Create by Ethan Ruan 2018/07/02
 */
class SpinnerGridFooter : BaseSpinnerFooter<GridFooterProperty> {

    var mOnFooterItemClickListener: OnFooterItemClickListener? = null

    companion object {
        const val TAG: String = "SpinnerGridFooter"
    }

    private var mAdapter: GridFooterAdapter? = null

    override val mFooterViewProperty: GridFooterProperty = GridFooterProperty()

    constructor(context: Context, title: String, spanCount: Int) : this(
            context,
            title,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SpinnerConfig.DEFAULT_GRID_FOOTER_ITEM_HEIGHT_DP, context.resources.displayMetrics),
            spanCount)

    constructor(context: Context, title: String, itemHeight: Float, spanCount: Int) : super(context) {
        mFooterViewProperty.text = title
        mFooterViewProperty.gridItemHeight = itemHeight
        mFooterViewProperty.gridSpanCount = spanCount
        init()
    }


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpinnerGridFooter)
        val footerMode = FOOTER_MODE_SPARSE[typedArray.getInt(R.styleable.SpinnerGridFooter_footerModeGrid, 1)]
        mFooterViewProperty.footerMode = footerMode

        val spanCount = typedArray.getInt(R.styleable.SpinnerGridFooter_spanCountGrid, SpinnerConfig.DEFAULT_GRID_FOOTER_SPAN_COUNT)
        mFooterViewProperty.gridSpanCount = spanCount

        val itemHeight = typedArray.getDimension(R.styleable.SpinnerGridFooter_itemHeightGrid, SpinnerConfig.ORIGIN_HEIGHT.toFloat())
        mFooterViewProperty.gridItemHeight = itemHeight
        Log.d(TAG, "constructor itemHeight = $itemHeight")

        val text = typedArray.getString(R.styleable.SpinnerGridFooter_textGrid)
        mFooterViewProperty.text = text

        val textSize = typedArray.getDimension(R.styleable.SpinnerGridFooter_textSizeGrid, SpinnerConfig.DEFAULT_SPINNER_TITLE_SIZE_DP)
        mFooterViewProperty.textSize = textSize

        val textColor = typedArray.getColor(R.styleable.SpinnerGridFooter_textColorGrid, SpinnerConfig.DEFAULT_SPINNER_BACK_SURFACE_COLOR)
        mFooterViewProperty.textColor = textColor

        val textSelectedColor = typedArray.getColor(R.styleable.SpinnerGridFooter_textColorSelectedGrid, SpinnerConfig.DEFAULT_SPINNER_UNIT_TITLE_COLOR_SELECTED)
        mFooterViewProperty.textSelectedColor = textSelectedColor

        val unitIconDrawable = if (typedArray.getDrawable(R.styleable.SpinnerGridFooter_iconGrid) != null) {
            typedArray.getDrawable(R.styleable.SpinnerLayout_icon)
        } else {
            resources.getDrawable(R.drawable.footer_triangle_down_black)
        }
        mFooterViewProperty.unitIcon = unitIconDrawable

        val unitIconSelectedDrawable = if (typedArray.getDrawable(R.styleable.SpinnerGridFooter_iconSelectedGrid) != null) {
            typedArray.getDrawable(R.styleable.SpinnerLayout_iconSelected)
        } else {
            resources.getDrawable(R.drawable.footer_triangle_up_blue)
        }
        mFooterViewProperty.unitIconSelected = unitIconSelectedDrawable

        val backSurfaceAvailable = typedArray.getBoolean(R.styleable.SpinnerGridFooter_backSurfaceAvailableGrid, SpinnerConfig.DEFAULT_BACK_SURFACE_AVAILABLE)
        mFooterViewProperty.backSurfaceAvailable = backSurfaceAvailable

        val isTouchOutsideCanceled = typedArray.getBoolean(R.styleable.SpinnerGridFooter_touchOutsideCanceledGrid, SpinnerConfig.DEFAULT_TOUCH_OUTSIDE_CANCELED)
        mFooterViewProperty.isTouchOutsideCanceled = isTouchOutsideCanceled


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

        recyclerView.layoutManager = GridLayoutManager(context, mFooterViewProperty.gridSpanCount, GridLayoutManager.VERTICAL, false)
        removeAllViews()
        addView(recyclerView)

        mAdapter = GridFooterAdapter(mFooterViewProperty.gridItemHeight)
        recyclerView.adapter = mAdapter

        mAdapter?.mOnFooterItemContainerClickListener = object : OnFooterItemContainerClickListener {
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
        val rowCount = size / mFooterViewProperty.gridSpanCount + 1
        val needHeight = rowCount * mFooterViewProperty.gridItemHeight
        Log.d(TAG, "needHeight = ${needHeight}")
        setupFooterHeight(needHeight.toInt())

        mAdapter?.setNewData(dataList)
    }

    fun setOnFooterItemClickListener(onFooterItemClickListener: OnFooterItemClickListener){
        this.mOnFooterItemClickListener = onFooterItemClickListener
    }
}