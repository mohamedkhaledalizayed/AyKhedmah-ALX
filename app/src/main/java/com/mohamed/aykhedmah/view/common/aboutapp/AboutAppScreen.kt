package com.mohamed.aykhedmah.view.common.aboutapp

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants.native_ad
import com.mohamed.aykhedmah.data.network.response.settings.SettingsResponse
import com.mohamed.aykhedmah.utils.ViewState
import com.mohamed.aykhedmah.view.common.base.BaseScreen
import com.mohamed.aykhedmah.view.common.base.BaseViewModel
import dagger.android.support.DaggerAppCompatActivity
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_about_app_screen.*
import javax.inject.Inject

class AboutAppScreen : BaseScreen() {

    private lateinit var dialog: AlertDialog
    private var adLoader: AdLoader? = null
    @Inject
    lateinit var baseViewModel: BaseViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app_screen)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "عن التطبيق"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_right_black_24dp)


        dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Please Wait...")
            .build()

        configViewModel()
        getSettings()

        adLoader = AdLoader.Builder(this, native_ad)
            .forNativeAd(object : NativeAd.OnNativeAdLoadedListener {
                private val background: ColorDrawable? = null
                override fun onNativeAdLoaded(nativeAd: NativeAd) {
                    val styles =
                        NativeTemplateStyle.Builder().withMainBackgroundColor(background).build()
                    nativeTemplateView.setStyles(styles)
                    nativeTemplateView.setNativeAd(nativeAd)

                    if (isDestroyed) {
                        nativeAd.destroy()
                        return
                    }
                }
            }).build()

        loadNativeAd()
    }

    private fun loadNativeAd() {
        // Creating  an Ad Request
        val adRequest = AdRequest.Builder().build()
        // load Native Ad with the Request
        adLoader!!.loadAd(adRequest)
    }

    fun configViewModel() {
        baseViewModel = ViewModelProviders.of(this, viewModelFactory).get(BaseViewModel::class.java)
    }

    private fun getSettings() {

        baseViewModel.getSettings().observe(this,
            Observer<ViewState<SettingsResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {
                            about_app.text = viewState.data.data.aboutApp
                        } else {
                            Toast.makeText(baseContext, "Error", Toast.LENGTH_LONG).show()
                        }
                    }

                    ViewState.Status.ERROR -> {
                        dialog.dismiss()
                        Toast.makeText(baseContext, "${viewState.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}