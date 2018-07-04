package com.sugarya.footer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.sugarya.footer.base.BaseSpinnerFooter
import com.sugarya.interfaces.FooterMode
import com.sugarya.utils.FOOTER_MODE_SPARSE
import com.sugarya.utils.formatDate
import com.sugarya.spinnerlibrary.R
import kotlinx.android.synthetic.main.filter_footer_date.view.*
import java.util.*

/**
 * 日期下拉列表
 */
class SpinnerDateFooter : BaseSpinnerFooter {


    private var mStartTime: Long = -1
    private var mEndTime: Long = -1

    override var mFooterMode: FooterMode? = null

    var mOnConfirmClickListener: OnConfirmClickListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SpinnerDateFooter)
        mFooterMode = FOOTER_MODE_SPARSE[typedArray.getInt(R.styleable.SpinnerDateFooter_dateFooterMode, 1)]
        typedArray.recycle()
        init()
    }

    private fun init() {
        if (background == null) {
            setBackgroundColor(resources.getColor(android.R.color.white))
        }

        setTag(R.id.footer_view_height, resources.getDimension(R.dimen.footer_date_height).toInt())

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