# SpinnerLayout

## Description
This is a custom library called SpinnerLayout in Android Application


## Adding to project
```
dependencies {
    implementation 'com.sugarya:spinnerlayout:0.2.25'
}
```

## Examples
![spinner display](https://upload-images.jianshu.io/upload_images/1933990-ce584265aabdb7bb.gif?imageMogr2/auto-orient/strip)

## Simple usage
How to use it with kotlin

```
    <com.sugarya.SpinnerLayout
        android:id="@+id/spinnerLayout3"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="240dp"
        app:spinnerHeight="45dp"
        app:touchOutsideCanceled="true"
        >

        <com.sugarya.footer.SpinnerLinearFooter
            android:id="@+id/linearFooter3"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@color/bg_light_gray_footer"
            app:itemHeightLinear="40dp"
            app:textLinear="状态"
            />

        <com.sugarya.footer.SpinnerGridFooter
            android:id="@+id/gridFooter3"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:paddingTop="7.5dp"
            android:paddingBottom="7.5dp"
            android:background="@color/bg_light_gray_footer"
            app:spanCountGrid="4"
            app:itemHeightGrid="45dp"
            app:textGrid="标签"
            />

        <com.sugarya.footer.SpinnerDateFooter
            android:id="@+id/dateFooter3"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@color/bg_light_gray_footer"
            app:textDate="选择日期"
            />

    </com.sugarya.SpinnerLayout>

```

```
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
```

## License
Apache-2.0

## Thanks
[Bigkoo/Android-PickerView](https://github.com/Bigkoo/Android-PickerView)

