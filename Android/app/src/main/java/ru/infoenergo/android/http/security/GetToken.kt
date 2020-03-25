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
import ru.infoenergo.android.common.OnReadyCallbackBoolean
import ru.infoenergo.mobia.android.app.Su
import java.lang.Exception

class GetToken {
    // Вызываем в случае когда получен ответ от ф-ии GetTicketForToken
    // или в случае когда какая-либо ф-я сервера сообщает о истечении
    // времени жизни токена
    // Передает TicketForToken
    // Получает зашифрованный новый билет TicketForToken и новый  зашифрованный Token:
    //  {
    //  'ticket_for_token': 'E5451F4D-32EB-4021-89E6-3FB18DC7072B',
    //  'token': 'ECC684D9-62E3-40D0-9797-2DAFB754E991'
    //  }

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    @RequiresApi(Build.VERSION_CODES.O)
    fun get(ticket_for_token: String, imei: String, onReadyCallbackBoolean: OnReadyCallbackBoolean) {
        uiScope.launch(Dispatchers.Main) {
            val d = uiScope.async(Dispatchers.IO) {
                try {
                    var result = ""
                    val url = Su.get_token
                    val httpPost = HttpPost(url.toString())
                    val jsonObject = JSONObject()
                    jsonObject.accumulate("ticket_for_token", ticket_for_token)
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
            val errors = ArrayList<String>()
            try {
                val mapper = ObjectMapper()
                val rootNode = mapper.readTree(result)
                if (rootNode.get("server_error") == null) {
                    if (rootNode.get("result") != null) {
                        if (rootNode.get("token") != null) {
                            val token_cr_b64 = rootNode.get("token").textValue()
                            val priv = Crypto.getInstance().getPrivKey()
                            Crypto.getInstance().token =
                                Crypto.getInstance().decrypt(token_cr_b64, priv!!)
                            onReadyCallbackBoolean.onReady(true, ArrayList())
                        } else {
                            errors.add("{8A874E42-C9E9-4B57-B5E8-C4527ABA31A9}")
                            onReadyCallbackBoolean.onReady(false, errors)
                        }
                    } else {
                        errors.add("{2EE2926B-186E-42A3-802E-805BC471F5B4}")
                        onReadyCallbackBoolean.onReady(false, errors)
                    }
                } else {
                    errors.add("{72860FE3-EA8B-4285-B34B-FF65B6DA191F}")
                    onReadyCallbackBoolean.onReady(false, errors)
                }
            } catch (ex: Exception) {
                errors.add(ex.message!!)
            }
        }

    }
}