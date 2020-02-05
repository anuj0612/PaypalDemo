package com.android.paypaldemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.content.Intent
import java.math.BigDecimal
import android.app.Activity
import org.json.JSONException
import android.util.Log
import com.paypal.android.sdk.payments.*
import com.paypal.android.sdk.payments.PayPalService


class MainActivity : AppCompatActivity() {
    var amount: Int = 0
    var amt: String = ""
    lateinit var etAmount: EditText
    lateinit var btnPay: Button
    val PAYPAL_REQUEST_CODE = 123
    lateinit var config: PayPalConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etAmount = findViewById(R.id.etAmount) as EditText
        btnPay = findViewById(R.id.btnPay) as Button

        //Paypal Configuration Object
        config = PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID)

        val intent = Intent(this, PayPalService::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        startService(intent)

        btnPay.setOnClickListener {
            getPayment()
        }
    }

    private fun getPayment() {
        if (etAmount.equals("")) {
            etAmount.error = "Please enter amount."
        } else {
            amt = etAmount.text.toString().trim()
        }

        //Creating a paypalpayment
        val payment = PayPalPayment(
            BigDecimal(amt), "INR", "Anuj mourya Paypal Demo",
            PayPalPayment.PAYMENT_INTENT_SALE
        )

        //Creating Paypal Payment activity intent
        val intent = Intent(this, PaymentActivity::class.java)

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                val confirm =
                    data!!.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        val paymentDetails = confirm.toJSONObject().toString(4)
                        Log.i("paymentExample", paymentDetails)

                        //Starting a new activity for the payment details and also putting the payment details with intent
                        startActivity(
                            Intent(this, ConfirmationActivity::class.java)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", amt)
                        )

                    } catch (e: JSONException) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e)
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.")
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                    "paymentExample",
                    "An invalid Payment or PayPalConfiguration was submitted. Please see the docs."
                )
            }
        }
    }

    public override fun onDestroy() {
        stopService(Intent(this, PayPalService::class.java))
        super.onDestroy()
    }

}

