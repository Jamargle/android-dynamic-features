package com.jmlb0003.dynamicfeatures.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.jmlb0003.dynamicfeatures.R
import com.jmlb0003.dynamicfeatures.dynamicfeaturesutils.DynamicModuleHandler
import com.jmlb0003.dynamicfeatures.dynamicfeaturesutils.ModulesContract
import com.jmlb0003.dynamicfeatures.dynamicfeaturesutils.ModulesContract.BancontactContract
import com.jmlb0003.dynamicfeatures.dynamicfeaturesutils.ModulesContract.InvestmentsContract
import com.jmlb0003.dynamicfeatures.dynamicfeaturesutils.ModulesContract.ZoomitContract
import kotlinx.android.synthetic.main.activity_main.btn_install_all_now
import kotlinx.android.synthetic.main.activity_main.btn_request_uninstall
import kotlinx.android.synthetic.main.activity_main.go_to_bancontact_button
import kotlinx.android.synthetic.main.activity_main.go_to_investments_button
import kotlinx.android.synthetic.main.activity_main.go_to_zoomit_button
import kotlinx.android.synthetic.main.activity_main.installed_modules
import kotlinx.android.synthetic.main.activity_main.progress_bar
import kotlinx.android.synthetic.main.activity_main.progress_bar_view
import kotlinx.android.synthetic.main.activity_main.progress_text

/** Activity that displays buttons and handles loading of feature modules. */
class MainActivity : AppCompatActivity() {

    // region setup for InstallRequestListener
    private val loadingStateListener: (Int, Int, String) -> Unit = { total, current, loadingMessage ->
        displayLoadingState(total, current, loadingMessage)
    }
    private val onUserConfirmationCallback: (SplitInstallSessionState) -> Unit = { state ->
        startIntentSender(state.resolutionIntent()?.intentSender, null, 0, 0, 0)
    }
    private val onErrorCallback: (String) -> Unit = { errorMessage ->
        toastAndLog(errorMessage)
        updateInstalledModulesLabel()
    }
    private val pendingInstallCallback: (String) -> Unit = { message ->
        toastAndLog(message)
        updateInstalledModulesLabel()
    }
    // endregion

    private val clickListener by lazy {
        View.OnClickListener {
            when (it.id) {
                R.id.go_to_investments_button -> loadAndLaunchModule(InvestmentsContract)
                R.id.go_to_zoomit_button -> loadAndLaunchModule(ZoomitContract)
                R.id.go_to_bancontact_button -> loadAndLaunchModule(BancontactContract)
                R.id.btn_install_all_now -> installAllFeaturesNow()
                R.id.btn_request_uninstall -> requestUninstall()
            }
        }
    }

