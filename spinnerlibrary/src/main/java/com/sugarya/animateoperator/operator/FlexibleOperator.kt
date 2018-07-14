package com.sugarya.animateoperator.operator

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.sugarya.animateoperator.base.BaseBuilder
import com.sugarya.animateoperator.interfaces.Expandable
import com.sugarya.animateoperator.base.BaseOperator
import com.sugarya.animateoperator.model.FlexibleOperatorParams

/**
 * Created by Ethan Ruan 2018/07/11
 * 展开收缩 属性动画操作类
 */
class FlexibleOperator(flexibleOperatorParams: FlexibleOperatorParams) : BaseOperator<FlexibleOperatorParams>(flexibleOperatorParams), Expandable {

    val DEFAULT_START_HEIGHT = 0

    /**
     * 起始高度
     */
    var startHeight = DEFAULT_START_HEIGHT

    /**
     * 下拉
     */
    fun expand(newViewHeight: Int) {
        if (!isExpand()) {
            operatorParams.height = newViewHeight

            startHeight = operatorParams.targetView.height
            if (startHeight < 0) {
                startHeight = DEFAULT_START_HEIGHT
            }

            if (operatorParams.height - startHeight < 0) {
                return
            }
            startOperateAnimator(startHeight, operatorParams.height)
        }
    }

    override fun expand() {
        expand(operatorParams.height)
    }

    /**
     * 收起
     */
    override fun collapse() {
        if (isExpand()) {
            startOperateAnimator(operatorParams.height, startHeight)
        }
    }

    fun forceCollapse() {
        setExpand(true)
        collapse()
    }

    fun forceExpand() {
        setExpand(false)
        expand()
    }


    override fun startOperateAnimator(startValue: Int, endValue: Int) {
        val animator = generateAnimator(startValue, endValue)
        animator.addUpdateListener {
            val animatedValue = it.animatedValue as Int
            operatorParams.targetView.layoutParams = (operatorParams.targetView.layoutParams
                    ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
                    .apply {
                        height = animatedValue
                    }

            val isExpanding = endValue - startValue > 0
            operatorParams.targetView.alpha = if (isExpanding) {
                animatedValue.toFloat() / endValue
            } else {
                animatedValue.toFloat() / startValue
            }
        }

        animator.start()
    }

    class Builder(private val targetView: View) : BaseBuilder<FlexibleOperator>() {

        private val operatorParams = FlexibleOperatorParams(targetView)

        fun setDuration(duration: Long): Builder{
            operatorParams.duration = duration
            return this
        }

        fun setHeight(height: Int): Builder {
            operatorParams.height = height
            return this
        }

        fun setHeight(unit: Int, height: Float): Builder{
            operatorParams.height = TypedValue.applyDimension(unit, height, targetView.resources.displayMetrics).toInt()
            return this
        }

        override fun create(): FlexibleOperator = FlexibleOperator(operatorParams)

    }


}