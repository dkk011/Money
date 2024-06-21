package com.tukorea.Fearow

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.tukorea.Fearow.R
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutionException

class ExchangeActivity : AppCompatActivity() {
    private val currencyList = arrayOf("KRW", "USD", "EUR", "CNY", "GBP", "AUD", "THB", "VND", "CAD", "HKD", "NZD", "TWD")
    private lateinit var etFrom: EditText
    private lateinit var tvTo: TextView
    private lateinit var btnExchange: Button
    private lateinit var refreshButton: ImageView
    private val fromTo = arrayOfNulls<String>(2)
    private var currencyRate = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exchange)

        etFrom = findViewById(R.id.amountInput)
        tvTo = findViewById(R.id.resultText)
        btnExchange = findViewById(R.id.convertButton)
        refreshButton = findViewById(R.id.refreshButton)

        val spinnerFrom: Spinner = findViewById(R.id.spinnerFrom)
        val spinnerTo: Spinner = findViewById(R.id.spinnerTo)

        val adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, currencyList)
        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter

        val baseCurrency = intent.getStringExtra("BASE_CURRENCY")
        val baseCurrencyIndex = currencyList.indexOf(baseCurrency)
        spinnerFrom.setSelection(baseCurrencyIndex)

        spinnerFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                fromTo[0] = currencyList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                fromTo[1] = currencyList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnExchange.setOnClickListener {
            try {
                currencyRate = Task().execute(*fromTo).get()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }

            val input = etFrom.text.toString().toDouble()
            val result = Math.round(input * currencyRate * 100.0) / 100.0
            val resultText = "$result ${fromTo[1]}"

            tvTo.text = resultText
        }

        refreshButton.setOnClickListener {
            etFrom.text.clear()
            tvTo.text = ""
            spinnerFrom.setSelection(baseCurrencyIndex)
            spinnerTo.setSelection(0)
        }
    }

    private inner class Task : AsyncTask<String, Void, Double>() {
        override fun doInBackground(vararg params: String): Double {
            val fromCurrency = params[0]
            val toCurrency = params[1]
            val apiKey = "fca_live_BVlsyU8lY3wxaCJWCYYcfZuTfacCB3aBaZpNhLQW"
            val urlString = "https://api.currencyapi.com/v3/latest?apikey=$apiKey&base_currency=$fromCurrency&currencies=$toCurrency"

            try {
                val url = URL(urlString)
                val urlConnection = url.openConnection() as HttpURLConnection
                try {
                    val reader = InputStreamReader(urlConnection.inputStream)
                    val data = reader.readText()
                    val jsonObject = JSONObject(data)
                    val rate = jsonObject.getJSONObject("data").getJSONObject(toCurrency).getDouble("value")
                    return rate
                } finally {
                    urlConnection.disconnect()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return 0.0
            }
        }
    }
}