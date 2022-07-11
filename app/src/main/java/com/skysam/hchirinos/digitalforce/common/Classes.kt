package com.skysam.hchirinos.digitalforce.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.skysam.hchirinos.digitalforce.BuildConfig
import com.skysam.hchirinos.digitalforce.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.FileNotFoundException
import java.util.*
import kotlin.math.ceil
import kotlin.math.max

/**
 * Created by Hector Chirinos on 30/05/2022.
 */

object Classes {
    fun getValueWeb(): Flow<String> {
        return callbackFlow {
            var valor: String? = null
            val url = "http://www.bcv.org.ve/"

            withContext(Dispatchers.IO) {
                try {
                    val doc = Jsoup.connect(url).get()
                    val data = doc.select("div#dolar")
                    valor = data.select("strong").last()?.text()
                } catch (e: Exception) {
                    Log.e("Error", e.toString())
                }
            }
            if (valor != null) {
                val valorNeto = valor?.replace(",", ".")
                val valorCotizacion = valorNeto!!.toFloat()
                val valorRounded = String.format(Locale.US, "%.2f", valorCotizacion)
                trySend(valorRounded)
            } else {
                trySend("1,00")
            }

            awaitClose { }
        }
    }

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