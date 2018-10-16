package com.vaynhanh.vaynhanh.app

import android.app.Application
import com.vaynhanh.vaynhanh.utils.SharePrefenceHelper

class VayNhanhApplication : Application(){
    companion object {
        lateinit var  instance : VayNhanhApplication
        var buildCode = "20180925"
    }

    val tag : String = "KotlinApplication"
    /**
     * 如果需要分包，请将方法置于 super.onCreate() 之前
     */
    override fun onCreate() {
        super.onCreate()
        instance = this
        initUtils()
    }

    private fun initUtils() {
        SharePrefenceHelper.initSharePreference(applicationContext)
    }
}