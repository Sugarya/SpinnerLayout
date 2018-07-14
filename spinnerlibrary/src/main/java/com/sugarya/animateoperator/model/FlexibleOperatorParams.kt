package com.sugarya.animateoperator.model

import android.view.View
import com.sugarya.animateoperator.AnimateOperatorManager
import com.sugarya.animateoperator.base.BaseOperatorParams

class FlexibleOperatorParams(
        targetView: View,
        duration: Long = AnimateOperatorManager.DURATION_MEDIUM,
        var height: Int = 1
) : BaseOperatorParams(targetView, duration)