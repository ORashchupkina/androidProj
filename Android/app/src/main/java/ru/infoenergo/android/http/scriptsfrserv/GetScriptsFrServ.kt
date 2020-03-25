package ru.infoenergo.mobia.android.app.http.scriptsfrserv

import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.*
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.params.BasicHttpParams
import ru.infoenergo.android.common.Crypto
import ru.infoenergo.android.common.OnReadyCallbackOnlyString
import ru.infoenergo.mobia.android.app.Su
import java.util.concurrent.ExecutionException


class GetScriptsFrServ {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    @RequiresApi(Build.VERSION_CODES.O)
    fun get(login: String, onReadyCallbackOnlyString: OnReadyCallbackOnlyString) {
        // result: false, "" - либо некорректный JSON пришел (нет полей result или server_error) или result = fail
        // в остальных случаях: true, "" - и
        val errors = ArrayList<String>()
        uiScope.launch(Dispatchers.Main) {
            val d = uiScope.async(Dispatchers.IO) {
                try {

                    var result = ""

                    val url = Su.get_script_frserv
                    val httpGet = HttpGet(url.toString())

                    httpGet.setHeader("Accept", "application/json");
                    httpGet.setHeader("Content-type", "application/json");
                    val token_cr_64 = Crypto.getInstance().getTokenToSend()
                    httpGet.setHeader("token_cr_64", token_cr_64);
                    httpGet.setHeader("login", login);
                    val params = BasicHttpParams()
//                    HttpConnectionParams.setConnectionTimeout(params, 3000)
//                    HttpConnectionParams.setSoTimeout(params, 10000)
                    val response = DefaultHttpClient().execute(httpGet)
                    val statusLine = response.statusLine
                    val statusCode = statusLine.statusCode
                    if (statusCode == 200) {
                        result = String(response.entity.content.readBytes())
                    } else {
                        result = "{\"result\":\"fail\",\"server_error\": \"197626B4-9181-4FB6-AC3C-F9ECA12E3CAF\"}"
                    }
                    return@async result
                } catch (e : ClientProtocolException) {
                    return@async "{\"result\":\"fail\",\"server_error\": \"{DDFBA204-6CF1-4F15-8139-BD65584E3C80} err = " + e + "\"}"
                } catch (err: Exception) {
                    return@async "{\"result\":\"fail\",\"server_error\": \"DEC3198F-7A0A-446D-9EAB-785F2F66BA48 err = " + err + "\"}"
                }
            }
            val result = launch@ d.await()
            onReadyCallbackOnlyString.onReady(result)
        }
    }

    fun getScriptsFrServ(login : String) : String{
        try {

            var result = ""

            val url = Su.get_script_frserv
            val httpGet = HttpGet(url.toString())

            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-type", "application/json");
            val token_cr_64 = Crypto.getInstance().getTokenToSend()
            httpGet.setHeader("token_cr_64", token_cr_64);
            httpGet.setHeader("login", login);
            val params = BasicHttpParams()
//                    HttpConnectionParams.setConnectionTimeout(params, 3000)
//                    HttpConnectionParams.setSoTimeout(params, 10000)
            val response = DefaultHttpClient().execute(httpGet)
            val statusLine = response.statusLine
            val statusCode = statusLine.statusCode
            if (statusCode == 200) {
                result = String(response.entity.content.readBytes())
            } else {
                result = "{\"result\":\"fail\",\"server_error\": \"197626B4-9181-4FB6-AC3C-F9ECA12E3CAF\"}"
            }
            return result
        } catch (e : ClientProtocolException) {
            return "{\"result\":\"fail\",\"server_error\": \"{DDFBA204-6CF1-4F15-8139-BD65584E3C80} err = " + e + "\"}"
        } catch (err: Exception) {
            return "{\"result\":\"fail\",\"server_error\": \"DEC3198F-7A0A-446D-9EAB-785F2F66BA48 err = " + err + "\"}"
        }
    }

    fun runAsyncGetScriptsFrServ(login : String): String {
        var result = ""
        val taskSqlExecuterAsync = TaskGetScriptsFrServAsync()
        try {
            result = taskSqlExecuterAsync.execute(login).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            Log.e(tagError, "{36CEBBA3-31CB-49A6-A546-FA1A3AC92BB6} " + e)
            return "{\"result\":\"fail\",\"server_error\": \"{36CEBBA3-31CB-49A6-A546-FA1A3AC92BB6} err = " + e + "\"}"
        } catch (e: ExecutionException) {
            e.printStackTrace()
            Log.e(tagError, "{83FD97E6-314D-4CB5-8D5D-BDB29F1683FC} " + e)
            return "{\"result\":\"fail\",\"server_error\": \"{83FD97E6-314D-4CB5-8D5D-BDB29F1683FC} err = " + e + "\"}"
        }

        return result
    }

    inner class TaskGetScriptsFrServAsync : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String {
            var result = ""
            try {
                result = getScriptsFrServ(params[0])
            } catch (e: Exception) {
                Log.w(tagWarn, "{911670D9-9823-40B6-9F49-9CF9AA81168B} " + this.javaClass.name + " " + e)
                return "{\"result\":\"fail\",\"server_error\": \"{911670D9-9823-40B6-9F49-9CF9AA81168B} err = " + e + "\"}"
            }
            return result
        }

        override fun onPostExecute(aString: String) {
        }

        override fun onPreExecute() {
        }

    }

    internal var fullNameClass = this.javaClass.toString()
    internal var tagError = "error." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)
    internal var tagInfo = "information." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)
    internal var tagWarn = "warning." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)
}