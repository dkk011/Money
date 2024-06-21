package com.tukorea.Fearow

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class ExchangeRateFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exchange_rate, container, false)

        val buttons = arrayOf(
            Pair(R.id.buttonKRW, "KRW"),
            Pair(R.id.buttonUSD, "USD"),
            Pair(R.id.buttonEUR, "EUR"),
            Pair(R.id.buttonCNY, "CNY"),
            Pair(R.id.buttonGBP, "GBP"),
            Pair(R.id.buttonAUD, "AUD"),
            Pair(R.id.buttonTHB, "THB"),
            Pair(R.id.buttonVND, "VND"),
            Pair(R.id.buttonCAD, "CAD"),
            Pair(R.id.buttonHKD, "HKD"),
            Pair(R.id.buttonNZD, "NZD"),
            Pair(R.id.buttonTWD, "TWD")
        )

        for (button in buttons) {
            view.findViewById<Button>(button.first).setOnClickListener {
                val intent = Intent(activity, ExchangeActivity::class.java)
                intent.putExtra("BASE_CURRENCY", button.second)
                startActivity(intent)
            }
        }

        return view
    }
}
