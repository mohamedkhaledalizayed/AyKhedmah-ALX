package com.mohamed.aykhedmah.view.provider.profile

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.data.Constants.AD_UNIT_ID
import com.mohamed.aykhedmah.data.network.response.deleteimage.DeleteImageResponse
import com.mohamed.aykhedmah.data.network.response.editprofile.EditUserProfileResponse
import com.mohamed.aykhedmah.data.network.response.providerdetails.ProfileResponse
import com.mohamed.aykhedmah.data.network.response.providerdetails.Service
import com.mohamed.aykhedmah.data.network.response.providerdetails.UserImage
import com.mohamed.aykhedmah.utils.FileUtils
import com.mohamed.aykhedmah.utils.ViewState
import com.mohamed.aykhedmah.view.common.base.BaseScreen
import com.mohamed.aykhedmah.view.common.editprofile.EditProfileScreen
import com.mohamed.aykhedmah.view.common.location.SelectLocationScreen
import com.mohamed.aykhedmah.view.common.providerdetails.PhotosAdapter
import com.mohamed.aykhedmah.view.common.providerdetails.SlideshowDialogFragment
import com.mohamed.aykhedmah.view.user.profile.EditWorkDateFragment
import com.mohamed.aykhedmah.view.user.profile.ProfileViewModel
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_provider_profile_screen.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class ProviderProfileScreen : BaseScreen() , IOnEditWorkClickListener {

    private val TAG: String? = "tt"
    private var workImages: List<UserImage> = ArrayList()
    private var adLoader: AdLoader? = null
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var profileViewModel: ProfileViewModel
    private var interstitialAdMob: InterstitialAd? = null
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var dialog: AlertDialog
    private var imageCount = 0
    private lateinit var mainAdapter: PhotosAdapter
    private var imageId:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_profile_screen)
        loadRewardedAd()
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "الصفحة الشخصية"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_right_black_24dp)

        dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage(getString(R.string.please_wait))
            .build()

        configViewModel()
        initAdMob()
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

    fun deleteImage(){

        val myMap: Map<String, Any> = mapOf(
            "user_id" to sharedPreferences.getString(Constants.USERID, "").toString(),
            "id" to imageId)

        profileViewModel.deleteImage(sharedPreferences.getString(Constants.ACCESS_TOKEN, "").toString(), myMap).observe(this,
            Observer<ViewState<DeleteImageResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {
                            getUserProfile()
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

    private fun setImages() {
        if (workImages.isEmpty()) {
            gridView.visibility = View.GONE
        } else {
            mainAdapter = PhotosAdapter(this)
            gridView.adapter = mainAdapter

            mainAdapter.submitList(workImages, false)

            mainAdapter.onItemClickListener = object :
                PhotosAdapter.OnItemClickListener {
                override fun setOnItemClickListener(id: Int) {
                    imageId = id

                    val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
                    val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)

                    Log.e("Time", minutes.toString())
                    if (minutes >= 5L && interstitialAdMob != null){
                        showAd()
                    }else{
                        deleteImage()
                    }
                }
            }


            gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                val newFragment = SlideshowDialogFragment.newInstance(workImages, position)
                newFragment.show(ft, "slideshow")
            }
        }
    }

    private fun configViewModel() {
        profileViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
    }

    private fun getUserProfile() {

        profileViewModel.getUserProfile(sharedPreferences.getString(Constants.ACCESS_TOKEN, "")!!, sharedPreferences.getString(Constants.USERID, "")!!).observe(this,
            Observer<ViewState<ProfileResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {

                            workImages = viewState.data.data.user_images
                            imageCount = workImages.size

                            setImages()

                            var servicesList = ""
                            for (item: Service in viewState.data.data.service) {
                                servicesList += "${item.service_data.title}, ${item.sub_service_data.title} \t"
                            }

                            services.text = servicesList

                            work_date.text = "مواعيد العمل : ${viewState.data.data.details?.work_days} من ${viewState.data.data.details?.work_hour}  "

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

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun init() {
        getUserProfile()
        if (sharedPreferences.getString(Constants.PHOTO, "") != null && sharedPreferences.getString(Constants.PHOTO, "") != "") {
            Picasso.get().load(Constants.STORAGE + sharedPreferences.getString(Constants.PHOTO, "")).into(user_image)
        }

        name.text = sharedPreferences.getString(Constants.FULLNAME, "")
        username.text = sharedPreferences.getString(Constants.FULLNAME, "")
        phone.text = sharedPreferences.getString(Constants.PHONE, "")
        email.text = sharedPreferences.getString(Constants.EMAIL, "")
        city.text = sharedPreferences.getString(Constants.CITY, "")
        area.text = sharedPreferences.getString(Constants.AREA, "")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_profile_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
            val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)

            Log.e("Time", minutes.toString())
            if (minutes >= 5L && interstitialAdMob != null){
                showAd()
            }else{
                finish()
            }
        } else if (item.itemId == R.id.edit) {
            val intent = Intent(this, EditProfileScreen::class.java)
            intent.putExtra(Constants.USERTYPE, Constants.PROVIDER)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    private var mRewardedAd: RewardedAd? = null
    private var isLoaded = false
    fun addImage(view: View) {
        if (imageCount >= 10) {
            Toast.makeText(this, "عفوا لا يمكنك تحمبل اكثر من 10 صور", Toast.LENGTH_LONG).show()
        }else{
            val diff:Long = Date().time - sharedPreferences.getLong("reward_time", 0L)
            val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)
            Log.e("Time", minutes.toString())
//            if (isLoaded){
//                checkPermissionsAndOpenFilePicker()
//                isLoaded = false
//                return
//            }
            if (minutes >= 5L){
                if (mRewardedAd != null){
                    showRewardedVideo()
                }else{
                    checkPermissionsAndOpenFilePicker()
                }
            }else{
                checkPermissionsAndOpenFilePicker()
            }


        }

    }
    private var mIsLoading = false
    private fun loadRewardedAd() {
        if (mRewardedAd == null) {
            mIsLoading = true
            var adRequest = AdRequest.Builder().build()

            RewardedAd.load(
                this, AD_UNIT_ID, adRequest,
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
                        Log.e("GGG", "User earned the reward.")
                    }
                }
            )
        }
    }

    private val PERMISSIONS_REQUEST_CODE = 15

    @Suppress("DEPRECATED_IDENTITY_EQUALS")
    private fun checkPermissionsAndOpenFilePicker() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showError()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    PERMISSIONS_REQUEST_CODE
                )
            }
        } else {
            showChooser()
        }
    }

    private val REQUEST_CODE = 1
    private var imageUrl: Uri? = null
    private var isFileSelected = false

    private fun showChooser() { // Use the GET_CONTENT intent from the utility class
        val intent = Intent()
        intent.type = "image/*";
        intent.action = Intent.ACTION_GET_CONTENT;
        startActivityForResult(Intent.createChooser(intent,
            "Select Picture"), REQUEST_CODE);
    }

    private fun showError() {
        Toast.makeText(this, "Allow external storage reading", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Log.e("Lionel", data.data.toString() + "")
            imageUrl = data.data
            isFileSelected = true
            updateProfile()
        }else  if (requestCode == GET_CURRENT_LOCATION_REQUEST && resultCode == RESULT_OK && data != null) {
            lat = data.getDoubleExtra("lat", DEFAULT_LAT_LON)
            lan = data.getDoubleExtra("lan", DEFAULT_LAT_LON)
            updateLocation()
        }
    }

    private fun updateLocation(){
        val myMap: Map<String, Any> = mapOf(
            "user_id" to sharedPreferences.getString(Constants.USERID, "").toString(),
            "latitude" to lat,
            "longitude" to lan)

        profileViewModel.changeWorkDates(sharedPreferences.getString(Constants.ACCESS_TOKEN, "").toString(), myMap).observe(this,
            Observer<ViewState<EditUserProfileResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {
                            getUserProfile()
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

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @NonNull
    private fun prepareFilePart(partName: String, fileUri: Uri): MultipartBody.Part { // use the FileUtils to get the actual file by uri
        val file = FileUtils.getFile(this, fileUri)
        // create RequestBody instance from file
        val requestFile = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            file
        );


        return MultipartBody.Part.createFormData("images[]", file.name, requestFile);
    }

    @NonNull
    private fun createPartFromString(content: String): RequestBody {
        return RequestBody.create(MultipartBody.FORM, content)
    }

    private fun updateProfile() {
        val parts: MultipartBody.Part = prepareFilePart("images[]", imageUrl!!)
        val id: RequestBody = createPartFromString(sharedPreferences.getString(Constants.USERID, "").toString())

        profileViewModel.uploadProviderImage(sharedPreferences.getString(Constants.ACCESS_TOKEN, "").toString(), id, parts).observe(this,
            Observer<ViewState<EditUserProfileResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        init()
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

    fun editWorkDate(view: View) {

        val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)

        Log.e("Time", minutes.toString())
        if (minutes >= 5L && interstitialAdMob != null){
            showAd()
        }else{
            val fm: FragmentManager = supportFragmentManager
            val dialog = EditWorkDateFragment()
            dialog.show(fm, "")
            dialog.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth)
        }

    }

    fun editImage(view: View) {
        val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)

        Log.e("Time", minutes.toString())
        if (minutes >= 5L && interstitialAdMob != null){
            showAd()
        }else{
            mainAdapter.clear()
            mainAdapter.submitList(workImages, true)
        }

    }

    override fun onEditClickListener(days: String, hours: String) {

        val myMap: Map<String, Any> = mapOf(
            "user_id" to sharedPreferences.getString(Constants.USERID, "").toString(),
            "work_days" to days,
            "work_hour" to hours)

        profileViewModel.changeWorkDates(sharedPreferences.getString(Constants.ACCESS_TOKEN, "").toString(), myMap).observe(this,
            Observer<ViewState<EditUserProfileResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {
                            getUserProfile()
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

    fun selectLocation(view: View) {

        val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)

        Log.e("Time", minutes.toString())
        if (minutes >= 5L && interstitialAdMob != null){
            showAd()
        }else{
            startActivityForResult(Intent(this, SelectLocationScreen::class.java), GET_CURRENT_LOCATION_REQUEST)
        }


    }

    private var lat = 34.8973188
    private var lan = -1.3501794
    private val GET_CURRENT_LOCATION_REQUEST = 115

    private val DEFAULT_LAT_LON = 30.45633

}