package com.sugarya.footer.base

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.sugarya.SpinnerLayout
import com.sugarya.footer.interfaces.FooterMode
import com.sugarya.footer.interfaces.IFooterMode
import com.sugarya.spinnerlibrary.R

abstract class BaseSpinnerFooter : RelativeLayout, IFooterMode {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    protected fun setupFooterHeight(needHeight: Int) {
        Log.d("BaseSpinnerFooter", "paddingTop = ${paddingTop} , paddingBottom = ${paddingBottom}")
        val showHeight = needHeight + paddingTop + paddingBottom
        Log.d("BaseSpinnerFooter", "showHeight = ${showHeight}")
        setTag(R.id.footer_view_height, showHeight)
    }




}