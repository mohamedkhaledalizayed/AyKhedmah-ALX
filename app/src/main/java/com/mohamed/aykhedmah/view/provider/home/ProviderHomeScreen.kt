package com.mohamed.aykhedmah.view.provider.home

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.data.network.response.logout.LogoutResponse
import com.mohamed.aykhedmah.data.network.response.providerdetails.ProfileResponse
import com.mohamed.aykhedmah.data.network.response.slider.Data
import com.mohamed.aykhedmah.data.network.response.slider.SliderResponse
import com.mohamed.aykhedmah.utils.LocaleHelper
import com.mohamed.aykhedmah.utils.ViewState
import com.mohamed.aykhedmah.view.common.aboutapp.AboutAppScreen
import com.mohamed.aykhedmah.view.common.allservices.ServicesScreen
import com.mohamed.aykhedmah.view.common.base.BaseScreen
import com.mohamed.aykhedmah.view.common.login.LoginScreen
import com.mohamed.aykhedmah.view.common.notification.NotificationScreen
import com.mohamed.aykhedmah.view.common.rate.ReviewsScreen
import com.mohamed.aykhedmah.view.common.socialmedia.SocialScreen
import com.mohamed.aykhedmah.view.common.terms.TermsScreen
import com.mohamed.aykhedmah.view.provider.profile.ProviderProfileScreen
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_provider_content.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList

class ProviderHomeScreen : BaseScreen() , NavigationView.OnNavigationItemSelectedListener {
    val imageList = ArrayList<SlideModel>()

    private lateinit var drawer: DrawerLayout
    private lateinit var dialog: AlertDialog
    private lateinit var toolbar: Toolbar
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var providerHomeViewModel: ProviderHomeViewModel
    private var adLoader: AdLoader? = null
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    var token: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_home_screen)
        toolbar = findViewById<Toolbar>(R.id.home_toolbar)
        dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Please Wait...")
            .build()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            token = task.result

        })

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
        drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.setDrawerListener(toggle)
        toggle.syncState()
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp)

        configViewModel()
        getSlider()
        getUserProfile()

    }

    override fun onStart() {
        super.onStart()
        initAdMob()
        LocaleHelper().setLocale(this, "ar")
    }

    private fun loadNativeAd() {
        // Creating  an Ad Request
        val adRequest = AdRequest.Builder().build()
        // load Native Ad with the Request
        adLoader!!.loadAd(adRequest)
    }

    private fun getUserProfile() {

        providerHomeViewModel.getUserProfile(sharedPreferences.getString(Constants.ACCESS_TOKEN, "")!!, sharedPreferences.getString(Constants.USERID, "")!!).observe(this,
            Observer<ViewState<ProfileResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {

                            watches.text = "${viewState.data.data.view_count} مشاهدة"
                            calls.text = "${viewState.data.data.call_count} مكالمة"

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

    private fun configViewModel() {
        providerHomeViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProviderHomeViewModel::class.java)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile -> {
                val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
                val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)

                Log.e("Time", minutes.toString())
                if (minutes >= 5L && interstitialAdMob != null){
                    showAd()
                }else{
                    startActivity(Intent(this, ProviderProfileScreen::class.java))
                }
            }
            R.id.normal_user -> {
                val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
                val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)

                Log.e("Time", minutes.toString())
                if (minutes >= 5L && interstitialAdMob != null){
                    showAd()
                }else{
                    startActivity(Intent(this, ServicesScreen::class.java))
                }
            }
            R.id.notifications -> {
                val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
                val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)

                Log.e("Time", minutes.toString())
                if (minutes >= 5L && interstitialAdMob != null){
                    showAd()
                }else{
                    startActivity(Intent(this, NotificationScreen::class.java))
                }
            }
            R.id.reviews -> {
                val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
                val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)

                Log.e("Time", minutes.toString())
                if (minutes >= 5L && interstitialAdMob != null){
                    showAd()
                }else{
                    startActivity(Intent(this, ReviewsScreen::class.java))
                }
            }
            R.id.about_app -> {
                val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
                val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)

                Log.e("Time", minutes.toString())
                if (minutes >= 5L && interstitialAdMob != null){
                    showAd()
                }else{
                    startActivity(Intent(this, AboutAppScreen::class.java))
                }
            }
            R.id.terms -> {
                val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
                val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)

                Log.e("Time", minutes.toString())
                if (minutes >= 5L && interstitialAdMob != null){
                    showAd()
                }else{
                    startActivity(Intent(this, TermsScreen::class.java))
                }
            }
            R.id.contact_us -> {
                val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
                val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)

                Log.e("Time", minutes.toString())
                if (minutes >= 5L && interstitialAdMob != null){
                    showAd()
                }else{
                    startActivity(Intent(this, SocialScreen::class.java))
                }
            }
            R.id.share -> {
                val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
                val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)

                Log.e("Time", minutes.toString())
                if (minutes >= 5L && interstitialAdMob != null){
                    showAd()
                }else{
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Hey check out my app at: https://play.google.com/store/apps/details?id=com.land.services"
                    )
                    sendIntent.type = "text/plain"
                    startActivity(sendIntent)
                }
            }
            R.id.logout -> {
                val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
                val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)

                Log.e("Time", minutes.toString())
                if (minutes >= 5L && interstitialAdMob != null){
                    showAd()
                }else{
                    logout()
                }
            }
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getSlider() {

        providerHomeViewModel.getSlider().observe(this,
            Observer<ViewState<SliderResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {

                            for (data: Data in viewState.data.data) {
                                imageList.add(
                                    SlideModel(
                                        Constants.STORAGE + data.image,
                                        ScaleTypes.FIT
                                    )
                                )
                            }

                            image_slider.setImageList(imageList)

                        } else {
                            Toast.makeText(baseContext, "Error", Toast.LENGTH_LONG).show()
                        }
                    }

                    ViewState.Status.ERROR -> {
                        dialog.dismiss()
                        Toast.makeText(baseContext, "${viewState.message}", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        )
    }

    private fun logout(){

        val myMap: Map<String, Any> = mapOf(
            "user_id" to sharedPreferences.getString(Constants.USERID, "").toString(),
            "phone_token" to token!!
        )

        providerHomeViewModel.logout(sharedPreferences.getString(Constants.ACCESS_TOKEN, "").toString(), myMap).observe(this,
            Observer<ViewState<LogoutResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {
                            sharedPreferences.edit().clear().apply()
                            startActivity(Intent(this, LoginScreen::class.java))
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

    private var interstitialAdMob: InterstitialAd? = null
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