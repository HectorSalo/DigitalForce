package com.skysam.hchirinos.digitalforce.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.skysam.hchirinos.digitalforce.BuildConfig
import com.skysam.hchirinos.digitalforce.R
import java.io.FileNotFoundException
import java.util.*
import kotlin.math.ceil
import kotlin.math.max

/**
 * Created by Hector Chirinos on 30/05/2022.
 */

object Classes {
    fun getEnviroment(): String {
        return BuildConfig.BUILD_TYPE
    }

    fun convertDoubleToString(value: Double): String {
        return String.format(Locale.GERMANY, "%,.2f", value)
    }

    fun close(view: View) {
        val imn = DigitalForce.DigitalForce.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imn.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun reduceBitmap(
        uri: String?,
        maxAncho: Int,
        maxAlto: Int
    ): Bitmap? {
        return try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(
                DigitalForce.DigitalForce.getContext().contentResolver.openInputStream(Uri.parse(uri)),
                null, options
            )
            options.inSampleSize = max(
                ceil(options.outWidth / maxAncho.toDouble()),
                ceil(options.outHeight / maxAlto.toDouble())
            ).toInt()
            options.inJustDecodeBounds = false
            BitmapFactory.decodeStream(
                DigitalForce.DigitalForce.getContext().contentResolver
                    .openInputStream(Uri.parse(uri)), null, options
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Toast.makeText(DigitalForce.DigitalForce.getContext(), R.string.error_image_notfound, Toast.LENGTH_SHORT).show()
            null
        }
    }
}