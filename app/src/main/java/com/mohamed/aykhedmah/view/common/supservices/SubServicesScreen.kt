package com.mohamed.aykhedmah.view.common.supservices

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.data.network.response.subservices.SubServicesResponse
import com.mohamed.aykhedmah.utils.ViewState
import com.mohamed.aykhedmah.view.common.base.BaseScreen
import com.mohamed.aykhedmah.view.common.providers.ProvidersScreen
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_sub_services_screen.*
import javax.inject.Inject

class SubServicesScreen : BaseScreen() {

    private lateinit var dialog: AlertDialog

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var subServicesViewModel: SubServicesViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var mAdapter: SubServicesAdapter
    private lateinit var id: String
    private lateinit var title: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_services_screen)
        id = intent.getStringExtra("id").toString()
        title = intent.getStringExtra("title").toString()

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = title
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_right_black_24dp)

        mAdapter = SubServicesAdapter()
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.adapter = mAdapter

        mAdapter.onItemClickListener = object :
            SubServicesAdapter.OnItemClickListener {
            override fun setOnItemClickListener(_id: Int, title: String) {

                    val intent = Intent(this@SubServicesScreen, ProvidersScreen::class.java)
                    intent.putExtra("id", "$_id")
                    intent.putExtra("title", title)
                    startActivity(intent)

            }
        }
        dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Please Wait...")
            .build()

        configViewModel()
        getSubServices()
    }

    private fun configViewModel() {
        subServicesViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SubServicesViewModel::class.java)
    }


    private fun getSubServices() {

        subServicesViewModel.getSubServices(id).observe(this,
            Observer<ViewState<SubServicesResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {
                            if (viewState.data.data.isEmpty()) {
                                recycler_view.visibility = View.GONE
                                empty_layout.visibility = View.VISIBLE
                            } else {
                                mListItems.addAll(viewState.data.data)
                                addAdMobBannerAds()
                                mAdapter.submitList(mListItems)
                                loadBannerAds()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}