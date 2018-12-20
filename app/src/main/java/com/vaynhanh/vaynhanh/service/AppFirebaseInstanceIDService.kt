package com.vaynhanh.vaynhanh.service

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vaynhanh.vaynhanh.MainActivity

class AppFirebaseInstanceIDService : FirebaseMessagingService(){
    override fun onNewToken(p0: String?) {
        Log.d("newToken","$p0")
        super.onNewToken(p0)
    }

    override fun onMessageReceived(p0: RemoteMessage?) {
        Log.d("TAG","${p0!!.messageType}")
        var intent = Intent(this@AppFirebaseInstanceIDService,MainActivity::class.java )
        startActivity(intent)
        super.onMessageReceived(p0)
    }
}