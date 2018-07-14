package com.sugarya.animateoperator.model

import android.view.View
import com.sugarya.animateoperator.AnimateOperatorManager
import com.sugarya.animateoperator.base.BaseOperatorParams

class TransitionOperatorParams(
        targetView: View,
        duration: Long = AnimateOperatorManager.DURATION_MEDIUM,
        var startTop: Int = 0,
        var endTop: Int = 0
) : BaseOperatorParams(targetView, duration)