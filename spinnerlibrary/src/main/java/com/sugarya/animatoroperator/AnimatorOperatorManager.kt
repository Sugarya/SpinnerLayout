package com.sugarya.animatoroperator

import android.view.View

/**
 * Create by Ethan Ruan 2018/07/10
 * 管理类
 */
class AnimatorOperatorManager {

    private val mAnimationMap = hashMapOf<View, FlexibleOperator>()

    companion object {
        const val DURATION_MEDIUM: Long = 300L
        const val DURATION_SLOW: Long = 500L
        const val DURATION_QUICK: Long = 200L

        private var sINSTANCE: AnimatorOperatorManager? = null

        @JvmStatic
        fun getInstance(): AnimatorOperatorManager {
            if (sINSTANCE == null) {
                synchronized(AnimatorOperatorManager::class.java) {
                    if (sINSTANCE == null) {
                        sINSTANCE = AnimatorOperatorManager()
                    }
                }
            }
            return sINSTANCE!!
        }

    }

    /**
     * 生成控件高度拉伸动画
     */
    fun flexibleBuilder(targetView: View): FlexibleOperator.Builder = FlexibleOperator.Builder(targetView)



}