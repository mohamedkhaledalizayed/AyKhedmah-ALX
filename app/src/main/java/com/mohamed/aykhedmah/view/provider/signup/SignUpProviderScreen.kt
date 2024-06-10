package com.mohamed.aykhedmah.view.provider.signup

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
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
import com.mohamed.aykhedmah.data.network.response.cities.CitiesResponse
import com.mohamed.aykhedmah.data.network.response.cities.CityData
import com.mohamed.aykhedmah.data.network.response.citieslist.CitiesList
import com.mohamed.aykhedmah.data.network.response.citieslist.City
import com.mohamed.aykhedmah.data.network.response.services.ServicesData
import com.mohamed.aykhedmah.data.network.response.services.ServicesResponse
import com.mohamed.aykhedmah.data.network.response.signupprovider.ProviderSignUpResponse
import com.mohamed.aykhedmah.data.network.response.subservices.SubData
import com.mohamed.aykhedmah.data.network.response.subservices.SubServicesResponse
import com.mohamed.aykhedmah.utils.ViewState
import com.mohamed.aykhedmah.view.common.base.BaseScreen
import com.mohamed.aykhedmah.view.common.login.LoginScreen
import com.mohamed.aykhedmah.view.common.socialmedia.SocialScreen
import com.mohamed.aykhedmah.view.provider.home.ProviderHomeScreen
import com.mohamed.aykhedmah.view.user.signup.AreasAdapter
import com.mohamed.aykhedmah.view.user.signup.CitiesAdapter
import com.mohamed.aykhedmah.view.user.signup.ServicesSpinnerAdapter
import com.mohamed.aykhedmah.view.user.signup.SignUpViewModel
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_sign_up_provider_screen.*
import kotlinx.android.synthetic.main.activity_sign_up_provider_screen.et_email
import kotlinx.android.synthetic.main.activity_sign_up_provider_screen.et_fullname
import kotlinx.android.synthetic.main.activity_sign_up_provider_screen.et_password
import kotlinx.android.synthetic.main.activity_sign_up_provider_screen.et_phone
import kotlinx.android.synthetic.main.activity_sign_up_provider_screen.sc_view_container
import kotlinx.android.synthetic.main.activity_sign_up_provider_screen.show_hide
import kotlinx.android.synthetic.main.activity_sign_up_provider_screen.sp_area
import kotlinx.android.synthetic.main.activity_sign_up_provider_screen.sp_city
import kotlinx.android.synthetic.main.activity_sign_up_user_screen.*
import javax.inject.Inject

class SignUpProviderScreen : BaseScreen() {

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


