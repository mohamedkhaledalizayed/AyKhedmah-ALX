package com.mohamed.aykhedmah.view.common.base

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.utils.LocaleHelper
import dagger.android.support.DaggerAppCompatActivity

open class BaseScreen : DaggerAppCompatActivity() {


    var aboutApp: MutableLiveData<String> = MutableLiveData()
    val mListItems: ArrayList<Any> = ArrayList()
    val ITEMS_PER_AD = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    fun addAdMobBannerAds() {
        var i: Int = 5
        while (i <= mListItems.size) {
            val adView = AdView(this)
            adView.adSize = AdSize.BANNER
            adView.adUnitId = Constants.banner_ad
            mListItems.add(i, adView)
            i += ITEMS_PER_AD
        }
    }

    fun loadBannerAds() {
        //Load the first banner ad in the items list (subsequent ads will be loaded automatically in sequence).
        loadBannerAd(ITEMS_PER_AD)
    }

    private fun loadBannerAd(index: Int) {
        if (index >= mListItems.size) {
            return
        }
        val item = mListItems[index] as? AdView
            ?: throw ClassCastException("Expected item at index $index to be a banner ad ad.")
        val adView = item

        // Set an AdListener on the AdView to wait for the previous banner ad
        // to finish loading before loading the next ad in the items list.
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                // The previous banner ad loaded successfully, call this method again to
                // load the next ad in the items list.
                loadBannerAd(index + ITEMS_PER_AD)
            }

            fun onAdFailedToLoad(errorCode: Int) {
                // The previous banner ad failed to load. Call this method again to load
                // the next ad in the items list.
                Log.e(
                    "MainActivity", "The previous banner ad failed to load. Attempting to"
                            + " load the next banner ad in the items list."
                )
                loadBannerAd(index + ITEMS_PER_AD)
            }
        }

        // Load the banner ad.
        adView.loadAd(AdRequest.Builder().build())
    }

    override fun onResume() {
        for (item in mListItems) {
            if (item is AdView) {
                item.resume()
            }
        }
        super.onResume()
    }

    override fun onPause() {
        for (item in mListItems) {
            if (item is AdView) {
                item.pause()
            }
        }
        super.onPause()
    }

    override fun onDestroy() {
        for (item in mListItems) {
            if (item is AdView) {
                item.destroy()
            }
        }
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        LocaleHelper().setLocale(this, "ar")
    }
}
