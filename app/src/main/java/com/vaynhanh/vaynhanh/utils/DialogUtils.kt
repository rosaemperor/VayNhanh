package com.vaynhanh.vaynhanh.utils

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.constraint.ConstraintLayout
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.vaynhanh.vaynhanh.R
import java.util.*

class DialogUtils {
    companion object {
        fun showUpdateDia( context: Context) {
            val updateDialog = Dialog(context)
            val layout = LayoutInflater.from(context).inflate(R.layout.dialog_update_layout, null) as ConstraintLayout
            val cancelview = layout.findViewById<ImageView>(R.id.cancel)
            val descriptionView = layout.findViewById<TextView>(R.id.description)
            cancelview.setOnClickListener { updateDialog.dismiss() }
            val updateView = layout.findViewById<TextView>(R.id.update_view)
            updateView.setOnClickListener { v ->
                val uri = Uri.parse("market://details?id=" + context.applicationContext.packageName)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.android.vending")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    context.startActivity(intent)
                } catch (exception: ActivityNotFoundException) {
                    //防止手机上未安装Google play而出现崩溃
                    Log.e("exception", "" + exception.toString())
                    Toast.makeText(v.context, "please install the Google Play at the first", Toast.LENGTH_LONG).show()
                }
            }
            updateDialog.setContentView(layout)
            updateDialog.window.setBackgroundDrawableResource(R.color.transparent)
            updateDialog.setCancelable(false)
            if ( !updateDialog.isShowing) {
                updateDialog.show()
            }


        }
    }
}