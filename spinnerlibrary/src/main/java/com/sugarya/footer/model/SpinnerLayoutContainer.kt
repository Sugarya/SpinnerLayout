package com.sugarya.footer.model

import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sugarya.SpinnerConfig
import com.sugarya.footer.base.BaseSpinnerFooter
import com.sugarya.footer.interfaces.FooterMode

/**
 * 筛选条单元实体类
 */
class SpinnerUnitEntity(var baseSpinnerFooter: BaseSpinnerFooter<BaseFooterProperty>) {

    /**
     * 筛选条单元视图
     */
    var spinnerUnitLayout: ViewGroup? = null
    /**
     * 筛选标题控件
     */
    var tvUnit: TextView? = null
    /**
     * 筛选单元 图片
     */
    var imgUnitIcon: ImageView? = null

    /**
     * FooterView根视图
     */
    var footerViewContainer: ViewGroup? = null

    /**
     * 是否处在下拉状态
     */
    var isExpanded = false



    var mBaseFooterProperty: BaseFooterProperty? = null


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SpinnerUnitEntity) return false

        if (spinnerUnitLayout != other.spinnerUnitLayout) return false
        if (footerViewContainer != other.footerViewContainer) return false

        return true
    }

    override fun hashCode(): Int {
        var result = spinnerUnitLayout?.hashCode() ?: 0
        result = 31 * result + (footerViewContainer?.hashCode() ?: 0)
        return result
    }


}


/**
 * todo backSurfaceAvailable 使用委托属性
 */
class SpinnerLayoutProperty(
        var barHeight: Int = SpinnerConfig.DEFAULT_SPINNER_BAR_HEIGHT,
        var textSize: Float = SpinnerConfig.DEFAULT_SPINNER_TITLE_SIZE_DP,
        var textColor: Int = SpinnerConfig.DEFAULT_SPINNER_UNIT_TITLE_COLOR,
        var textSelectedColor: Int = SpinnerConfig.DEFAULT_SPINNER_UNIT_TITLE_COLOR_SELECTED,
        var backSurfaceColor: Int = SpinnerConfig.DEFAULT_SPINNER_BACK_SURFACE_COLOR,
        var backSurfaceAvailable: Boolean = SpinnerConfig.DEFAULT_BACK_SURFACE_AVAILABLE,
        var unitIcon: Drawable? = null,
        var unitIconSelected: Drawable? = null,
        var isTouchOutsideCanceled: Boolean = SpinnerConfig.DEFAULT_TOUCH_OUTSIDE_CANCELED,
        var lineScale: Float = SpinnerConfig.DEFAULT_LINE_SCALE,
        var barBackground: Int = SpinnerConfig.DEFAULT_SPINNER_BACKGROUND_COLOR,
        var footerMode: FooterMode = FooterMode.MODE_EXPAND,
        var spinnerGravity: Int = Gravity.CENTER_HORIZONTAL
){



//    constructor(property: SpinnerLayoutProperty): this(){
//        this.barHeight = property.barHeight
//        this.textSize = property.textSize
//        this.textColor = property.textColor
//        this.textSelectedColor = property.textSelectedColor
//        this.backSurfaceColor = property.backSurfaceColor
//        this.backSurfaceAvailable = property.backSurfaceAvailable
//        this.unitIcon = property.unitIcon
//        this.unitIconSelected = property.unitIconSelected
//        this.isTouchOutsideCanceled = property.isTouchOutsideCanceled
//        this.lineScale = property.lineScale
//        this.barBackground = property.barBackground
//        this.footerMode = property.footerMode
//        this.spinnerGravity = property.spinnerGravity
//    }
}



