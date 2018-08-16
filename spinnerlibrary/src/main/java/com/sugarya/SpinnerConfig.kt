package com.sugarya

import android.graphics.Color
import android.view.Gravity
import com.sugarya.footer.interfaces.FooterMode
import com.sugarya.spinnerlibrary.R

class SpinnerConfig{

    companion object {
        const val STATUS_BAR_HEIGHT_DP = 24
        const val TITLE_BAR_HEIGHT_DP = 56

        const val DEFAULT_SPINNER_BAR_HEIGHT = 135
        const val DEFAULT_BACK_SURFACE_AVAILABLE = true
        const val ORIGIN_HEIGHT = 0
        const val DEFAULT_SPINNER_TITLE_SIZE_PX = 30f
        const val DEFAULT_TOUCH_OUTSIDE_CANCELED = true
        const val DEFAULT_LINE_SCALE = 0.3f
        val DEFAULT_FOOTER_MODE = FooterMode.MODE_EXPAND

        const val DEFAULT_SPINNER_BACK_SURFACE_COLOR = 0x55000000.toInt()
        const val DEFAULT_SPINNER_UNIT_TITLE_COLOR = 0xff333333.toInt()
        const val DEFAULT_SPINNER_UNIT_TITLE_COLOR_SELECTED = 0xff00a7f8.toInt()
        const val DEFAULT_LINE_COLOR = 0xffe0e0e0.toInt()
        const val DEFAULT_BAR_BACKGROUND = Color.WHITE

        const val DEFAULT_GRID_FOOTER_SPAN_COUNT = 4
        const val DEFAULT_SPINNER_GRAVITY = Gravity.CENTER_HORIZONTAL

        //footerView

        const val DEFAULT_LINEAR_FOOTER_ITEM_HEIGHT_DP = 45f
        const val DEFAULT_GRID_FOOTER_ITEM_HEIGHT_DP = 40f
    }
}