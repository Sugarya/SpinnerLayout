package com.sugarya.footer

import android.content.Context
import android.util.AttributeSet
import com.sugarya.footer.base.BaseSpinnerFooter
import com.sugarya.footer.interfaces.FooterMode

/**
 * 自定义下拉列表
 */
class SpinnerCustomFooter: BaseSpinnerFooter {

    override var mFooterMode: FooterMode? = FooterMode.MODE_EXPAND


    constructor(context: Context): super(context)


    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)




}