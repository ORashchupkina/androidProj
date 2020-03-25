package ru.infoenergo.mobiaapp.http.security

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.*
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.params.BasicHttpParams
import org.apache.http.params.HttpConnectionParams
import ru.infoenergo.android.common.OnReadyCallbackOnlyBoolean
import ru.infoenergo.mobia.android.app.Su


class IsServOn {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    @RequiresApi(Build.VERSION_CODES.O)
    fun get(onReadyCallback: OnReadyCallbackOnlyBoolean) {
        var res = false;
        uiScope.launch(Dispatchers.Main) {
            val d = uiScope.async(Dispatchers.IO) {
                try {
                    var result = false
                    val url = Su.is_server_online
                    val httpGet = HttpGet(url.toString())
                    httpGet.setHeader("Accept", "application/json");
                    httpGet.setHeader("Content-type", "application/json");


                    val httpParameters = BasicHttpParams()
                    val timeoutConnection = 10000
                    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection)
                    val timeoutSocket = 10000
                    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket)
                    val client = DefaultHttpClient(httpParameters)

                    val response = /*DefaultHttpClient()*/client.execute(httpGet)
                    val statusLine = response.statusLine
                    val statusCode = statusLine.statusCode
                    if (statusCode == 200) {
                        result = true
                    } else {
                        result = false
                    }
                    return@async result
                } catch (err: Exception) {
                    var e = err;
                    return@async false
                }
            }
            res = launch@ d.await()
            onReadyCallback.onReady(res);
        }
    }
}