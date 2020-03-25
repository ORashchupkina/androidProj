package ru.infoenergo.mobia.android.app

import java.net.URL

class Settings private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: Settings? = null

        fun getInstance(): Settings {
            return INSTANCE ?: synchronized(this) {
                Settings().also {
                    INSTANCE = it
                }
            }
        }
        val server_ip = "192.168.47.38"
        val server_port ="3003"

        val url = URL("http://" + server_ip + ":" + server_port + "/is_server_online/")
    }


}