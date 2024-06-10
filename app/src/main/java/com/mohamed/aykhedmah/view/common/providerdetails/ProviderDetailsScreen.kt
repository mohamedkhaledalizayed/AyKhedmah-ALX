package com.mohamed.aykhedmah.view.common.providerdetails

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.data.network.response.providerdetails.ProfileResponse
import com.mohamed.aykhedmah.data.network.response.providerdetails.UserImage
import com.mohamed.aykhedmah.utils.AppUtils
import com.mohamed.aykhedmah.utils.ViewState
import com.mohamed.aykhedmah.view.common.base.BaseScreen
import com.mohamed.aykhedmah.view.common.rate.ReviewsScreen
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_provider_details_screen.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import androidx.lifecycle.Observer
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.mohamed.aykhedmah.data.network.response.addreview.AddReviewResponse
import com.mohamed.aykhedmah.data.network.response.increasecount.IncreaseCountResponse
import com.mohamed.aykhedmah.view.provider.profile.ProviderProfileScreen


class ProviderDetailsScreen : BaseScreen(), IOnAddRateClickListener {

    private val TAG: String? = "tag"

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var providerDetailsViewModel: ProviderDetailsViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var dialog: AlertDialog
    private lateinit var id: String
    private lateinit var phoneNumber: String
    private var adLoader: AdLoader? = null
    private var type = "view_count"
    private var clickType = 0
    private var workImages: List<UserImage> = ArrayList()
    private var interstitialAdMob: InterstitialAd? = null
    private var latitude = 0.0
    private var longitude = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_details_screen)
        loadRewardedAd()
        AppUtils().setHtmlText(R.string.more_info, show)
        AppUtils().setHtmlText(R.string.location, location)
        id = intent.getStringExtra("id").toString()
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "بيانات مزود الخدمة"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_right_black_24dp)


        location.setOnClickListener {
            if (latitude == 0.0 || longitude == 0.0){
                Toast.makeText(this, "عفوا الموقع غير متاح", Toast.LENGTH_LONG).show()
            }else{
                location()
            }
        }
        dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Please Wait...")
            .build()

        configViewModel()
        getProviderProfile()
        addCount()
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
        initAdMob()
    }

    private fun loadNativeAd() {
        // Creating  an Ad Request
        val adRequest = AdRequest.Builder().build()
        // load Native Ad with the Request
        adLoader!!.loadAd(adRequest)
    }

    private fun location() {
        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse("geo:<lat>,<long>?q=<$latitude>,<$longitude>(عنوان مقدم الخدمة)"))
        intent.setPackage("com.google.android.apps.maps")
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun setImages() {
        if (workImages.isEmpty()){
            gridView.visibility = View.GONE
        }else{
            val mainAdapter = PhotosAdapter(this)
            gridView.adapter = mainAdapter

            mainAdapter.submitList(workImages, false)
            gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                val newFragment = SlideshowDialogFragment.newInstance(workImages, position)
                newFragment.show(ft, "slideshow")
            }
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

    private fun configViewModel() {
        providerDetailsViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProviderDetailsViewModel::class.java)
    }

    private fun getProviderProfile() {
        providerDetailsViewModel.getUserProfile(sharedPreferences.getString(Constants.ACCESS_TOKEN, "")!!, id).observe(this,
            Observer<ViewState<ProfileResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {
                            service_name.text = viewState.data.data.name
                            workImages = viewState.data.data.user_images
                            latitude = viewState.data.data.latitude
                            longitude = viewState.data.data.longitude
                            if (viewState.data.data.note.isNullOrEmpty()){
                                about.text = ""
                                about.visibility = View.GONE
                            }else{
                                about.text = "${viewState.data.data.note}"
                            }
                            setImages()
                            phoneNumber = viewState.data.data.phone
                            if (!viewState.data.data.area.title.isNullOrEmpty()){
                                if (viewState.data.data.zone.isNullOrEmpty()){
                                    address.text = "${viewState.data.data.area.title}"
                                }else{
                                    address.text = "${viewState.data.data.area.title}, ${viewState.data.data.zone}"
                                }
                            }else{
                                address.text = ""
                                about.visibility = View.GONE
                            }

                            if(viewState.data.data.user_type == Constants.CLIENT){
                                service_type_value.visibility = View.GONE
                            }else{
                                service_type_value.text = "${viewState.data.data.service[0].service_data.title}, ${viewState.data.data.service[0].sub_service_data.title}"
                            }
                            if(viewState.data.data.details != null){
                                work_date.text = "مواعيد العمل : ${viewState.data.data.details?.work_days} من ${viewState.data.data.details?.work_hour}  "
                            }else{
                                work_date.text = "مواعيد العمل : "
                                work_date.visibility = View.GONE
                            }


                            if (!viewState.data.data.logo.isNullOrEmpty()){
                                Picasso.get().load(Constants.STORAGE +viewState.data.data.logo).placeholder(
                                    R.drawable.ic_logo__1_).into(image)
                            }

                            review.text = viewState.data.data.rate.toString()

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

    private var mRewardedAd: RewardedAd? = null
    private var isLoaded = false
    private var mIsLoading = false
    private fun loadRewardedAd() {
        if (mRewardedAd == null) {
            mIsLoading = true
            var adRequest = AdRequest.Builder().build()

            RewardedAd.load(
                this, Constants.AD_UNIT_ID, adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.e(TAG, adError.message)
                        mIsLoading = false
                        mRewardedAd = null
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        Log.e(TAG, "Ad was loaded.")
                        mRewardedAd = rewardedAd
                        mIsLoading = false
                    }
                }
            )
        }
    }

    private fun showRewardedVideo() {

        if (mRewardedAd != null) {
            mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.e(TAG, "Ad was dismissed.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    sharedPreferences.edit().putLong("reward_time", Date().time).apply()
                    isLoaded = true
                    mRewardedAd = null
                    loadRewardedAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    Log.e(TAG, "Ad failed to show.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mRewardedAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    Log.e(TAG, "Ad showed fullscreen content.")
                    // Called when ad is dismissed.
                }
            }

            mRewardedAd?.show(
                this,
                OnUserEarnedRewardListener() {
                    fun onUserEarnedReward(rewardItem: RewardItem) {
//                        var rewardAmount = rewardItem.amount
                        sharedPreferences.edit().putLong("reward_time", Date().time).apply()
                        isLoaded = true
                        Log.e("GGG", "User earned the reward.")
                    }
                }
            )
        }
    }
    private fun showAlertDialog(message: String) {

        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("تنبيه")
        alertDialog.setMessage(message)
        alertDialog.setCancelable(true)
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "موافق"
        ) { dialog, _ ->
            dialog.dismiss()
            addRev()
        }
        alertDialog.show()

    }
    private fun showAlertDialog2(message: String) {

        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("تنبيه")
        alertDialog.setMessage(message)
        alertDialog.setCancelable(true)
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "موافق"
        ) { dialog, _ ->
            dialog.dismiss()
            addCall()
        }
        alertDialog.show()

    }
    private fun addReview() {

        if (Constants.isGuest) {
            Toast.makeText(this, "من فضلك قم بتسجل الدخول اولا", Toast.LENGTH_LONG).show()
            return
        }

        if (id == sharedPreferences.getString(Constants.USERID, "")!!){
            Toast.makeText(this, "لا يمكنك اضافة تقييم", Toast.LENGTH_LONG).show()
            return
        }
        val fm: FragmentManager = supportFragmentManager
        val dialog = AddReviewDialog()
        dialog.show(fm, "")
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth)
    }

    fun seeReviews(view: View) {

        if (Constants.isGuest) {
            Toast.makeText(this, "من فضلك قم بتسجل الدخول اولا", Toast.LENGTH_LONG).show()
            return
        }

        val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)

        Log.e("Time", minutes.toString())
        if (minutes >= 5L && interstitialAdMob != null){
            showAd()
        }else{
            val intent = Intent(this, ReviewsScreen::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun addReview(view: View) {
        clickType = 0
//        if (isLoaded){
//            addReview()
//            isLoaded = false
//            return
//        }

        val diff:Long = Date().time - sharedPreferences.getLong("reward_time", 0L)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)
        Log.e("Time", minutes.toString())
//        if (isLoaded){
//            checkPermissionsAndOpenFilePicker()
//            isLoaded = false
//            return
//        }
        if (minutes >= 5L){
            showAlertDialog("لاضافة تقييم برجاء مشاهدة الاعلان كاملا اولا.")
        }else{
            addReview()
        }



    }

    private fun addRev(){
        val diff:Long = Date().time - sharedPreferences.getLong("reward_time", 0L)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)
        Log.e("Time", minutes.toString())
//        if (isLoaded){
//            checkPermissionsAndOpenFilePicker()
//            isLoaded = false
//            return
//        }
        if (minutes >= 5L){
            if (mRewardedAd != null){
                showRewardedVideo()
            }else{
                addReview()
            }
        }else{
            addReview()
        }
    }

    private fun addCall(){
        val diff:Long = Date().time - sharedPreferences.getLong("reward_time", 0L)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)
        Log.e("Time", minutes.toString())
