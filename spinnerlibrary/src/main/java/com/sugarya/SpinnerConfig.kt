package com.sugarya

import android.graphics.Color

class SpinnerConfig{

    companion object {

        const val DEFAULT_SPINNER_BAR_HEIGHT = 135
        const val DEFAULT_SPINNER_BACKGROUND_COLOR = Color.WHITE
        const val DEFAULT_BACK_SURFACE_AVAILABLE = true
        const val ORIGIN_HEIGHT = 0
        const val DEFAULT_SPINNER_TITLE_SIZE_DP = 14f
        const val DEFAULT_TOUCH_OUTSIDE_CANCELED = true
        const val DEFAULT_LINE_SCALE = 1f

//        val DEFAULT_SPINNER_BACK_SURFACE_COLOR = Color.parseColor("#55000000")
        const val DEFAULT_SPINNER_BACK_SURFACE_COLOR = 0x55000000.toInt()
//        val DEFAULT_SPINNER_UNIT_TITLE_COLOR = Color.parseColor("#333333")
        const val DEFAULT_SPINNER_UNIT_TITLE_COLOR = 0xff333333.toInt()
//        val DEFAULT_SPINNER_UNIT_TITLE_COLOR_SELECTED = Color.parseColor("#00a7f8")
        const val DEFAULT_SPINNER_UNIT_TITLE_COLOR_SELECTED = 0xff00a7f8.toInt()
//        val DEFAULT_LINE_COLOR = Color.parseColor("#e0e0e0")
        const val DEFAULT_LINE_COLOR = 0xffe0e0e0.toInt()

        const val DEFAULT_GRID_FOOTER_SPAN_COUNT = 4
    }
}