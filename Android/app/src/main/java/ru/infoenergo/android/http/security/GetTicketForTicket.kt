package ru.infoenergo.android.http.security

import android.os.Build
import androidx.annotation.RequiresApi
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.params.BasicHttpParams
import org.apache.http.params.HttpConnectionParams
import org.json.JSONObject
import ru.infoenergo.android.common.Crypto
import ru.infoenergo.android.common.OnReadyCallbackBoolean
import ru.infoenergo.mobia.android.app.Su
import java.lang.Exception

class GetTicketForTicket {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    @RequiresApi(Build.VERSION_CODES.O)
    fun get(key_file_dir: String, onReadyCallbackBoolean: OnReadyCallbackBoolean) {
        val errors = ArrayList<String>()
        uiScope.launch(Dispatchers.Main) {
            Crypto.getInstance().createKeys(key_file_dir)

            val d = uiScope.async(Dispatchers.IO) {
                try {
                    var result = ""
                    val pubKeyStr =
                        "-----BEGIN PUBLIC KEY-----" + Crypto.getInstance().getPubKeyStr() + "-----END PUBLIC KEY-----"
                    val jsonObject = JSONObject()
                    jsonObject.accumulate("mob_pub_key", pubKeyStr)
                    val url = Su.get_ticket_for_ticket
                    val httpPost = HttpPost(url.toString())
                    val se = StringEntity(jsonObject.toString())
                    httpPost.entity = se
                    httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader("Content-type", "application/json");
                    val params = BasicHttpParams()
                    HttpConnectionParams.setConnectionTimeout(params, 3000)
                    HttpConnectionParams.setSoTimeout(params, 10000)
                    val response = DefaultHttpClient(params).execute(httpPost)
                    val statusLine = response.statusLine
                    val statusCode = statusLine.statusCode
                    if (statusCode == 200) {
                        result = String(response.entity.content.readBytes())
                    } else {
                        result = "{'server_error': '9B795E12-9CFF-42BC-8D66-3A613A783F0C'}"
                    }
                    return@async result
                } catch (err: Exception) {
                    return@async "{\"server_error\": \"63DC6689-3D68-4D31-B7A4-620A9FFC6B0F\"}"
                }
            }
            val result = d.await()
            val mapper = ObjectMapper()
            val rootNode = mapper.readTree(result)
            if (rootNode.get("server_error") == null) {
                if (rootNode.get("ticket_for_ticket") != null) {
                    if (rootNode.get("serv_pub_key") != null) {
                        val serv_pub_key_str = rootNode.get("serv_pub_key").textValue()
                        val serv_pub_key = Crypto.getInstance().getPublicKeyFromString(serv_pub_key_str)
                        val ticket_for_ticket64 = rootNode.get("ticket_for_ticket").textValue()
                        Crypto.getInstance().setServKey(serv_pub_key)
                        val priv = Crypto.getInstance().getPrivKey()
                        try {
                            Crypto.getInstance().ticket_for_ticket = Crypto.getInstance().decrypt(
                                ticket_for_ticket64,
                                priv!!
                            )
                            onReadyCallbackBoolean.onReady(true, errors)
                        } catch (ex: Error) {
                            errors.add(ex.message!!)
                            onReadyCallbackBoolean.onReady(false, errors)
                        }
                    } else {
                        errors.add(rootNode.get("{2D24DB3E-7E7F-4718-A8B1-DB0BDB929E31}").textValue())
                        onReadyCallbackBoolean.onReady(false, errors)
                    }
                } else {
                    errors.add("{5BF790A2-FC23-4C6D-8D8F-7BEBD5302257}")
                    onReadyCallbackBoolean.onReady(false, errors)
                }
            } else {
                errors.add(rootNode.get("server_error").textValue())
                onReadyCallbackBoolean.onReady(false, errors)
            }
        }
    }
}