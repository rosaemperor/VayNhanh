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
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.vaynhanh.vaynhanh.commons.config
import com.vaynhanh.vaynhanh.databinding.ActivityMainBinding
import com.vaynhanh.vaynhanh.parts.WVJBWebViewClient
import com.vaynhanh.vaynhanh.parts.WVWebViewClient
import com.vaynhanh.vaynhanh.utils.DialogUtils
import com.vaynhanh.vaynhanh.utils.SplashScreen
import com.vaynhanh.vaynhanh.viewModels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity() {
   lateinit var binding: ActivityMainBinding
    lateinit var client: WVWebViewClient
    lateinit var viewModel : MainViewModel
     var mCurrentPhotoPath: String? = null
//    var dontGrantedPermissions: MutableList<String> = ArrayList()
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
        viewModel.getUpdateMessage(this@MainActivity)
    }











    //按键处理
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK ){
            if(client.isAnswerBack && webView.canGoBack()){
                webView.goBack()
                return true
            }
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { p0 ->
                var firebaseToken = p0!!.token
                Log.d("TAG","$firebaseToken")
            }

        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
       client.callHandler("checkIsRootPage")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_CANCELED) client.onActivityResult(data)
        if (requestCode == client.ACTIVITYFOROMCLIENT && resultCode == RESULT_OK){
            client.onActivityResult(data)
        }
//        if (data ==null || resultCode != RESULT_OK) return


    }

    /**
     * 谁请求的谁处理
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.isEmpty()) return
        when(requestCode){
            client.CAMERA_REQUEST_CODE -> client.onRequestPermissionsResult(requestCode,permissions,grantResults)
            client.DATA_PERMISSIONS -> client.onRequestPermissionsResult(requestCode,permissions,grantResults)
            client.READ_PHONE_CODE -> client.onRequestPermissionsResult(requestCode,permissions,grantResults)
        }


    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = filesDir
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    override fun onNewIntent(intent: Intent?) {
        Log.d("TAG","SSSintent")
        super.onNewIntent(intent)
    }

}
