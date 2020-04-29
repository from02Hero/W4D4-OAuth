package com.example.w4d4_oauth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        logout.setOnClickListener {
            logout()
        }
        //Obtain the token from the Intent's extras
        val accessToken = intent.getStringExtra(LoginActivity.EXTRA_ACCESS_TOKEN)
        credentials.text = accessToken
    }

    private fun logout() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra(LoginActivity.EXTRA_CLEAR_CREDENTIALS, true)
        startActivity(intent)
        finish()
    }
}
