package com.ssh.appdataremotedb

import android.annotation.SuppressLint
import android.os.StrictMode
import android.util.Log
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class HTTPDownload {

    private val TAG = "HTTPDownload"

    companion object {
        val STATUS_SUCCESS = 0
        val STATUS_TIMEOUT = 1
        val STATUS_FAILED = 2
    }

    inner class HTTPStatus {
        var status: Int = STATUS_FAILED
        var value: JSONObject? = null
    }

    @SuppressLint("LongLogTag")
    fun downloadUrl(url : String): HTTPStatus {
        Log.d(TAG, "downloadUrl: " + url)
        var httpStatus = HTTPStatus()

        //set policy
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        //declare variable and json object
        var text: String? = null
        var obj: JSONObject? = null
        try
        {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.setConnectTimeout(10000)
            connection.connect()

            text = connection.inputStream.use {
                it.reader().use { reader -> reader.readText() }
            }
            connection.disconnect()

            Log.d(TAG, "downloadUrl Json:" + text)
            obj = JSONObject(text)

            if(text == null || obj == null) {
                httpStatus.status = STATUS_FAILED
            }
            else {
                httpStatus.status = STATUS_SUCCESS
                httpStatus.value = obj
            }
        }
        catch (timeEx : SocketTimeoutException) {
            Log.d(TAG, "downloadUrl exception" + timeEx)
            httpStatus.status = STATUS_TIMEOUT
        }
        catch (ex: Exception) {
            val e = ex.toString()
            Log.d(TAG, "downloadUrl exception" + e)
            httpStatus.status = STATUS_FAILED
        }
        return httpStatus
    }
}