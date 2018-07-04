package com.sugarya

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.sugarya.footer.SpinnerDateFooter
import com.sugarya.footer.SpinnerGridFooter
import com.sugarya.footer.SpinnerLinearFooter
import com.sugarya.interfaces.IFooterItem
import com.sugarya.interfaces.OnFooterItemClickListener
import com.sugarya.model.LabelModel
import com.sugarya.model.StatusModel
import com.sugarya.spinnerlayout.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSpinnerLayout1()
        initSpinnerLayout1_2()
        initSpinnerLayout2()
        initSpinnerLayout3()
    }

    fun initSpinnerLayout1(){
        mockStatusFooter(linearFooter1, spinnerLayout1)
    }

    fun initSpinnerLayout1_2(){
        mockStatusFooter(linearFooter1_2, spinnerLayout1_2)
    }

    fun initSpinnerLayout2(){
        mockStatusFooter(linearFooter2, spinnerLayout2)
        mockLabelFooter(gridFooter2, spinnerLayout2)
    }

    fun initSpinnerLayout3(){
        mockStatusFooter(linearFooter3, spinnerLayout3)
        mockLabelFooter(gridFooter3, spinnerLayout3)
        mockDateFooter(dateFooter3, spinnerLayout3)
    }


    fun mockStatusFooter(spinnerLinearFooter: SpinnerLinearFooter, spinnerLayout: SpinnerLayout){
        val statusList = arrayListOf(StatusModel("11", "状态1"),
                StatusModel("12", "状态2"),
                StatusModel("13", "状态3"),
                StatusModel("14", "状态4"),
                StatusModel("15", "状态5"))
        spinnerLinearFooter.setNewData(statusList)
        spinnerLinearFooter.setOnFooterItemClickListener(object : OnFooterItemClickListener{
            override fun onClick(iFooterItem: IFooterItem) {

                spinnerLayout.back()
            }
        })
    }

    fun mockLabelFooter(spinnerGridFooter: SpinnerGridFooter, spinnerLayout: SpinnerLayout){
        val labelList = arrayListOf(
                LabelModel("21","标签1"),
                LabelModel("22","标签2"),
                LabelModel("23","标签3"),
                LabelModel("24","标签4"),
                LabelModel("25","标签5"),
                LabelModel("26","标签6"),
                LabelModel("27","标签7")
        )
        spinnerGridFooter.setNewData(labelList)
        spinnerGridFooter.setOnFooterItemClickListener(object : OnFooterItemClickListener{
            override fun onClick(iFooterItem: IFooterItem) {

                spinnerLayout.back()
            }
        })
    }

    fun mockDateFooter(spinnerDateFooter: SpinnerDateFooter, spinnerLayout: SpinnerLayout){
        spinnerDateFooter.setOnConfirmClickListener(object: SpinnerDateFooter.OnConfirmClickListener{
            override fun onConfirmClick(startTime: Long, endTime: Long) {

                spinnerLayout.back()
            }
        })
    }


}
