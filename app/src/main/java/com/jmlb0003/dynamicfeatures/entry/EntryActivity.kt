package com.jmlb0003.dynamicfeatures.entry

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jmlb0003.dynamicfeatures.R
import com.jmlb0003.dynamicfeatures.login.LoginActivity
import kotlinx.android.synthetic.main.activity_entry.button_with_bancontact
import kotlinx.android.synthetic.main.activity_entry.button_with_investements
import kotlinx.android.synthetic.main.activity_entry.button_with_nothing

class EntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        initViews()
    }

    private fun initViews() {
        button_with_nothing.setOnClickListener { startActivity(LoginActivity.openForBasicUser(this)) }

        button_with_bancontact.setOnClickListener { startActivity(LoginActivity.openForBancontactUser(this)) }

        button_with_investements.setOnClickListener { startActivity(LoginActivity.openForInvestmentsUser(this)) }
    }
}
