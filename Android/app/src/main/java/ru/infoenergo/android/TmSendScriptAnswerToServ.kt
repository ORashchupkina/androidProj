package ru.infoenergo.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.*
import org.json.JSONObject
import ru.infoenergo.android.common.OnReadyCallback
import ru.infoenergo.android.common.db_work.DBLocal
import ru.infoenergo.android.common.db_work.TSCRIPTSFRSERV
import ru.infoenergo.mobia.android.app.http.scriptsfrserv.SendAnswerScriptToServ
import java.lang.Exception
import java.util.*
import kotlin.concurrent.thread

class TmSendScriptAnswerToServ : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        timertick()
    }

    var isTimerWork = false

    internal var fullNameClass = this.javaClass.toString()
    internal var tagError = "error." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)
    internal var tagInfo =
        "information." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)
    internal var tagWarn = "warning." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)

    var placeCode = "0"
    var idScript = "";

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    @RequiresApi(Build.VERSION_CODES.O)
    fun timertick() {
        if (!isTimerWork) {
            isTimerWork = true
        } else {
            return
        }
        var arrayNoteToServ: Array<Any>? = null
        try {
            asyncSendScriptAnswerToServ(object : OnReadyCallback {
                override fun onReady() {
                    val res =
                        "{\"Error\":\"\", \"Result\":\"{4A690B7D-5C22-4240-ABFA-0E8103B10C0B}\"}"
                    Log.i(
                        tagInfo,
                        res
                    )
                    isTimerWork = false
                }
            })

//            var taskSendScriptAnswerToServ : TaskSendScriptAnswerToServ = TaskSendScriptAnswerToServ()
////            taskSendScriptAnswerToServ.execute().get()
        } catch (e: Exception) {
            val error =
                "{\"Error\":\"\", \"Result\":\"{1533238D-402D-461C-9B46-D99D7A2B7171} placeCode = " + placeCode + "idScript = " + idScript + " e = " + e + "\"}"
            Log.e(
                tagError,
                error
            )
            arrayNoteToServ = arrayOf(
                "fail_mob",
                idScript!!,
                error
            )
        } finally {

        }
        // TODO: если arrayNoteToServ != null то отправить на сервер инфу об этом (как-то нужно сообщить серверу, что на мобильном произошла ошибка и ответ по скрипту никогда не попадет на сервер)
        // ЭТО ОШИБКА ОБРАТНАЯ ПРЕДЫДУЩЕМУ todo

    }

