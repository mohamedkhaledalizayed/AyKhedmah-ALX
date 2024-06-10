package com.mohamed.aykhedmah.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.*

class LocaleHelper {


    fun setLocale(context: Context?,lang: String) {
        val locale = Locale(lang)

        Locale.setDefault(locale)


        if (Build.VERSION.SDK_INT >= 19) {

            val resources = context?.resources

            val configuration = resources?.configuration

            val displayMetrics = resources?.displayMetrics

            configuration?.setLocale(locale)

            resources?.updateConfiguration(configuration, displayMetrics)

        } else {
            val config = Configuration()

            config.locale = locale

            context?.resources?.updateConfiguration(
                config,

                context.resources.displayMetrics
            )

        }


    }
}
