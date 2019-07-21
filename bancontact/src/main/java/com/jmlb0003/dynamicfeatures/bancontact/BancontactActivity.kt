package com.jmlb0003.dynamicfeatures.bancontact

import android.os.Bundle
import com.jmlb0003.dynamicfeatures.BaseActivity

class BancontactActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feature_bancontact)
        setTitle(R.string.title_bancontact)
    }
}
