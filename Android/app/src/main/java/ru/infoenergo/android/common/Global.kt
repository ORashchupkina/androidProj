package ru.infoenergo.android.common

class Global private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: Global? = null

        fun getInstance(): Global {
            return INSTANCE ?: synchronized(this) {
                Global().also {
                    INSTANCE = it
                }
            }
        }
    }

    var login = ""
    var password_hash = ""
    var imei = ""

}