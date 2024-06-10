package com.mohamed.aykhedmah.view.common.usertype

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.data.Constants.PROVIDER
import com.mohamed.aykhedmah.data.Constants.USERTYPE
import com.mohamed.aykhedmah.utils.LocaleHelper
import com.mohamed.aykhedmah.view.provider.signup.SignUpProviderScreen
import com.mohamed.aykhedmah.view.user.signup.SignUpUserScreen
import kotlinx.android.synthetic.main.activity_login_screen.*

class UserTypeScreen : AppCompatActivity() {
    private var adLoader: AdLoader? = null
    private lateinit var userType: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_type_screen)

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

    fun client(view: View) {
        userType = Constants.CLIENT
        val intent = Intent(this, SignUpUserScreen::class.java)
        intent.putExtra(USERTYPE, userType)
        startActivity(intent)
        finish()
    }
    fun provider(view: View) {
        userType = PROVIDER
        val intent = Intent(this, SignUpProviderScreen::class.java)
        intent.putExtra(USERTYPE, userType)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        LocaleHelper().setLocale(this, "ar")
    }

    private fun loadNativeAd() {
        // Creating  an Ad Request
        val adRequest = AdRequest.Builder().build()
        // load Native Ad with the Request
        adLoader!!.loadAd(adRequest)
    }
}