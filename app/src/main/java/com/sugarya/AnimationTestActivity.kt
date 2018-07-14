package com.sugarya

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import com.sugarya.animateoperator.AnimateOperatorManager
import com.sugarya.spinnerlayout.R
import kotlinx.android.synthetic.main.activity_animation_test.*

class AnimationTestActivity: AppCompatActivity(){

    val mAdapter: RecyclerViewAdapter by lazy {
        RecyclerViewAdapter()
    }

    val mAdapter2: RecyclerViewAdapter by lazy {
        RecyclerViewAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation_test)

        initRecycleView()
        initRecycleView2()

        val flexibleOperator = AnimateOperatorManager
                .getInstance()
                .flexibleBuilder(containerView)
                .setHeight(TypedValue.COMPLEX_UNIT_DIP, 140f)
                .setDuration(AnimateOperatorManager.DURATION_MEDIUM)
                .create()

        tvTitleExpand.setOnClickListener {
            flexibleOperator.expand()
        }

        tvTitleCollapse.setOnClickListener {
            flexibleOperator.collapse()
        }


        val transitionOperator = AnimateOperatorManager
                .getInstance()
                .transitionBuild(containerFooter2)
                .setStartMarginTop(-140f)
                .setEndMarginTop(0f)
                .setDuration(AnimateOperatorManager.DURATION_MEDIUM)
                .create()

        tvTitleExpand2.setOnClickListener {
            transitionOperator.expand()
        }

        tvTitleCollapse2.setOnClickListener {
            transitionOperator.collapse()
        }
    }


    private fun initRecycleView(){
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = mAdapter

        val dataList = arrayListOf<String>("Java", "Ios", "Android", "Go", "Google")
        mAdapter.setNewData(dataList)
    }

    private fun initRecycleView2(){
        recyclerView2.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView2.adapter = mAdapter2

        val dataList = arrayListOf<String>("Java", "Ios", "Android", "Go", "Google")
        mAdapter2.setNewData(dataList)
    }
}