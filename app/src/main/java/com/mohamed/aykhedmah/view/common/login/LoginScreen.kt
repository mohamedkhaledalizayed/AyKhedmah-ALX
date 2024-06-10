package com.mohamed.aykhedmah.view.common.login

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.data.network.response.login.LoginResponse
import com.mohamed.aykhedmah.utils.ViewState
import com.mohamed.aykhedmah.view.common.allservices.ServicesScreen
import com.mohamed.aykhedmah.view.common.base.BaseScreen
import com.mohamed.aykhedmah.view.common.usertype.UserTypeScreen
import com.mohamed.aykhedmah.view.provider.home.ProviderHomeScreen
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_login_screen.*
import kotlinx.android.synthetic.main.activity_login_screen.nativeTemplateView
import kotlinx.android.synthetic.main.activity_provider_details_screen.*
import javax.inject.Inject

class LoginScreen : BaseScreen() {
    private lateinit var dialog: AlertDialog
    @Inject
    lateinit var loginViewModel: LoginViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var adLoader: AdLoader? = null
    private var password: String = ""
    private var email: String = ""
    var token: String? = null
    private var isHidden = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)
        dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage(getString(R.string.please_wait))
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

        configViewModel()
    }

    private fun loadNativeAd() {
        // Creating  an Ad Request
        val adRequest = AdRequest.Builder().build()
        // load Native Ad with the Request
        adLoader!!.loadAd(adRequest)
    }

    private fun configViewModel() {
        loginViewModel =
            ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)
    }

    fun showHidePassword(view: View) {
        if (isHidden) {
            isHidden = false
            et_password.transformationMethod = null
            show_hide.setImageResource(R.drawable.ic_baseline_visibility_24)
        } else {
            isHidden = true
            et_password.transformationMethod = PasswordTransformationMethod()
            show_hide.setImageResource(R.drawable.ic_baseline_visibility_off_24)
        }
    }

    private fun updateToken(){

        val myMap: Map<String, Any> = mapOf(
            "phone_token" to token!!
        )

        loginViewModel.updateToken(myMap).observe(this,
            Observer<ViewState<String>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                    }

                    ViewState.Status.SUCCESS -> {
                    }

                    ViewState.Status.ERROR -> {
                    }

                }
            }
        )

    }

    fun login(view: View) {

        password= et_password.text.toString()
        email = et_username.text.toString().trim()

        when {
            email.isEmpty() -> {
                Toast.makeText(this, "من فضلك ادخل رقم الهاتف", Toast.LENGTH_LONG).show()
                return
            }
            password.isEmpty() -> {
                Toast.makeText(this, "من فضلك ادخل كلمة المرور", Toast.LENGTH_LONG).show()
                return
            }
        }

        val myMap: Map<String, String> = mapOf(
            "phone" to email,
            "password" to password
        )

        loginViewModel.login(myMap).observe(this,
            Observer<ViewState<LoginResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {
                            updateToken()
                            sharedPreferences.edit().putString(Constants.ACCESS_TOKEN, viewState.data.data.token).apply()
                            sharedPreferences.edit().putString(Constants.FULLNAME, viewState.data.data.name).apply()
                            sharedPreferences.edit().putString(Constants.EMAIL, viewState.data.data.email).apply()
                            sharedPreferences.edit().putString(Constants.PHONE, viewState.data.data.phone).apply()
                            sharedPreferences.edit().putString(Constants.PHOTO, viewState.data.data.logo).apply()
                            sharedPreferences.edit().putString(Constants.USERID, viewState.data.data.id.toString()).apply()
                            sharedPreferences.edit().putString(Constants.CITY, viewState.data.data.area.title).apply()
                            sharedPreferences.edit().putString(Constants.AREA, viewState.data.data.zone).apply()
                            if (viewState.data.data.note.isNullOrEmpty()){
                                sharedPreferences.edit().putString(Constants.ABOUT, "").apply()
                            }else{
                                sharedPreferences.edit().putString(Constants.ABOUT, viewState.data.data.note).apply()
                            }
                            sharedPreferences.edit().putString(Constants.USERSTATUS, "true").apply()
                            Constants.isGuest = false

                            if (viewState.data.data.userType == Constants.CLIENT){
                                sharedPreferences.edit().putString(Constants.USERTYPE, Constants.CLIENT).apply()
                                startActivity(Intent(this, ServicesScreen::class.java))
                                finish()
                            }else{
                                sharedPreferences.edit().putString(Constants.USERTYPE, Constants.PROVIDER).apply()
                                startActivity(Intent(this, ProviderHomeScreen::class.java))
                                finish()
                            }


                        } else if (viewState.data.status == 401){
                            Toast.makeText(baseContext, "الرقم السرى غير صحيح", Toast.LENGTH_LONG).show()
                        } else if (viewState.data.status == 402){
                            Toast.makeText(baseContext, "هذا الهاتف غير موجود", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(baseContext, viewState.message, Toast.LENGTH_LONG).show()
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
    fun signUp(view: View) {
        startActivity(Intent(this, UserTypeScreen::class.java))
        finish()
    }
    fun skip(view: View) {
        updateToken()
        Constants.isGuest = true
        startActivity(Intent(this, ServicesScreen::class.java))
        finish()
    }
}