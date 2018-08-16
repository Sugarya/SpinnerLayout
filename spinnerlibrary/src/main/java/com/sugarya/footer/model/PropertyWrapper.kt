package com.sugarya.footer.model

import android.content.Context
import android.graphics.drawable.Drawable
import com.sugarya.SpinnerConfig
import com.sugarya.footer.interfaces.FooterMode
import com.sugarya.spinnerlibrary.R


class BaseFooterPropertyWrapper(
        val context: Context,
        private val spinnerLayoutProperty: SpinnerLayoutProperty,
        private val baseFooterProperty: BaseFooterProperty)
    : BaseFooterProperty(
        baseFooterProperty.text,
        baseFooterProperty.selectedOptionText,
        baseFooterProperty.textSize,
        baseFooterProperty.textColor,
        baseFooterProperty.textSelectedColor,
        baseFooterProperty.unitIcon,
        baseFooterProperty.unitIconSelected,
        baseFooterProperty.backSurfaceAvailable,
        baseFooterProperty.isTouchOutsideCanceled,
        baseFooterProperty.footerMode
) {

    override val textSize: Float = baseFooterProperty.textSize
            ?: spinnerLayoutProperty.textSize
            ?: SpinnerConfig.DEFAULT_SPINNER_TITLE_SIZE_PX

    override val textColor: Int = baseFooterProperty.textColor
            ?: spinnerLayoutProperty.textColor
            ?: SpinnerConfig.DEFAULT_SPINNER_UNIT_TITLE_COLOR

    override val textSelectedColor: Int = baseFooterProperty.textSelectedColor
            ?: spinnerLayoutProperty.textSelectedColor
            ?: SpinnerConfig.DEFAULT_SPINNER_UNIT_TITLE_COLOR_SELECTED

    override val unitIcon: Drawable = baseFooterProperty.unitIcon
            ?: spinnerLayoutProperty.unitIcon
            ?: context.resources.getDrawable(R.drawable.footer_triangle_down_black)

    override val unitIconSelected: Drawable = baseFooterProperty.unitIconSelected
            ?: spinnerLayoutProperty.unitIconSelected
            ?: context.resources.getDrawable(R.drawable.footer_triangle_up_blue)

    override val backSurfaceAvailable: Boolean = baseFooterProperty.backSurfaceAvailable
            ?: spinnerLayoutProperty.backSurfaceAvailable
            ?: SpinnerConfig.DEFAULT_BACK_SURFACE_AVAILABLE

    override val isTouchOutsideCanceled: Boolean = baseFooterProperty.isTouchOutsideCanceled
            ?: spinnerLayoutProperty.isTouchOutsideCanceled
            ?: SpinnerConfig.DEFAULT_TOUCH_OUTSIDE_CANCELED

    override var footerMode: FooterMode = baseFooterProperty.footerMode
            ?: spinnerLayoutProperty.footerMode
            ?: SpinnerConfig.DEFAULT_FOOTER_MODE

}


class SpinnerLayoutPropertyWrapper(
        val context: Context,
        private val spinnerLayoutProperty: SpinnerLayoutProperty
        ) : SpinnerLayoutProperty(
        spinnerLayoutProperty.barHeight,
        spinnerLayoutProperty.textSize,
        spinnerLayoutProperty.textColor,
        spinnerLayoutProperty.textSelectedColor,
        spinnerLayoutProperty.backSurfaceColor,
        spinnerLayoutProperty.unitIcon,
        spinnerLayoutProperty.unitIconSelected,
        spinnerLayoutProperty.backSurfaceAvailable,
        spinnerLayoutProperty.isTouchOutsideCanceled,
        spinnerLayoutProperty.lineScale,
        spinnerLayoutProperty.barBackground,
        spinnerLayoutProperty.footerMode,
        spinnerLayoutProperty.spinnerGravity
) {


    override val textSize: Float = spinnerLayoutProperty.textSize ?: SpinnerConfig.DEFAULT_SPINNER_TITLE_SIZE_PX

    override val textColor: Int = spinnerLayoutProperty.textColor ?: SpinnerConfig.DEFAULT_SPINNER_UNIT_TITLE_COLOR

    override val textSelectedColor: Int = spinnerLayoutProperty.textSelectedColor ?: SpinnerConfig.DEFAULT_SPINNER_UNIT_TITLE_COLOR_SELECTED

    override val backSurfaceColor: Int = spinnerLayoutProperty.backSurfaceColor ?: SpinnerConfig.DEFAULT_SPINNER_BACK_SURFACE_COLOR

    override val unitIcon: Drawable = spinnerLayoutProperty.unitIcon ?: context.resources.getDrawable(R.drawable.footer_triangle_down_black)

    override val unitIconSelected: Drawable = spinnerLayoutProperty.unitIconSelected ?: context.resources.getDrawable(R.drawable.footer_triangle_up_blue)

    override val backSurfaceAvailable: Boolean = spinnerLayoutProperty.backSurfaceAvailable ?: SpinnerConfig.DEFAULT_BACK_SURFACE_AVAILABLE

    override val isTouchOutsideCanceled: Boolean = spinnerLayoutProperty.isTouchOutsideCanceled ?: SpinnerConfig.DEFAULT_TOUCH_OUTSIDE_CANCELED

    override val lineScale: Float = spinnerLayoutProperty.lineScale ?: SpinnerConfig.DEFAULT_LINE_SCALE

    override val barBackground: Int = spinnerLayoutProperty.barBackground ?: SpinnerConfig.DEFAULT_BAR_BACKGROUND

    override val footerMode: FooterMode = spinnerLayoutProperty.footerMode ?: SpinnerConfig.DEFAULT_FOOTER_MODE

    override val spinnerGravity: Int = spinnerLayoutProperty.spinnerGravity ?: SpinnerConfig.DEFAULT_SPINNER_GRAVITY
}

