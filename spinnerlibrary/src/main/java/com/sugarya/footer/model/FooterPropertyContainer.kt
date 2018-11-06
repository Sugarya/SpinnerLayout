package com.sugarya.footer.model

import android.graphics.drawable.Drawable
import com.sugarya.SpinnerConfig
import com.sugarya.footer.interfaces.FooterMode

open class BaseFooterProperty(
//        var text: String = "",
//        var textSize: Float = SpinnerConfig.DEFAULT_SPINNER_TITLE_SIZE_PX,
//        var textColor: Int = SpinnerConfig.DEFAULT_SPINNER_UNIT_TITLE_COLOR,
//        var textSelectedColor: Int = SpinnerConfig.DEFAULT_SPINNER_UNIT_TITLE_COLOR_SELECTED,
//
//        var unitIcon: Drawable? = null,
//        var unitIconSelected: Drawable? = null,
//
//        var backSurfaceAvailable: Boolean = SpinnerConfig.DEFAULT_BACK_SURFACE_AVAILABLE,
//        var isTouchOutsideCanceled: Boolean = SpinnerConfig.DEFAULT_TOUCH_OUTSIDE_CANCELED,
//
//        var footerMode: FooterMode = FooterMode.MODE_EXPAND

        val text: String,
        var selectedOptionText: String,
        open val textSize: Float?,
        open val textColor: Int?,
        open val textSelectedColor: Int?,

        open val unitIcon: Drawable?,
        open val unitIconSelected: Drawable?,
        open val backSurfaceAvailable: Boolean?,
        open val isTouchOutsideCanceled: Boolean?,

        open val footerMode: FooterMode?
)

class LinearFooterProperty(
        var linearItemHeight: Float,
        text: String,
        textSize: Float?,
        textColor: Int?,
        textSelectedColor: Int?,
        unitIcon: Drawable?,
        unitIconSelected: Drawable?,
        backSurfaceAvailable: Boolean?,
        isTouchOutsideCanceled: Boolean?,
        footerMode: FooterMode?
) : BaseFooterProperty(text, "" ,textSize, textColor, textSelectedColor, unitIcon, unitIconSelected, backSurfaceAvailable, isTouchOutsideCanceled, footerMode)


class GridFooterProperty(
        var gridItemHeight: Float,
        var gridSpanCount: Int,
        text: String,
        textSize: Float?,
        textColor: Int?,
        textSelectedColor: Int?,

        unitIcon: Drawable?,
        unitIconSelected: Drawable?,

        backSurfaceAvailable: Boolean?,
        isTouchOutsideCanceled: Boolean?,

        footerMode: FooterMode?
) : BaseFooterProperty(text, "", textSize, textColor, textSelectedColor, unitIcon, unitIconSelected, backSurfaceAvailable, isTouchOutsideCanceled, footerMode)


class DateFooterProperty(
        text: String,
        var hint: String,
        textSize: Float?,
        textColor: Int?,
        textSelectedColor: Int?,
        unitIcon: Drawable?,
        unitIconSelected: Drawable?,
        backSurfaceAvailable: Boolean?,
        isTouchOutsideCanceled: Boolean?,
        footerMode: FooterMode?
) : BaseFooterProperty(text, "", textSize, textColor, textSelectedColor, unitIcon, unitIconSelected, backSurfaceAvailable, isTouchOutsideCanceled, footerMode)