    private lateinit var servicesAdapter: ServicesSpinnerAdapter
    private lateinit var subAdapter: SupServicesAdapter
    private var services: MutableList<ServicesData> = mutableListOf()
    private val selectCategory = ServicesData(0, "", "", 0, 0, 0, "اختار التصنيف")
    private var supServices: MutableList<SubData> = mutableListOf()
    private val selectSubServices = SubData(0, "", "", 0, 0, 0, 0, "اختار الخدمة")
    private var serviceId = ""
    private var supServiceId = ""
    var token: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_provider_screen)
        dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage(getString(R.string.please_wait))
            .build()

        configViewModel()
        getCities()
        getServices()


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

        sp_services.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                if (services.size != 0) {
                    serviceId = services[i].id.toString()
                    getSubServices(serviceId)
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        sp_sup_services.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                if (supServices.size != 0) {
                    supServiceId = supServices[i].id.toString()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun configViewModel() {
        signUpViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SignUpViewModel::class.java)
    }

    private fun getServices() {

        signUpViewModel.getServices().observe(this,
            Observer<ViewState<ServicesResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {

                            services = viewState.data.data.toMutableList()
                            services.add(0, selectCategory)
                            servicesAdapter = ServicesSpinnerAdapter(services)

                            sp_services.adapter = servicesAdapter
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


    private fun getSubServices(id: String) {

        signUpViewModel.getSubServices(id).observe(this,
            Observer<ViewState<SubServicesResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {

                            supServices = viewState.data.data.toMutableList()
                            supServices.add(0, selectSubServices)
                            subAdapter = SupServicesAdapter(supServices)

                            sp_sup_services.adapter = subAdapter
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
            email.isEmpty() -> {
                Toast.makeText(this, "من فضلك ادخل البريد الالكترونى", Toast.LENGTH_LONG).show()
                return
            }
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
            sp_services.selectedItemPosition == 0 -> {
                Toast.makeText(this, "من فضلك اختر التصنيف اولا", Toast.LENGTH_LONG).show()
                return
            }
            sp_sup_services.selectedItemPosition == 0 -> {
                Toast.makeText(this, "من فضلك اختر الخدمة اولا", Toast.LENGTH_LONG).show()
                return
            }

        }



        val myMap: Map<String, String> = mapOf(
            "name" to fullname,
            "email" to email,
            "phone" to phone,
            "password" to password,
            "latitude" to "29.9109079",
            "longitude" to "31.05665",
            "governorate_id" to "1",
            "city_id" to cityId,
            "area_id" to areaId,
            "address" to "address 1",
            "service_id" to serviceId,
            "sub_service_id" to supServiceId,
            "phone_token" to token!!
        )

        signUpViewModel.signUpProvider(myMap).observe(this,
            Observer<ViewState<ProviderSignUpResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {
                            sharedPreferences.edit().putString(Constants.ACCESS_TOKEN, viewState.data.data.token).apply()
                            sharedPreferences.edit().putString(Constants.FULLNAME, viewState.data.data.name).apply()
                            sharedPreferences.edit().putString(Constants.USERID, viewState.data.data.id.toString()).apply()
                            sharedPreferences.edit().putString(Constants.GOVERNMENT, viewState.data.data.governorate.title).apply()
                            sharedPreferences.edit().putString(Constants.CITY, viewState.data.data.city.title).apply()
                            sharedPreferences.edit().putString(Constants.AREA, viewState.data.data.area?.title).apply()
                            sharedPreferences.edit().putString(Constants.EMAIL, viewState.data.data.email).apply()
                            sharedPreferences.edit().putString(Constants.PHONE, viewState.data.data.phone).apply()
                            if (viewState.data.data.note.isNullOrEmpty()){
                                sharedPreferences.edit().putString(Constants.ABOUT, "").apply()
                            }else{
                                sharedPreferences.edit().putString(Constants.ABOUT, viewState.data.data.note).apply()
                            }
                            Constants.isGuest = false

//                            val intent = Intent(this, VerificationScreen::class.java)
//                            intent.putExtra("phone", phone)
//                            intent.putExtra(Constants.USERTYPE, Constants.PROVIDER)
//                            startActivity(intent)
//                            finish()


                            sharedPreferences.edit().putString(Constants.USERTYPE, Constants.PROVIDER).apply()
                            sharedPreferences.edit().putString(Constants.USERSTATUS, "true").apply()
                            showAlertDialog()
                        } else if (viewState.data.status == 409) {
                            Toast.makeText(baseContext, "هذا الهاتف موجود من قبل", Toast.LENGTH_LONG).show()
                            sc_view_container.visibility = View.GONE
                            error_container.visibility = View.VISIBLE
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

    private fun showAlertDialog() {

        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("تهانينا")
        alertDialog.setMessage("لقد تم إنشاء حسابك بنجاح سوف نقوم بمراجعة حسابك و الموافقة عليه فى اقل من 24 ساعة اثناء ذلك يرجى تكملة بياناتك الشخصية")
        alertDialog.setCancelable(false)
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "التالى"
        ) { dialog, _ ->
            dialog.dismiss()

            val intent = Intent(this, ProviderHomeScreen::class.java)
            startActivity(intent)
            finish()
        }
        alertDialog.show()
    }

    fun contactUs(view: View) {
        val intent = Intent(this, SocialScreen::class.java)
        startActivity(intent)
        finish()
    }

}