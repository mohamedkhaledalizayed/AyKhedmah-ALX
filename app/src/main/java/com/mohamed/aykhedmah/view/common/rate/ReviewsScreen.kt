package com.mohamed.aykhedmah.view.common.rate

import android.app.AlertDialog
import android.content.SharedPreferences
import androidx.lifecycle.Observer
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.data.network.response.reviews.ReviewsResponse
import com.mohamed.aykhedmah.utils.ViewState
import com.mohamed.aykhedmah.view.common.base.BaseScreen
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_reviews_screen.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ReviewsScreen : BaseScreen() {

    private lateinit var mAdapter: ReviewsAdapter

    private lateinit var dialog: AlertDialog
    private var interstitialAdMob: InterstitialAd? = null
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var reviewsViewModel: ReviewsViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var id: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews_screen)
        id = intent.getStringExtra("id").toString()
        if (id == "null" || id == "") {
            id = sharedPreferences.getString(Constants.USERID, "")!!
        }
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "التقييمات"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_right_black_24dp)

        mAdapter = ReviewsAdapter()
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.adapter = mAdapter

        dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Please Wait...")
            .build()

        configViewModel()
        getReviews()
        initAdMob()

        val diff:Long = Date().time - sharedPreferences.getLong("click_time", 0L)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff)

        Log.e("Time", minutes.toString())
        if (minutes >= 60L){
            showAd()
        }
    }

    private fun configViewModel() {
        reviewsViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ReviewsViewModel::class.java)
    }

    private fun getReviews() {
        reviewsViewModel.getReviews(sharedPreferences.getString(Constants.ACCESS_TOKEN, "")!!, id)
            .observe(this,
                Observer<ViewState<ReviewsResponse>>
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

}