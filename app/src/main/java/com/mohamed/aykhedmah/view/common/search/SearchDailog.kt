package com.mohamed.aykhedmah.view.common.search

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.view.common.allservices.ServicesScreen

import kotlinx.android.synthetic.main.fragment_search_dailog.*


class SearchDialog : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_dailog, container, false)
    }
    private var adLoader: AdLoader? = null
    private var keyWord: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adLoader = AdLoader.Builder(requireContext(), Constants.native_ad)
            .forNativeAd(object : NativeAd.OnNativeAdLoadedListener {
                private val background: ColorDrawable? = null
                override fun onNativeAdLoaded(nativeAd: NativeAd) {
                    val styles =
                        NativeTemplateStyle.Builder().withMainBackgroundColor(background).build()
                    nativeTemplateView.setStyles(styles)
                    nativeTemplateView.setNativeAd(nativeAd)

                    if (requireActivity().isDestroyed) {
                        nativeAd.destroy()
                        return
                    }
                }
            }).build()

        loadNativeAd()

        btn_search.setOnClickListener {
            keyWord = txt_search.text.toString()

            if (keyWord.isEmpty()) {
                Toast.makeText(context, "من فضلك ادخل كلمة البحث اولا", Toast.LENGTH_LONG).show()
            } else {
                val recipeDetailsActivity = activity as ServicesScreen
                recipeDetailsActivity.onSearchClickListener(keyWord)
                dismiss()
            }
        }
    }

    private fun loadNativeAd() {
        // Creating  an Ad Request
        val adRequest = AdRequest.Builder().build()
        // load Native Ad with the Request
        adLoader!!.loadAd(adRequest)
    }

}