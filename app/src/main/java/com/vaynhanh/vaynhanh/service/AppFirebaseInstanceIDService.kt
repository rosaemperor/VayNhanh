package com.vaynhanh.vaynhanh.service

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AppFirebaseInstanceIDService : FirebaseMessagingService(){
    override fun onNewToken(p0: String?) {
        Log.d("newToken","$p0")
        super.onNewToken(p0)
    }

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
    }
}