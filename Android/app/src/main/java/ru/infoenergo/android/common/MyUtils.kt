package ru.infoenergo.android.common

import android.os.Build
import android.text.Html
import android.text.Spanned

class MyUtils {
    companion object {
        // Для версий api 24+ Html.fromHtml дополнен FLAG. Иначе ругается на его отсутствие
        public fun fromHtml(s: String): Spanned {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY) // for 24 api and more
            } else {
                Html.fromHtml(s) // or for older api
            }
        }
    }
}