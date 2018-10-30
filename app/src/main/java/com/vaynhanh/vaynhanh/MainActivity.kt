package com.vaynhanh.vaynhanh

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import club.rosaemperor.myeyesopen.base.BaseActivity
import com.vaynhanh.vaynhanh.commons.config
import com.vaynhanh.vaynhanh.databinding.ActivityMainBinding
import com.vaynhanh.vaynhanh.parts.WVJBWebViewClient
import com.vaynhanh.vaynhanh.parts.WVWebViewClient
import com.vaynhanh.vaynhanh.utils.DialogUtils
import com.vaynhanh.vaynhanh.utils.SplashScreen
import com.vaynhanh.vaynhanh.viewModels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.ArrayList

class MainActivity : BaseActivity() {
   lateinit var binding: ActivityMainBinding
    lateinit var client: WVWebViewClient
    lateinit var viewModel : MainViewModel

    var dontGrantedPermissions: MutableList<String> = ArrayList()
    override fun initBinding() {
        SplashScreen.show(this@MainActivity)
        binding = DataBindingUtil.setContentView(this@MainActivity,R.layout.activity_main)
    }

    @SuppressLint("ResourceAsColor")
    override fun initViewModel() {
        viewModel = MainViewModel()
        binding.llNetworkError.visibility = View.GONE
        binding.swipeLayout.setColorSchemeColors(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light)
        binding.viewModel = viewModel
        binding.swipeLayout.setOnRefreshListener { binding.webView.reload() }
        binding.webView.initialze()
        binding.webView.webChromeClient = WebChromeClient()
        client = WVWebViewClient(binding.webView)
        binding.webView.webViewClient = client
        var link : Uri = Uri.parse(config.BASE_SERVER_WEBUI_URL)
        binding.webView.loadUrl(link.toString())
    }











    //按键处理
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if(keyCode == KeyEvent.KEYCODE_BACK ){
////           DialogUtils.showUpdateDia(this@MainActivity)
////            if (ActivityCompat.checkSelfPermission(
////                            this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
////
////            }else{
////                var list= ArrayList<String>()
////                list.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
////                list.add(Manifest.permission.CAMERA)
////                requestPermissions(list.toTypedArray(),client.CAMERA_REQUEST_CODE)
//////                checkPermissions(arrayListOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA))
////
////            }
//            return true
//        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
       client.callHandler("checkIsRootPage")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data ==null || resultCode != RESULT_OK) return
        if (requestCode == client.ACTIVITYFOROMCLIENT){
            client.onActivityResult(data)
        }
    }

    /**
     * 谁请求的谁处理
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when(requestCode){
            client.CAMERA_REQUEST_CODE -> client.onRequestPermissionsResult(requestCode,permissions,grantResults)
            client.DATA_PERMISSIONS -> client.onRequestPermissionsResult(requestCode,permissions,grantResults)
        }


    }


}
