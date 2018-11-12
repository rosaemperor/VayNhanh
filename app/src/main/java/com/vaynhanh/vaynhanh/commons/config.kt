package com.vaynhanh.vaynhanh.commons

import com.vaynhanh.vaynhanh.BuildConfig

object config {

    private var HOST_ADDRESS_PROD :String = "http://m.vaynhanhchong.vn/api/"
    private var HOST_ADDRESS_DEV : String ="http://vietnam.zetafin.cn/api/"


    private var WEB_UI_URL_PROD : String = "http://m.vaynhanhchong.vn/"
    private var WEB_UI_URL_DEV : String = "http://vietnam.zetafin.cn/"



    //H5前端页面地址
    var BASE_SERVER_WEBUI_URL :String = if(BuildConfig.DEBUG) WEB_UI_URL_DEV else WEB_UI_URL_PROD
    //后台接口地址
    var HOST_ADDRESS :String = if (BuildConfig.DEBUG) HOST_ADDRESS_DEV else HOST_ADDRESS_PROD
}