package com.mohamed.aykhedmah.view.common.socialmedia

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.utils.LocaleHelper
import kotlinx.android.synthetic.main.activity_social_screen.*

class SocialScreen : AppCompatActivity() {

    private var adLoader: AdLoader? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social_screen)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "تواصل معنا"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_right_black_24dp)


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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun facebook(view: View) {
        val facebookIntent = Intent(Intent.ACTION_VIEW)
        val facebookUrl: String = getOpenFacebookIntent()
        facebookIntent.data = Uri.parse(facebookUrl)
        startActivity(facebookIntent)
    }

    override fun onStart() {
        super.onStart()
        LocaleHelper().setLocale(this, "ar")
    }

    fun instagram(view: View) {
        val uri = Uri.parse("https://www.instagram.com/aykhedmah/")
        val likeIng = Intent(Intent.ACTION_VIEW, uri)

        likeIng.setPackage("com.instagram.android")

        try {
            startActivity(likeIng)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.instagram.com/aykhedmah/")
                )
            )
        }
    }

    fun email(view: View) {
        sendMail(this, "khedmaha7@gmail.com", "Support")
    }

    fun youtube(view: View) {
//        val intent = YouTubeIntents.createChannelIntent(this, "UCI5JqXlrgDgV-aF-Dv0Ibcg")
//        startActivity(intent)
    }

    private fun sendMail(context: Context, email: String, shareTitle: String?) {
        try {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.data = Uri.parse("mailto:")
            emailIntent.type = "text/plain"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            context.startActivity(emailIntent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private val FACEBOOK_URL = "https://www.facebook.com/Ay-Khedmah-111280908138467"
    private val FACEBOOK_PAGE_ID = "Ay-Khedmah-111280908138467"
    private fun getOpenFacebookIntent(): String {
        val packageManager: PackageManager = packageManager
        return try {
            val versionCode =
                packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
            if (versionCode >= 3002850) { //newer versions of fb app
                "fb://facewebmodal/f?href=$FACEBOOK_URL"
            } else { //older versions of fb app
                "fb://page/$FACEBOOK_PAGE_ID"
            }
        } catch (e: PackageManager.NameNotFoundException) {
            FACEBOOK_URL //normal web url
        }
    }

    fun twitter(view: View) {
//        try {
//            val intent = Intent(
//                Intent.ACTION_VIEW,
//                Uri.parse("twitter://user?screen_name=cityservicesc")
//            )
//            startActivity(intent)
//        } catch (e: java.lang.Exception) {
//            startActivity(
//                Intent(
//                    Intent.ACTION_VIEW,
//                    Uri.parse("https://twitter.com/cityservicesc")
//                )
//            )
//        }
    }

    fun whatsApp(view: View?) {

    }

    fun openWhatsApp(view: View) {
        val number = "201113595439"
        try {
            val sendIntent = Intent("android.intent.action.MAIN")
            sendIntent.component = ComponentName("com.whatsapp", "com.whatsapp.Conversation")
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(number) + "@s.whatsapp.net")
            startActivity(sendIntent)
        } catch (e: Exception) {
            Log.e("Error", "ERROR_OPEN_MESSANGER$e")
        }
    }
}