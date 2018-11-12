package com.vaynhanh.vaynhanh.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import java.io.File

object BitmapUtils {
    fun getFitSampleBitmap(file_path: String, width: Int, height: Int): Bitmap? {
        if (isFileExists(file_path)) {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(file_path, options)
            options.inSampleSize = calculateInSampleSize(options, width, height)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeFile(file_path, options)
        } else {
            return null
        }
    }

    private fun isFileExists(path: String?): Boolean {
        if (TextUtils.isEmpty(path)) {
            return false
        }
        val f = File(path!!)
        return f.exists()
    }

    fun calculateInSampleSize(
            options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}