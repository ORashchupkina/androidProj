package ru.infoenergo.mobia.android.app

import java.net.URL

class Su private constructor() {

    companion object {

        val is_server_online = URL("http://" + Settings.server_ip + ":" + Settings.server_port + "/is_server_online/")
        val get_ticket_for_ticket = URL("http://" + Settings.server_ip + ":" + Settings.server_port + "/get_ticket_for_ticket/")
        val get_ticket_for_token = URL("http://" + Settings.server_ip + ":" + Settings.server_port + "/get_ticket_for_token/")
        val get_token = URL("http://" + Settings.server_ip + ":" + Settings.server_port + "/get_token/")
        val get_data = URL("http://" + Settings.server_ip + ":" + Settings.server_port + "/get_data/")
        val get_script_frserv = URL("http://" + Settings.server_ip + ":" + Settings.server_port + "/get_script_frserv/")
        val send_answerscript_toserv = URL("http://" + Settings.server_ip + ":" + Settings.server_port + "/send_answerscript_toserv/")
    }


}