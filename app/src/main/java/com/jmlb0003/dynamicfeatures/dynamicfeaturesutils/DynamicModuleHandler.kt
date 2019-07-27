package com.jmlb0003.dynamicfeatures.dynamicfeaturesutils

import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.jmlb0003.dynamicfeatures.dynamicfeaturesutils.ModulesContract.BancontactContract
import com.jmlb0003.dynamicfeatures.dynamicfeaturesutils.ModulesContract.InvestmentsContract
import com.jmlb0003.dynamicfeatures.dynamicfeaturesutils.ModulesContract.ZoomitContract

class DynamicModuleHandler(
        private val manager: SplitInstallManager,
        installingModuleStateCallback: (Int, Int, String) -> Unit,
        installingModuleUserConfirmationCallback: (SplitInstallSessionState) -> Unit = {},
        installingModuleOnModuleReadyToLoad: (ModulesContract) -> Unit = {},
        installingModuleOnModulesSuccessfulInstalled: (String) -> Unit = {},
        installingModuleErrorCallback: (String) -> Unit = {}
) {
    /** Listener used to handle changes in state for install requests. */
    private val installRequestListener = InstallRequestListener(
            installingModuleStateCallback,
            installingModuleUserConfirmationCallback,
            installingModuleOnModuleReadyToLoad,
            installingModuleOnModulesSuccessfulInstalled,
            installingModuleErrorCallback
    )

    fun registerListeners() {
        manager.registerListener(installRequestListener)
    }

    fun removeListeners() {
        manager.unregisterListener(installRequestListener)
    }

    fun installModule(
            moduleContract: ModulesContract,
            onExistingModuleCallback: () -> Unit,
            onCompleteCallback: (() -> Unit)? = null
    ) {
        // Skip loading if the module already is installed. Perform success action directly.
        if (manager.installedModules.contains(moduleContract.name)) {
            onExistingModuleCallback()
        } else {
            // Create request to install a feature module by name.
            val request = SplitInstallRequest.newBuilder()
                    .addModule(moduleContract.name)
                    .build()
            manager.startInstall(request)
                    .addOnCompleteListener {
                        onCompleteCallback?.invoke()
                    }
        }
    }

    fun installAllModules(
            onSuccessListener: (List<String>) -> Unit,
            onErrorListener: (List<String>) -> Unit
    ) {
        // Request all known modules to be downloaded in a single session.
        val moduleNames = listOf(InvestmentsContract.name, ZoomitContract.name, BancontactContract.name)
        val requestBuilder = SplitInstallRequest.newBuilder()

        moduleNames.forEach { name ->
            if (!manager.installedModules.contains(name)) {
                requestBuilder.addModule(name)
            }
        }
        val request = requestBuilder.build()

        manager.startInstall(request)
                .addOnSuccessListener {
                    onSuccessListener(request.moduleNames)
                }.addOnFailureListener {
                    onErrorListener(request.moduleNames)
                }
    }

    fun installAllModulesDeferred(
            onSuccessListener: (List<String>) -> Unit,
            onErrorListener: (List<String>) -> Unit
    ) {
        // Request all known modules to be downloaded in a single session.
        val modules = listOf(InvestmentsContract.name, ZoomitContract.name, BancontactContract.name)
        manager.deferredInstall(modules)
                .addOnSuccessListener {
                    onSuccessListener(modules)
                }.addOnFailureListener {
                    onErrorListener(modules)
                }
    }

    fun requestUninstallAllModules(
            onSuccessListener: (List<String>) -> Unit
    ) {
        val installedModules = manager.installedModules.toList()
        manager.deferredUninstall(installedModules)
                .addOnSuccessListener {
                    onSuccessListener(installedModules)
                }
    }
}