package com.jmlb0003.dynamicfeatures.dynamicfeaturesutils

import com.jmlb0003.dynamicfeatures.BuildConfig

private const val PACKAGE_NAME = BuildConfig.APPLICATION_ID

/**
 * Contains class names of the module´s entry activities to be able to start the activities
 * from the app module.
 */
sealed class ModulesContract(
        val name: String,
        val entryActivityClassName: String
) {

    object BancontactContract : ModulesContract(
            "bancontact",
            "$PACKAGE_NAME.bancontact.BancontactActivity"
    )

    object InvestmentsContract : ModulesContract(
            "investments",
            "$PACKAGE_NAME.investments.InvestmentsActivity"
    )

    object ZoomitContract : ModulesContract(
            "zoomit",
            "$PACKAGE_NAME.zoomit.ZoomitActivity"
    )
}