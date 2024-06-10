package com.mohamed.aykhedmah.view.common.editprofile

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
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.data.network.response.updateprofile.UpdateUserProfile
import com.mohamed.aykhedmah.utils.FileUtils
import com.mohamed.aykhedmah.utils.LocaleHelper
import com.mohamed.aykhedmah.utils.ViewState
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_about_app_screen.toolbar
import kotlinx.android.synthetic.main.activity_edit_profile_screen.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import androidx.lifecycle.Observer
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.mohamed.aykhedmah.data.network.response.editprofile.EditUserProfileResponse
import com.mohamed.aykhedmah.view.common.base.BaseScreen
import com.mohamed.aykhedmah.view.provider.home.ProviderHomeScreen

class EditProfileScreen : BaseScreen() {

    private val TAG: String? = "tag"

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var adLoader: AdLoader? = null
    @Inject
    lateinit var editProfileViewModel: EditProfileViewModel
    private var interstitialAdMob: InterstitialAd? = null
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var dialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_screen)
        setSupportActionBar(toolbar)
        loadRewardedAd()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "تعديل"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_right_black_24dp)

        if (intent.getStringExtra(Constants.USERTYPE)!! == Constants.CLIENT){
            about.visibility = View.GONE
        }
        dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage(getString(R.string.please_wait))
            .build()

        username.setText(sharedPreferences.getString(Constants.FULLNAME, ""))
        phone.setText(sharedPreferences.getString(Constants.PHONE, ""))
        email.setText(sharedPreferences.getString(Constants.EMAIL, ""))
        city.setText(sharedPreferences.getString(Constants.CITY, ""))
        area.setText(sharedPreferences.getString(Constants.AREA, ""))
        about.setText(sharedPreferences.getString(Constants.ABOUT, ""))

        if (sharedPreferences.getString(Constants.PHOTO, "")!= null && sharedPreferences.getString(
                Constants.PHOTO,
                ""
            )!= ""){
            Picasso.get()
                .load(Constants.STORAGE + sharedPreferences.getString(Constants.PHOTO, ""))
                .into(profile_image, object : Callback {
                    override fun onSuccess() {
                        image_progress.hide()
                    }

                    override fun onError(e: Exception?) {
                        image_progress.hide()
                    }

                }
                )
        }
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
        initAdMob()
    }

    private fun loadNativeAd() {
        // Creating  an Ad Request
        val adRequest = AdRequest.Builder().build()
        // load Native Ad with the Request
        adLoader!!.loadAd(adRequest)
    }

    private fun configViewModel() {
        editProfileViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            EditProfileViewModel::class.java
        )
    }

    private fun editUserProfile() {

        when {
            username.text.toString().isNullOrEmpty() -> {
                Toast.makeText(this, "من فضلك تاكد من الاسم كامل", Toast.LENGTH_LONG).show()
                return
            }
//            email.text.toString().isNullOrEmpty() -> {
//                Toast.makeText(this, "من فضلك تاكد من البريد الالكترونى", Toast.LENGTH_LONG).show()
//                return
//            }
//            city.text.toString().isNullOrEmpty() -> {
//                Toast.makeText(this, "من فضلك تاكد من المدينة", Toast.LENGTH_LONG).show()
//                return
//            }
//            area.text.toString().isNullOrEmpty() -> {
//                Toast.makeText(this, "من فضلك تاكد من المنطقة", Toast.LENGTH_LONG).show()
//                return
//            }
        }

        var myMap: Map<String, String>
        if (intent.getStringExtra(Constants.USERTYPE)!! == Constants.CLIENT){
            myMap = mapOf(
                "user_id" to sharedPreferences.getString(Constants.USERID, "")!!,
                "name" to username.text.toString(),
//                "zone" to area.text.toString(),
                "email" to email.text.toString()
            )
        }else{
            myMap = mapOf(
                "user_id" to sharedPreferences.getString(Constants.USERID, "")!!,
                "name" to username.text.toString(),
//                "zone" to area.text.toString(),
                "note" to about.text.toString(),
                "email" to email.text.toString()
            )
        }


        editProfileViewModel.editUserProfile(
            sharedPreferences.getString(
                Constants.ACCESS_TOKEN,
                ""
            )!!, myMap
        ).observe(this,
            Observer<ViewState<UpdateUserProfile>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {
                            sharedPreferences.edit().putString(
                                Constants.FULLNAME,
                                viewState.data.data.name
                            ).apply()
                            sharedPreferences.edit().putString(
                                Constants.EMAIL,
                                viewState.data.data.email
                            ).apply()
                            sharedPreferences.edit().putString(
                                Constants.PHOTO,
                                viewState.data.data.logo
                            ).apply()
                            sharedPreferences.edit().putString(
                                Constants.CITY,
                                viewState.data.data.area.title
                            ).apply()
                            sharedPreferences.edit().putString(
                                Constants.AREA,
                                viewState.data.data.zone
                            ).apply()
                            Toast.makeText(this, "تم تغيير البيانات بنجاح", Toast.LENGTH_LONG)
                                .show()
                            finish()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
            val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)

            Log.e("Time", minutes.toString())
            if (minutes >= 5L && interstitialAdMob != null){
                showAd()
            }else{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun changeImage(view: View) {

//        if (isLoaded){
//            checkPermissionsAndOpenFilePicker()
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
            showAlertDialog()
        }else{
            checkPermissionsAndOpenFilePicker()
        }

    }

    private fun showAlertDialog() {

        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("تنبيه")
        alertDialog.setMessage("لتغيير الصورة الشخصية الرجاء مشاهدة الاعلان كاملا اولا")
        alertDialog.setCancelable(true)
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "موافق"
        ) { dialog, _ ->
            dialog.dismiss()
            changeImage()
        }
        alertDialog.show()

    }

    private fun changeImage(){
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
                checkPermissionsAndOpenFilePicker()
            }
        }else{
            checkPermissionsAndOpenFilePicker()
        }
    }

    fun save(view: View) {

        val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)

        Log.e("Time", minutes.toString())
        if (minutes >= 5L && interstitialAdMob != null){
            showAd()
        }else{
            editUserProfile()
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
        // in onCreate or any event where your want the user to
        // select a file
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
            profile_image.setImageURI(imageUrl)
            updateProfile()
        }
    }
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @NonNull
    private fun prepareFilePart(
        partName: String,
        fileUri: Uri
    ): MultipartBody.Part { // use the FileUtils to get the actual file by uri
        val file = FileUtils.getFile(this, fileUri)
        // create RequestBody instance from file
        val requestFile = RequestBody.create(
            contentResolver.getType(fileUri).toString().toMediaTypeOrNull(),
            file
        );


        return MultipartBody.Part.createFormData("logo", file.name, requestFile);
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
                        Log.e("GGG", "User earned the reward.")
                    }
                }
            )
        }
    }

    private fun updateProfile() {
        val parts: MultipartBody.Part = prepareFilePart("logo", imageUrl!!)
        val id: RequestBody = createPartFromString(sharedPreferences.getString(
            Constants.USERID,
            ""
        ).toString())
        editProfileViewModel.updateProfile(
            sharedPreferences.getString(Constants.ACCESS_TOKEN, "").toString(),
            id,
            parts
        ).observe(this,
            Observer<ViewState<EditUserProfileResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data != null) {
                            if (!viewState.data.data.logo.isNullOrEmpty()) {
                                Picasso.get()
                                    .load(Constants.STORAGE + viewState.data.data.logo)
                                    .into(profile_image, object : Callback {
                                        override fun onSuccess() {
                                            image_progress.hide()
                                            Toast.makeText(
                                                baseContext,
                                                "لقد تم تغيير الصورة بنجاح",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }

                                        override fun onError(e: Exception?) {
                                            image_progress.hide()
                                            Toast.makeText(
                                                baseContext,
                                                "Failed",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                    )
                                sharedPreferences.edit().putString(
                                    Constants.PHOTO,
                                    viewState.data.data.logo
                                ).apply()
                            }

                        } else {
                            Toast.makeText(baseContext, viewState.message, Toast.LENGTH_LONG)
                                .show()
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

    @NonNull
    private fun createPartFromString(content: String): RequestBody {
        return RequestBody.create(MultipartBody.FORM, content)
    }

    private fun initAdMob(){
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this, Constants.advanced_ad, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("TAG", adError.message)
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

    override fun onStart() {
        super.onStart()
        LocaleHelper().setLocale(this, "ar")
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