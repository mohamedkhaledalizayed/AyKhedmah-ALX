package com.mohamed.aykhedmah.view.user.profile

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
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
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.data.network.response.providerdetails.ProfileResponse
import com.mohamed.aykhedmah.utils.ViewState
import com.mohamed.aykhedmah.view.common.base.BaseScreen
import com.mohamed.aykhedmah.view.common.editprofile.EditProfileScreen
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_user_profile_screen.*
import javax.inject.Inject

class UserProfileScreen : BaseScreen() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var profileViewModel: ProfileViewModel
    private var adLoader: AdLoader? = null
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var dialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_screen)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "الصفحة الشخصية"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_right_black_24dp)

        dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Please Wait...")
            .build()

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

    override fun onResume() {
        super.onResume()
        if (sharedPreferences.getString(Constants.PHOTO, "")!= null && sharedPreferences.getString(Constants.PHOTO, "")!= ""){
            Picasso.get().load(Constants.STORAGE + sharedPreferences.getString(Constants.PHOTO, "")).into(user_image)
        }

        name.text = sharedPreferences.getString(Constants.FULLNAME, "")
        username.text = sharedPreferences.getString(Constants.FULLNAME, "")
        phone.text = sharedPreferences.getString(Constants.PHONE, "")
        email.text = sharedPreferences.getString(Constants.EMAIL, "")
        city.text = sharedPreferences.getString(Constants.CITY, "")
        area.text = sharedPreferences.getString(Constants.AREA, "")
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
                            name.text = viewState.data.data.name
                            username.text = viewState.data.data.name
                            phone.text = viewState.data.data.phone
                            email.text = viewState.data.data.email

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_profile_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }else if (item.itemId == R.id.edit){
            val intent = Intent(this, EditProfileScreen::class.java)
            intent.putExtra(Constants.USERTYPE, Constants.CLIENT)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }
}