package com.mohamed.aykhedmah.view.common.terms

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.data.network.response.settings.SettingsResponse
import com.mohamed.aykhedmah.utils.ViewState
import com.mohamed.aykhedmah.view.common.base.BaseScreen
import com.mohamed.aykhedmah.view.common.base.BaseViewModel
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_terms_screen.*
import javax.inject.Inject

class TermsScreen : BaseScreen() {

    private lateinit var dialog: AlertDialog

    @Inject
    lateinit var baseViewModel: BaseViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_screen)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "الشروط و الاحكام"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_right_black_24dp)


        dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Please Wait...")
            .build()

        configViewModel()
        getSettings()

        val adView = AdView(this)

        val adRequest = AdRequest.Builder().build()
        adView.adUnitId = Constants.banner_ad
        adView.adSize = AdSize.BANNER
        adView.loadAd(adRequest)

        val params: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        banner_container.addView(adView, params)
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
                            terms.text = viewState.data.data.termisCondition
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