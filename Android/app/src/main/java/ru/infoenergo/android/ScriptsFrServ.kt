package ru.infoenergo.android

import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.RoomDatabase
import androidx.sqlite.db.SimpleSQLiteQuery
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.TextNode
import kotlinx.coroutines.*
import ru.infoenergo.android.common.OnReadyCallbackArray
import ru.infoenergo.android.common.db_work.DBLocal
import ru.infoenergo.android.common.db_work.TSCRIPTSFRSERV
import ru.infoenergo.mobia.android.app.http.scriptsfrserv.GetScriptsFrServ
import java.text.SimpleDateFormat
import java.util.*

class ScriptsFrServ(private val context: Context) {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var flagIsInWork = false
    internal var TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"
    internal var sdf = SimpleDateFormat(TIME_FORMAT)

    internal var fullNameClass = this.javaClass.toString()
    internal var tagError = "error." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)
    internal var tagInfo =
        "information." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)
    internal var tagWarn = "warning." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)

/*    @RequiresApi(Build.VERSION_CODES.O)
    fun getScriptsFrServ(login: String, onReadyCallbackArray: OnReadyCallbackArray) {

        var idScript: String? = null
        var script = ""
        var typeScript = ""
        var dbName = ""
        var tName = ""
        var date: Date? = null

        // получение скрипта с сервера
        val getScriptsFrServ = GetScriptsFrServ()
        getScriptsFrServ.get(login, object : OnReadyCallbackOnlyString {
            override fun onReady(result: String) {
                try {
                    val mapper = ObjectMapper()
                    val rootNode = mapper.readTree(result)
                    if (rootNode.get("result") != null) {
                        // значит, структура JSON верная
                        if (rootNode.get("server_error") != null) {
                            if ((rootNode.get("result") as TextNode).textValue().equals("fail")) {
                                Log.w(
                                    tagWarn,
                                    "{39E7DBD6-95D9-4E23-BE4E-317505A67990} " + rootNode.get("result") + " " + rootNode.get(
                                        "server_error"
                                    )
                                )
                                onReadyCallbackArray.onReady(arrayOf("fail_serv", rootNode.get("server_error")))
                            } else {
                                Log.w(
                                    tagError,
                                    "{07DBCE05-37F2-4D8D-B7E5-CD2FC23A087A} " + rootNode.get("result") + " " + rootNode.get(
                                        "server_error"
                                    )
                                );
                                onReadyCallbackArray.onReady(
                                    arrayOf(
                                        "fail_serv",
                                        "{07DBCE05-37F2-4D8D-B7E5-CD2FC23A087A} " + rootNode.get("result") + " " + rootNode.get(
                                            "server_error"
                                        )
                                    )
                                )
                            }
                        } else {
                            if ((rootNode.get("result") as TextNode).textValue().equals("no")) {
                                Log.i(tagInfo, "{7B1CA558-8DB2-4ADF-AB43-34AAC88E675E} Нет скриптов для выполнения");
                                onReadyCallbackArray.onReady(arrayOf("Нет скриптов для выполнения"))
                            } else if ((rootNode.get("result") as TextNode).textValue().equals("ok")) {
                                if (rootNode.get("data") != null) {
                                    if (rootNode.get("id_script") != null) {
                                        if (rootNode.get("script") != null) {
                                            if (rootNode.get("type_script") != null) {
                                                if (rootNode.get("db_name") != null) {
                                                    if (rootNode.get("dtc") != null) {
                                                        idScript = rootNode.get("id_script").textValue()
                                                        script = rootNode.get("script").textValue()
                                                        typeScript = rootNode.get("type_script").textValue()
                                                        dbName = rootNode.get("db_name").textValue()
                                                        tName = rootNode.get("t_name").textValue()
                                                        date = parser.parse(rootNode.get("dtc").textValue())

                                                    } else {
                                                        Log.e(
                                                            tagError,
                                                            "{E94E6C91-9F7B-48D1-BE7E-D090725E0E67} " + rootNode.get("result") + " " + rootNode.get(
                                                                "server_error"
                                                            )
                                                        );
                                                        onReadyCallbackArray.onReady(
                                                            arrayOf(
                                                                "fail_serv",
                                                                "{E94E6C91-9F7B-48D1-BE7E-D090725E0E67} " + rootNode.get(
                                                                    "result"
                                                                ) + " " + rootNode.get(
                                                                    "server_error" + " " + rootNode.get("result")
                                                                )
                                                            )
                                                        )
                                                    }
                                                } else {
                                                    Log.e(
                                                        tagError,
                                                        "{EDDD9DA9-DB47-4455-893B-4080FB095921} " + rootNode.get("result") + " " + rootNode.get(
                                                            "server_error"
                                                        )
                                                    )
                                                    onReadyCallbackArray.onReady(
                                                        arrayOf(
                                                            "fail_serv",
                                                            "{EDDD9DA9-DB47-4455-893B-4080FB095921} " + rootNode.get("result") + " " + rootNode.get(
                                                                "server_error" + " " + rootNode.get("result")
                                                            )
                                                        )
                                                    )
                                                }
                                            } else {
                                                Log.e(
                                                    tagError,
                                                    "{2ADBDE72-654B-4A0A-BA3D-BD06176012B3} " + rootNode.get("result") + " " + rootNode.get(
                                                        "server_error"
                                                    )
                                                );
                                                onReadyCallbackArray.onReady(
                                                    arrayOf(
                                                        "fail_serv",
                                                        "{2ADBDE72-654B-4A0A-BA3D-BD06176012B3} " + rootNode.get("result") + " " + rootNode.get(
                                                            "server_error" + " " + rootNode.get("result")
                                                        )
                                                    )
                                                )
                                            }
                                        } else {
                                            Log.e(
                                                tagError,
                                                "{8D6B8C92-B1DA-4C7A-A490-66FE67351051} " + rootNode.get("result") + " " + rootNode.get(
                                                    "server_error"
                                                )
                                            );
                                            onReadyCallbackArray.onReady(
                                                arrayOf(
                                                    "fail_serv",
                                                    "{8D6B8C92-B1DA-4C7A-A490-66FE67351051} " + rootNode.get("result") + " " + rootNode.get(
                                                        "server_error" + " " + rootNode.get("result")
                                                    )
                                                )
                                            )
                                        }
                                    } else {
                                        Log.e(
                                            tagError,
                                            "{C301A9BE-C534-4C7A-85C5-3B6A08A4BF46} " + rootNode.get("result") + " " + rootNode.get(
                                                "server_error"
                                            )
                                        );
                                        onReadyCallbackArray.onReady(
                                            arrayOf(
                                                "fail_serv",
                                                "{C301A9BE-C534-4C7A-85C5-3B6A08A4BF46} " + rootNode.get("result") + " " + rootNode.get(
                                                    "server_error" + " " + rootNode.get("result")
                                                )
                                            )
                                        )
                                    }
                                } else {
                                    Log.e(
                                        tagError,
                                        "{985C2501-9C17-4FD5-A976-043DF2CF6E9C} " + rootNode.get("result") + " " + rootNode.get(
                                            "server_error"
                                        )
                                    );
                                    onReadyCallbackArray.onReady(
                                        arrayOf(
                                            "fail_serv",
                                            "{985C2501-9C17-4FD5-A976-043DF2CF6E9C} " + rootNode.get("result") + " " + rootNode.get(
                                                "server_error" + " " + rootNode.get("result")
                                            )
                                        )
                                    )
                                }

                            } else {
                                Log.e(
                                    tagError,
                                    "{D330FA6A-5C28-4D36-83F1-ACADE2646CE6} " + rootNode.get("result") + " " + rootNode.get(
                                        "server_error"
                                    )
                                );
                                onReadyCallbackArray.onReady(
                                    arrayOf(
                                        "fail_serv",
                                        "{D330FA6A-5C28-4D36-83F1-ACADE2646CE6} " + rootNode.get("result") + " " + rootNode.get(
                                            "server_error"
                                        )
                                    )
                                )
                            }
                        }
                    } else {
                        Log.e(
                            tagError,
                            "{7D97C523-2BD6-4B1A-8C23-C4D044A5B543} " + rootNode.get("result") + " " + rootNode.get("server_error")
                        )
                        onReadyCallbackArray.onReady(
                            arrayOf(
                                "fail_serv",
                                "{7D97C523-2BD6-4B1A-8C23-C4D044A5B543} " + rootNode.get("result") + " " + rootNode.get(
                                    "server_error"
                                )
                            )
                        )
                    }
                    // итог: либо вышли с ошибкой, либо получили скрипт для выполнения

                    //TODO: определить, можно ли подключиться к бд


                    // поместить новый скрипт в бд:
                    //  есть ли такой скрипт
                    uiScope.launch(Dispatchers.Main) {
                        val d = uiScope.async(Dispatchers.IO) {
                            val res = DBLocal.invoke().todoTScriptsFrServDao().getCountByUUID(idScript!!)
                            return@async res
                        }
                        val resIsScriptExist = launch@ d.await()

                        if (resIsScriptExist.equals(0)) {
                            // если нет, то добавляем
                            val d = uiScope.async(Dispatchers.IO) {
                                val res = DBLocal.invoke().todoTScriptsFrServDao()
                                    .insScript(
                                        TSCRIPTSFRSERV(
                                            idScript!!,
                                            date!!,
                                            script,
                                            typeScript,
                                            dbName,
                                            tName,
                                            null
                                        )
                                    )
                                return@async res
                            }
                            launch@ d.await()

                            // добавился ли скрипт в бд?
                            uiScope.launch(Dispatchers.Main) {
                                val d = uiScope.async(Dispatchers.IO) {
                                    val res = DBLocal.invoke().todoTScriptsFrServDao().getCountByUUID(idScript!!)
                                    return@async res
                                }
                                val resIsScriptExist = launch@ d.await()
                                if (resIsScriptExist.equals(0)) {
                                    Log.e(
                                        tagError,
                                        "{774D260B-CB56-4939-A9D5-F429628C1249} скрипт не добавился в бд. idScript = " + idScript
                                    )
                                    onReadyCallbackArray.onReady(
                                        arrayOf(
                                            "fail_mob",
                                            idScript!!,
                                            "{774D260B-CB56-4939-A9D5-F429628C1249} скрипт не добавился в бд. idScript = " + idScript
                                        )
                                    )
                                }
                            }
                        } else {
                            // если есть, то проверяем, выполнен ли он
                            uiScope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                val d = uiScope.async(kotlinx.coroutines.Dispatchers.IO) {
                                    val res = ru.infoenergo.roomdb.DBLocal.invoke().todoTScriptsFrServDao()
                                        .isSqriptNotExec(idScript!!)
                                    return@async res
                                }
                                val resIsSqriptNotExec = launch@ d.await()
                                if (resIsSqriptNotExec.equals(0)) {
                                    // значит, выполнен скрипт
                                    Log.e(
                                        tagError,
                                        "{1A91374B-F72A-4058-B934-A66DD88C4EA7} скрипт с таким id уже был передан в приложение и был выполнен"
                                    )
                                    onReadyCallbackArray.onReady(
                                        arrayOf(
                                            "fail_mob",
                                            idScript!!,
                                            "{1A91374B-F72A-4058-B934-A66DD88C4EA7} скрипт с таким id уже был передан в приложение и был выполнен"
                                        )
                                    )
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(
                        tagError,
                        "{642F0DFD-33BD-4CE8-8BAD-1157A1F51E1E} e = " + e
                    )
                    if (idScript != null) {
                        onReadyCallbackArray.onReady(
                            arrayOf(
                                "fail_mob",
                                idScript!!,
                                "{642F0DFD-33BD-4CE8-8BAD-1157A1F51E1E} e = " + e
                            )
                        )
                    } else {
                        onReadyCallbackArray.onReady(
                            arrayOf(
                                "fail_serv",
                                idScript!!,
                                "{8D4D71D0-1CD9-4944-84B7-9CBDF4295B51} e = " + e
                            )
                        )
                    }
                } finally {
                    Log.i(
                        tagInfo,
                        "{F7B72DB0-90F8-4606-92AC-7CB3F244971A}1"
                    )
                }
                onReadyCallbackArray.onReady(
                    arrayOf(
                        "OK",
                        idScript!!
                    )
                )
                Log.i(
                    tagInfo,
                    "{F7B72DB0-90F8-4606-92AC-7CB3F244971A}2"
                )
            }
        })
    }*/

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
                placeCode = "3"
                // значит, структура JSON верная
                if (rootNode.get("server_error") != null) {
                    placeCode = "4"
                    if ((rootNode.get("result") as TextNode).textValue().equals("fail")) {
                        placeCode = "5"
                        Log.w(
                            tagWarn,
                            "{\"Error\":\"{39E7DBD6-95D9-4E23-BE4E-317505A67990} " + rootNode.get("result") + rootNode.get(
                                "server_error"
                            ) + "\", \"Result\":\"\"}"
                        )
                        arrayOnReadyCallBack = arrayOf(
                            "fail_serv",
                            "{\"Error\":\"{39E7DBD6-95D9-4E23-BE4E-317505A67990} " + rootNode.get("result") + rootNode.get(
                                "server_error"
                            ) + "\", \"Result\":\"\"}"
                        )
                        return
                    } else {
                        placeCode = "5.1"
                        Log.w(
                            tagError,
                            "{07DBCE05-37F2-4D8D-B7E5-CD2FC23A087A} " + rootNode.get("result") + " " + rootNode.get(
                                "server_error"
                            )
                        );
                        arrayOnReadyCallBack = arrayOf(
                            "fail_serv",
                            "{\"Error\":\"{07DBCE05-37F2-4D8D-B7E5-CD2FC23A087A} " + rootNode.get("result") + " " + rootNode.get(
                                "server_error"
                            ) + "\", \"Result\":\"\"}"
                        )
                        return
                    }
                } else {
                    placeCode = "6"
                    if ((rootNode.get("result") as TextNode).textValue().equals("no")) {
                        placeCode = "7"
                        Log.i(
                            tagInfo,
                            "{7B1CA558-8DB2-4ADF-AB43-34AAC88E675E} Нет скриптов для выполнения"
                        );
                        arrayOnReadyCallBack = arrayOf("Нет скриптов для выполнения")
                        return
                    } else if ((rootNode.get("result") as TextNode).textValue().equals("ok")) {
                        placeCode = "8"
                        if (rootNode.get("data") != null) {
                            placeCode = "9"
                            if (rootNode.get("data").get("id_script") != null) {
                                placeCode = "10"
                                idScript = rootNode.get("data").get("id_script").textValue()
                                placeCode = "11"
                                if (rootNode.get("data").get("script") != null) {
                                    placeCode = "12"
                                    if (rootNode.get("data").get("type_script") != null) {
                                        placeCode = "13"
                                        if (rootNode.get("data").get("db_name") != null) {
                                            placeCode = "14"
                                            if (rootNode.get("data").get("dtc") != null) {
                                                placeCode = "15.1"
                                                script =
                                                    rootNode.get("data").get("script").textValue()
                                                placeCode = "15.2"
                                                typeScript = rootNode.get("data").get("type_script")
                                                    .textValue()
                                                placeCode = "15.3"
                                                dbName =
                                                    rootNode.get("data").get("db_name").textValue()
                                                placeCode = "15.4"
                                                tName =
                                                    rootNode.get("data").get("t_name").textValue()
                                                placeCode = "15.5"
                                                date = sdf.parse(
                                                    rootNode.get("data").get("dtc").textValue().replace(
                                                        "T",
                                                        " "
                                                    )
                                                )
                                                placeCode = "15.6"
                                            } else {
                                                placeCode = "15.7"
                                                Log.e(
                                                    tagError,
                                                    "{E94E6C91-9F7B-48D1-BE7E-D090725E0E67} " + rootNode.get(
                                                        "result"
                                                    ) + " " + rootNode.get(
                                                        "server_error"
                                                    ) + " idScript = " + idScript
                                                );
                                                arrayOnReadyCallBack = arrayOf(
                                                    "fail_serv",
                                                    "{\"Error\":\"{E94E6C91-9F7B-48D1-BE7E-D090725E0E67} " + rootNode.get(
                                                        "result"
                                                    ) + " " + rootNode.get(
                                                        "server_error"
                                                    ) + " " + rootNode.get("result") + " idScript = " + idScript + "\",\"Result\"=\"\"}"

                                                )
                                                return
                                            }
                                        } else {
                                            placeCode = "14.1"
                                            Log.e(
                                                tagError,
                                                "{EDDD9DA9-DB47-4455-893B-4080FB095921} " + rootNode.get(
                                                    "result"
                                                ) + " " + rootNode.get(
                                                    "server_error"
                                                ) + " idScript = " + idScript
                                            )
                                            arrayOnReadyCallBack = arrayOf(
                                                "fail_serv",
                                                "{\"Error\":\"{EDDD9DA9-DB47-4455-893B-4080FB095921} " + rootNode.get(
                                                    "result"
                                                ) + " " + rootNode.get(
                                                    "server_error"
                                                ) + " " + rootNode.get("result") + " idScript = " + idScript + "\",\"Result\"=\"\"}"
                                            )
                                            return
                                        }
                                    } else {
                                        placeCode = "13.1"
                                        Log.e(
                                            tagError,
                                            "{2ADBDE72-654B-4A0A-BA3D-BD06176012B3} " + rootNode.get(
                                                "result"
                                            ) + " " + rootNode.get(
                                                "server_error"
                                            ) + " idScript = " + idScript
                                        );
                                        arrayOnReadyCallBack = arrayOf(
                                            "fail_serv",
                                            "{\"Error\":\"{2ADBDE72-654B-4A0A-BA3D-BD06176012B3} " + rootNode.get(
                                                "result"
                                            ) + " " + rootNode.get(
                                                "server_error"
                                            ) + " " + rootNode.get("result") + " idScript = " + idScript + "\",\"Result\"=\"\"}"
                                        )
                                        return
                                    }
                                } else {
                                    placeCode = "12.1"
                                    Log.e(
                                        tagError,
                                        "{8D6B8C92-B1DA-4C7A-A490-66FE67351051} " + rootNode.get("result") + " " + rootNode.get(
                                            "server_error"
                                        ) + " idScript = " + idScript
                                    );
                                    arrayOnReadyCallBack = arrayOf(
                                        "fail_serv",
                                        "{\"Error\":\"{8D6B8C92-B1DA-4C7A-A490-66FE67351051} " + rootNode.get(
                                            "result"
                                        ) + " " + rootNode.get(
                                            "server_error"
                                        ) + " " + rootNode.get("result") + " idScript = " + idScript + "\",\"Result\"=\"\"}"
                                    )
                                    return
                                }
                            } else {
                                placeCode = "10.1"
                                Log.e(
                                    tagError,
                                    "{C301A9BE-C534-4C7A-85C5-3B6A08A4BF46} " + rootNode.get("result") + " " + rootNode.get(
                                        "server_error"
                                    )
                                );
                                arrayOnReadyCallBack =
                                    arrayOf(
                                        "fail_serv",
                                        "{\"Error\":\"{C301A9BE-C534-4C7A-85C5-3B6A08A4BF46} " + rootNode.get("result") + " " + rootNode.get(
                                            "server_error"
                                        ) + " " + rootNode.get("result") + "\",\"Result\"=\"\"}"
                                    )
                                return
                            }
                        } else {
                            placeCode = "9.1"
                            Log.e(
                                tagError,
                                "{985C2501-9C17-4FD5-A976-043DF2CF6E9C} " + rootNode.get("result") + " " + rootNode.get(
                                    "server_error"
                                )
                            );
                            arrayOnReadyCallBack = arrayOf(
                                "fail_serv",
                                "{\"Error\":\"{985C2501-9C17-4FD5-A976-043DF2CF6E9C} " + rootNode.get("result") + " " + rootNode.get(
                                    "server_error"
                                ) + " " + rootNode.get("result") + "\",\"Result\"=\"\"}"
                            )
                            return
                        }

                    } else {
                        placeCode = "8.1"
                        Log.e(
                            tagError,
                            "{D330FA6A-5C28-4D36-83F1-ACADE2646CE6} " + rootNode.get("result") + " " + rootNode.get(
                                "server_error"
                            )
                        );
                        arrayOnReadyCallBack = arrayOf(
                            "fail_serv",
                            "{\"Error\":\"{D330FA6A-5C28-4D36-83F1-ACADE2646CE6} " + rootNode.get("result") + " " + rootNode.get(
                                "server_error"
                            ) + "\",\"Result\"=\"\"}"
                        )
                        return
                    }
                }
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
            DBLocal.invoke().close() // TODO: УДАЛИТЬ!!!!
