package com.sugarya.footer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.sugarya.footer.base.BaseSpinnerFooter
import com.sugarya.footer.interfaces.FooterMode
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


    override val baseFooterViewProperty: DateFooterProperty

    constructor(context: Context, title: String) : super(context) {
        baseFooterViewProperty = DateFooterProperty(
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

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SpinnerDateFooter)

        val footerModeOrdinal = typedArray.getInt(R.styleable.SpinnerDateFooter_footerModeDate, -1)
        val footerMode: FooterMode? = if(footerModeOrdinal == -1){
            null
        }else{
            FOOTER_MODE_SPARSE[footerModeOrdinal]
        }

        val text = typedArray.getString(R.styleable.SpinnerDateFooter_textDate)

        val textSizeValue = typedArray.getDimension(R.styleable.SpinnerDateFooter_textSizeDate, -1f)
        val textSize: Float? = if(textSizeValue == -1f){
            null
        }else{
            textSizeValue
        }

        val textColorValue = typedArray.getColor(R.styleable.SpinnerDateFooter_textColorDate, -1)
        val textColor: Int? = if(textColorValue == -1){
            null
        }else{
            textColorValue
        }

        val textSelectedColorValue = typedArray.getColor(R.styleable.SpinnerDateFooter_textColorSelectedDate, -1)
        val textSelectedColor: Int? = if(textSelectedColorValue == -1){
            null
        }else{
            textSelectedColorValue
        }

        val unitIconDrawable = typedArray.getDrawable(R.styleable.SpinnerDateFooter_iconDate)
        val unitIconSelectedDrawable = typedArray.getDrawable(R.styleable.SpinnerDateFooter_iconSelectedDate)

        val testBackSurfaceAvailableValue1 = typedArray.getBoolean(R.styleable.SpinnerDateFooter_backSurfaceAvailableDate, false)
        val testBackSurfaceAvailableValue2 = typedArray.getBoolean(R.styleable.SpinnerDateFooter_backSurfaceAvailableDate, true)
        val backSurfaceAvailable: Boolean? = if(testBackSurfaceAvailableValue1 == testBackSurfaceAvailableValue2){
            testBackSurfaceAvailableValue1
        }else{
            null
        }

        val testTouchOutsideCanceledValue1 = typedArray.getBoolean(R.styleable.SpinnerDateFooter_touchOutsideCanceledDate, false)
        val testTouchOutsideCanceledValue2 = typedArray.getBoolean(R.styleable.SpinnerDateFooter_touchOutsideCanceledDate, true)
        val isTouchOutsideCanceled: Boolean? = if(testTouchOutsideCanceledValue1 == testTouchOutsideCanceledValue2){
            testTouchOutsideCanceledValue1
        }else{
            null
        }

        typedArray.recycle()

        baseFooterViewProperty = DateFooterProperty(
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