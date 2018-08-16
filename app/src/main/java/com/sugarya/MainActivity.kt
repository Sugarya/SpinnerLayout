package com.sugarya

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.sugarya.footer.SpinnerDateFooter
import com.sugarya.footer.SpinnerGridFooter
import com.sugarya.footer.SpinnerLinearFooter
import com.sugarya.footer.interfaces.IFooterItem
import com.sugarya.footer.interfaces.OnFooterItemClickListener
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

        initSpinnerLayout3_2()
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

    fun initSpinnerLayout3_2(){
        val spinnerLinearFooter = SpinnerLinearFooter(this, "状态")

//        val spinnerGridFooter = SpinnerGridFooter(this, "标签", TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, resources.displayMetrics), 4)
//        spinnerGridFooter.setPadding(0,
//                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7.5f, resources.displayMetrics).toInt(),
//                0,
//                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7.5f, resources.displayMetrics).toInt())
//
//        val spinnerDateFooter = SpinnerDateFooter(this, "选择日期")

        spinnerLayout3_2.addFooterView(spinnerLinearFooter)
//        spinnerLayout3_2.addFooterView(spinnerGridFooter)
//        spinnerLayout3_2.addFooterView(spinnerDateFooter)

        mockStatusFooter(spinnerLinearFooter, spinnerLayout3_2)
//        mockLabelFooter(spinnerGridFooter, spinnerLayout3_2)
//        mockDateFooter(spinnerDateFooter, spinnerLayout3_2)
    }


    fun mockStatusFooter(spinnerLinearFooter: SpinnerLinearFooter, spinnerLayout: SpinnerLayout){
        val statusList = arrayListOf(StatusModel("11", "status1"),
                StatusModel("12", "status2"),
                StatusModel("13", "status3"),
                StatusModel("14", "status4"),
                StatusModel("15", "status5"))
        spinnerLinearFooter.setNewData(statusList)
        spinnerLinearFooter.setOnFooterItemClickListener(object : OnFooterItemClickListener{
            override fun onClick(iFooterItem: IFooterItem) {

                spinnerLayout.back()
            }
        })
    }

    fun mockLabelFooter(spinnerGridFooter: SpinnerGridFooter, spinnerLayout: SpinnerLayout){
        val labelList = arrayListOf(
                LabelModel("21","label1"),
                LabelModel("22","label2"),
                LabelModel("23","label3"),
                LabelModel("24","label4"),
                LabelModel("25","label5"),
                LabelModel("26","label6"),
                LabelModel("27","label7")
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
