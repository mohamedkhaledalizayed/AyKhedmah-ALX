package com.mohamed.aykhedmah.view.common.splash

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mohamed.aykhedmah.BuildConfig
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.data.Constants.CLIENT
import com.mohamed.aykhedmah.data.Constants.USERTYPE
import com.mohamed.aykhedmah.data.Constants.advanced_ad
import com.mohamed.aykhedmah.data.Constants.banner_ad
import com.mohamed.aykhedmah.data.Constants.native_ad
import com.mohamed.aykhedmah.data.network.response.settings.SettingsResponse
import com.mohamed.aykhedmah.utils.ViewState
import com.mohamed.aykhedmah.view.common.allservices.ServicesScreen
import com.mohamed.aykhedmah.view.common.base.BaseScreen
import com.mohamed.aykhedmah.view.common.base.BaseViewModel
import com.mohamed.aykhedmah.view.common.login.LoginScreen
import com.mohamed.aykhedmah.view.provider.home.ProviderHomeScreen
import javax.inject.Inject

class SplashScreen : BaseScreen() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences


    @Inject
    lateinit var baseViewModel: BaseViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        configViewModel()
        getSettings()
    }

    private fun configViewModel() {
        baseViewModel = ViewModelProvider(this, viewModelFactory).get(BaseViewModel::class.java)
    }

    private fun getSettings() {

        baseViewModel.getSettings().observe(this,
            Observer<ViewState<SettingsResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                    }

                    ViewState.Status.SUCCESS -> {

                        if (viewState.data!!.status == 200) {
                            if (viewState.data.data.appVersion != 2) {
                                showAlertDialogAndOpenAppStore(getString(R.string.new_release))
                            } else {
                                if (!BuildConfig.DEBUG){
                                    banner_ad = viewState.data.data.bannerAd
                                    native_ad = viewState.data.data.nativeAd
                                    advanced_ad = viewState.data.data.advancedAd
                                }

                                Handler().postDelayed({

                                    if (sharedPreferences.getString(Constants.USERSTATUS, "false") == "true") {
                                        if (sharedPreferences.getString(USERTYPE, CLIENT) == CLIENT) {
                                            startActivity(Intent(this@SplashScreen, ServicesScreen::class.java))
                                            finish()
                                        } else {
                                            startActivity(
                                                Intent(
                                                    this@SplashScreen,
                                                    ProviderHomeScreen::class.java
                                                )
                                            )
                                            finish()
                                        }
                                    } else {
                                        startActivity(Intent(this@SplashScreen, LoginScreen::class.java))
                                        finish()
                                    }

                                }, 1000)
                            }

                        } else {
                            Toast.makeText(baseContext, "Error", Toast.LENGTH_LONG).show()
                        }

                    }

                    ViewState.Status.ERROR -> {
                        Toast.makeText(baseContext, "${viewState.message}", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        )
    }

    private fun showAlertDialogAndOpenAppStore(message: String) {

        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle(getString(R.string.update_available))
        alertDialog.setMessage(message)
        alertDialog.setCancelable(false)
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, getString(R.string.update)
        ) { dialog, _ ->
            dialog.dismiss()

            val appPackageName =
                this.packageName
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
            } catch (anfe: android.content.ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        }
        alertDialog.show()
    }
}