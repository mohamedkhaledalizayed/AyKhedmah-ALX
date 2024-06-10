package com.mohamed.aykhedmah.view.common.allservices

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.navigation.NavigationView
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.data.network.response.slider.SliderResponse
import com.mohamed.aykhedmah.utils.ViewState
import com.mohamed.aykhedmah.view.common.base.BaseScreen
import com.mohamed.aykhedmah.view.common.supservices.SubServicesScreen
import kotlinx.android.synthetic.main.activity_content.*
import java.util.*
import javax.inject.Inject
import androidx.lifecycle.Observer
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.mohamed.aykhedmah.data.network.response.logout.LogoutResponse
import com.mohamed.aykhedmah.data.network.response.notificationcount.NotificationsCount
import com.mohamed.aykhedmah.data.network.response.services.ServicesResponse
import com.mohamed.aykhedmah.data.network.response.slider.Data
import com.mohamed.aykhedmah.view.common.aboutapp.AboutAppScreen
import com.mohamed.aykhedmah.view.common.contactus.ContactUsScreen
import com.mohamed.aykhedmah.view.common.login.LoginScreen
import com.mohamed.aykhedmah.view.common.notification.NotificationScreen
import com.mohamed.aykhedmah.view.common.search.IOnSearchClickListener
import com.mohamed.aykhedmah.view.common.search.SearchDialog
import com.mohamed.aykhedmah.view.common.search.SearchScreen
import com.mohamed.aykhedmah.view.common.socialmedia.SocialScreen
import com.mohamed.aykhedmah.view.common.terms.TermsScreen
import com.mohamed.aykhedmah.view.user.profile.UserProfileScreen
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.home_toolbar.*
import java.util.concurrent.TimeUnit

