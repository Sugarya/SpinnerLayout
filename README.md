# SpinnerLayout

## Description
This is a custom library called SpinnerLayout in Android Application


## Adding to project

```
dependencies {
    implementation 'com.sugarya:spinnerlayout:0.1.1'
}
```

## Simple usage
How to use it with kotlin

```
    <com.sugarya.SpinnerLayout
        android:id="@+id/spinnerLayout2"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="160dp"
        app:spinnerHeight="45dp"
        app:firstText="status"
        app:secondText="label"
        >

        <com.sugarya.footer.SpinnerLinearFooter
            android:id="@+id/linearFooter2"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:linearItemHeight="40dp"
            android:background="@color/bg_light_gray_footer"
            />

        <com.sugarya.footer.SpinnerGridFooter
            android:id="@+id/gridFooter2"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:paddingTop="7.5dp"
            android:paddingBottom="7.5dp"
            android:background="@color/bg_light_gray_footer"
            app:gridSpanCount="4"
            app:gridItemHeight="45dp"
            />

    </com.sugarya.SpinnerLayout>

```

```
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
```

## License
Apache-2.0

