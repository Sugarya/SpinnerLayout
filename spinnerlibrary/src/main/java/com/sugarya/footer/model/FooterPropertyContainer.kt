package com.sugarya.footer.model

import android.graphics.drawable.Drawable
import com.sugarya.SpinnerConfig
import com.sugarya.footer.interfaces.FooterMode

open class BaseFooterProperty(
        var text: String = "",
        var textSize: Float = SpinnerConfig.DEFAULT_SPINNER_TITLE_SIZE_DP,
        var textColor: Int = SpinnerConfig.DEFAULT_SPINNER_UNIT_TITLE_COLOR,
        var textSelectedColor: Int = SpinnerConfig.DEFAULT_SPINNER_UNIT_TITLE_COLOR_SELECTED,

        var unitIcon: Drawable? = null,
        var unitIconSelected: Drawable? = null,

        var backSurfaceAvailable: Boolean = SpinnerConfig.DEFAULT_BACK_SURFACE_AVAILABLE,
        var isTouchOutsideCanceled: Boolean = SpinnerConfig.DEFAULT_TOUCH_OUTSIDE_CANCELED,

        var footerMode: FooterMode = FooterMode.MODE_EXPAND
)

class LinearFooterProperty(var linearItemHeight: Float = SpinnerConfig.ORIGIN_HEIGHT.toFloat()): BaseFooterProperty()


class GridFooterProperty(
        var gridItemHeight: Float = SpinnerConfig.ORIGIN_HEIGHT.toFloat(),
        var gridSpanCount: Int = SpinnerConfig.DEFAULT_GRID_FOOTER_SPAN_COUNT
): BaseFooterProperty()


class DateFooterProperty(): BaseFooterProperty()