//        if (isLoaded){
//            checkPermissionsAndOpenFilePicker()
//            isLoaded = false
//            return
//        }
        if (minutes >= 5L){
            if (mRewardedAd != null){
                showRewardedVideo()
            }else{
                call()
            }
        }else{
            call()
        }
    }

    private fun call() {

        type = "call_count"
        addCount()
        if (phoneNumber.isNullOrEmpty()){
            Toast.makeText(this, "لا يوجد رقم لمزود الخدمة برجاء التواصل مع الادارة", Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        try {
            startActivity(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onAddClickListener(rate: Float, comment: String) {

        val myMap: Map<String, Any> = mapOf(
            "user_id" to sharedPreferences.getString(Constants.USERID, "").toString(),
            "provider_id" to id,
            "rate" to rate,
            "comment" to comment)

        providerDetailsViewModel.addReview(sharedPreferences.getString(Constants.ACCESS_TOKEN, "").toString(), myMap).observe(this,
            Observer<ViewState<AddReviewResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {


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

    private fun addCount(){

        val myMap: Map<String, Any> = mapOf(
            "type" to type,
            "provider_id" to id)

        providerDetailsViewModel.increaseCount(sharedPreferences.getString(Constants.ACCESS_TOKEN, "").toString(), myMap).observe(this,
            Observer<ViewState<IncreaseCountResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
//                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
//                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {

                        } else {
                            Toast.makeText(baseContext, "Error", Toast.LENGTH_LONG).show()
                        }
                    }

                    ViewState.Status.ERROR -> {
//                        dialog.dismiss()
                        Toast.makeText(baseContext, "${viewState.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )

    }

    fun callProvider(view: View) {
        clickType = 1


//        if (isLoaded){
//            call()
//            isLoaded = false
//            return
//        }

        val diff:Long = Date().time - sharedPreferences.getLong("reward_time", 0L)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)
        Log.e("Time", minutes.toString())
//        if (isLoaded){
//            checkPermissionsAndOpenFilePicker()
//            isLoaded = false
//            return
//        }
        if (minutes >= 5L){
            showAlertDialog2("للحصول على رقم مزود الخدمة برجاء مشاهدة الاعلان كاملا اولا.")
        }else{
            call()
        }

    }

    private fun showAd(){
//        interstitialAdMob?.show(this)
//        sharedPreferences.edit().putLong("click_time", Date().time).apply()
        if (interstitialAdMob != null) {
            interstitialAdMob?.show(this)
            sharedPreferences.edit().putLong("click_time", Date().time).apply()
        } else {
            if (clickType == 1){
                call()
            }else{
                addReview()
            }
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }

}