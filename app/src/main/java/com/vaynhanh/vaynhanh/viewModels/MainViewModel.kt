package com.vaynhanh.vaynhanh.viewModels

import android.content.Context
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.view.View
import club.rosaemperor.myeyesopen.http.RetrofitUtil
import com.vaynhanh.vaynhanh.app.VayNhanhApplication
import com.vaynhanh.vaynhanh.base.BaseViewModel
import com.vaynhanh.vaynhanh.databinding.ActivityMainBinding
import com.vaynhanh.vaynhanh.http.beans.UpdateEntity
import com.vaynhanh.vaynhanh.http.beans.UpdateResultEntity
import com.vaynhanh.vaynhanh.parts.WVWebViewClient
import com.vaynhanh.vaynhanh.utils.DialogUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : BaseViewModel(){
    var webViewVisiable : ObservableField<Int> = ObservableField()
    var errorViewVisiable :  ObservableField<Int> = ObservableField()
    override fun initViewModel() {
        webViewVisiable.set(View.VISIBLE)
        errorViewVisiable.set(View.GONE)
    }

    override fun initData() {
    }

    fun reload(view: View){
        var binding= DataBindingUtil.findBinding<ActivityMainBinding>(view)
        webViewVisiable.set(View.VISIBLE)
        errorViewVisiable.set(View.GONE)
        binding!!.webView.reload()
    }

    fun getUpdateMessage(context: Context){
        var updateEntity  = UpdateEntity()
        var applicationInfo = VayNhanhApplication.instance.packageManager.getApplicationInfo(VayNhanhApplication.instance.packageName ,
                PackageManager.GET_META_DATA)
        var packageInfo = VayNhanhApplication.instance.packageManager.getPackageInfo(VayNhanhApplication.instance.packageName,0)
        updateEntity.verName = packageInfo.versionName
        updateEntity.verCode = ""+packageInfo.versionCode
        updateEntity.channelCode = applicationInfo.metaData.getString("CHANNEL_CODE")
        var call = RetrofitUtil.instance.help.checkUpdate(updateEntity)
        call.enqueue(object : Callback<UpdateResultEntity> {
            override fun onFailure(call: Call<UpdateResultEntity>, t: Throwable) {

            }

            override fun onResponse(call: Call<UpdateResultEntity>, response: Response<UpdateResultEntity>) {
                if (response.body()!!.data != null){
                    DialogUtils.showUpdateDia(context)
                }

            }
        })

    }

}