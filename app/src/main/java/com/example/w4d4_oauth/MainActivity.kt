package com.example.w4d4_oauth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.main_activity.*
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

    companion object {
        private const val API_URL = "YOUR API URL"
    }

    private var accessToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        logout.setOnClickListener {
            logout()
        }

        callAPIWithTokenButton.setOnClickListener {
            callAPI(true)
        }

        callAPIWithoutTokenButton.setOnClickListener {
            callAPI(false)
        }

        //Obtain the token from the Intent's extras
        accessToken = intent.getStringExtra(LoginActivity.EXTRA_ACCESS_TOKEN)
    }

    private fun logout() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra(LoginActivity.EXTRA_CLEAR_CREDENTIALS, true)
        startActivity(intent)
        finish()
    }

    private fun callAPI(sendToken: Boolean) {
        val reqBuilder = Request.Builder()
            .get()
            .url(API_URL)
        if (sendToken) {
            if (accessToken == null) {
                Toast.makeText(
                    this@MainActivity,
                    "Token not found. Log in first.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            reqBuilder.addHeader("Authorization", "Bearer $accessToken")
        }
        val client = OkHttpClient()
        val request: Request = reqBuilder.build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "An error occurred" + e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MainActivity, "API call success!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "API call failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
