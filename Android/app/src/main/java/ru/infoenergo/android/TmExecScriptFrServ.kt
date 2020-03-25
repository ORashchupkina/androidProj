package ru.infoenergo.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.RoomDatabase
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.*
import org.json.JSONObject
import ru.infoenergo.android.common.OnReadyCallback
import ru.infoenergo.android.common.OnReadyCallbackArray
import ru.infoenergo.android.common.OnReadyCallbackArrayNull
import ru.infoenergo.android.common.db_work.DBLocal
import ru.infoenergo.android.common.db_work.TSCRIPTSFRSERV
import ru.infoenergo.mobia.android.app.http.scriptsfrserv.SendAnswerScriptToServ
import java.lang.Runnable
import java.util.*

class TmExecScriptFrServ : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        timertick()
    }

    var isTimerWork = false

    internal var fullNameClass = this.javaClass.toString()
    internal var tagError = "error." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)
    internal var tagInfo =
        "information." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)
    internal var tagWarn = "warning." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    @RequiresApi(Build.VERSION_CODES.O)
    fun timertick() {
        if (!isTimerWork) {
            isTimerWork = true
        } else {
            return
        }
        try {
            asyncExecScriptFrServ(object : OnReadyCallback {
                override fun onReady() {
                    val res =
                        "{\"Error\":\"\", \"Result\":\"{B1C5C6BF-5764-4D11-9D2C-D1950C60A546}\"}"
                    Log.i(
                        tagInfo,
                        res
                    )
                    isTimerWork = false
                }
            })

//            val taskExecScriptFrServ: TaskExecScriptFrServ = TaskExecScriptFrServ()
//            taskExecScriptFrServ.execute().get()
        } catch (e: java.lang.Exception) {
            val error =
                "{\"Error\":\"\", \"Result\":\"{0D5F4BE1-99F0-4698-BA55-5C934DAD9FFE} e = " + e + "\"}"
            Log.e(
                tagError,
                error
            )
        }
    }

    fun execScriptFrServ(
        scriptNotExec: TSCRIPTSFRSERV,
        onReadyCallbackArray: OnReadyCallbackArrayNull
    ) {
        var arrayOnReadyCallBack: Array<Any>? = null
        var placeCode = "0";

        var idScript: String = ""
        var answer = ""
        var script = ""
        var typeScript = ""
        var dbName = ""
        var tName = ""
        var date: Date? = null
        try {
            DBLocal.invoke().runInTransaction() {
                idScript = scriptNotExec.uuid
                script = scriptNotExec.script
                typeScript = scriptNotExec.typeScript
                dbName = scriptNotExec.dbName
                date = scriptNotExec.dtc
                var resExecQuery: Array<Any> = execQuery(idScript, dbName, typeScript, script)
                if (resExecQuery[0].equals("ok")) {
                    // значит, запрос выполнился и нужно заполнить поле IS_EXECUTE
                    val resUpdIsExecute = DBLocal.invoke()
                        .todoTScriptsFrServDao()
                        .updIsExecuteByIdScript(resExecQuery[2] as String, idScript)
                    if (resUpdIsExecute.compareTo(1) != 0) {
                        val error =
                            "{\"Error\":\"\", \"Result\":\"{52A4063C-87A6-4ABA-AA9B-15FEB3CDF670} placeCode = " + placeCode + "idScript = " + idScript + " resUpdIsExecute = " + resUpdIsExecute + "\"}"
                        Log.e(
                            tagError,
                            error
                        )
                        arrayOnReadyCallBack = arrayOf(
                            "fail_mob",
                            idScript!!,
                            error
                        )
                        return@runInTransaction//@Runnable
                    } else {
                        Log.i(
                            tagInfo,
                            "{0FC88D91-2D4A-4528-8A68-B78F48CBFA55} idScript = " + idScript + " placeCode = " + placeCode + " resUpdIsExecute = " + resUpdIsExecute
                        )
                        return@runInTransaction//@Runnable
                    }
                } else if (resExecQuery[0].equals("fail_mob")) {
                    // значит, запрос выполнился с ошибкой и нужно заполнить поле IS_EXECUTE
                    val resUpdIsExecute = DBLocal.invoke()
                        .todoTScriptsFrServDao()
                        .updIsExecuteByIdScript(resExecQuery[2] as String, idScript)
                    if (resUpdIsExecute.compareTo(1) != 0) {
                        val error =
                            "{\"Error\":\"\", \"Result\":\"{8C87C12B-F7D6-4148-B92B-BC437268F37E} resUpdIsExecute = " + resUpdIsExecute + "idScript = " + idScript + " placeCode = " + placeCode + " resUpdIsExecute = " + resUpdIsExecute + "\"}"
                        Log.e(
                            tagError,
                            error
                        )
                        arrayOnReadyCallBack = arrayOf(
                            "fail_mob",
                            idScript!!,
                            error
                        )
                        return@runInTransaction
                    } else {
                        Log.e(
                            tagError,
                            "{5CD2CA64-00CD-4C27-A91F-540014871D13} idScript = " + idScript + " placeCode = " + placeCode + " resUpdIsExecute = " + resUpdIsExecute
                        )
                        return@runInTransaction
                    }
                } else {
                    // Неизвестный тип результата
                    // TODO: может быть запись результата есть смысл вынести из if в общий блок ?
                    var result = ""
                    if (resExecQuery.size == 3) {
                        if (resExecQuery[2] != null) {
                            result = resExecQuery[2] as String
                        }
                    }
                    val errorFirst =
                        "{\"Error\":\"\", \"Result\":\"{4DAF0630-F60C-4769-8B19-A6452FEFC8FB} неизвестный тип результата: resExecQuery[0] = " + resExecQuery[0] + " placeCode = " + placeCode + ", result = " + result + "\"}" // мы это в случае чего отправим на сервер с известным id скрипта. поэтому ошибку пишем в Result
                    Log.e(
                        tagError,
                        errorFirst
                    )
                    val resUpdIsExecute = DBLocal.invoke()
                        .todoTScriptsFrServDao()
                        .updIsExecuteByIdScript(errorFirst, idScript)
                    if (resUpdIsExecute.compareTo(1) != 0) {
                        val error =
                            "{\"Error\":\"\", \"Result\":\"{F837BCDC-ADBC-4A10-8539-AC617DCE9973} " + "idScript = " + idScript + " placeCode = " + placeCode + " resUpdIsExecute = " + resUpdIsExecute + "; " + errorFirst + "\"}"
                        Log.e(
                            tagError,
                            error
                        )
                        arrayOnReadyCallBack = arrayOf(
                            "fail_mob",
                            idScript!!,
                            error
                        )
                        return@runInTransaction
                    } else {
                        Log.e(
                            tagError,
                            "{1917EF73-CE57-46AD-8396-4C1C4005DF6E} idScript = " + idScript + " placeCode = " + placeCode + " resUpdIsExecute = " + resUpdIsExecute
                        )
                    }
                    return@runInTransaction
                }
            }
        } catch (e: Exception) {
            //TODO: описать ошибку в логе и записать результат в таблицу скриптов на мобильном устройстве
            val resultExec =
                "{CDE3B0D0-6953-4773-B319-AFCC3B090FB3} error exist: placeCode = " + placeCode + " e = " + e
            val error =
                "{\"Error\":\"\", \"Result\":\"" + resultExec + "\"}"
            Log.e(
                tagError,
                error
            )
            try {
                val resUpdIsExecute = DBLocal.invoke()
                    .todoTScriptsFrServDao()
                    .updIsExecuteByIdScript(error, idScript)
                if (resUpdIsExecute.compareTo(1) != 0) {
                    val error =
                        "{\"Error\":\"\", \"Result\":\"{B9406166-980D-4DD6-A673-37D0F2F44CC3} " + "idScript = " + idScript + " placeCode = " + placeCode + " resUpdIsExecute = " + resUpdIsExecute + ";  " + resultExec + "\"}"
                    Log.e(
                        tagError,
                        error
                    )
                    arrayOnReadyCallBack = arrayOf(
                        "fail_mob",
                        idScript!!,
                        error
                    )
                    return
                } else {
                    Log.e(
                        tagError,
                        "{561F59D4-C8FD-4AAF-ACC4-35A21EDE6C26} idScript = " + idScript + " placeCode = " + placeCode + " resUpdIsExecute = " + resUpdIsExecute + ";  " + resultExec
                    )
                }
            } catch (e: java.lang.Exception) {
                val error =
                    "{\"Error\":\"\", \"Result\":\"{A5FABDEC-3065-4D07-84F4-9426C4D135A5} idScript = " + idScript + " placeCode = " + placeCode + " e = " + e + ";  " + resultExec + "\"}"
                Log.e(
                    tagError,
                    error
                )
                arrayOnReadyCallBack = arrayOf(
                    "fail_mob",
                    idScript!!,
                    error
                )
            }
            return
        } finally {
//            DBLocal.close()
            Log.i(
                tagInfo,
                "{E913072B-2929-453F-AE9C-F8CA652A4797} placeCode = " + placeCode + " isOpen = " + DBLocal.invoke().isOpen + " inTransaction = " + DBLocal.invoke().inTransaction()
            )
//            DBLocal.invoke().close()
            onReadyCallbackArray.onReady(arrayOnReadyCallBack)
            return
        }
    }

    fun execQuery(
        idScript: String,
        dbName: String,
        typeScript: String,
        script: String
    ): Array<Any> {
        var roomDatabase: RoomDatabase? = null
        //dbName
        if (dbName.equals("local.db")) {
            roomDatabase = DBLocal.invoke()
            try {
                if (typeScript.equals("SELECT")) {
                    var cur: Cursor = roomDatabase.query(SimpleSQLiteQuery(script))//ID_JOBS_METERING_TYPE
                    return arrayOf(
                        "ok",
                        idScript,
                        "{\"Result\":" + convertCurInString(cur) + ", \"Error\":\"\"}"
                    )
                } else if (typeScript.equals("INSERT")) {
                    var countIns: Long = roomDatabase.compileStatement(script).executeInsert()
                    if (countIns.compareTo(0) != 0) {
                        return arrayOf(
                            "ok",
                            idScript,
                            "{\"Result\":\"{5931DC54-DA8A-4717-B3C1-1AEA1FAE59BF} insert execute without errors\", \"Error\":\"\"}"
                        )
                    } else {
                        return arrayOf(
                            "fail_mob",
                            idScript,
                            "{\"Result\":\"{0E646A04-9487-4C67-8540-3625243C8B1B} insert failed: countIns = " + countIns + "\", \"Error\":\"\"}"
                        )
                    }
                } else if (typeScript.equals("UPDATE") || typeScript.equals("DELETE")) {
                    var countChange: Int = roomDatabase.compileStatement(script).executeUpdateDelete()
                    roomDatabase.query(SimpleSQLiteQuery("VACUUM"))
                    if (countChange.compareTo(0) != 0) {
                        return arrayOf(
                            "ok",
                            idScript,
                            "{\"Result\":\"{A25A2C34-3F1A-4942-9CCB-4113E24B3671} " + typeScript + " execute without errors\", \"Error\":\"\"}"
                        )
                    } else {
                        return arrayOf(
                            "fail_mob",
                            idScript,
                            "{\"Result\":\"{E6890D9D-D493-4B48-8A06-8227BBCB0EAC} " + typeScript + " failed: countChange = " + countChange + "\", \"Error\":\"\"}"
                        )
                    }
                } else {
                    val error =
                        "{\"Error\":\"\",\"Result\":\"{EF13D18B-7D02-40E3-9E31-162F00069BB4} incorrect typeScript = " + typeScript + "\"}"
                    Log.e(tagError, error)
                    return arrayOf(
                        "fail_mob",
                        idScript,
                        error
                    )
                }
            } catch (e: java.lang.Exception) {
                val error =
                    "{\"Error\":\"\", \"Result\":\"{AD34CDFF-1554-4407-B72B-33623649F7C2} " + typeScript + " failed: e = " + e + " in script with id: " + idScript + "\"}"
                Log.e(
                    tagError,
                    error
                )
                return arrayOf(
                    "fail_mob",
                    idScript,
                    error
                )
            }
        } else {
            val error =
                "{\"Error\":\"\", \"Result\":\"{4AC47BF6-58E4-4F0F-9669-F60AE9D4AB41} incorrect DB_NAME: " + dbName + " у скрипта с id: " + idScript + "\"}"
            Log.e(
                tagError,
                error
            )
            return arrayOf(
                "fail_mob",
                idScript,
                error
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    fun convertCurInString(cur: Cursor): String {
        var result = ""
        var countRow = cur.count
        if (countRow.compareTo(1) > 0) {
            result += "["
        }
        while (cur.moveToNext()) {
            result += "{"
            // перебор строк
            var countColumn = cur.columnCount - 1

            while (countColumn > -1) {
                result += "\"" + cur.getColumnName(countColumn) + "\":"
                // перебор значений в строке
                if (cur.getType(countColumn) != 4) { // если не байты
                    if (cur.getType(countColumn) == 1)
                        result += "\"" + cur.getInt(countColumn) + "\"," // одно поле в строке
                    else if (cur.getType(countColumn) == 3)
                        result += "\"" + cur.getString(countColumn) + "\"," // одно поле в строке
                    else if (cur.getType(countColumn) == 0)
                        result += "\" \","
                } else
                    result += "\"\","
                countColumn--
            }

            result = result.substring(0, result.lastIndexOf(",")) + "},\n" // конец строки
        }

        if (result.equals(""))
            result += "{\"res\":\"empty res\"}"
        else{
            result = result.substring(0, result.lastIndexOf(","))
            if (countRow.compareTo(1) > 0) {
                result += "]"
            }
        }
        return "$result"
    }

    fun asyncExecScriptFrServ(callBack: OnReadyCallback) {
        uiScope.launch(Dispatchers.Main) {
            val d = uiScope.async(Dispatchers.IO) {
                execScriptFrServ()
                return@async
            }
            launch@ d.await()
        }
    }

    fun execScriptFrServ() {
        // получение невыполненного скрипта из таблицы TSCRIPTSFRSERV
        var resgetScriptNotExec: List<TSCRIPTSFRSERV>? = null
        resgetScriptNotExec = DBLocal.invoke().todoTScriptsFrServDao()
            .getSqriptNotExec()
        if (resgetScriptNotExec != null) {
            var count = resgetScriptNotExec!!.size;
            for (scriptNotExec: TSCRIPTSFRSERV in resgetScriptNotExec!!) {
                execScriptFrServ(scriptNotExec, object : OnReadyCallbackArrayNull {
                    override fun onReady(result: Array<Any>?) {
                        count--
                        if (result != null) {
                            // отправить информацию на сервер в промежуточный ответ
                            // отправляется здесь только та информация, которая не была записана в бд на мобильном устройстве
                            val sendAnswerScriptToServ: SendAnswerScriptToServ = SendAnswerScriptToServ()
                            val resultSubStringFirst = (result[2] as String).substring(
                                (result[2] as String).indexOf("Error") + 9,
                                (result[2] as String).indexOf("Result") - 3
                            ).replace("\"", " ")
                            val resultSubStringSecond =
                                (result[2] as String).substring((result[2] as String).indexOf("Result") + 9)
                                    .replace("\"", " ")
                            val resultEdit =
                                "{\"Error\":\"" + resultSubStringFirst + "\",\"Result\":\"" + resultSubStringSecond + "\"}"
                            sendAnswerScriptToServ.runAsyncSendAnswerScriptToServ(
                                "result_interim",
                                result[1] as String,
                                JSONObject(resultEdit)
                            )
                        }
                        if (count.compareTo(0) == 0) {
//                            isTimerWork = false
                        }
                    }
                })
                if (count.compareTo(0) == 0) {
//                    isTimerWork = false

//                if (DBLocal.invoke().isOpen) {
//                    if (DBLocal.invoke().inTransaction()) {
//                        DBLocal.invoke().endTransaction()
//                    }
//                    DBLocal.invoke().close()
//                }
                    break
                }
            }
        } else {
//            isTimerWork = false
        }
    }

//    inner class TaskExecScriptFrServ : AsyncTask<Void, Void, Void>() {
//
//        override fun doInBackground(vararg p0: Void?): Void? {
//            try {
//                execScriptFrServ()
//            } catch (e: java.lang.Exception) {
//                val error =
//                    "{\"Error\":\"\", \"Result\":\"{625DAB54-8F7E-4F4A-AC6D-EF3B00778E32} " + this.javaClass.name + " " + e +"\"}"
//                Log.e(
//                    tagError,
//                    error
//                )
//                return null
//            }
//            return null
//        }
//
//        override fun onPostExecute(result: Void?) {
//        }
//
//        override fun onPreExecute() {
//        }
//
//    }
}
