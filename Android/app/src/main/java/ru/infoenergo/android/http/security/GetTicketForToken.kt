package ru.infoenergo.android.http.security

import android.os.Build
import androidx.annotation.RequiresApi
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONObject
import ru.infoenergo.android.common.Crypto
import ru.infoenergo.android.common.OnReadyCallbackOnlyString
import ru.infoenergo.mobia.android.app.Su

class GetTicketForToken {
    //Вызываем в случае когда получен ответ от ф-ии GetTicketForTicket
    // Передает зашифрованные полученным serv_pub_key
    // пару login\password, uuid TicketForTicket
    // Получает зашифрованный mob_pub_key:
    //  'ticket_for_token': 'E5451F4D-32EB-4021-89E6-3FB18DC7072B'

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    @RequiresApi(Build.VERSION_CODES.O)
    fun get(login: String, pass_hash: String, imei: String, onReadyCallback: OnReadyCallbackOnlyString) {
        uiScope.launch(Dispatchers.Main) {
            val d = uiScope.async(Dispatchers.IO) {
                try {
                    var result = ""
                    val url = Su.get_ticket_for_token
                    val httpPost = HttpPost(url.toString())
                    val jsonObject = JSONObject()
                    var login_cr_b64 = Crypto.getInstance().encrypt(login, Crypto.getInstance().getServKey()!!)
                    var pass_hash_cr_b64 = Crypto.getInstance().encrypt(pass_hash, Crypto.getInstance().getServKey()!!)
                    jsonObject.accumulate("ticket_for_ticket", Crypto.getInstance().ticket_for_ticket)
                    jsonObject.accumulate("login_cr_b64", login_cr_b64)
                    jsonObject.accumulate("pass_hash_cr_b64", pass_hash_cr_b64)
                    jsonObject.accumulate("imei", imei)
                    val se = StringEntity(jsonObject.toString())
                    httpPost.entity = se
                    httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader("Content-type", "application/json");
                    val response = DefaultHttpClient().execute(httpPost)
                    val statusLine = response.statusLine
                    val statusCode = statusLine.statusCode
                    if (statusCode == 200) {
                        result = String(response.entity.content.readBytes())
                    } else {
                        result = "{'server_error': '0AAE4B0E-6C75-413C-8478-F5EFE9E61285'}"
                    }
                    return@async result
                } catch (ex: Exception) {
                    return@async "{}"
                }
            }
            val result = d.await()
            try {
                val mapper = ObjectMapper()
                val rootNode = mapper.readTree(result)
                if (rootNode.get("server_error") == null) {
                    if (rootNode.get("result") != null) {
                        if (rootNode.get("ticket_for_token") != null) {
                            val ticket_for_token_cr_b64 = rootNode.get("ticket_for_token").textValue()
                            val priv = Crypto.getInstance().getPrivKey()
                            Crypto.getInstance().ticket_for_token =
                                Crypto.getInstance().decrypt(ticket_for_token_cr_b64, priv!!)
                            onReadyCallback.onReady("")
                        }
                    }
                }
            } catch (ex: Exception) {
                onReadyCallback.onReady(ex.message!!)
            }
        }

    }
}