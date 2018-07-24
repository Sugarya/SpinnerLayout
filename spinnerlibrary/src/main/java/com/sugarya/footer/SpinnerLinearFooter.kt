package com.sugarya.footer

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import com.sugarya.SpinnerConfig
import com.sugarya.footer.adapter.LinearFooterAdapter
import com.sugarya.footer.base.BaseSpinnerFooter
import com.sugarya.footer.interfaces.FooterMode
import com.sugarya.footer.interfaces.IFooterItem
import com.sugarya.footer.interfaces.OnFooterItemClickListener
import com.sugarya.footer.interfaces.OnFooterItemContainerClickListener
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


    override val baseFooterViewProperty: LinearFooterProperty

    constructor(context: Context, title: String) : this(
            context,
            title,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SpinnerConfig.DEFAULT_LINEAR_FOOTER_ITEM_HEIGHT_DP, context.resources.displayMetrics))

    constructor(context: Context, title: String, itemHeight: Float) : super(context) {
        baseFooterViewProperty = LinearFooterProperty(
                itemHeight,
                title,
                null,
                null,
                null,
                null,
                null,
                SpinnerConfig.DEFAULT_BACK_SURFACE_AVAILABLE,
                SpinnerConfig.DEFAULT_TOUCH_OUTSIDE_CANCELED,
                null
        )
        init(context)
    }

    /*

    val linearItemHeight: Float?,
        text: String,
        textSize: Float?,
        textColor: Int?,
        textSelectedColor: Int?,
        unitIcon: Drawable?,
        unitIconSelected: Drawable?,
        backSurfaceAvailable: Boolean?,
        isTouchOutsideCanceled: Boolean?,
        footerMode: FooterMode?
     */
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SpinnerLinearFooter)

        val itemHeightValue = typedArray.getDimension(R.styleable.SpinnerLinearFooter_itemHeightLinear, -1f)
        val itemHeight: Float = if (itemHeightValue == -1f) {
            throw IllegalStateException("SpinnerLinearFooter needs a item height")
        } else {
            itemHeightValue
        }

        val text = typedArray.getString(R.styleable.SpinnerLinearFooter_textLinear) ?: ""

        val textSizeValue = typedArray.getDimension(R.styleable.SpinnerLinearFooter_textSizeLinear, -1f)
        val textSize: Float? = if (textSizeValue == -1f) {
            null
        } else {
            textSizeValue
        }

        val textColorValue = typedArray.getColor(R.styleable.SpinnerLinearFooter_textColorLinear, -1)
        val textColor: Int? = if (textColorValue == -1) {
            null
        } else {
            textColorValue
        }

        val textSelectedColorValue = typedArray.getColor(R.styleable.SpinnerLinearFooter_textColorSelectedLinear, -1)
        val textSelectedColor: Int? = if (textSelectedColorValue == -1) {
            null
        } else {
            textSelectedColorValue
        }

        val unitIcon: Drawable? = typedArray.getDrawable(R.styleable.SpinnerLinearFooter_iconLinear)
        val unitIconSelectedDrawable: Drawable? = typedArray.getDrawable(R.styleable.SpinnerLinearFooter_iconSelectedLinear)

        val testAvailableValue1 = typedArray.getBoolean(R.styleable.SpinnerLinearFooter_backSurfaceAvailableLinear, false)
        val testAvailableValue2 = typedArray.getBoolean(R.styleable.SpinnerLinearFooter_backSurfaceAvailableLinear, true)

        val backSurfaceAvailable: Boolean? = if(testAvailableValue1 == testAvailableValue2){
            testAvailableValue1
        }else{
            null
        }

        val testTouchOutsideCanceledValue1 = typedArray.getBoolean(R.styleable.SpinnerLinearFooter_touchOutsideCanceledLinear, false)
        val testTouchOutsideCanceledValue2 = typedArray.getBoolean(R.styleable.SpinnerLinearFooter_touchOutsideCanceledLinear, true)

        val isTouchOutsideCanceled: Boolean? = if(testTouchOutsideCanceledValue1 == testTouchOutsideCanceledValue2){
            testTouchOutsideCanceledValue1
        }else{
            null
        }

        val index = typedArray.getInt(R.styleable.SpinnerLinearFooter_footerModeLinear, -1)
        val footerMode: FooterMode? = if (index == -1) {
            null
        } else {
            FOOTER_MODE_SPARSE[index]
        }

        typedArray.recycle()

        baseFooterViewProperty = LinearFooterProperty(
                itemHeight,
                text,
                textSize,
                textColor,
                textSelectedColor,
                unitIcon,
                unitIconSelectedDrawable,
                backSurfaceAvailable,
                isTouchOutsideCanceled,
                footerMode)

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

        mAdapter = LinearFooterAdapter(baseFooterViewProperty.linearItemHeight, recyclerView)
        recyclerView.adapter = mAdapter
        mAdapter?.mOnFooterItemContainerClickListener = object : OnFooterItemContainerClickListener {
            override fun onItemClick(list: MutableList<IFooterItem>, position: Int) {
                for (item in list) {
                    item.isSelected = false
                }
                val footerItem = list[position]
                footerItem.isSelected = true
                mAdapter?.notifyDataSetChanged()

                mOnFooterItemClickListener?.onClick(list[position])
            }
        }
    }

    fun <T : IFooterItem> setNewData(sourceList: MutableList<T>) {
        val size = sourceList.size
        val needHeight = baseFooterViewProperty.linearItemHeight * size
        Log.d(TAG, "setNewData needHeight = $needHeight")
        setupFooterHeight(needHeight.toInt())

//        val filterContainerLayout = (filterLayout.getChildAt(0) as ViewGroup)
//        val filterBarLayout = filterContainerLayout.getChildAt(0)
        //bringToFront方法并不会真实改变视图在父容器的位置，只是展示上看起来像
//        filterBarLayout.bringToFront()
        mAdapter?.setNewData(sourceList)
    }

    fun setOnFooterItemClickListener(onFooterItemClickListener: OnFooterItemClickListener) {
        this.mOnFooterItemClickListener = onFooterItemClickListener
    }


}