    private val modulesHandler: DynamicModuleHandler by lazy {
        DynamicModuleHandler(
            manager = SplitInstallManagerFactory.create(this),
            installingModuleStateCallback = loadingStateListener,
            installingModulePendingCallback = pendingInstallCallback,
            installingModuleUserConfirmationCallback = onUserConfirmationCallback,
            installingModuleOnModuleReadyToLoad = { contract ->
                onModuleSuccessfulLoad(contract)
                updateInstalledModulesLabel()
            },
            installingModuleOnModulesSuccessfulInstalled = { onModulesSuccessfulLoad() },
            installingModuleErrorCallback = onErrorCallback
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()
        checkModulesToBeInstalled()
    }

    private fun checkModulesToBeInstalled() {
        when (intent.extras?.get(USER_TYPE)) {
            USER_WITH_BANCONTACT -> {
                modulesHandler.installModuleDeferred(
                    BancontactContract,
                    onCompleteCallback = { Toast.makeText(this, "Bancontact install Complete!!", Toast.LENGTH_LONG).show() },
                    onSuccessCallback = { Toast.makeText(this, "Bancontact install Success!!", Toast.LENGTH_LONG).show() },
                    onFailureCallback = { Toast.makeText(this, "Bancontact install Failure because ${it.message}!!", Toast.LENGTH_LONG).show() })
            }
            USER_WITH_INVESTMENTS -> {
                modulesHandler.installModuleDeferred(InvestmentsContract,
                    onCompleteCallback = { Toast.makeText(this, "Investments install Complete!!", Toast.LENGTH_LONG).show() },
                    onSuccessCallback = { Toast.makeText(this, "Investments install Success!!", Toast.LENGTH_LONG).show() },
                    onFailureCallback = { Toast.makeText(this, "Investments install Failure because ${it.message}!!", Toast.LENGTH_LONG).show() })
            }
        }
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

    private fun initializeViews() {
        go_to_investments_button.setOnClickListener(clickListener)
        go_to_zoomit_button.setOnClickListener(clickListener)
        go_to_bancontact_button.setOnClickListener(clickListener)
        btn_install_all_now.setOnClickListener(clickListener)
        btn_request_uninstall.setOnClickListener(clickListener)
        updateInstalledModulesLabel()
    }

    /**
     * Load a feature by module name.
     * @param moduleContract Contains the name of the module to load.
     */
    private fun loadAndLaunchModule(moduleContract: ModulesContract) {
        updateProgressMessage("Loading module ${moduleContract.name}")

        modulesHandler.installModule(moduleContract, {
            onModuleSuccessfulLoad(moduleContract, launch = true)
            return@installModule
        })
    }

    /**
     * Install all features but do not launch any of them.
     */
    private fun installAllFeaturesNow() {
        modulesHandler.installAllModules({ moduleNames ->
            toastAndLog("Loading $moduleNames")
        }, { moduleNames ->
            toastAndLog("Failed loading $moduleNames")
        })
    }

    /**
     * Request uninstall of all features.
     */
    private fun requestUninstall() {
        toastAndLog("Requesting uninstall of all modules." +
            "This will happen at some point in the future.")

        modulesHandler.requestUninstallAllModules { modules ->
            toastAndLog("Uninstalling $modules")
        }
    }

    /**
     * Define what to do once a feature module is loaded successfully.
     * @param moduleContract Contains the name of the module and the entry activity of the successfully
     * loaded module.
     * @param launch `true` if the feature module should be launched, else `false`.
     */
    private fun onModuleSuccessfulLoad(
        moduleContract: ModulesContract,
        launch: Boolean = true
    ) {
        updateInstalledModulesLabel()
        if (launch) {
            launchActivity(moduleContract.entryActivityClassName)
        }
        displayProgress(false)
    }

    /**
     * Define what to do once some feature modules are installed.
     */
    private fun onModulesSuccessfulLoad() {
        displayProgress(false)
    }

    /**
     * Launch an activity by its class name.
     */
    private fun launchActivity(className: String) {
        Intent().setClassName(packageName, className)
            .also {
                startActivity(it)
            }
    }

    private fun displayLoadingState(
        totalProgress: Int,
        currentProgress: Int,
        message: String
    ) {
        displayProgress()
        with(progress_bar_view) {
            max = totalProgress
            progress = currentProgress
        }

        updateProgressMessage(message)
    }

    private fun updateProgressMessage(message: String) {
        if (progress_bar.visibility != View.VISIBLE) {
            displayProgress(true)
        }
        progress_text.text = message
    }

    private fun displayProgress(isVisible: Boolean = true) {
        progress_bar.visibility = if (isVisible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun updateInstalledModulesLabel() {
        installed_modules.text = getString(R.string.installed_modules, modulesHandler.getInstalledModules())
    }

    companion object {
        private const val USER_TYPE = "key:UserType"
        private const val USER_WITH_BANCONTACT = "param:bancontact"
        private const val USER_WITH_INVESTMENTS = "param:investments"

        fun openForBancontactUser(context: Context) =
            Intent(context, MainActivity::class.java).apply {
                putExtras(Bundle().apply {
                    putString(USER_TYPE, USER_WITH_BANCONTACT)
                })
            }

        fun openForInvestmentsUser(context: Context) =
            Intent(context, MainActivity::class.java).apply {
                putExtras(Bundle().apply {
                    putString(USER_TYPE, USER_WITH_INVESTMENTS)
                })
            }
    }
}

fun MainActivity.toastAndLog(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    Log.d("DynamicFeatures", text)
}
