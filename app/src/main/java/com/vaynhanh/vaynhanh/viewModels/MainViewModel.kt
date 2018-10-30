package com.vaynhanh.vaynhanh.viewModels

import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.view.View
import com.vaynhanh.vaynhanh.base.BaseViewModel
import com.vaynhanh.vaynhanh.databinding.ActivityMainBinding
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
    fun reloadClick(view : View){
        var binding = DataBindingUtil.findBinding<ActivityMainBinding>(view)
        binding!!.webView.reload()
    }

}