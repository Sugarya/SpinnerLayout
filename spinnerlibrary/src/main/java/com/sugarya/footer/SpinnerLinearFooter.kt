package com.sugarya.footer

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import com.sugarya.SpinnerConfig
import com.sugarya.SpinnerConfig.Companion.DEFAULT_LINEAR_FOOTER_ITEM_HEIGHT_DP
import com.sugarya.footer.adapter.LinearFooterAdapter
import com.sugarya.footer.base.BaseSpinnerFooter
import com.sugarya.footer.interfaces.FooterMode
import com.sugarya.footer.interfaces.IFooterItem
import com.sugarya.footer.interfaces.OnFooterItemClickListener
import com.sugarya.footer.interfaces.OnFooterItemContainerClickListener
import com.sugarya.footer.model.BaseFooterProperty
import com.sugarya.footer.model.LinearFooterProperty
import com.sugarya.utils.FOOTER_MODE_SPARSE
import com.sugarya.spinnerlibrary.R


/**
 * Created by Ethan Ruan 2018/06/14
 * FilterLayout 的下拉视图  垂直列表
 * 支持FooterView优先控制下拉动画的模式
 */
class SpinnerLinearFooter : BaseSpinnerFooter<LinearFooterProperty> {

    companion object {
        const val TAG = "SpinnerLinearFooter"
    }

    private val FOOTER_ITEM_HEIGHT: Float = 50f

    var mOnFooterItemClickListener: OnFooterItemClickListener? = null
    private var mAdapter: LinearFooterAdapter? = null


    override val mFooterViewProperty: LinearFooterProperty = LinearFooterProperty()

    constructor(context: Context, title: String) : this(
            context,
            title,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SpinnerConfig.DEFAULT_LINEAR_FOOTER_ITEM_HEIGHT_DP, context.resources.displayMetrics))

    constructor(context: Context, title: String, itemHeight: Float) : super(context) {
        mFooterViewProperty.linearItemHeight = itemHeight
        mFooterViewProperty.text = title
        init(context)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SpinnerLinearFooter)

        val footerMode = FOOTER_MODE_SPARSE[typedArray.getInt(R.styleable.SpinnerLinearFooter_footerModeLinear, 1)]
        mFooterViewProperty.footerMode = footerMode

        val itemHeight = typedArray.getDimension(R.styleable.SpinnerLinearFooter_itemHeightLinear, FOOTER_ITEM_HEIGHT)
        mFooterViewProperty.linearItemHeight = itemHeight

        val text = typedArray.getString(R.styleable.SpinnerLinearFooter_textLinear)
        mFooterViewProperty.text = text

        val textSize = typedArray.getDimension(R.styleable.SpinnerLinearFooter_textSizeLinear, SpinnerConfig.DEFAULT_SPINNER_TITLE_SIZE_DP)
        mFooterViewProperty.textSize = textSize

        val textColor = typedArray.getColor(R.styleable.SpinnerLinearFooter_textColorLinear, SpinnerConfig.DEFAULT_SPINNER_UNIT_TITLE_COLOR)
        mFooterViewProperty.textColor = textColor

        val textSelectedColor = typedArray.getColor(R.styleable.SpinnerLinearFooter_textColorSelectedLinear, SpinnerConfig.DEFAULT_SPINNER_UNIT_TITLE_COLOR_SELECTED)
        mFooterViewProperty.textSelectedColor = textSelectedColor

        mFooterViewProperty.unitIcon = typedArray.getDrawable(R.styleable.SpinnerLinearFooter_iconLinear)

//        val unitIconSelectedDrawable = if (typedArray.getDrawable(R.styleable.SpinnerLinearFooter_iconSelectedLinear) != null) {
//            typedArray.getDrawable(R.styleable.SpinnerLinearFooter_iconSelectedLinear)
//        } else {
//            null
//        }
        mFooterViewProperty.unitIconSelected = typedArray.getDrawable(R.styleable.SpinnerLinearFooter_iconSelectedLinear)

        val backSurfaceAvailable = typedArray.getBoolean(R.styleable.SpinnerLinearFooter_backSurfaceAvailableLinear, SpinnerConfig.DEFAULT_BACK_SURFACE_AVAILABLE)
        mFooterViewProperty.backSurfaceAvailable = backSurfaceAvailable

        val isTouchOutsideCanceled = typedArray.getBoolean(R.styleable.SpinnerLinearFooter_touchOutsideCanceledLinear, SpinnerConfig.DEFAULT_TOUCH_OUTSIDE_CANCELED)
        mFooterViewProperty.isTouchOutsideCanceled = isTouchOutsideCanceled

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

        mAdapter = LinearFooterAdapter(mFooterViewProperty.linearItemHeight, recyclerView)
        recyclerView.adapter = mAdapter
        mAdapter?.mOnFooterItemContainerClickListener = object : OnFooterItemContainerClickListener {
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



    fun <T: IFooterItem> setNewData(sourceList: MutableList<T>) {
        val size = sourceList.size
        val needHeight = mFooterViewProperty.linearItemHeight * size
        Log.d(TAG, "setNewData needHeight = $needHeight")
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