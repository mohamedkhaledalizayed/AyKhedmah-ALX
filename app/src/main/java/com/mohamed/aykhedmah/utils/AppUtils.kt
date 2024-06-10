package com.mohamed.aykhedmah.utils

import android.os.Build
import android.text.Html
import android.widget.TextView
import androidx.annotation.StringRes

class AppUtils {

    fun setHtmlText(@StringRes text: Int, textView: TextView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.text = Html.fromHtml(
                textView.context.getString(text),
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            textView.text = Html.fromHtml(textView.context.getString(text))
        }
    }
}