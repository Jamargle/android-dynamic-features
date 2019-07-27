package com.jmlb0003.dynamicfeatures.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.jmlb0003.dynamicfeatures.R
import com.jmlb0003.dynamicfeatures.dynamicfeaturesutils.DynamicModuleHandler
import com.jmlb0003.dynamicfeatures.hideKeyboard
import com.jmlb0003.dynamicfeatures.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.buttons
import kotlinx.android.synthetic.main.activity_login.login_bancontact
import kotlinx.android.synthetic.main.activity_login.login_button
import kotlinx.android.synthetic.main.activity_login.login_investments
import kotlinx.android.synthetic.main.activity_login.password
import kotlinx.android.synthetic.main.activity_login.progress
import kotlinx.android.synthetic.main.activity_login.progress_bar
import kotlinx.android.synthetic.main.activity_login.progress_text
import kotlinx.android.synthetic.main.activity_login.username

class LoginActivity : AppCompatActivity() {

    // region setup for InstallRequestListener
    private val loadingStateListener: (Int, Int, String) -> Unit = { total, current, loadingMessage ->
        displayLoadingState(total, current, loadingMessage)
    }
    private val onErrorCallback: (String) -> Unit = { errorMessage ->
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }
    // endregion
    private val modulesHandler: DynamicModuleHandler by lazy {
        DynamicModuleHandler(
                manager = SplitInstallManagerFactory.create(this),
                installingModuleStateCallback = loadingStateListener,
                installingModuleErrorCallback = onErrorCallback
        )
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initViews()
    }

    override fun onResume() {
        // Listener can be registered even without directly triggering a download.
        modulesHandler.registerListeners()
        super.onResume()
    }

    override fun onPause() {
        // Make sure to dispose of the installRequestListener once it's no longer needed.
        modulesHandler.removeListeners()
        super.onPause()
    }

    private fun initViews() {
        username.afterTextChanged { text ->
            login_button.isEnabled = text.isNotBlank() && password.text.toString().isNotBlank()
        }
        password.afterTextChanged { text ->
            login_button.isEnabled = text.isNotBlank() && username.text.toString().isNotBlank()
        }

        login_button.setOnClickListener {
            hideKeyboard()
            displayProgress(true)
            Handler().postDelayed({
                displayProgress(false)
                goToMainActivity()
            }, 2000)
        }

        login_bancontact.setOnClickListener {
            hideKeyboard()
            displayProgress(true)
            // TODO Download bancontact module in background
            Toast.makeText(this, "Download bancontact module in background", Toast.LENGTH_SHORT).show()

            Handler().postDelayed({
                displayProgress(false)
//                goToMainActivity()
            }, 2000)
        }

        login_investments.setOnClickListener {
            hideKeyboard()
            displayProgress(true)

            // TODO Download investments module in background
            Toast.makeText(this, "Download investments module in background", Toast.LENGTH_SHORT).show()

            Handler().postDelayed({
                displayProgress(false)
//                goToMainActivity()
            }, 2000)
        }
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun displayLoadingState(
            totalProgress: Int,
            currentProgress: Int,
            message: String
    ) {
        displayProgress(true, message)
        with(progress_bar) {
            max = totalProgress
            progress = currentProgress
        }
    }

    private fun displayProgress(isVisible: Boolean = true, message: String? = null) {
        progress.visibility = if (isVisible) {
            progress_text.text = message
            buttons.visibility = View.GONE
            View.VISIBLE
        } else {
            buttons.visibility = View.VISIBLE
            View.GONE
        }
    }
}

private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
