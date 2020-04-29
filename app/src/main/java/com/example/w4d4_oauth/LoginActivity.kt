package com.example.w4d4_oauth

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.Auth0
import com.auth0.android.Auth0Exception
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.provider.AuthCallback
import com.auth0.android.provider.VoidCallback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CLEAR_CREDENTIALS = "com.auth0.CLEAR_CREDENTIALS"
        const val EXTRA_ACCESS_TOKEN = "com.auth0.ACCESS_TOKEN"
    }

    private lateinit var auth0: Auth0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginButton.setOnClickListener {
            login()
        }
        auth0 = Auth0(this)
        auth0.isOIDCConformant = true

        //Check if the activity was launched to log the user out
        if (intent.getBooleanExtra(EXTRA_CLEAR_CREDENTIALS, false)) {
            logout();
        }
    }

    fun login() {
        WebAuthProvider.login(auth0)
            .withScheme("demo")
            .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
            .start(this, object : AuthCallback {
                override fun onSuccess(credentials: Credentials) {
                    runOnUiThread {
                        val intent = Intent(
                            this@LoginActivity,
                            MainActivity::class.java
                        )
                        intent.putExtra(EXTRA_ACCESS_TOKEN, credentials.accessToken)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onFailure(dialog: Dialog) {
                    runOnUiThread { dialog.show() }
                }

                override fun onFailure(exception: AuthenticationException?) {
                    runOnUiThread {
                        exception?.printStackTrace()
                    }
                }

            })
    }

    fun logout() {
        WebAuthProvider.logout(auth0)
            .withScheme("demo")
            .start(this, object : VoidCallback {
                override fun onSuccess(payload: Void?) {}
                override fun onFailure(error: Auth0Exception?) { //Log out canceled, keep the user logged in
                    showNextActivity()
                }
            })
    }

    private fun showNextActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}