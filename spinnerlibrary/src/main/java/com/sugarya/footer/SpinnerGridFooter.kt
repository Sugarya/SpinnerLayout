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

    override val baseFooterViewProperty: GridFooterProperty

    constructor(context: Context, title: String) : this(
            context,
            title,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SpinnerConfig.DEFAULT_GRID_FOOTER_ITEM_HEIGHT_DP, context.resources.displayMetrics),
            SpinnerConfig.DEFAULT_GRID_FOOTER_SPAN_COUNT)

    constructor(context: Context, title: String, spanCount: Int) : this(
            context,
            title,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SpinnerConfig.DEFAULT_GRID_FOOTER_ITEM_HEIGHT_DP, context.resources.displayMetrics),
            spanCount)

    constructor(context: Context, title: String, itemHeight: Float, spanCount: Int) : super(context) {
        baseFooterViewProperty = GridFooterProperty(
                itemHeight,
                spanCount,
                title,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        )
        init()
    }


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpinnerGridFooter)

        val ordinalFooterMode = typedArray.getInt(R.styleable.SpinnerGridFooter_footerModeGrid, -1)
        val footerMode: FooterMode? = if(ordinalFooterMode == -1){
            null
        }else{
            FOOTER_MODE_SPARSE[ordinalFooterMode]
        }

        val spanCountValue = typedArray.getInt(R.styleable.SpinnerGridFooter_spanCountGrid, -1)
        val spanCount: Int = if(spanCountValue == -1){
            SpinnerConfig.DEFAULT_GRID_FOOTER_SPAN_COUNT
        }else{
            spanCountValue
        }

        val itemHeightValue = typedArray.getDimension(R.styleable.SpinnerGridFooter_itemHeightGrid, -1f)
        val itemHeight: Float = if(itemHeightValue == -1f){
            throw IllegalArgumentException("${SpinnerGridFooter::class.java.simpleName} need a item height")
        }else{
            itemHeightValue
        }

        val text = typedArray.getString(R.styleable.SpinnerGridFooter_textGrid) ?: ""

        val textSizeValue = typedArray.getDimension(R.styleable.SpinnerGridFooter_textSizeGrid, -1f)
        val textSize: Float? = if(textSizeValue == -1f){
            null
        }else{
            textSizeValue
        }

        val textColorValue = typedArray.getColor(R.styleable.SpinnerGridFooter_textColorGrid, -1)
        val textColor: Int? = if(textColorValue == -1){
            null
        }else{
            textColorValue
        }

        val textSelectedColorValue =  typedArray.getColor(R.styleable.SpinnerGridFooter_textColorSelectedGrid, -1)
        val textSelectedColor: Int? = if(textSelectedColorValue == -1){
            null
        }else{
            textSelectedColorValue
        }

        val unitIconDrawable = typedArray.getDrawable(R.styleable.SpinnerGridFooter_iconGrid)
        val unitIconSelectedDrawable = typedArray.getDrawable(R.styleable.SpinnerGridFooter_iconSelectedGrid)


        val testAvailableValue1 = typedArray.getBoolean(R.styleable.SpinnerGridFooter_backSurfaceAvailableGrid, false)
        val testAvailableValue2 = typedArray.getBoolean(R.styleable.SpinnerGridFooter_backSurfaceAvailableGrid, true)
        val backSurfaceAvailable: Boolean? = if(testAvailableValue1 == testAvailableValue2){
            testAvailableValue1
        }else{
            null
        }

        val testTouchOutsideCanceledValue1 = typedArray.getBoolean(R.styleable.SpinnerGridFooter_touchOutsideCanceledGrid, false)
        val testTouchOutsideCanceledValue2 = typedArray.getBoolean(R.styleable.SpinnerGridFooter_touchOutsideCanceledGrid, true)
        val isTouchOutsideCanceled: Boolean? = if(testTouchOutsideCanceledValue1 == testTouchOutsideCanceledValue2){
            testTouchOutsideCanceledValue1
        }else{
            null
        }

        typedArray.recycle()

        baseFooterViewProperty = GridFooterProperty(
                itemHeight,
                spanCount,
                text,
                textSize,
                textColor,
                textSelectedColor,
                unitIconDrawable,
                unitIconSelectedDrawable,
                backSurfaceAvailable,
                isTouchOutsideCanceled,
                footerMode
        )

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

        recyclerView.layoutManager = GridLayoutManager(context, baseFooterViewProperty.gridSpanCount, GridLayoutManager.VERTICAL, false)
        removeAllViews()
        addView(recyclerView)

        mAdapter = GridFooterAdapter(baseFooterViewProperty.gridItemHeight)
        recyclerView.adapter = mAdapter

        mAdapter?.mOnFooterItemContainerClickListener = object : OnFooterItemContainerClickListener {
            override fun onItemClick(list: MutableList<IFooterItem>, position: Int) {
                for (footerItem in list) {
                    footerItem.isSelected = false
                }

                val currentFooterItem = list[position]
                currentFooterItem.isSelected = true
                mAdapter?.notifyDataSetChanged()

                baseFooterViewProperty.selectedOptionText = currentFooterItem.titleName
                mOnFooterItemClickListener?.onClick(currentFooterItem)
            }
        }


    }

    /**
     * 设置数据源
     */
    fun setNewData(dataList: MutableList<out IFooterItem>) {
        val size = dataList.size
        val rowCount = size / baseFooterViewProperty.gridSpanCount + 1
        val needHeight = rowCount * baseFooterViewProperty.gridItemHeight
        Log.d(TAG, "needHeight = ${needHeight}")
        setupFooterHeight(needHeight.toInt())

        mAdapter?.setNewData(dataList)
    }

    fun setOnFooterItemClickListener(onFooterItemClickListener: OnFooterItemClickListener){
        this.mOnFooterItemClickListener = onFooterItemClickListener
    }
}