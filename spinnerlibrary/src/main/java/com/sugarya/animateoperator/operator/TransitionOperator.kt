package com.sugarya.animateoperator.operator

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.sugarya.animateoperator.base.BaseBuilder
import com.sugarya.animateoperator.base.BaseOperator
import com.sugarya.animateoperator.interfaces.Expandable
import com.sugarya.animateoperator.model.TransitionOperatorParams

/**
 * Created by Ethan Ruan 2018/07/13
 * 平移动画操作
 */
class TransitionOperator(transitionOperatorParams: TransitionOperatorParams) : BaseOperator<TransitionOperatorParams>(transitionOperatorParams), Expandable {


    override fun expand() {
        if(!isExpand()){
            if(operatorParams.endTop - operatorParams.startTop <= 0){
                return
            }
            startOperateAnimator(operatorParams.startTop, operatorParams.endTop)
        }

    }

    override fun collapse() {
        if(isExpand()){
            startOperateAnimator(operatorParams.endTop, operatorParams.startTop)
        }
    }

    override fun startOperateAnimator(startValue: Int, endValue: Int) {
        val animator = generateAnimator(startValue, endValue)

        animator.addUpdateListener {
            val animatedValue = it.animatedValue as Int

            operatorParams.targetView.layoutParams = ((operatorParams.targetView.layoutParams
                    ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
                    as ViewGroup.MarginLayoutParams)
                    .apply {
                        topMargin = animatedValue
                    }
        }

        animator.start()
    }

    class Builder(val targetView: View)
        : BaseBuilder<TransitionOperator>() {

        private val operatorParams = TransitionOperatorParams(targetView)

        fun setDuration(duration: Long): Builder{
            operatorParams.duration = duration
            return this
        }

        fun setStartMarginTop(topValue: Float): Builder{
            setStartMarginTop(TypedValue.COMPLEX_UNIT_DIP, topValue)
            return this
        }


        fun setStartMarginTop(unit: Int, marginTop: Float): Builder {
            operatorParams.startTop = TypedValue.applyDimension(unit, marginTop, targetView.resources.displayMetrics).toInt()
            return this
        }

        fun setEndMarginTop(endValue: Float): Builder{
            setEndMarginTop(TypedValue.COMPLEX_UNIT_DIP, endValue)
            return this
        }

        fun setEndMarginTop(unit: Int, marginTop: Float): Builder {
            operatorParams.endTop = TypedValue.applyDimension(unit, marginTop, targetView.resources.displayMetrics).toInt()
            return this
        }

        override fun create(): TransitionOperator = TransitionOperator(operatorParams)
    }
}