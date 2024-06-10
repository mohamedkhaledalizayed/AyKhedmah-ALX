package com.mohamed.aykhedmah.view.common.providers

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import com.land.services.view.user.providerserviceslist.ProvidersAdapter
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.network.response.providers.ProvidersResponse
import com.mohamed.aykhedmah.utils.ViewState
import com.mohamed.aykhedmah.view.common.base.BaseScreen
import com.mohamed.aykhedmah.view.common.providerdetails.ProviderDetailsScreen
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_providers_screen.*
import javax.inject.Inject

class ProvidersScreen : BaseScreen() {

    private lateinit var mAdapter: ProvidersAdapter
    private lateinit var dialog: AlertDialog
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var providersViewModel: ProvidersViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var id: String
    private lateinit var title: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_providers_screen)
        id = intent.getStringExtra("id").toString()
        title = intent.getStringExtra("title").toString()

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = title
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_right_black_24dp)

        mAdapter = ProvidersAdapter()
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.adapter = mAdapter

        mAdapter.onItemClickListener = object :
            ProvidersAdapter.OnItemClickListener {
            override fun setOnItemClickListener(id: Int) {
                val intent = Intent(this@ProvidersScreen, ProviderDetailsScreen::class.java)
                intent.putExtra("id", "$id")
                startActivity(intent)
            }
        }
        dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Please Wait...")
            .build()

        configViewModel()
        getProviders()
    }

    private fun configViewModel() {
        providersViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProvidersViewModel::class.java)
    }


    private fun getProviders() {

        providersViewModel.getProviders(id).observe(this,
            Observer<ViewState<ProvidersResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {

                            if (viewState.data.data.isEmpty()){
                                recycler_view.visibility = View.GONE
                                empty_layout.visibility = View.VISIBLE
                            }else {
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
                        Toast.makeText(baseContext, "${viewState.message}", Toast.LENGTH_LONG).show()
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