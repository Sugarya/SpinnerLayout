package com.sugarya.footer

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.sugarya.SpinnerConfig
import com.sugarya.footer.base.BaseSpinnerFooter
import com.sugarya.footer.model.DateFooterProperty
import com.sugarya.utils.FOOTER_MODE_SPARSE
import com.sugarya.utils.formatDate
import com.sugarya.spinnerlibrary.R
import kotlinx.android.synthetic.main.filter_footer_date.view.*
import java.util.*

/**
 * 日期下拉列表
 */
class SpinnerDateFooter : BaseSpinnerFooter<DateFooterProperty> {


    private var mStartTime: Long = -1
    private var mEndTime: Long = -1


    var mOnConfirmClickListener: OnConfirmClickListener? = null


    override val mFooterViewProperty: DateFooterProperty = DateFooterProperty()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SpinnerDateFooter)

        val footerMode = FOOTER_MODE_SPARSE[typedArray.getInt(R.styleable.SpinnerDateFooter_footerModeDate, 1)]
        mFooterViewProperty.footerMode = footerMode

        val text = typedArray.getString(R.styleable.SpinnerDateFooter_textDate)
        mFooterViewProperty.text = text

        val textSize = typedArray.getDimension(R.styleable.SpinnerDateFooter_textSizeDate,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SpinnerConfig.DEFAULT_SPINNER_TITLE_SIZE_DP, resources.displayMetrics))
        mFooterViewProperty.textSize = textSize

        val textColor = typedArray.getColor(R.styleable.SpinnerDateFooter_textColorDate, SpinnerConfig.DEFAULT_SPINNER_BACK_SURFACE_COLOR)
        mFooterViewProperty.textColor = textColor

        val textSelectedColor = typedArray.getColor(R.styleable.SpinnerDateFooter_textColorSelectedDate, SpinnerConfig.DEFAULT_SPINNER_UNIT_TITLE_COLOR_SELECTED)
        mFooterViewProperty.textSelectedColor = textSelectedColor

        val unitIconDrawable = if (typedArray.getDrawable(R.styleable.SpinnerDateFooter_iconDate) != null) {
            typedArray.getDrawable(R.styleable.SpinnerLayout_icon)
        } else {
            resources.getDrawable(R.drawable.footer_triangle_down_black)
        }
        mFooterViewProperty.unitIcon = unitIconDrawable

        val unitIconSelectedDrawable = if (typedArray.getDrawable(R.styleable.SpinnerDateFooter_iconSelectedDate) != null) {
            typedArray.getDrawable(R.styleable.SpinnerLayout_iconSelected)
        } else {
            resources.getDrawable(R.drawable.footer_triangle_up_blue)
        }
        mFooterViewProperty.unitIconSelected = unitIconSelectedDrawable

        val backSurfaceAvailable = typedArray.getBoolean(R.styleable.SpinnerDateFooter_backSurfaceAvailableDate, SpinnerConfig.DEFAULT_BACK_SURFACE_AVAILABLE)
        mFooterViewProperty.backSurfaceAvailable = backSurfaceAvailable

        val isTouchOutsideCanceled = typedArray.getBoolean(R.styleable.SpinnerDateFooter_touchOutsideCanceledDate, SpinnerConfig.DEFAULT_TOUCH_OUTSIDE_CANCELED)
        mFooterViewProperty.isTouchOutsideCanceled = isTouchOutsideCanceled

        typedArray.recycle()
        init()
    }

    private fun init() {
        if (background == null) {
            setBackgroundColor(resources.getColor(android.R.color.white))
        }

        setupFooterHeight(resources.getDimension(R.dimen.footer_date_height).toInt())

        val inflateView = LayoutInflater.from(context).inflate(R.layout.filter_footer_date, this, false)
        addView(inflateView)

        containerFooterStartTime.setOnClickListener {
            val timePickerView = TimePickerBuilder(context) { date: Date?, v: View? ->
                tvFooterDateConfirm.isEnabled = mEndTime > 0
                date?.let {
                    mStartTime = it.time
                    tvFooterStartTime.text = formatDate(it)
                }
            }.build()
            timePickerView.show()
        }

        containerFooterEndTime.setOnClickListener {
            val timePickerView = TimePickerBuilder(context) { date: Date?, v: View? ->
                tvFooterDateConfirm.isEnabled = mStartTime > 0
                date?.let {
                    mEndTime = it.time
                    tvFooterEndTime.text = formatDate(it)
                }
            }.build()
            timePickerView.show()
        }

        tvFooterDateConfirm.setOnClickListener {
            mOnConfirmClickListener?.onConfirmClick(mStartTime, mEndTime)
        }
    }

    fun setOnConfirmClickListener(onConfirmClickListener: OnConfirmClickListener){
        this.mOnConfirmClickListener = onConfirmClickListener
    }




    interface OnConfirmClickListener {
        fun onConfirmClick(startTime: Long, endTime: Long)
    }

}