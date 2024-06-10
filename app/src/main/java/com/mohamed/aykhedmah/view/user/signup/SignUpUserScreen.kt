package com.mohamed.aykhedmah.view.user.signup

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.data.network.response.arealist.AreaData
import com.mohamed.aykhedmah.data.network.response.arealist.AreasList
import com.mohamed.aykhedmah.data.network.response.citieslist.CitiesList
import com.mohamed.aykhedmah.data.network.response.citieslist.City
import com.mohamed.aykhedmah.data.network.response.signupuser.UserSignUpResponse
import com.mohamed.aykhedmah.utils.ViewState
import com.mohamed.aykhedmah.view.common.allservices.ServicesScreen
import com.mohamed.aykhedmah.view.common.base.BaseScreen
import com.mohamed.aykhedmah.view.common.login.LoginScreen
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_sign_up_user_screen.*
import javax.inject.Inject

class SignUpUserScreen : BaseScreen() {
    private var fullname: String = ""

    @Inject
    lateinit var signUpViewModel: SignUpViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var dialog: AlertDialog

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var phone: String = ""
    private var username: String = ""
    private var password: String = ""
    private var email: String = ""
    private var area: String = ""
    private lateinit var adapter: CitiesAdapter
    private lateinit var areasAdapter: AreasAdapter
    private var cities: MutableList<City> = mutableListOf()
    private val city = City(0, "", "اختار المدينة")
    private var areas: MutableList<AreaData> = mutableListOf()
    private val areaItem = AreaData(0, "", "اختار المنطقة")
    private var cityId = ""
    private var areaId = ""
    var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_user_screen)

        dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage(getString(R.string.please_wait))
            .build()

        configViewModel()
        getCities()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            token = task.result

        })

        sp_city.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                if (cities.size != 0) {
                    cityId = cities[i].id.toString()
                    getAreas(cityId)
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        sp_area.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                if (areas.size != 0) {
                    areaId = areas[i].id.toString()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun configViewModel() {
        signUpViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SignUpViewModel::class.java)
    }

    private var isHidden = true
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

    fun login(view: View) {
        startActivity(Intent(this, LoginScreen::class.java))
        finish()
    }

    fun signUp(view: View) {
        username = et_fullname.text.toString().trim()
        fullname = et_fullname.text.toString().trim()
        password = et_password.text.toString()
        email = et_email.text.toString().trim()
        phone = et_phone.text.toString().trim()

        when {
            username.isEmpty() -> {
                Toast.makeText(this, "من فضلك ادخل اسم المستخدم", Toast.LENGTH_LONG).show()
                return
            }
            fullname.isEmpty() -> {
                Toast.makeText(this, "من فضلك ادخل الاسم بالكامل", Toast.LENGTH_LONG).show()
                return
            }
//            email.isEmpty() -> {
//                Toast.makeText(this, "من فضلك ادخل البريد الالكترونى", Toast.LENGTH_LONG).show()
//                return
//            }
            password.isEmpty() -> {
                Toast.makeText(this, "من فضلك ادخل كلمة المرور", Toast.LENGTH_LONG).show()
                return
            }
            sp_city.selectedItemPosition == 0 -> {
                Toast.makeText(this, "من فضلك اختر المدينة اولا", Toast.LENGTH_LONG).show()
                return
            }
            sp_area.selectedItemPosition == 0 -> {
                Toast.makeText(this, "من فضلك اختر المنطقة اولا", Toast.LENGTH_LONG).show()
                return
            }

        }


        val myMap: Map<String, String> = mapOf(
            "name" to fullname,
            "email" to email,
            "phone" to phone,
            "password" to password,
            "governorate_id" to "1",
            "city_id" to cityId,
            "area_id" to areaId,
            "phone_token" to token!!
        )

        signUpViewModel.signUp(myMap).observe(this,
            Observer<ViewState<UserSignUpResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {
                            sharedPreferences.edit()
                                .putString(Constants.ACCESS_TOKEN, viewState.data.data.token)
                                .apply()
                            sharedPreferences.edit()
                                .putString(Constants.USERID, viewState.data.data.id.toString())
                                .apply()
                            sharedPreferences.edit().putString(Constants.GOVERNMENT, viewState.data.data.governorate.title).apply()
                            sharedPreferences.edit().putString(Constants.CITY, viewState.data.data.city.title).apply()
                            sharedPreferences.edit().putString(Constants.AREA, viewState.data.data.area.title).apply()
                            sharedPreferences.edit()
                                .putString(Constants.FULLNAME, viewState.data.data.name).apply()
                            sharedPreferences.edit()
                                .putString(Constants.EMAIL, viewState.data.data.email).apply()
                            sharedPreferences.edit()
                                .putString(Constants.PHONE, viewState.data.data.phone).apply()
                            if (viewState.data.data.note.isNullOrEmpty()){
                                sharedPreferences.edit().putString(Constants.ABOUT, "").apply()
                            }else{
                                sharedPreferences.edit().putString(Constants.ABOUT, viewState.data.data.note).apply()
                            }
                            Constants.isGuest = false

//                            val intent = Intent(this, VerificationScreen::class.java)
//                            intent.putExtra("phone", phone)
//                            intent.putExtra(Constants.USERTYPE, Constants.CLIENT)
//                            startActivity(intent)
//                            finish()

                            sharedPreferences.edit().putString(Constants.USERTYPE, Constants.CLIENT).apply()
                            sharedPreferences.edit().putString(Constants.USERSTATUS, "true").apply()
                            val intent = Intent(this, ServicesScreen::class.java)

                            startActivity(intent)
                            finish()
                        } else if (viewState.data.status == 409) {
                            Toast.makeText(
                                baseContext,
                                "هذا الهاتف موجود من قبل",
                                Toast.LENGTH_LONG
                            ).show()
                        }else if (viewState.data.status == 408) {
                            Toast.makeText(
                                baseContext,
                                "هذا البريد الالكترونى موجود من قبل",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(baseContext, viewState.message, Toast.LENGTH_LONG).show()
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

    private fun getCities() {

        signUpViewModel.getCitiesList().observe(this,
            Observer<ViewState<CitiesList>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {

                            cities = viewState.data.data.toMutableList()
                            cities.add(0, city)
                            adapter = CitiesAdapter(cities)

                            sp_city.adapter = adapter

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

    private fun getAreas(id: String) {

        signUpViewModel.getAreasList(id).observe(this,
            Observer<ViewState<AreasList>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {

                            areas = viewState.data.data.toMutableList()
                            areas.add(0, areaItem)
                            areasAdapter = AreasAdapter(areas)

                            sp_area.adapter = areasAdapter

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
}