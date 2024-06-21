package com.tukorea.Fearow

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ExchangeRateFragment : Fragment() {
    private val currencyList = arrayOf(
        Pair(R.id.viewKRW, "KRW"),
        Pair(R.id.viewUSD, "USD"),
        Pair(R.id.viewJPY, "JPY"),
        Pair(R.id.viewCNY, "CNY"),
        Pair(R.id.viewVND, "VND")
    )

    private val rateTextViews = arrayOf(
        Pair(R.id.rateKRW, "KRW"),
        Pair(R.id.rateUSD, "USD"),
        Pair(R.id.rateJPY, "JPY"),
        Pair(R.id.rateCNY, "CNY"),
        Pair(R.id.rateVND, "VND")
    )

    private lateinit var refreshImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_exchange_rate, container, false)

        // Find refresh ImageView
        refreshImageView = view.findViewById(R.id.refreshImage)
        refreshImageView.setOnClickListener {
            refreshExchangeRates()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for (item in currencyList) {
            view.findViewById<View>(item.first).setOnClickListener {
                val intent = Intent(activity, ExchangeActivity::class.java)
                intent.putExtra("BASE_CURRENCY", item.second)
                startActivity(intent)
            }
        }

        // Load exchange rates and set them to TextViews
        for (rate in rateTextViews) {
            LoadExchangeRateTask(rate.second, view.findViewById(rate.first)).execute()
        }
    }

    private fun refreshExchangeRates() {
        for (rate in rateTextViews) {
            LoadExchangeRateTask(rate.second, view?.findViewById(rate.first)).execute()
        }
        Toast.makeText(activity, "환율 정보 새로고침", Toast.LENGTH_SHORT).show()
    }

    private inner class LoadExchangeRateTask(private val currency: String, private val textView: TextView?) : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {
            if (currency == "KRW") return "1.0"

            val apiKey = "fca_live_BVlsyU8lY3wxaCJWCYYcfZuTfacCB3aBaZpNhLQW"
            val urlString = "https://api.currencyapi.com/v3/latest?apikey=$apiKey&base_currency=$currency&currencies=KRW"
            try {
                val url = URL(urlString)
                val urlConnection = url.openConnection() as HttpURLConnection
                return try {
                    val reader = InputStreamReader(urlConnection.inputStream)
                    val data = reader.readText()
                    val jsonObject = JSONObject(data)
                    val rate = jsonObject.getJSONObject("data").getJSONObject("KRW").getDouble("value")
                    "1 $currency = %.2f KRW".format(rate)
                } finally {
                    urlConnection.disconnect()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return "N/A"
            }
        }

        override fun onPostExecute(result: String) {
            textView?.text = result
        }
    }
}