class ServicesScreen : BaseScreen(), NavigationView.OnNavigationItemSelectedListener,
    IOnSearchClickListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var toolbar: Toolbar
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var dialog: AlertDialog
    private val imageList = ArrayList<SlideModel>()
    @Inject
    lateinit var servicesViewModel: ServicesViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var mAdapter: ServicesAdapter
    var token: String? = null
    private var unreadCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services_screen)

        toolbar = findViewById<Toolbar>(R.id.home_toolbar)
        initAdMob()
        drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        search.setOnClickListener {
            val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
            val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)

            if (minutes >= 3L){
                showAd()
            }else{
                val fm: FragmentManager = supportFragmentManager
                val dialog = SearchDialog()
                dialog.show(fm, "")
                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth)
            }
        }

        if (sharedPreferences.getString(Constants.USERTYPE, Constants.CLIENT) == Constants.CLIENT){
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                token = task.result

            })
            navigationView.setNavigationItemSelectedListener(this)
            drawer.addDrawerListener(toggle)
            toggle.syncState()
            toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp)
        }



        dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage(getString(R.string.please_wait))
            .build()

        if (!Constants.isGuest){
            getUnReadNotifications()
            notifications.setOnClickListener { startActivity(Intent(this, NotificationScreen::class.java)) }
        }

        setUIRef()
        configViewModel()
        getSlider()
        getServices()
    }

    private fun setUIRef() {
        mAdapter = ServicesAdapter()
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.adapter = mAdapter


        mAdapter.onItemClickListener = object :
            ServicesAdapter.OnItemClickListener {
            override fun setOnItemClickListener(id: Int, title: String) {
                val intent = Intent(this@ServicesScreen, SubServicesScreen::class.java)
                intent.putExtra("id", "$id")
                intent.putExtra("title", title)
                startActivity(intent)
            }
        }
    }

    private fun getUnReadNotifications() {

        servicesViewModel.getUnReadNotifications(sharedPreferences.getString(Constants.ACCESS_TOKEN, "")!!, sharedPreferences.getString(Constants.USERID, "")!!).observe(this,
            Observer<ViewState<NotificationsCount>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {
                            unreadCount = viewState.data.data.countUnread
                            if (unreadCount!=0){
                                notifications_numbers.text = "$unreadCount"
                            }else{
                                notifications_numbers.visibility = View.GONE
                            }
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


    private fun getSlider() {

        servicesViewModel.getSlider().observe(this,
            Observer<ViewState<SliderResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                    }

                    ViewState.Status.SUCCESS -> {
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
//                            image_slider.setTouchListener(object : TouchListener {
//                                override fun onTouched(touched: ActionTypes) {
//                                    Log.e("index", "$index")
//                                    val intent = Intent(this@UserHomeScreen, ProviderDetailsScreen::class.java)
//                                    intent.putExtra("id", "${viewState.data.data[index].user_id}")
//                                    startActivity(intent)
//                                }
//                            })
                        } else {
                            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                        }
                    }

                    ViewState.Status.ERROR -> {
                        Toast.makeText(this, "${viewState.message}", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        )
    }

    private fun getServices() {

        servicesViewModel.getServices().observe(this,
            Observer<ViewState<ServicesResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                    }

                    ViewState.Status.SUCCESS -> {
                        if (viewState.data!!.status == 200) {
                            mListItems.addAll(viewState.data.data)
                            addAdMobBannerAds()
                            mAdapter.submitList(mListItems)
                            loadBannerAds()
                        } else {
                            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                        }
                    }

                    ViewState.Status.ERROR -> {
                        Toast.makeText(this, "${viewState.message}", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        )
    }

    private fun configViewModel() {
        servicesViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ServicesViewModel::class.java)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)
        when (item.itemId) {
            R.id.profile -> {
                if (minutes >= 3L){
                    showAd()
                }else{
                    if (!Constants.isGuest) {
                        startActivity(Intent(this, UserProfileScreen::class.java))
                    }else{
                        Toast.makeText(this, "من فضلك قم بتسجل الدخول اولا", Toast.LENGTH_LONG).show()
                    }
                }
            }
            R.id.notifications -> {
                if (minutes >= 3L){
                    showAd()
                }else{
                    if (!Constants.isGuest) {
                        startActivity(Intent(this, NotificationScreen::class.java))
                    }else{
                        Toast.makeText(this, "من فضلك قم بتسجل الدخول اولا", Toast.LENGTH_LONG).show()
                    }
                }
            }
            R.id.suggistion -> {
                if (minutes >= 3L){
                    showAd()
                }else{
                    if (!Constants.isGuest) {
                        startActivity(Intent(this, ContactUsScreen::class.java))
                    }else{
                        Toast.makeText(this, "من فضلك قم بتسجل الدخول اولا", Toast.LENGTH_LONG).show()
                    }
                }
            }
            R.id.about_app -> {
                if (minutes >= 3L){
                    showAd()
                }else{
                    startActivity(Intent(this, AboutAppScreen::class.java))
                }
            }
            R.id.ads -> {

            }
            R.id.terms -> {
                if (minutes >= 3L){
                    showAd()
                }else{
                    startActivity(Intent(this, TermsScreen::class.java))
                }
            }
            R.id.contact_us -> {
                if (minutes >= 3L){
                    showAd()
                }else{
                    startActivity(Intent(this, SocialScreen::class.java))
                }
            }
            R.id.share -> {
                if (minutes >= 3L){
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
                if (minutes >= 3L){
                    showAd()
                }else{
                    if (!Constants.isGuest) {
                        logout()
                    }else{
                        sharedPreferences.edit().clear().apply()
                        startActivity(Intent(this, LoginScreen::class.java))
                        finish()
                    }
                }
            }
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private var interstitialAdMob: InterstitialAd? = null

    private fun showAd(){
        sharedPreferences.edit().putLong("click_time", Date().time).apply()
        if (interstitialAdMob != null) {
            interstitialAdMob?.show(this)
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }

    private fun initAdMob(){
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this, Constants.advanced_ad, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("TAG Error", adError.message)
                sharedPreferences.edit().putLong("click_time", Date().time).apply()
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

    private fun logout(){

        val myMap: Map<String, Any> = mapOf(
            "user_id" to sharedPreferences.getString(Constants.USERID, "").toString(),
            "phone_token" to token!!
        )

        servicesViewModel.logout(sharedPreferences.getString(Constants.ACCESS_TOKEN, "").toString(), myMap).observe(this,
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

    override fun onSearchClickListener(keyWord: String) {
        val intent = Intent(this, SearchScreen::class.java)
        intent.putExtra("searchKeyWord", keyWord)
        startActivity(intent)
    }

}