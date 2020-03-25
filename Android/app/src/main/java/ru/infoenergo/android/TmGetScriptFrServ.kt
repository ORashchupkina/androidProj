package ru.infoenergo.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.Transaction
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.TextNode
import org.json.JSONObject
import ru.infoenergo.android.common.OnReadyCallbackArray
import ru.infoenergo.android.common.db_work.DBLocal
import ru.infoenergo.android.common.db_work.TSCRIPTSFRSERV
import ru.infoenergo.mobia.android.app.http.scriptsfrserv.GetScriptsFrServ
import ru.infoenergo.mobia.android.app.http.scriptsfrserv.SendAnswerScriptToServ
import java.text.SimpleDateFormat
import java.util.*

class TmGetScriptFrServ : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        timertick()
    }
    var isTimerWork = false

    internal var fullNameClass = this.javaClass.toString()
    internal var tagError = "error." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)
    internal var tagInfo =
        "information." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)
    internal var tagWarn = "warning." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)

    internal var TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"
    internal var sdf = SimpleDateFormat(TIME_FORMAT)

    @RequiresApi(Build.VERSION_CODES.O)
    fun timertick() {
        if (!isTimerWork) {
            isTimerWork = true
        } else {
            return
        }

        val login = "user"//MyUtils.getConst("login", context) TODO: разобраться
        getScriptsFrServ(login, object : OnReadyCallbackArray {
            override fun onReady(result: Array<Any>) {
                try {
                    if (result[0].equals("fail_mob") || result[0].equals("OK")) {
                        // отправить сообщение серверу о результате
                        val sendAnswerScriptToServ: SendAnswerScriptToServ = SendAnswerScriptToServ()
                        val resultSubStringFirst = (result[2] as String).substring((result[2] as String).indexOf("Error")+9, (result[2] as String).indexOf("Result")-3).replace("\"", " ")
                        val resultSubStringSecond = (result[2] as String).substring((result[2] as String).indexOf("Result")+9).replace("\"", " ")
                        val resultEdit = "{\"Error\":\"" + resultSubStringFirst + "\",\"Result\":\"" + resultSubStringSecond + "\"}"
                        sendAnswerScriptToServ.runAsyncSendAnswerScriptToServ(
                            "result_send",
                            result[1] as String,
                            JSONObject(resultEdit)
                        )
                    }
                    else if (result[0].equals("fail_serv")) {
                        // отправить сообщение серверу о результате
                        val sendAnswerScriptToServ: SendAnswerScriptToServ = SendAnswerScriptToServ()
                        val resultSubStringFirst = (result[1] as String).substring((result[1] as String).indexOf("Error")+9, (result[1] as String).indexOf("Result")-3).replace("\"", " ")
                        val resultSubStringSecond = (result[1] as String).substring((result[1] as String).indexOf("Result")+9).replace("\"", " ")
                        val resultEdit = "{\"Error\":\"" + resultSubStringFirst + "\",\"Result\":\"" + resultSubStringSecond + "\"}"
                        sendAnswerScriptToServ.runAsyncSendAnswerScriptToServ("result_send","-1", JSONObject(resultEdit))
                    }
                } catch (e: Exception) {
                    if (result != null) {
                        if (result.size == 2) {
                            Log.e(
                                tagError,
                                "{2B39770D-56F7-498E-B5C9-A7BE87C65D56}1 e = " + e + " result[0] = " + result[0] + " result[1] = " + result[1]
                            )
                        } else if (result.size == 3) {
                            Log.e(
                                tagError,
                                "{2B39770D-56F7-498E-B5C9-A7BE87C65D56}2 e = " + e + " result[0] = " + result[0] + " result[1] = " + result[1] + " result[2] = " + result[2]
                            )
                        } else {
                            Log.e(
                                tagError,
                                "{2B39770D-56F7-498E-B5C9-A7BE87C65D56}3 e = " + e + " result[0] = " + result[0] + " result.size = " + result.size
                            )
                        }
                    } else {
                        Log.e(
                            tagError,
                            "{2B39770D-56F7-498E-B5C9-A7BE87C65D56}3 e = " + e + " result = null"
                        )
                    }
                }
                isTimerWork = false
                return
            }
        })
    }

    var placeCode = "0"
    var canCloseCon = false
    @RequiresApi(Build.VERSION_CODES.O)
    fun getScriptsFrServ(login: String, onReadyCallbackArray: OnReadyCallbackArray) {
        var arrayOnReadyCallBack: Array<Any>? = null

        var idScript: String? = null
        var script = ""
        var typeScript = ""
        var dbName = ""
        var tName = ""
        var date: Date? = null

        // получение скрипта с сервера
        val getScriptsFrServ = GetScriptsFrServ()
        var result = getScriptsFrServ.runAsyncGetScriptsFrServ(login)
        placeCode = "1"
        try {
            val mapper = ObjectMapper()
            val rootNode = mapper.readTree(result)
            placeCode = "2"
            if (rootNode.get("result") != null) {
//                for(instantRootNode : JsonNode in rootNode){
                val instantRootNode = rootNode
                placeCode = "3"
                // значит, структура JSON верная
                if (instantRootNode.get("server_error") != null) {
                    placeCode = "4"
                    if ((instantRootNode.get("result") as TextNode).textValue().equals("fail")) {
                        placeCode = "5"
                        Log.w(
                            tagWarn,
                            "{\"Error\":\"{39E7DBD6-95D9-4E23-BE4E-317505A67990} " + instantRootNode.get("result") + instantRootNode.get(
                                "server_error"
                            ) + "\", \"Result\":\"\"}"
                        )
                        arrayOnReadyCallBack = arrayOf(
                            "fail_serv",
                            "{\"Error\":\"{39E7DBD6-95D9-4E23-BE4E-317505A67990} " + instantRootNode.get("result") + instantRootNode.get(
                                "server_error"
                            ) + "\", \"Result\":\"\"}"
                        )
                        return
                    } else {
                        placeCode = "5.1"
                        Log.w(
                            tagError,
                            "{07DBCE05-37F2-4D8D-B7E5-CD2FC23A087A} " + instantRootNode.get("result") + " " + instantRootNode.get(
                                "server_error"
                            )
                        );
                        arrayOnReadyCallBack = arrayOf(
                            "fail_serv",
                            "{\"Error\":\"{07DBCE05-37F2-4D8D-B7E5-CD2FC23A087A} " + instantRootNode.get("result") + " " + instantRootNode.get(
                                "server_error"
                            ) + "\", \"Result\":\"\"}"
                        )
                        return
                    }
                } else {
                    placeCode = "6"
                    if ((instantRootNode.get("result") as TextNode).textValue().equals("no")) {
                        placeCode = "7"
                        Log.i(
                            tagInfo,
                            "{7B1CA558-8DB2-4ADF-AB43-34AAC88E675E} Нет скриптов для выполнения"
                        );
                        arrayOnReadyCallBack = arrayOf("Нет скриптов для выполнения")
                        return
                    } else if ((instantRootNode.get("result") as TextNode).textValue().equals("ok")) {
                        placeCode = "8"
                        if (instantRootNode.get("data") != null) {
                            placeCode = "9"
                            if (instantRootNode.get("data").get("id_script") != null) {
                                placeCode = "10"
                                idScript = instantRootNode.get("data").get("id_script").textValue()
                                placeCode = "11"
                                if (instantRootNode.get("data").get("script") != null) {
                                    placeCode = "12"
                                    if (instantRootNode.get("data").get("type_script") != null) {
                                        placeCode = "13"
                                        if (instantRootNode.get("data").get("db_name") != null) {
                                            placeCode = "14"
                                            if (instantRootNode.get("data").get("dtc") != null) {
                                                placeCode = "15.1"
                                                script =
                                                    instantRootNode.get("data").get("script").textValue()
                                                placeCode = "15.2"
                                                typeScript = instantRootNode.get("data").get("type_script")
                                                    .textValue()
                                                placeCode = "15.3"
                                                dbName =
                                                    instantRootNode.get("data").get("db_name").textValue()
                                                placeCode = "15.4"
                                                date = sdf.parse(
                                                    instantRootNode.get("data").get("dtc").textValue().replace(
                                                        "T",
                                                        " "
                                                    )
                                                )
                                                placeCode = "15.6"
                                            } else {
                                                placeCode = "15.7"
                                                Log.e(
                                                    tagError,
                                                    "{E94E6C91-9F7B-48D1-BE7E-D090725E0E67} " + instantRootNode.get(
                                                        "result"
                                                    ) + " " + instantRootNode.get(
                                                        "server_error"
                                                    ) + " idScript = " + idScript
                                                );
                                                arrayOnReadyCallBack = arrayOf(
                                                    "fail_serv",
                                                    "{\"Error\":\"{E94E6C91-9F7B-48D1-BE7E-D090725E0E67} " + instantRootNode.get(
                                                        "result"
                                                    ) + " " + instantRootNode.get(
                                                        "server_error"
                                                    ) + " " + instantRootNode.get("result") + " idScript = " + idScript + "\",\"Result\"=\"\"}"

                                                )
                                                return
                                            }
                                        } else {
                                            placeCode = "14.1"
                                            Log.e(
                                                tagError,
                                                "{EDDD9DA9-DB47-4455-893B-4080FB095921} " + instantRootNode.get(
                                                    "result"
                                                ) + " " + instantRootNode.get(
                                                    "server_error"
                                                ) + " idScript = " + idScript
                                            )
                                            arrayOnReadyCallBack = arrayOf(
                                                "fail_serv",
                                                "{\"Error\":\"{EDDD9DA9-DB47-4455-893B-4080FB095921} " + instantRootNode.get(
                                                    "result"
                                                ) + " " + instantRootNode.get(
                                                    "server_error"
                                                ) + " " + instantRootNode.get("result") + " idScript = " + idScript + "\",\"Result\"=\"\"}"
                                            )
                                            return
                                        }
                                    } else {
                                        placeCode = "13.1"
                                        Log.e(
                                            tagError,
                                            "{2ADBDE72-654B-4A0A-BA3D-BD06176012B3} " + instantRootNode.get(
                                                "result"
                                            ) + " " + instantRootNode.get(
                                                "server_error"
                                            ) + " idScript = " + idScript
                                        );
                                        arrayOnReadyCallBack = arrayOf(
                                            "fail_serv",
                                            "{\"Error\":\"{2ADBDE72-654B-4A0A-BA3D-BD06176012B3} " + instantRootNode.get(
                                                "result"
                                            ) + " " + instantRootNode.get(
                                                "server_error"
                                            ) + " " + instantRootNode.get("result") + " idScript = " + idScript + "\",\"Result\"=\"\"}"
                                        )
                                        return
                                    }
                                } else {
                                    placeCode = "12.1"
                                    Log.e(
                                        tagError,
                                        "{8D6B8C92-B1DA-4C7A-A490-66FE67351051} " + instantRootNode.get("result") + " " + instantRootNode.get(
                                            "server_error"
                                        ) + " idScript = " + idScript
                                    );
                                    arrayOnReadyCallBack = arrayOf(
                                        "fail_serv",
                                        "{\"Error\":\"{8D6B8C92-B1DA-4C7A-A490-66FE67351051} " + instantRootNode.get(
                                            "result"
                                        ) + " " + instantRootNode.get(
                                            "server_error"
                                        ) + " " + instantRootNode.get("result") + " idScript = " + idScript + "\",\"Result\"=\"\"}"
                                    )
                                    return
                                }
                            } else {
                                placeCode = "10.1"
                                Log.e(
                                    tagError,
                                    "{C301A9BE-C534-4C7A-85C5-3B6A08A4BF46} " + instantRootNode.get("result") + " " + instantRootNode.get(
                                        "server_error"
                                    )
                                );
                                arrayOnReadyCallBack =
                                    arrayOf(
                                        "fail_serv",
                                        "{\"Error\":\"{C301A9BE-C534-4C7A-85C5-3B6A08A4BF46} " + instantRootNode.get("result") + " " + instantRootNode.get(
                                            "server_error"
                                        ) + " " + instantRootNode.get("result") + "\",\"Result\"=\"\"}"
                                    )
                                return
                            }
                        } else {
                            placeCode = "9.1"
                            Log.e(
                                tagError,
                                "{985C2501-9C17-4FD5-A976-043DF2CF6E9C} " + instantRootNode.get("result") + " " + instantRootNode.get(
                                    "server_error"
                                )
                            );
                            arrayOnReadyCallBack = arrayOf(
                                "fail_serv",
                                "{\"Error\":\"{985C2501-9C17-4FD5-A976-043DF2CF6E9C} " + instantRootNode.get("result") + " " + instantRootNode.get(
                                    "server_error"
                                ) + " " + instantRootNode.get("result") + "\",\"Result\"=\"\"}"
                            )
                            return
                        }

                    } else {
                        placeCode = "8.1"
                        Log.e(
                            tagError,
                            "{D330FA6A-5C28-4D36-83F1-ACADE2646CE6} " + instantRootNode.get("result") + " " + instantRootNode.get(
                                "server_error"
                            )
                        );
                        arrayOnReadyCallBack = arrayOf(
                            "fail_serv",
                            "{\"Error\":\"{D330FA6A-5C28-4D36-83F1-ACADE2646CE6} " + instantRootNode.get("result") + " " + instantRootNode.get(
                                "server_error"
                            ) + "\",\"Result\"=\"\"}"
                        )
                        return
                    }
                }
//            }
            } else {
                placeCode = "3.1"
                Log.e(
                    tagError,
                    "{7D97C523-2BD6-4B1A-8C23-C4D044A5B543} " + rootNode.get("result") + " " + rootNode.get(
                        "server_error"
                    )
                )
                arrayOnReadyCallBack = arrayOf(
                    "fail_serv",
                    "{\"Error\":\"{7D97C523-2BD6-4B1A-8C23-C4D044A5B543} " + rootNode.get("result") + " " + rootNode.get(
                        "server_error"
                    ) + "\",\"Result\"=\"\"}"
                )
                return
            }
            // итог: либо вышли с ошибкой, либо получили скрипт для выполнения

            //TODO: определить, можно ли подключиться к бд
            // TODO: в каком случае нельзя открывать здесь соединение? (если открыто в другом месте или если транзакция открыта)
            placeCode = "-16.1"
//            if (DBLocal.invoke().isOpen) {
//                placeCode = "-16.2"
//                if (DBLocal.invoke().inTransaction()) {
//                    placeCode = "-16.3"
//                }
//                val error = "{\"Error\":\"\", \"Result\":\"Не удалось открыть базу данных local_db_fr_s. Информация для разработчика: {BA770F2E-A98E-4516-945B-6BB445BFA21A} " + rootNode.get(
//                    "result"
//                ) + " " + rootNode.get("result") + " idScript = " + idScript + "\"}"
//                Log.e(
//                    tagError,
//                    error
//                )
//                canCloseCon = false // и так false, можно убрать
//                arrayOnReadyCallBack = arrayOf(
//                    "fail_mob",
//                    idScript!!,
//                    error
//                )
//                return
//            }

            placeCode = "16.4"
            // поместить новый скрипт в бд:
            //  есть ли такой скрипт
            placeCode = "16.1"

            var taskInsScriptToDbInTransaction : TaskInsScriptToDbInTransaction = TaskInsScriptToDbInTransaction()
            arrayOnReadyCallBack = taskInsScriptToDbInTransaction.execute(idScript, date, script, typeScript, dbName).get()

        } catch (e: Exception) {
            if (idScript != null) {
                Log.e(
                    tagError,
                    "{\"Error\":\"\", \"Result\":\"{642F0DFD-33BD-4CE8-8BAD-1157A1F51E1E} e = " + e + " idScript = " + idScript + " placeCode = " + placeCode + "\"}"
                )
                if (placeCode.compareTo("16") >= 0 && DBLocal.invoke().isOpen) {
                    if (DBLocal.invoke().inTransaction()) {
                        DBLocal.invoke().endTransaction()
                        DBLocal.invoke().close()
                        canCloseCon = false
                    }
                }
                arrayOnReadyCallBack = arrayOf(
                    "fail_mob",
                    idScript!!,
                    "{\"Error\":\"\", \"Result\":\"{642F0DFD-33BD-4CE8-8BAD-1157A1F51E1E} e = " + e + " idScript = " + idScript + " placeCode = " + placeCode + "\"}"
                )
                return
            } else {
                Log.e(
                    tagError,
                    "{49D5A638-2E52-4EBB-A352-F83602A471CA} e = " + e + " placeCode = " + placeCode
                )
                arrayOnReadyCallBack = arrayOf(
                    "fail_serv",
                    "{\"Error\":\"{8D4D71D0-1CD9-4944-84B7-9CBDF4295B51} e = " + e + " placeCode = " + placeCode + "\",\"Result\"=\"\"}"
                )
                return
            }
        } finally {
            Log.i(
                tagInfo,
                "{F7B72DB0-90F8-4606-92AC-7CB3F244971A} placeCode = " + placeCode + " canCloseCon = " + canCloseCon + " isOpen = " + DBLocal.invoke().isOpen + " inTransaction = " + DBLocal.invoke().inTransaction()
            )
            if (canCloseCon) {
                if (DBLocal.invoke().isOpen) {
                    if (!DBLocal.invoke().inTransaction()) {
                        DBLocal.invoke().close()
                    }
                }
            }
            if (arrayOnReadyCallBack != null) {
                onReadyCallbackArray.onReady(arrayOnReadyCallBack!!)
                return
            }
        }

        val res = "{\"Error\":\"\", \"Result\":\"{ACFD6EE1-5E5D-4EC2-819A-325F7EA8D7CC} " + " placeCode = " + placeCode + "\"}"
        Log.i(
            tagInfo,
            res
        )

        onReadyCallbackArray.onReady(
            arrayOf(
                "OK",
                idScript!!,
                res
            )
        )
        return
    }

    inner class TaskInsScriptToDbInTransaction : AsyncTask<Any, Void, Array<Any>?>() {

        override fun doInBackground(vararg params: Any): Array<Any>? {
            var result : Array<Any>? = null
            try {
                result = insScriptToDbInTransaction(params[0] as String, params[1] as Date, params[2] as String, params[3] as String, params[4] as String)
            } catch (e: java.lang.Exception) {
                val error =
                    "{\"Error\":\"\", \"Result\":\"{625DAB54-8F7E-4F4A-AC6D-EF3B00778E32} placeCode = " + placeCode + "; this.javaClass.name = " + this.javaClass.name + " " + e +"\"}"
                Log.e(
                    tagError,
                    error
                )
                return arrayOf(
                    "fail_mob",
                    params[0] as String,
                    error
                )
            }
            return result
        }

        override fun onPostExecute(aArray: Array<Any>?) {
        }

        override fun onPreExecute() {
        }

    }

    fun insScriptToDbInTransaction(idScript : String, date: Date, script : String, typeScript : String, dbName : String) : Array<Any>?{
        var arrayOnReadyCallBack: Array<Any>? = null
        DBLocal.invoke().runInTransaction{
            placeCode = "17.1"
            var resIsScriptExist =
                DBLocal.invoke().todoTScriptsFrServDao().getCountByUUID(idScript!!)
            placeCode = "18.1"
            if (resIsScriptExist.compareTo(0) == 0) {
                placeCode = "21.1"
                DBLocal.invoke().todoTScriptsFrServDao()//!!!!
                    .insScript(
                        TSCRIPTSFRSERV(
                            idScript!!,
                            date!!,
                            script,
                            typeScript,
                            dbName,
                            null,
                            null
                        )
                    )
                placeCode = "22.1"
                resIsScriptExist =
                    DBLocal.invoke().todoTScriptsFrServDao().getCountByUUID(idScript!!)
                if (resIsScriptExist.compareTo(0) == 0) {
                    placeCode = "27.1"
                    // значит, выполнен скрипт
                    val error =
                        "{\"Error\":\"\", \"Result\":\"{0F71E856-F55F-409F-9020-9705F03AC7CF} скрипт НЕ добавился в бд мобильного устройства\"}"
                    Log.e(
                        tagError,
                        error
                    )
//                    DBLocal.invoke().endTransaction()
                    arrayOnReadyCallBack = arrayOf(
                        "fail_mob",
                        idScript!!,
                        error
                    )
                    return@runInTransaction
                }
                else{
                    placeCode = "27.2"
                    val res = "{\"Error\":\"\", \"Result\":\"{3A2EA26A-7757-4C42-8243-0BAB7D8DB19E} the script is added to the db of the mobile device\"}"
                    Log.i(
                        tagInfo,
                        res
                    )
                    arrayOnReadyCallBack = arrayOf(
                        "OK",
                        idScript!!,
                        res
                    )
                }
            } else {
                placeCode = "27.3"
                // значит, выполнен скрипт
                val error =
                    "{\"Error\":\"\", \"Result\":\"{C626AFCA-1AE6-4290-B9EB-ED891C968AAB} a script with that id has already been passed to the application\"}"
                Log.e(
                    tagError,
                    error
                )
//                    DBLocal.invoke().endTransaction()
                arrayOnReadyCallBack = arrayOf(
                    "fail_mob",
                    idScript!!,
                    error
                )
                return@runInTransaction
            }
            return@runInTransaction
        }
        return arrayOnReadyCallBack
    }
}
