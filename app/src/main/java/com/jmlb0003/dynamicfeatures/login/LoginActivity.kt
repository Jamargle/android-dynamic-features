package com.jmlb0003.dynamicfeatures.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.jmlb0003.dynamicfeatures.R
import com.jmlb0003.dynamicfeatures.hideKeyboard
import com.jmlb0003.dynamicfeatures.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.debug_text
import kotlinx.android.synthetic.main.activity_login.login_button
import kotlinx.android.synthetic.main.activity_login.password
import kotlinx.android.synthetic.main.activity_login.progress
import kotlinx.android.synthetic.main.activity_login.progress_text
import kotlinx.android.synthetic.main.activity_login.username

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initViews()
    }

    private fun initViews() {
        when (checkNotNull(intent.extras)[USER_TYPE]) {
            USER_BASIC -> getString(R.string.login_user_with_nothing)
            USER_WITH_BANCONTACT -> getString(R.string.login_user_with_bancontact)
            USER_WITH_INVESTMENTS -> getString(R.string.login_user_with_investments)
            else -> throw IllegalArgumentException("There should be a type of user")
        }.let { userTypeText ->
            debug_text.text = getString(R.string.debug_text_regarding_user_type, userTypeText)
        }

        username.afterTextChanged { text ->
            login_button.isEnabled = text.isNotBlank() && password.text.toString().isNotBlank()
        }
        password.afterTextChanged { text ->
            login_button.isEnabled = text.isNotBlank() && username.text.toString().isNotBlank()
        }
        username.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    doLogin()
                    true
                }
                else -> false
            }
        }
        password.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    doLogin()
                    true
                }
                else -> false
            }
        }

        login_button.setOnClickListener {
            doLogin()
        }
    }

    private fun doLogin() {
        hideKeyboard()
        displayProgress(true)
        Handler().postDelayed({
            goToMainActivity()
            displayProgress(false)
        }, 2000)
    }

    private fun goToMainActivity() {
        when (checkNotNull(intent.extras)[USER_TYPE]) {
            USER_BASIC -> startActivity(Intent(this, MainActivity::class.java))
            USER_WITH_BANCONTACT -> startActivity(MainActivity.openForBancontactUser(this))
            USER_WITH_INVESTMENTS -> startActivity(MainActivity.openForInvestmentsUser(this))
        }
    }

    private fun displayProgress(isVisible: Boolean = true, message: String? = null) {
        progress.visibility = if (isVisible) {
            progress_text.text = message
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    companion object {
        private const val USER_TYPE = "key:UserType"
        private const val USER_BASIC = "param:basic"
        private const val USER_WITH_BANCONTACT = "param:bancontact"
        private const val USER_WITH_INVESTMENTS = "param:investments"

        fun openForBasicUser(context: Context) =
                Intent(context, LoginActivity::class.java).apply {
                    putExtras(Bundle().apply {
                        putString(USER_TYPE, USER_BASIC)
                    })
                }

        fun openForBancontactUser(context: Context) =
                Intent(context, LoginActivity::class.java).apply {
                    putExtras(Bundle().apply {
                        putString(USER_TYPE, USER_WITH_BANCONTACT)
                    })
                }

        fun openForInvestmentsUser(context: Context) =
                Intent(context, LoginActivity::class.java).apply {
                    putExtras(Bundle().apply {
                        putString(USER_TYPE, USER_WITH_INVESTMENTS)
                    })
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
