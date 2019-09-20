package com.vaynhanh.vaynhanh.ui

import android.databinding.DataBindingUtil
import club.rosaemperor.myeyesopen.base.BaseActivity
import com.vaynhanh.vaynhanh.R
import com.vaynhanh.vaynhanh.databinding.ActivityTestLayoutBinding
import com.vaynhanh.vaynhanh.viewModels.TestViewModel

class TestActivity : BaseActivity(){
    lateinit var binding: ActivityTestLayoutBinding
    lateinit var viewModel : TestViewModel
    override fun initBinding() {
        binding = DataBindingUtil.setContentView(this@TestActivity ,R.layout.activity_test_layout)
    }

    override fun initViewModel() {
        viewModel= TestViewModel()
    }
}