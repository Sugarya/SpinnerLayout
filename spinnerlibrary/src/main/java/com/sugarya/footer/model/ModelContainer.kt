package com.sugarya.footer.model

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sugarya.footer.interfaces.FooterMode

/**
 * 筛选条单元实体类
 */
class SpinnerUnitEntity(
        /**
         * 各个筛选单元的标题
         */
        var unitTitle: String?,
        /**
         * 打开下拉视图时，是否需要暗色屏幕显示
         */
        var isScreenDimAvailable: Boolean) {

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


    /**
     * footerView最初的高度
     */
    var footerViewOriginHeight: Int? = null
        private set

//    /**
//     * 触摸筛选条外部是，是否可以关闭筛选条的标示
//     */
//    var isCanceledOnTouchOutside = false








    /**
     * 下拉视图动画模式 1.平移  2.折叠
     */
    var footerMode: FooterMode? = null



    fun setFooterViewOriginHeight(footerViewOriginHeight: Int) {
        if (this.footerViewOriginHeight == null) {
            this.footerViewOriginHeight = footerViewOriginHeight
        }
    }
}

class SpinnerLayoutProperty(
        var barHeight: Int,
        var textSize: Float,
        var textColor: Int,
        var textSelectedColor: Int,
        var backSurfaceColor: Int,
        var unitIcon: Drawable,
        var unitIconSelected: Drawable,
        var isTouchOutsideCanceled: Boolean,
        var lineScale: Float,
        var barBackground: Int,
        var footerMode: FooterMode,
        var spinnerGravity: Int
)

class BaseFooterViewProperty(
        var text: String,
        var textSize: Float,
        var textColor: Int,
        var textSelectedColor: Int,

        var unitIcon: Drawable,
        var unitIconSelected: Drawable,
        var isTouchOutsideCanceled: Boolean,
        var footerMode: FooterMode
)

