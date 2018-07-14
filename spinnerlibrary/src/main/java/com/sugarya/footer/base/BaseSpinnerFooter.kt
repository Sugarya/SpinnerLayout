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
//        val spinnerLayout = (parent as ViewGroup).parent as ViewGroup as SpinnerLayout
//        val lp = RelativeLayout.LayoutParams(layoutParams)
//        if (mFooterMode == null) {
//            if (spinnerLayout.globalFilterMode != null) {
//                if (spinnerLayout.globalFilterMode == FooterMode.MODE_TRANSLATE) {
//                    lp.height = needHeight
//                    lp.topMargin = -needHeight
//                } else {
//                    lp.height = 1
//                    lp.topMargin = spinnerLayout.spinnerBarHeight
//                }
//            }
//        } else {
//            if (mFooterMode == FooterMode.MODE_TRANSLATE) {
//                lp.height = needHeight
//                lp.topMargin = -needHeight
//            } else {
//                lp.height = 1
//                lp.topMargin = spinnerLayout.spinnerBarHeight
//            }
//        }

//        layoutParams = lp
        //放在layoutParams之后，这样值才能成功保存
        Log.d("BaseSpinnerFooter", "paddingTop = ${paddingTop} , paddingBottom = ${paddingBottom}")
        val showHeight = needHeight + paddingTop + paddingBottom
        Log.d("BaseSpinnerFooter", "showHeight = ${showHeight}")
        setTag(R.id.footer_view_height, showHeight)
    }




}