//    inner class TaskSendScriptAnswerToServ : AsyncTask<Void, Void, Void>() {
//        override fun doInBackground(vararg p0: Void?): Void? {
//            var result : Array<Any>? = null
//            try {
//                sendScriptAnswerToServ()
//            } catch (e: java.lang.Exception) {
//                val error =
//                    "{\"Error\":\"\", \"Result\":\"{6985C5CC-B8A0-43FD-A6C8-ABC0F97652C6} " + this.javaClass.name + " " + e +"\"}"
//                Log.e(
//                    tagError,
//                    error
//                )
//            }
//return null
//        }
//
//        override fun onPostExecute(result: Void?) {
//        }
//
//        override fun onPreExecute() {
//        }
//
//    }

    fun asyncSendScriptAnswerToServ(callBack: OnReadyCallback) {
        uiScope.launch(Dispatchers.Main) {
            val d = uiScope.async(Dispatchers.IO) {
                sendScriptAnswerToServ()
                return@async
            }
            launch@ d.await()
        }
    }

    var arrayNoteToServ: Array<Any>? = null // TODO: правильно ли, что глобальная?
    fun sendScriptAnswerToServ() {
        placeCode = "1"
        // получение выполненного скрипта из таблицы TSCRIPTSFRSERV
        val resgetScriptByUUID = DBLocal.invoke().todoTScriptsFrServDao()
            .getSqriptExec()
        /*uiScope.launch(Dispatchers.Main) {
                val d = uiScope.async(Dispatchers.IO) {
                    val res = DBLocal.invoke().todoTScriptsFrServDao()
                        .getSqriptExec()
                    return@async res
                }
                resgetScriptExec = launch@ d.await()
            }*/
        placeCode = "2"
        var count = resgetScriptByUUID.size;
        placeCode = "3"
        for (scriptNotExec: TSCRIPTSFRSERV in resgetScriptByUUID) {
            try {
                placeCode = "4"
                idScript = scriptNotExec.uuid

                if (scriptNotExec.isExecute != null) {
                    sendAnswerById(scriptNotExec.isExecute!!)
                }


            } catch (e: Exception) {
                var extraPlaceCode = placeCode + " 1"
                val error =
                    "{\"Error\":\"\", \"Result\":\"{9EBB3A42-0BF5-4ECE-9A69-F434764660CB} idScript = " + idScript + " placeCode = " + placeCode + "; e = " + e + "\"}"
                placeCode = "10.11"
                Log.e(
                    tagError,
                    error
                )
                if (!idScript.equals("")) {
                    val resultSubStringFirst = (error as String).substring(
                        (error as String).indexOf("Error") + 9,
                        (error as String).indexOf("Result") - 3
                    ).replace("\"", " ")
                    val resultSubStringSecond =
                        (error as String).substring((error as String).indexOf("Result") + 9).replace("\"", " ")
                    val resultEdit =
                        "{\"Error\":\"" + resultSubStringFirst + "\",\"Result\":\"" + resultSubStringSecond + "\"}"

//                    // TODO: лучше вызвать функцию, что в цикле внутри try..catch запускается (ВЫЗВАЛИ)
//                    val sendAnswerScriptToServ: SendAnswerScriptToServ = SendAnswerScriptToServ()
//                    val resSendAnswer = sendAnswerScriptToServ.runAsyncSendAnswerScriptToServ(
//                        "answer",
//                        idScript,
//                        JSONObject(resultEdit)
//                    )
                    sendAnswerById(resultEdit, extraPlaceCode)
                }
            }
        }
        placeCode = "12"
    }

    fun sendAnswerById(answerToJson: String, extraPlaceCode : String = "") {
        val sendAnswerScriptToServ: SendAnswerScriptToServ = SendAnswerScriptToServ()
        placeCode = "5"
        val resSendAnswer = sendAnswerScriptToServ.runAsyncSendAnswerScriptToServ(
            "answer",
            idScript,
            JSONObject(answerToJson)
        )
        placeCode = "9"
        Log.i(
            tagInfo,
            "{2220FFC2-6F97-42BB-B7E0-C07C6A2C4018} resSendAnswer = " + resSendAnswer + " idScript = " + idScript + "; extraPlaceCode = " + extraPlaceCode
        )
        placeCode = "10"
        // проанализировать ответ: если fail, то запись в лог, а если ok, то заполнение поля IS_SEND

        if (resSendAnswer.getString("result").equals("fail_mob")) {
            // IS_SEND не трогаем, будем отправлять ответ на сервер вновь
            placeCode = "10.01"
            val error =
                "{\"Error\":\"\", \"Result\":\"{1F8F0FDF-B3D1-49B2-AD82-128223E0FD85} idScript = " + idScript + " result_serv = fail_mob; error = " + resSendAnswer.getString(
                    "server_error"
                ) + "; extraPlaceCode = " + extraPlaceCode + "\"}"
            placeCode = "10.02"
            Log.e(
                tagError,
                error
            )
//            continue //TODO: важно ли тут это
        } else if (resSendAnswer.getString("result").equals("fail")) {
            // IS_SEND заполняем
            placeCode = "10.1"
            val error =
                "{\"Error\":\"\", \"Result\":\"{54BD977D-E969-4BF6-B749-3862C9044CB9} сервер не принял ответ, но IS_SEND все равно ставим, т.к. иначе на сервере так и будут сыпаться ошибки. idScript = " + idScript + " result_serv = fail; error = " + resSendAnswer.getString(
                    "server_error"
                ) + "; extraPlaceCode = " + extraPlaceCode + "\"}"
            placeCode = "10.2"
            Log.e(
                tagError,
                error
            )
            val resUpdIsSendByIdScript = DBLocal.invoke().todoTScriptsFrServDao()
                .updIsSendByIdScript(error, idScript)
            if (resUpdIsSendByIdScript.compareTo(1) != 0) {
                placeCode = "10.05"
                val error =
                    "{\"Error\":\"\", \"Result\":\"{DB4D3DE5-CF90-47F1-84E9-9BC7E28E5656} поле IS_SEND скрипта с idScript = " + idScript + " не обновилось. Т.е. ответ будет отправляться на сервер, хотя ответ на сервер уже приходил" + "; extraPlaceCode = " + extraPlaceCode + "\"}"
                Log.e(
                    tagError,
                    error
                )
                placeCode = "10.06"
                arrayNoteToServ = arrayOf(
                    "fail_mob",
                    idScript!!,
                    error
                )
                placeCode = "10.08"
            }
        } else if (resSendAnswer.getString("result").equals("ok")) {
            // IS_SEND заполняем
            placeCode = "10.3"
            // заполнить поле IS_SEND
            val resUpdIsSendByIdScript = DBLocal.invoke().todoTScriptsFrServDao()
                .updIsSendByIdScript(Date().toString(), idScript)
            placeCode = "10.4"
            if (resUpdIsSendByIdScript.compareTo(1) != 0) {
                placeCode = "10.5"
                val error =
                    "{\"Error\":\"\", \"Result\":\"{62BC8D2F-E22B-402A-82F8-46A3FDB5893A} поле IS_SEND скрипта с idScript = " + idScript + " не обновилось. Т.е. ответ будет отправляться на сервер, хотя ответ на сервер уже приходил" + "; extraPlaceCode = " + extraPlaceCode + "\"}"
                Log.e(
                    tagError,
                    error
                )
                placeCode = "10.6"
                arrayNoteToServ = arrayOf(
                    "fail_mob",
                    idScript!!,
                    error
                )
                placeCode = "10.8"
            }
        } else {
            // IS_SEND не трогаем, будем отправлять ответ на сервер вновь
            placeCode = "10.9"
            val error =
                "{\"Error\":\"\", \"Result\":\"{9EBB3A42-0BF5-4ECE-9A69-F434764660CB} idScript = " + idScript + " result_serv = " + resSendAnswer.getString(
                    "result"
                ) + "; error = " + resSendAnswer.getString("server_error") + "; extraPlaceCode = " + extraPlaceCode + "\"}"
            placeCode = "10.11"
            Log.e(
                tagError,
                error
            )
//            continue //TODO: важно ли тут это
        }
        // TODO: если arrayNoteToServ != null то отправить на сервер инфу об этом (т.е. на сервер уже стучались, но IS_SEND не обновлен. это грозит тем, что мы повторно будем туже информацию отправлять)
        placeCode = "11"
    }
}
