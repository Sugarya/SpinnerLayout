package com.sugarya.animatoroperator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator

/**
 * Create by Ethan Ruan 2018/07/10
 * 伸缩动画操作类
 */
class FlexibleOperator(var operatorUnit: OperatorUnit) {

    class Builder(val targetView: View){

        private val operatorUnitBuilder: OperatorUnit = OperatorUnit(targetView)

        fun setHeight(height: Int): Builder{
            operatorUnitBuilder.height = height
            return this
        }

        fun setHeight(unit: Int, height: Float): Builder{
            operatorUnitBuilder.height = TypedValue.applyDimension(unit, height, targetView.resources.displayMetrics).toInt()
            return this
        }

        fun setDuration(duration: Long): Builder{
            operatorUnitBuilder.duration = duration
            return this
        }

        fun create(): FlexibleOperator = FlexibleOperator(operatorUnitBuilder)

    }

    private var isExpand = false

    fun isExpand(): Boolean = isExpand

    fun expand() {
        if (!isExpand) {
            startOperateAnimator(1, operatorUnit.height)
        }
    }

    fun expand(newViewHeight: Int) {
        operatorUnit.height = newViewHeight
        if (!isExpand) {
            startOperateAnimator(1, newViewHeight)
        }
    }

    fun collapse() {
        if (isExpand) {
            startOperateAnimator(operatorUnit.height, 1)
        }
    }

    private fun startOperateAnimator(startValue: Int, endValue: Int) {
        val animator = ValueAnimator.ofInt(startValue, endValue)
        animator.setTarget(operatorUnit.targetView)
        animator.duration = operatorUnit.duration
        val isExpanding = endValue - startValue > 0
        if(isExpanding){
            animator.interpolator = DecelerateInterpolator()
        }else{
            animator.interpolator = AccelerateInterpolator()
        }

        animator.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                isExpand = isExpanding
            }
        })
        animator.addUpdateListener {
            val animatedValue = it.animatedValue as Int
            operatorUnit.targetView.layoutParams.height = animatedValue
            operatorUnit.targetView.layoutParams = operatorUnit.targetView.layoutParams
        }

        animator.start()
    }

}