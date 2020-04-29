package com.example.w4d4_oauth

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.Auth0
import com.auth0.android.Auth0Exception
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.callback.BaseCallback
import com.auth0.android.provider.AuthCallback
import com.auth0.android.provider.VoidCallback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CLEAR_CREDENTIALS = "com.auth0.CLEAR_CREDENTIALS"
        const val EXTRA_ACCESS_TOKEN = "com.auth0.ACCESS_TOKEN"
        const val EXTRA_ID_TOKEN = "com.auth0.ID_TOKEN"

        const val CODE_DEVICE_AUTHENTICATION = 22
        const val API_IDENTIFIER = "YOUR API IDENTIFIER"
    }

    private lateinit var auth0: Auth0
    private lateinit var credentialsManager: SecureCredentialsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginButton.setOnClickListener {
            login()
        }
        auth0 = Auth0(this)
        auth0.isOIDCConformant = true

        credentialsManager = SecureCredentialsManager(
            this,
            AuthenticationAPIClient(auth0),
            SharedPreferencesStorage(this)
        )

        //Check if the activity was launched to log the user out
        if (intent.getBooleanExtra(EXTRA_CLEAR_CREDENTIALS, false)) {
            logout()
        } else if (credentialsManager.hasValidCredentials()) {
            // Obtain the existing credentials and move to the next activity
            showNextActivity()
        }
    }

    fun login() {
        WebAuthProvider.login(auth0)
            .withScheme("demo")
            .withScope("openid offline_access")
//            .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
            .withAudience(API_IDENTIFIER)
            .start(this, loginCallback)
    }

    fun logout() {
        WebAuthProvider.logout(auth0)
            .withScheme("demo")
            .start(this, logoutCallback)
    }

    /**
     * Override required when setting up Local Authentication in the Credential Manager
     * Refer to SecureCredentialsManager#requireAuthentication method for more information.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (credentialsManager.checkAuthenticationResult(requestCode, resultCode)) {
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showNextActivity() {
        credentialsManager.getCredentials(object :
            BaseCallback<Credentials, CredentialsManagerException?> {
            override fun onSuccess(credentials: Credentials) { // Move to the next activity
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.putExtra(EXTRA_ACCESS_TOKEN, credentials.accessToken)
                intent.putExtra(EXTRA_ID_TOKEN, credentials.idToken)
                startActivity(intent)
                finish()
            }
            override fun onFailure(error: CredentialsManagerException?) {
                // Credentials could not be retrieved.
                finish()
            }
        })
    }

    private val logoutCallback: VoidCallback = object : VoidCallback {
        override fun onSuccess(payload: Void?) {
            credentialsManager.clearCredentials()
        }

        override fun onFailure(error: Auth0Exception) { // Log out canceled, keep the user logged in
            showNextActivity()
        }
    }

    private val loginCallback: AuthCallback = object : AuthCallback {
        override fun onSuccess(credentials: Credentials) {
            credentialsManager.saveCredentials(credentials)
            showNextActivity()
        }

        override fun onFailure(dialog: Dialog) { // Show error dialog
            runOnUiThread { dialog.show() }
        }

        override fun onFailure(exception: AuthenticationException?) { // Show error message
            runOnUiThread {
                Toast.makeText(this@LoginActivity, "Log In - Error Occurred", Toast.LENGTH_SHORT).show()
            }
        }

    }
}