//            DBLocal.invoke().close() // TODO: УДАЛИТЬ!!!!
//            DBLocal.invoke().close() // TODO: УДАЛИТЬ!!!!
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
            DBLocal.invoke().beginTransaction()
            // поместить новый скрипт в бд:
            //  есть ли такой скрипт
            placeCode = "16.1"

            val taskInsScriptInDBAsync = TaskInsScriptInDBAsync()
            arrayOnReadyCallBack =
                taskInsScriptInDBAsync.execute(idScript, date, script, typeScript, dbName, tName).get()
            if (arrayOnReadyCallBack != null) {
                // значит вставка скрипта в бд выполнена с ошибкой
                return
            }

//            uiScope.launch(Dispatchers.Main) {
//                val d = uiScope.async(Dispatchers.IO) {
//                    placeCode = "17"
//                    val res =
//                        DBLocal.invoke().todoTScriptsFrServDao().getCountByUUID(idScript!!)
//                    placeCode = "18"
//                    return@async res
//                }
//                val resIsScriptExist = launch@ d.await()
//                placeCode = "19"
//
//                if (resIsScriptExist.equals(0)) {
//                    placeCode = "20"
//                    // если нет, то добавляем
//                    val d = uiScope.async(Dispatchers.IO) {
//                        placeCode = "21"
//                        val res1 = DBLocal.invoke().todoTScriptsFrServDao()
//                            .insScript(
//                                TSCRIPTSFRSERV(
//                                    idScript!!,
//                                    date!!,
//                                    script,
//                                    typeScript,
//                                    dbName,
//                                    tName,
//                                    null
//                                )
//                            )
//                        placeCode = "22"
//                        // добавился ли скрипт в бд?
//                        placeCode = "24"
//                        val res2 = DBLocal.invoke().todoTScriptsFrServDao()
//                            .getCountByUUID(idScript!!)
//                        placeCode = "25"
//                        if (res2.equals(0)) {
//                            canCloseCon = true
//                            placeCode = "27"
//                            Log.e(
//                                tagError,
//                                "{774D260B-CB56-4939-A9D5-F429628C1249} скрипт не добавился в бд. idScript = " + idScript
//                            )
//                            DBLocal.invoke().endTransaction()
//                            arrayOnReadyCallBack = arrayOf(
//                                "fail_mob",
//                                idScript!!,
//                                "{774D260B-CB56-4939-A9D5-F429628C1249} скрипт не добавился в бд. idScript = " + idScript
//                            )
//                            return
//                        }
//                    }
//                    launch@ d.await()
//                    placeCode = "23"
//
//                    // добавился ли скрипт в бд?
//                    uiScope.launch(Dispatchers.Main) {
//                        val d = uiScope.async(Dispatchers.IO) {
//                            placeCode = "24"
//                            val res = DBLocal.invoke().todoTScriptsFrServDao()
//                                .getCountByUUID(idScript!!)
//                            placeCode = "25"
//                            return@async res
//                        }
//                        val resIsScriptExist = launch@ d.await()
//                        placeCode = "26"
//                        if (resIsScriptExist.equals(0)) {
//                            canCloseCon = true
//                            placeCode = "27"
//                            Log.e(
//                                tagError,
//                                "{774D260B-CB56-4939-A9D5-F429628C1249} скрипт не добавился в бд. idScript = " + idScript
//                            )
//                            DBLocal.invoke().endTransaction()
//                            arrayOnReadyCallBack = arrayOf(
//                                "fail_mob",
//                                idScript!!,
//                                "{774D260B-CB56-4939-A9D5-F429628C1249} скрипт не добавился в бд. idScript = " + idScript
//                            )
//                            return@launch
//                        }
//                    }
//                } else {
//                    placeCode = "20.1"
//                    // если есть, то проверяем, выполнен ли он
//                    uiScope.launch(kotlinx.coroutines.Dispatchers.Main) {
//                        val d = uiScope.async(kotlinx.coroutines.Dispatchers.IO) {
//                            val res =
//                                DBLocal.invoke().todoTScriptsFrServDao()
//                                    .isSqriptNotExec(idScript!!)
//                            return@async res
//                        }
//                        val resIsSqriptNotExec = launch@ d.await()
//                        if (resIsSqriptNotExec.equals(0)) {
//                            canCloseCon = true
//                            placeCode = "27"
//                            // значит, выполнен скрипт
//                            Log.e(
//                                tagError,
//                                "{1A91374B-F72A-4058-B934-A66DD88C4EA7} скрипт с таким id уже был передан в приложение и был выполнен"
//                            )
////                            DBLocal.invoke().setTransactionSuccessful()
//                            DBLocal.invoke().endTransaction()
//                            arrayOnReadyCallBack = arrayOf(
//                                "fail_mob",
//                                idScript!!,
//                                "{1A91374B-F72A-4058-B934-A66DD88C4EA7} скрипт с таким id уже был передан в приложение и был выполнен"
//                            )
//                            return@launch
//                        }
//                    }
//                    DBLocal.invoke()
//                        .setTransactionSuccessful() // TODO: нужно ли закрывать транзакцию?
//                    canCloseCon = false
//                }
//            }
        } catch (e: Exception) {
            if (idScript != null) {
                Log.e(
                    tagError,
                    "{\"Error\":\"\", \"Result\":\"{642F0DFD-33BD-4CE8-8BAD-1157A1F51E1E} e = " + e + " idScript = " + idScript + " placeCode = " + placeCode + "\"}"
                )
                if (placeCode.toInt() >= 16 && DBLocal.invoke().isOpen) {
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
                "{8B041049-B293-4A6A-BD8E-6804432EDAF3} placeCode = " + placeCode + " canCloseCon = " + canCloseCon + " isOpen = " + DBLocal.invoke().isOpen + " inTransaction = " + DBLocal.invoke().inTransaction()
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

        Log.i(
            tagInfo,
            "{ACFD6EE1-5E5D-4EC2-819A-325F7EA8D7CC}" + " placeCode = " + placeCode
        )

        onReadyCallbackArray.onReady(
            arrayOf(
                "OK",
                idScript!!
            )
        )
        return
    }

    fun insScriptInDB(
        idScript: String,
        date: Date,
        script: String,
        typeScript: String,
        dbName: String,
        tName: String
    ): Array<Any>? {
        try {
            var arrayOnReadyCallBack: Array<Any>? = null
            placeCode = "17.1"
            val resIsScriptExist =
                DBLocal.invoke().todoTScriptsFrServDao().getCountByUUID(idScript!!)
            placeCode = "18.1"
            if (resIsScriptExist.compareTo(0) == 0) {
                placeCode = "21.1"
                val res = DBLocal.invoke().todoTScriptsFrServDao()//!!!!
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
            } else {
                val res3 =
                    DBLocal.invoke().todoTScriptsFrServDao()
                        .isSqriptNotExec(idScript!!)
                if (res3.compareTo(0) == 0) {
                    canCloseCon = true
                    placeCode = "27.1"
                    // значит, выполнен скрипт
                    val error =
                        "{\"Error\":\"\", \"Result\":\"{1A91374B-F72A-4058-B934-A66DD88C4EA7} скрипт с таким id уже был передан в приложение и был выполнен\"}"
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
                    return arrayOnReadyCallBack
                }
                canCloseCon = false
            }
            return null
        } catch (e: java.lang.Exception) {
            val error =
                "{\"Error\":\"\", \"Result\":\"{C0477E7F-C1B9-4EC5-AC7E-C01B71B5EB1D} this.javaClass.name = " + this.javaClass.name + " e = " + e + "\"}"
            Log.w(tagWarn, error)
            return arrayOf(
                "fail_mob",
                idScript,
                error
            )
        }
    }

    inner class TaskInsScriptInDBAsync : AsyncTask<Any, Void, Array<Any>?>() {

        override fun doInBackground(vararg params: Any): Array<Any>? {
            var result: Array<Any>? = null
            try {
                result = insScriptInDB(
                    params[0] as String,
                    params[1] as Date,
                    params[2] as String,
                    params[3] as String,
                    params[4] as String,
                    params[5] as String
                )
            } catch (e: Exception) {
                val error =
                    "{\"Error\":\"\", \"Result\":\"{C55787A3-F2D3-42FD-8C07-3C1E11D414BA} this.javaClass.name = " + this.javaClass.name + " e = " + e + "\"}"
                Log.w(tagWarn, error)
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

    fun execScriptFrDb(
        login: String,
        idScript: String
    ): Array<Any>? {
        var arrayOnReadyCallBack: Array<Any>? = null

        var answer = ""
        var script = ""
        var typeScript = ""
        var dbName = ""
        var tName = ""
        var date: Date? = null

        // получение скрипта из таблицы TSCRIPTSFRSERV
        val resgetScriptByUUID = DBLocal.invoke().todoTScriptsFrServDao()
            .getSqriptNotExecByUUID(idScript)
        if (resgetScriptByUUID != null) {
            script = resgetScriptByUUID.script
            typeScript = resgetScriptByUUID.typeScript
            dbName = resgetScriptByUUID.dbName
            date = resgetScriptByUUID.dtc
            var resExecQuery: Array<Any> = execQuery(idScript, dbName, typeScript, script)
            if (resExecQuery[0].equals("ok")) {
                // значит, запрос выполнился и нужно заполнить поле IS_EXECUTE
                val resUpdIsExecute = DBLocal.invoke()
                    .todoTScriptsFrServDao()
                    .updIsExecuteByIdScript(Date().toString(), idScript)
                if (resUpdIsExecute.compareTo(1) != 0) {
                    val error =
                        "{\"Error\":\"\", \"Result\":\"{3493F24A-C4BD-4712-BDD0-1DA0DF291F46} placeCode = " + placeCode + "idScript = " + idScript + "\"}"
                    Log.e(
                        tagError,
                        error
                    )
                    arrayOnReadyCallBack = arrayOf(
                        "fail_mob",
                        idScript!!,
                        error
                    )
                    return arrayOnReadyCallBack
                } else {
                    DBLocal.invoke().setTransactionSuccessful()
                }
                Log.i(
                    tagInfo,
                    "{2C11C290-02E9-4F6E-A073-7F3127AE9017} idScript = " + idScript + " placeCode = " + placeCode
                )
            }
            Log.i(
                tagInfo,
                "{2C11C290-02E9-4F6E-A073-7F3127AE9017} idScript = " + idScript + " placeCode = " + placeCode
            )
            arrayOnReadyCallBack = resExecQuery
            return arrayOnReadyCallBack
        } else {
            val error =
                "{\"Error\":\"\", \"Result\":\"{98D2670E-4B1C-4CB3-A2EF-0516496F3CE8} Нет невыполненного скрипта с id: " + idScript + "\"}"
            Log.e(
                tagError,
                error
            )
            arrayOnReadyCallBack = arrayOf(
                "fail_mob",
                idScript!!,
                error
            )
            return arrayOnReadyCallBack
        }
    }

    inner class TaskExecScriptFrDb : AsyncTask<String, Void, Array<Any>?>() {

        override fun doInBackground(vararg params: String): Array<Any>? {
            var result: Array<Any>? = null
            try {
                result = execScriptFrDb(params[0], params[1])
            } catch (e: Exception) {
                val error =
                    "{\"Error\":\"\", \"Result\":\"{C55787A3-F2D3-42FD-8C07-3C1E11D414BA} e = " + e + " this.javaClass.name = " + this.javaClass.name + "\"}"
                Log.w(tagWarn, error)
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

    fun executeScriptFrServ(
        login: String,
        idScript: String,
        onReadyCallbackArray: OnReadyCallbackArray
    ) {
        var arrayOnReadyCallBack: Array<Any>? = null
        var taskExecScriptFrDb = TaskExecScriptFrDb()
        try {
            arrayOnReadyCallBack = taskExecScriptFrDb.execute(login, idScript).get()
        } finally {
            if (DBLocal.invoke().isOpen) {
                if (DBLocal.invoke().inTransaction()) {
                    DBLocal.invoke().endTransaction()
                }
                DBLocal.invoke().close()
            }

            Log.i(
                tagInfo,
                "{E913072B-2929-453F-AE9C-F8CA652A4797} placeCode = " + placeCode + " isOpen = " + DBLocal.invoke().isOpen + " inTransaction = " + DBLocal.invoke().inTransaction()
            )

            if (arrayOnReadyCallBack != null) {
                onReadyCallbackArray.onReady(arrayOnReadyCallBack!!)
                return
            }
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
            if (typeScript.equals("SELECT")) {
                var cur: Cursor = roomDatabase.query(SimpleSQLiteQuery(script))
                return arrayOf(
                    "ok",
                    idScript,
                    "{\"Result\":" + convertCurInString(cur) + ", \"Error\":\"\"}"
                )
            } else if (typeScript.equals("INSERT")) {
                var cur: Cursor = roomDatabase.query(SimpleSQLiteQuery(script))
                return arrayOf(
                    "ok",
                    idScript,
                    "{\"Result\":\"insert execute without errors\", \"Error\":\"\"}"
                )
            } else if (typeScript.equals("UPDATE")) {
                var cur: Cursor = roomDatabase.query(SimpleSQLiteQuery(script))
                return arrayOf(
                    "ok",
                    idScript,
                    "{\"Result\":\"update execute without errors\", \"Error\":\"\"}"
                )
            } else {
                val error =
                    "{\"Result\":\"{EF13D18B-7D02-40E3-9E31-162F00069BB4} некорректное dbName = " + dbName + "\", \"Error\":\"\"}"
                Log.e(tagError, error)
                return arrayOf(
                    "fail_mob",
                    idScript,
                    error
                )
            }
        } else {
            val error =
                "{\"Error\":\"\", \"Result\":\"{4AC47BF6-58E4-4F0F-9669-F60AE9D4AB41} Некорректное DB_NAME: " + dbName + " у скрипта с id: " + idScript + "\"}"
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
        if(countRow.compareTo(1) > 0){
            result +="["
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
        result = result.substring(0, result.lastIndexOf(","))
        if(countRow.compareTo(1) > 0){
            result +="]"
        }

        if (result.equals(""))
            result += "{\"res\":\"empty res\"}"
        return "$result"
    }


//    fun executeScriptFrServ1(
//        login: String,
//        idScript: String,
//        onReadyCallbackArray: OnReadyCallbackArray
//    ) {
//        var arrayOnReadyCallBack: Array<Any>? = null
//
//        var answer = ""
//        var script = ""
//        var typeScript = ""
//        var dbName = ""
//        var tName = ""
//        var date: Date? = null
//
//        var placeCode = "0"
//        try {
//
//            // получение скрипта из таблицы TSCRIPTSFRSERV
//            uiScope.launch(kotlinx.coroutines.Dispatchers.Main) {
//                val d = uiScope.async(kotlinx.coroutines.Dispatchers.IO) {
//                    val res = DBLocal.invoke().todoTScriptsFrServDao()
//                        .getSqriptNotExecByUUID(idScript)
//                    return@async res
//                }
//                val resgetScriptByUUID = launch@ d.await()
//
//                if (resgetScriptByUUID != null) {
//                    script = resgetScriptByUUID.script
//                    typeScript = resgetScriptByUUID.typeScript
//                    dbName = resgetScriptByUUID.dbName
//                    tName = resgetScriptByUUID.tName
//                    date = resgetScriptByUUID.dtc
//                    var resExecQuery: Array<Any> = execQuery(idScript, dbName, typeScript, script)
//                    if (resExecQuery[0].equals("ok")) {
//                        // значит, запрос выполнился и нужно заполнить поле IS_EXECUTE
//                        uiScope.launch(kotlinx.coroutines.Dispatchers.Main) {
//                            val d = uiScope.async(kotlinx.coroutines.Dispatchers.IO) {
//                                val res = DBLocal.invoke()
//                                    .todoTScriptsFrServDao()
//                                    .updIsExecuteByIdScript(Date().toString(), idScript)
//                                return@async res
//                            }
//                            val resUpdIsExecute = launch@ d.await()
//                            if (!resUpdIsExecute.equals(1)) {
//                                Log.e(
//                                    tagError,
//                                    "{3493F24A-C4BD-4712-BDD0-1DA0DF291F46} idScript = " + idScript + " placeCode = " + placeCode
//                                )
//                                arrayOnReadyCallBack = arrayOf(
//                                    "fail_mob",
//                                    idScript!!,
//                                    "{3493F24A-C4BD-4712-BDD0-1DA0DF291F46} placeCode = " + placeCode + "idScript = " + idScript
//                                )
//                                return@launch
//                            } else {
//                                DBLocal.invoke().setTransactionSuccessful()
//                            }
//                        }
//                        Log.i(
//                            tagInfo,
//                            "{2C11C290-02E9-4F6E-A073-7F3127AE9017} idScript = " + idScript + " placeCode = " + placeCode
//                        )
//                    }
//                    Log.i(
//                        tagInfo,
//                        "{2C11C290-02E9-4F6E-A073-7F3127AE9017} idScript = " + idScript + " placeCode = " + placeCode
//                    )
//                    arrayOnReadyCallBack = resExecQuery
//                    return@launch
//
//                } else {
//                    Log.e(
//                        tagError,
//                        "{98D2670E-4B1C-4CB3-A2EF-0516496F3CE8} Нет невыполненного скрипта с id: " + idScript
//                    )
//                    arrayOnReadyCallBack = arrayOf(
//                        "fail_mob",
//                        idScript,
//                        "{98D2670E-4B1C-4CB3-A2EF-0516496F3CE8} Нет невыполненного скрипта с id: " + idScript
//                    )
//                    return@launch
//                }
//            }
//        } finally {
//            if (DBLocal.invoke().isOpen) {
//                if (DBLocal.invoke().inTransaction()) {
//                    DBLocal.invoke().endTransaction()
//                }
//                DBLocal.invoke().close()
//            }
//
//            Log.i(
//                tagInfo,
//                "{E913072B-2929-453F-AE9C-F8CA652A4797} placeCode = " + placeCode + " isOpen = " + DBLocal.invoke().isOpen + " inTransaction = " + DBLocal.invoke().inTransaction()
//            )
//
//            if (arrayOnReadyCallBack != null) {
//                onReadyCallbackArray.onReady(arrayOnReadyCallBack!!)
//                return
//            }
//        }
//    }
}
