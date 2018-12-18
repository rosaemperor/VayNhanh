package com.vaynhanh.vaynhanh.app

import android.app.Application
import cn.fraudmetrix.octopus.aspirit.main.OctopusManager
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import com.vaynhanh.vaynhanh.BuildConfig
import com.vaynhanh.vaynhanh.R
import com.vaynhanh.vaynhanh.utils.SharePrefenceHelper

class VayNhanhApplication : Application(){
    companion object {
        lateinit var  instance : VayNhanhApplication
        var buildCode = "20180925"
        lateinit var sAnalytics : GoogleAnalytics
        lateinit var sTracker : Tracker
    }

    val tag : String = "VayNhanhApplication"
    /**
     * 如果需要分包，请将方法置于 super.onCreate() 之前
     */
    override fun onCreate() {
        super.onCreate()
        instance = this
        initUtils()
        initPlugins()
    }

    private fun initPlugins() {
        sAnalytics= GoogleAnalytics.getInstance(this)
        sTracker = sAnalytics.newTracker(R.xml.global_tracker)
        if (BuildConfig.DEBUG) {
            OctopusManager.getInstance().init(this, "lld_vn_test", "3027bf82c461b4200b9cfcea59e22b961")

        } else {
            OctopusManager.getInstance().init(this, "lld_vn_test", "3027bf82c461b4200b9cfcea59e22b961")
        }
    }

    private fun initUtils() {
        SharePrefenceHelper.initSharePreference(applicationContext)
    }
}