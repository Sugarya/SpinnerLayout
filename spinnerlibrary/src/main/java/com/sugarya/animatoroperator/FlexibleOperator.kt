package com.sugarya.animatoroperator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator

/**
 * Created by Ethan Ruan 2018/07/11
 * 展开收缩 属性动画操作类
 */
class FlexibleOperator(var operatorUnit: OperatorUnit) {

    val DEFAULT_START_HEIGHT = 0

    /**
     * 起始高度
     */
    var startHeight = 0

    class Builder(val targetView: View) {

        private val operatorUnitBuilder: OperatorUnit = OperatorUnit(targetView)

        fun setHeight(height: Int): Builder {
            operatorUnitBuilder.height = height
            return this
        }

        fun setHeight(unit: Int, height: Float): Builder {
            operatorUnitBuilder.height = TypedValue.applyDimension(unit, height, targetView.resources.displayMetrics).toInt()
            return this
        }

        fun setDuration(duration: Long): Builder {
            operatorUnitBuilder.duration = duration
            return this
        }

        fun create(): FlexibleOperator = FlexibleOperator(operatorUnitBuilder)

    }

    private var isExpand = false

    fun isExpand(): Boolean = isExpand

    /**
     * 下拉
     */
    fun expand(newViewHeight: Int) {
        if (!isExpand) {
            operatorUnit.height = newViewHeight

            startHeight = operatorUnit.targetView.height
            if (startHeight < 0) {
                startHeight = DEFAULT_START_HEIGHT
            }

            if (operatorUnit.height - startHeight < 0) {
                return
            }
            startOperateAnimator(startHeight, operatorUnit.height)
        }
    }

    fun expand() {
        expand(operatorUnit.height)
    }

    /**
     * 收起
     */
    fun collapse() {
        if (isExpand) {
            startOperateAnimator(operatorUnit.height, startHeight)
        }
    }

    fun forceCollapse() {
        isExpand = true
        collapse()
    }

    fun forceExpand(){
        isExpand = false
        expand()
    }

    private fun startOperateAnimator(startValue: Int, endValue: Int) {
        val animator = ValueAnimator.ofInt(startValue, endValue)
        animator.setTarget(operatorUnit.targetView)
        animator.duration = operatorUnit.duration
        val isExpanding = endValue - startValue > 0
        if (isExpanding) {
            animator.interpolator = DecelerateInterpolator()
        } else {
            animator.interpolator = AccelerateInterpolator()
        }

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                isExpand = isExpanding
            }
        })
        animator.addUpdateListener {
            val animatedValue = it.animatedValue as Int
            val layoutParams = operatorUnit.targetView.layoutParams
                    ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.height = animatedValue
            operatorUnit.targetView.layoutParams = layoutParams

            if (isExpanding) {
                operatorUnit.targetView.alpha = animatedValue.toFloat() / endValue
            } else {
                operatorUnit.targetView.alpha = animatedValue.toFloat() / startValue
            }

        }

        animator.start()
    }

}