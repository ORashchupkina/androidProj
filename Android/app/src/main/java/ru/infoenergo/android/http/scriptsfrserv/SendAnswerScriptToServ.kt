package ru.infoenergo.mobia.android.app.http.scriptsfrserv

import android.location.Criteria
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONObject
import ru.infoenergo.mobia.android.app.Su
import java.lang.Exception
import org.apache.http.params.BasicHttpParams
import org.apache.http.params.HttpConnectionParams
import ru.infoenergo.android.common.Crypto
import java.util.concurrent.ExecutionException


class SendAnswerScriptToServ {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    @RequiresApi(Build.VERSION_CODES.O)
    fun get(login: String, idScript : String, jsonObject : JSONObject) {
        val errors = ArrayList<String>()
        uiScope.launch(Dispatchers.Main) {
            val d = uiScope.async(Dispatchers.IO) {
                try {
                    var result = ""

                    val url = Su.send_answerscript_toserv
                    val httpPost = HttpPost(url.toString())

                    httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader("Content-type", "application/json");
                    val token_cr_64 = Crypto.getInstance().getTokenToSend()
                    httpPost.setHeader("token_cr_64", token_cr_64);
                    httpPost.setHeader("login", login)
                    httpPost.setHeader("id_script", idScript)
                    val se = StringEntity(jsonObject.toString())
                    httpPost.entity = se
                    val params = BasicHttpParams()
//                    HttpConnectionParams.setConnectionTimeout(params, 3000)
//                    HttpConnectionParams.setSoTimeout(params, 10000)
                    val response = DefaultHttpClient(params).execute(httpPost)
                    val statusLine = response.statusLine
                    val statusCode = statusLine.statusCode
                    if (statusCode == 200) {
                        result = String(response.entity.content.readBytes())
                    } else {
                        result = "{\"result\":\"fail\",\"server_error\": \"{ECB9B8DB-50F2-4A1E-97AE-9DFD6BF97342}\"}"
                    }
                    return@async result
                } catch (err: Exception) {
                    return@async "{\"result\":\"fail\",\"server_error\": \"{1FCFD117-3533-46F8-970A-C35FCDCA8607}\"}"
                }
            }
            val result = d.await()
        }
    }

    fun sendAnswerScriptToServ(typeAnswer : String, idScript : String, jsonObject : JSONObject) : JSONObject{
        try {
            var result = ""

            val url = Su.send_answerscript_toserv
            val httpPost = HttpPost(url.toString())

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            val token_cr_64 = Crypto.getInstance().getTokenToSend()
            httpPost.setHeader("token_cr_64", token_cr_64);
            httpPost.setHeader("id_script", idScript)
            httpPost.setHeader("type_answer", typeAnswer)
            val se = StringEntity(jsonObject.toString())
            httpPost.entity = se
            val params = BasicHttpParams()
            HttpConnectionParams.setConnectionTimeout(params, 3000)
            HttpConnectionParams.setSoTimeout(params, 10000)
            val response = DefaultHttpClient(params).execute(httpPost)
            val statusLine = response.statusLine
            val statusCode = statusLine.statusCode
            if (statusCode == 200) {
                result = String(response.entity.content.readBytes())
            } else {
                result = "{\"result\":\"fail_mob\",\"server_error\": \"{091F4472-1B5B-4F0C-8DB3-1C310A616461}\"}"
            }
            return JSONObject(result)
        } catch (err: Exception) {
            return JSONObject("{\"result\":\"fail_mob\",\"server_error\": \"{C8A3B4F8-FBC7-4069-B4E3-DE2BBC30DCB7} idScript = " + idScript +"\"}")
        }
    }

    fun runAsyncSendAnswerScriptToServ(typeAnswer : String, idScript : String, jsonObject : JSONObject): JSONObject {
        var result : JSONObject? = null
        val taskSendAnswerScriptToServ = TaskSendAnswerScriptToServ()
        try {
            result = taskSendAnswerScriptToServ.execute(typeAnswer, idScript, jsonObject).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            Log.e(tagError, "{D9DFBF37-D783-40F6-84DE-9C17171B9BB4} " + e)
            return JSONObject("{\"result\":\"fail_mob\",\"server_error\": \"{D9DFBF37-D783-40F6-84DE-9C17171B9BB4} err = " + e + "\"}")
        } catch (e: ExecutionException) {
            e.printStackTrace()
            Log.e(tagError, "{0D01A0B3-8C0E-4D77-A0FA-4B24EAC3575B} " + e)
            return JSONObject("{\"result\":\"fail_mob\",\"server_error\": \"{0D01A0B3-8C0E-4D77-A0FA-4B24EAC3575B} err = " + e + "\"}")
        }

        return result
    }

    inner class TaskSendAnswerScriptToServ : AsyncTask<Any, Void, JSONObject>() {

        override fun doInBackground(vararg params: Any): JSONObject {
            var result : JSONObject? = null
            try {
                result = sendAnswerScriptToServ(params[0] as String, params[1] as String, params[2] as JSONObject)
            } catch (e: Exception) {
                Log.w(tagWarn, "{22B62CF5-07AA-464F-AC56-57CFC0A5E384} " + this.javaClass.name + " " + e)
                return JSONObject("{\"result\":\"fail_mob\",\"server_error\": \"{22B62CF5-07AA-464F-AC56-57CFC0A5E384} err = " + e + "\"}")
            }
            return result
        }

        override fun onPostExecute(aJSONObject: JSONObject) {
        }

        override fun onPreExecute() {
        }

    }

    internal var fullNameClass = this.javaClass.toString()
    internal var tagError = "error." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)
    internal var tagInfo = "information." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)
    internal var tagWarn = "warning." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)
}