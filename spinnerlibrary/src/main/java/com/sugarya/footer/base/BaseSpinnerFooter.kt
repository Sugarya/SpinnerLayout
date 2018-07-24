package com.sugarya.footer.base

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.sugarya.footer.interfaces.FooterMode
import com.sugarya.footer.interfaces.IFooterMode
import com.sugarya.footer.model.BaseFooterProperty

abstract class BaseSpinnerFooter<out T: BaseFooterProperty> : RelativeLayout, IFooterMode {

    abstract val baseFooterViewProperty: T

    var computedEndingHeight: Int = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    protected fun setupFooterHeight(needHeight: Int) {
        val showHeight = needHeight + paddingTop + paddingBottom
        computedEndingHeight = showHeight
    }

    override var mFooterMode: FooterMode?
        get() = baseFooterViewProperty.footerMode
        set(value) {}
}