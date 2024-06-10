package com.mohamed.aykhedmah.view.common.search

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
import com.land.services.data.model.response.search.SearchResponse
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.utils.ViewState
import com.mohamed.aykhedmah.view.common.base.BaseScreen
import com.mohamed.aykhedmah.view.common.providerdetails.ProviderDetailsScreen
import com.mohamed.aykhedmah.view.common.providers.ProvidersViewModel
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_search_screen.*
import javax.inject.Inject

class SearchScreen : BaseScreen() {
    private lateinit var mAdapter: SearchAdapter
    private lateinit var dialog: AlertDialog
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var providersViewModel: ProvidersViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var searchKeyWord: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_screen)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "نتائج البحث"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_right_black_24dp)

        searchKeyWord = intent.getStringExtra("searchKeyWord")
        mAdapter = SearchAdapter()
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.adapter = mAdapter

        mAdapter.onItemClickListener = object :
            SearchAdapter.OnItemClickListener {
            override fun setOnItemClickListener(id: Int) {
                val intent = Intent(this@SearchScreen, ProviderDetailsScreen::class.java)
                intent.putExtra("id", "$id")
                startActivity(intent)
            }
        }
        dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Please Wait...")
            .build()

        configViewModel()
        search()
    }

    private fun configViewModel() {
        providersViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProvidersViewModel::class.java)
    }


    private fun search() {

        providersViewModel.search(sharedPreferences.getString(Constants.ACCESS_TOKEN, "")!!, "$searchKeyWord").observe(this,
            Observer<ViewState<SearchResponse>>
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