package com.jmlb0003.dynamicfeatures.dynamicfeaturesutils

import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.jmlb0003.dynamicfeatures.dynamicfeaturesutils.ModulesContract.BancontactContract
import com.jmlb0003.dynamicfeatures.dynamicfeaturesutils.ModulesContract.InvestmentsContract
import com.jmlb0003.dynamicfeatures.dynamicfeaturesutils.ModulesContract.ZoomitContract

class InstallRequestListener(
        private val loadingStateCallback: (Int, Int, String) -> Unit,
        private val onUserConfirmationCallback: (SplitInstallSessionState) -> Unit,
        private val onModuleReadyToLoad: (ModulesContract) -> Unit,
        private val onModulesSuccessfulInstalled: (String) -> Unit,
        private val onErrorCallback: (String) -> Unit
) : SplitInstallStateUpdatedListener {

    override fun onStateUpdate(state: SplitInstallSessionState) {
        val names = state.moduleNames().joinToString(" - ")
        when (state.status()) {
            SplitInstallSessionStatus.DOWNLOADING -> {
                //  In order to see this, the application has to be uploaded to the Play Store.
                sendLoadingStateCallback(state, "Downloading $names")
            }
            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                /*
                  This may occur when attempting to download a sufficiently large module.

                  In order to see this, the application has to be uploaded to the Play Store.
                  Then features can be requested until the confirmation path is triggered.
                 */
                onUserConfirmationCallback(state)
            }
            SplitInstallSessionStatus.INSTALLED -> handleModuleInstalled(state, names)
            SplitInstallSessionStatus.INSTALLING -> sendLoadingStateCallback(state, "Installing $names")
            SplitInstallSessionStatus.CANCELED,
            SplitInstallSessionStatus.FAILED -> {
                onErrorCallback("Error: ${state.errorCode()} for module ${state.moduleNames()}")
            }
        }
    }

    private fun sendLoadingStateCallback(state: SplitInstallSessionState, message: String) {
        loadingStateCallback(
                state.totalBytesToDownload().toInt(),
                state.bytesDownloaded().toInt(),
                message)
    }

    private fun handleModuleInstalled(state: SplitInstallSessionState, moduleNames: String) {
        if (state.moduleNames().size == 1) {
            val contract = when (moduleNames) {
                ZoomitContract.name -> ZoomitContract
                InvestmentsContract.name -> InvestmentsContract
                BancontactContract.name -> BancontactContract
                else -> throw IllegalArgumentException("The module $moduleNames is not valid")
            }
            onModuleReadyToLoad(contract)
        } else {
            onModulesSuccessfulInstalled(moduleNames)
        }


    }
}
