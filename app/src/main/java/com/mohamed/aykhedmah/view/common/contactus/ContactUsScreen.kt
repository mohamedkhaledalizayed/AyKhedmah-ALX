package com.mohamed.aykhedmah.view.common.contactus

import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.data.network.response.contactus.ContactUsResponse
import com.mohamed.aykhedmah.utils.ViewState
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_contact_us_screen.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import androidx.lifecycle.Observer
import com.mohamed.aykhedmah.view.common.base.BaseScreen

class ContactUsScreen : BaseScreen() {

    @Inject
    lateinit var contactUsViewModel: ContactUsViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var dialog: AlertDialog
    private var adLoader: AdLoader? = null
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var interstitialAdMob: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us_screen)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "تواصل معنا"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_right_black_24dp)


        dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Please Wait...")
            .build()

        configViewModel()

        adLoader = AdLoader.Builder(this, Constants.native_ad)
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

    private fun configViewModel() {
        contactUsViewModel = ViewModelProviders.of(this, viewModelFactory).get(ContactUsViewModel::class.java)
    }

    private fun contactUs() {

        when {
            name.text.toString().isNullOrEmpty() -> {
                Toast.makeText(this, "من فضلك تاكد من الاسم كامل", Toast.LENGTH_LONG).show()
                return
            }
            phone.text.toString().isNullOrEmpty() -> {
                Toast.makeText(this, "من فضلك تاكد من رقم الهاتف", Toast.LENGTH_LONG).show()
                return
            }
            subject.text.toString().isNullOrEmpty() -> {
                Toast.makeText(this, "من فضلك تاكد من الموضوع", Toast.LENGTH_LONG).show()
                return
            }
            message.text.toString().isNullOrEmpty() -> {
                Toast.makeText(this, "من فضلك تاكد من رسالتك", Toast.LENGTH_LONG).show()
                return
            }
        }

        val myMap: Map<String, String> = mapOf(
            "name" to name.text.toString(),
            "phone" to phone.text.toString(),
            "subject" to subject.text.toString(),
            "details" to message.text.toString())

        contactUsViewModel.contactUs(myMap).observe(this,
            Observer<ViewState<ContactUsResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {
                            Toast.makeText(baseContext, "تم ارسال رسالتك بنجاح", Toast.LENGTH_LONG).show()
                            finish()
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
        if (item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun send(view: View) {
        val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)

        Log.e("Time", minutes.toString())
        if (minutes >= 60L){
            showAd()
        }else{
            contactUs()
        }
    }

    private fun initAdMob(){
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this, Constants.advanced_ad, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("TAG", adError.message)
                interstitialAdMob = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.e("TAG", "Ad was loaded.")
                interstitialAdMob = interstitialAd
            }
        })

        interstitialAdMob?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.e("TAG", "Ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                Log.e("TAG", "Ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.e("TAG", "Ad showed fullscreen content.")
                interstitialAdMob = null;
            }
        }
    }

    fun showAd(){
        if (interstitialAdMob != null) {
            interstitialAdMob?.show(this)
            sharedPreferences.edit().putLong("click_time", Date().time).apply()
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }
}