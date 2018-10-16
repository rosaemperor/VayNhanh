package com.vaynhanh.vaynhanh.viewModels

import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.view.View
import com.vaynhanh.vaynhanh.base.BaseViewModel
import com.vaynhanh.vaynhanh.parts.WVWebViewClient

class MainViewModel : BaseViewModel(){
    var webViewVisiable : ObservableField<Int> = ObservableField()
    var errorViewVisiable :  ObservableField<Int> = ObservableField()
    override fun initViewModel() {
        webViewVisiable.set(View.VISIBLE)
        errorViewVisiable.set(View.GONE)
    }

    override fun initData() {
    }


}