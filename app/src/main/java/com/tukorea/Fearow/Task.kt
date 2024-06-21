package com.tukorea.Fearow

import android.os.AsyncTask
import android.util.Log
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Task : AsyncTask<String, Void, Double>() {

    private val clientKey = "fca_live_BVlsyU8lY3wxaCJWCYYcfZuTfacCB3aBaZpNhLQW"
    private var str: String? = null
    private var receiveMsg: String? = null
    private var currencyRate = 0.0

    override fun doInBackground(vararg params: String?): Double? {
        val from = params[0]
        val to = params[1]
        if (from == null || to == null) {
            return null
        }

        try {
            val url = URL("https://api.freecurrencyapi.com/v1/latest?apikey=$clientKey&currencies=$from,$to")

            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val tmp = InputStreamReader(conn.inputStream, "UTF-8")
                val reader = BufferedReader(tmp)
                val buffer = StringBuffer()

                while (reader.readLine().also { str = it } != null) {
                    buffer.append(str)
                }
                receiveMsg = buffer.toString()
                Log.e("receiveMsg : ", receiveMsg!!)

                reader.close()
            } else {
                Log.e("통신 결과", "${conn.responseCode} 에러")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val jsonParser = JsonParser()
            val obj = jsonParser.parse(receiveMsg)

            val jsonObj = obj.asJsonObject
            val curobj = jsonObj.getAsJsonObject("data")

            Log.e("parse", "$from ${curobj.get(from).toString()}")
            Log.e("parse", "$to ${curobj.get(to).toString()}")
            val a = curobj.get(from).toString().toDouble()
            val b = curobj.get(to).toString().toDouble()

            currencyRate = b / a
            Log.e("parse", "currencyRate $currencyRate")

        } catch (e: JsonParseException) {
            e.printStackTrace()
        }
        return currencyRate
    }
}