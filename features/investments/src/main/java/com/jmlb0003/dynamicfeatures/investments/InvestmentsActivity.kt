package com.jmlb0003.dynamicfeatures.investments

import android.os.Bundle
import com.jmlb0003.dynamicfeatures.BaseActivity

class InvestmentsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feature_investments)
        setTitle(R.string.title_investments)
    }
}
