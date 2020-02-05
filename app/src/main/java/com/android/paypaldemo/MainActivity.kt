package com.android.paypaldemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    var amount: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val etAmount = findViewById(R.id.etAmount) as EditText
        val btnPay = findViewById(R.id.btnPay) as Button

        btnPay.setOnClickListener {
            if (etAmount.equals("")) {
                etAmount.error = "Please enter amount."
            } else {
                val amt: String = etAmount.text.toString().trim()
                amount = amt.toInt()
            }
        }
    }
}
