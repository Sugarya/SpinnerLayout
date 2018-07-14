package com.sugarya.animateoperator

import android.view.View
import com.sugarya.animateoperator.operator.FlexibleOperator
import com.sugarya.animateoperator.operator.TransitionOperator

/**
 * Create by Ethan Ruan 2018/07/10
 * 管理类
 */
class AnimateOperatorManager {

    companion object {
        const val DURATION_MEDIUM: Long = 300L
        const val DURATION_SLOW: Long = 500L
        const val DURATION_QUICK: Long = 200L

        private var sINSTANCE: AnimateOperatorManager? = null

        @JvmStatic
        fun getInstance(): AnimateOperatorManager {
            if (sINSTANCE == null) {
                synchronized(AnimateOperatorManager::class.java) {
                    if (sINSTANCE == null) {
                        sINSTANCE = AnimateOperatorManager()
                    }
                }
            }
            return sINSTANCE!!
        }

    }

    /**
     * 控件高度 拉伸动画
     */
    fun flexibleBuilder(targetView: View): FlexibleOperator.Builder = FlexibleOperator.Builder(targetView)

    /**
     * 控件平移 动画
     */
    fun transitionBuild(targetView: View): TransitionOperator.Builder = TransitionOperator.Builder(targetView)


}