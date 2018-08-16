package com.sugarya

import android.graphics.Color
import android.view.Gravity
import com.sugarya.footer.interfaces.FooterMode
import com.sugarya.spinnerlibrary.R

class SpinnerConfig{

    private var windowPaddingTop: Float = 0f

    companion object {
        const val DEFAULT_SPINNER_BAR_HEIGHT = 135
        const val DEFAULT_BACK_SURFACE_AVAILABLE = true
        const val ORIGIN_HEIGHT = 0
        const val DEFAULT_SPINNER_TITLE_SIZE_PX = 42f
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

        private var INSTANCE: SpinnerConfig? = null

        @JvmStatic
        fun getInstance(): SpinnerConfig{
            if(INSTANCE == null){
                synchronized(SpinnerConfig::class.java){
                    if(INSTANCE == null){
                        INSTANCE = SpinnerConfig()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    fun setWindowPaddingTop(top: Float){
        this.windowPaddingTop = top
    }

    fun getWindowPaddingTop(): Float = windowPaddingTop

//    class SpinnerConfigBuilder(private val spinnerConfigParam: SpinnerConfigParam = SpinnerConfigParam()){
//
//        fun setupTitleBarHeight(height: Float): SpinnerConfigBuilder{
//            spinnerConfigParam.titleBarHeight = height
//            return this
//        }
//
//        fun build(): SpinnerConfig{
//            return SpinnerConfig()
//        }
//    }
//
//
//    data class SpinnerConfigParam(var titleBarHeight: Float = 56f)

}