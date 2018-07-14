package com.sugarya.animateoperator.base

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator

abstract class BaseOperator<T: BaseOperatorParams>(val operatorParams: T){

    private var isExpand = false

    protected fun isExpand(): Boolean = isExpand

    protected fun setExpand(isExpand: Boolean){
        this.isExpand = isExpand
    }

    protected fun generateAnimator(startValue: Int, endValue: Int): ValueAnimator =
        ValueAnimator.ofInt(startValue, endValue).apply {
            setTarget(operatorParams.targetView)
            duration = operatorParams.duration

            val isExpanding = endValue - startValue > 0
            interpolator = when(isExpanding){
                true -> DecelerateInterpolator()
                false -> AccelerateInterpolator()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    setExpand(isExpanding)
                }
            })
        }

    protected abstract fun startOperateAnimator(startValue: Int, endValue: Int)
}
