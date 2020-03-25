package ru.infoenergo.android

import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.*
import ru.infoenergo.android.common.*
import ru.infoenergo.android.common.cripto.Sha256
import ru.infoenergo.android.common.db_work.DBConst
import ru.infoenergo.android.common.db_work.DBLocal
import ru.infoenergo.android.common.db_work.TUSER
import ru.infoenergo.android.http.security.GetTicketForTicket
import ru.infoenergo.android.http.security.GetTicketForToken
import ru.infoenergo.android.http.security.GetToken
import ru.infoenergo.mobiaapp.http.security.IsServOn
import java.io.File

class LoginActivityModel(application: Application) : AndroidViewModel(application) {

    var context: Context = application.applicationContext
    var viewLoginActivity: ViewLoginActivity? = null

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val login = ObservableField("user")
    val pass = ObservableField("111")

    internal val showMessage = SingleLiveEvent<Array<String>>()

    internal var fullNameClass = this.javaClass.toString()
    internal var tagError = "error." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)
    internal var tagInfo =
        "information." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)
    internal var tagWarn = "warning." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(Build.VERSION_CODES.M)
    fun onLoginClick() {
        try {
            var res_log: Boolean
            IsServOn().get(object : OnReadyCallbackOnlyBoolean {
                override fun onReady(resIsServOn: Boolean) {
                    if (resIsServOn) {
                        Log.i(tagInfo, "1{AE4C68B0-07D1-4771-8D5B-358C5C9AD439}")
                        loginRun(object : OnReadyCallbackBoolean {
                            override fun onReady(result: Boolean, errors: ArrayList<String>) {
                                if (result) {
                                    // обновление информации о пользователе
                                    refreshUserInfoIfNeed(
                                        login.get()!!,
                                        Sha256.getInstance().getHash(pass.get()!!)!!,
                                        object : OnReadyCallbackOnlyBoolean {
                                            override fun onReady(result: Boolean) {
                                                // проверка, есть ли папка текущего пользователя
                                                val sDirectoryUser = DBConst.dbPath + "/" + login.get()!!
                                                val fDirectoryUser = File(sDirectoryUser)
                                                val sDBuser = sDirectoryUser + "/" + login.get()!! + ".db"
                                                if (DBLocal.invoke().isOpen) {
                                                    DBLocal.close()
                                                }
                                                if (viewLoginActivity != null) {
                                                    viewLoginActivity!!.startMainActivity()
                                                } else {
                                                    showMessage.value = (arrayOf(
                                                        "Сообщение",
                                                        "Ошибка входа. Информация для разработчика: {E51D4376-5BB4-4B8A-9069-96422A9F5E0B}"
                                                    ))
                                                }
                                            }
                                        })
                                } else {
                                    showMessage.value = (arrayOf(
                                        "Сообщение",
                                        "Ошибка авторизации. <\br> Информация для разработчиков: {FE9F7624-7267-491C-918E-EE0F6888A807} error: " + errors.toString()
                                    ))
                                }
                                Log.i(tagInfo, "2{AE4C68B0-07D1-4771-8D5B-358C5C9AD439}")
                            }
                        })
                    } else {
                        localLogin(
                            login.get()!!,
                            Sha256.getInstance().getHash(pass.get()!!)!!,//pass.get()!!
                            object : OnReadyCallbackBoolean {
                                override fun onReady(result: Boolean, errors: ArrayList<String>) {
                                    try {
                                        Log.i(tagInfo, "{2A342767-99DA-46B5-B60D-E140949BF2FB}")
                                        res_log = result
                                        if(DBLocal.invoke().isOpen) {
                                            DBLocal.close()
                                        }
                                        if (res_log) {
                                            if (viewLoginActivity != null) {
                                                DBLocal.close()
                                                viewLoginActivity!!.startMainActivity()
                                            }
                                            else{
                                                showMessage.value = (arrayOf(
                                                    "Сообщение",
                                                    "Ошибка входа. Информация для разработчика: {3C333D78-2336-40C5-9783-FD953D59234F}"
                                                ))
                                            }
                                        }else {
                                            if (errors[0].equals(""))
                                                showMessage.value = (arrayOf(
                                                    "Сообщение",
                                                    "Ошибка автономного входа: Неправильный логин или пароль."
                                                ))
                                            else
                                                showMessage.value = (arrayOf(
                                                    "Сообщение",
                                                    "Ошибка автономного входа. <\br> Информация для разработчиков: {93527FE7-4A6F-4010-B834-AC01B910795D} error: " + errors[0]
                                                ))
                                        }
                                    } catch (e: Exception) {
                                        // TODO: что если соединение открыто не тут?
                                        if (DBLocal.invoke().isOpen) {
                                            if (DBLocal.invoke().inTransaction()) {
                                                DBLocal.invoke().endTransaction()
                                            }
                                            DBLocal.close()
                                        }
                                        showMessage.value = (arrayOf(
                                            "Сообщение",
                                            "Ошибка автономного входа. <\br> Информация для разработчиков: {EE94B428-7A9F-4312-9C54-A5161FA7A3AA} error: " + e
                                        ))
                                }
                            }
                    })
                }
            }
        })

    }catch(e:Exception)
    {
            showMessage.value = (arrayOf(
                "Сообщение",
                "Ошибка входа. <\br> Информация для разработчиков: {2D65C57B-4D06-4900-9DFC-1480D86A01E3} error: " + e
            ))
    }
//        finally {
//            // TODO: что если соединение открыто не тут?
//            if (DBLocal.invoke().isOpen) {
//                if (DBLocal.invoke().inTransaction()) {
//                    DBLocal.invoke().endTransaction()
//                }
//                DBLocal.invoke().close()
//            }
//        }
}

@TargetApi(Build.VERSION_CODES.O)
@RequiresApi(Build.VERSION_CODES.M)
fun loginRun(callback: OnReadyCallbackBoolean) {
    val getTicketForTicket = GetTicketForTicket()
    getTicketForTicket.get(context.getFilesDir().toString() + "/key/", object : OnReadyCallbackBoolean {
        override fun onReady(result: Boolean, errors: ArrayList<String>) {
            if (errors.size == 0) {
                val getTicketForToken = GetTicketForToken()
                getTicketForToken.get(login.get()!!,
                    Sha256.getInstance().getHash(pass.get()!!)!!,
                    Global.getInstance().imei,
                    object : OnReadyCallbackOnlyString {
                        override fun onReady(error: String) {

                            val getToken = GetToken()
                            getToken.get(
                                Crypto.getInstance().ticket_for_token!!,
                                Global.getInstance().imei,
                                object : OnReadyCallbackBoolean {
                                    override fun onReady(result: Boolean, errors: ArrayList<String>) {
                                        callback.onReady(result, errors)
                                    }
                                })
                        }
                    })
            } else {
//                    showErrorMsg(errors[errors.size - 1])
                callback.onReady(false, errors)
            }
        }
    })
}

fun refreshUserInfoIfNeed(login: String, passHash: String, callBack: OnReadyCallbackOnlyBoolean) {
    try {
        // есть ли информация о пользователе в базе
        uiScope.launch(Dispatchers.Main) {
            val d = uiScope.async(Dispatchers.IO) {
                var res : Long? = null
                try{
                res = DBLocal.invoke().todoTUserDao().getCountByLogin(login)
                }catch(e:java.lang.Exception){
                    Log.e(tagError, "{5A2C5932-51A7-4DAC-9F52-73BBD173DC2F} e = " + e)
                }
                return@async res

            }
            val res: Long = launch@ d.await()!!
            var checkInfo: Boolean = res > 0
            if (checkInfo) {
                // то изменяем информацию о пользователе
                uiScope.launch(Dispatchers.Main) {
                    val d = uiScope.async(Dispatchers.IO) {
                        val res = DBLocal.invoke().todoTUserDao().updPassByLogin(passHash, login)
                        return@async res
                    }
                    val res: Int = launch@ d.await()
                    if (res.equals(1)) {
                        callBack.onReady(true)
                    } else
                        callBack.onReady(false)
                }
            } else {
                // вставляем информацию о пользователе
                uiScope.launch(Dispatchers.Main) {
                    val d = uiScope.async(Dispatchers.IO) {
                        DBLocal.invoke().todoTUserDao().insUser(TUSER(login, passHash))
                        return@async
                    }
                    launch@ d.await()
                    callBack.onReady(true)
                }
            }
        }
    }catch(e:java.lang.Exception){
        Log.e(tagError, "{6575DB10-E512-42EF-9B34-44FFD424EBD1} e = " + e)
    }
}

fun localLogin(pi_login: String, pi_pass_hash: String, callBack: OnReadyCallbackBoolean) {
    var checkAuth = false
    var error = ArrayList<String>()
    uiScope.launch(Dispatchers.Main) {
        val d = uiScope.async(Dispatchers.IO) {
            Log.i(tagInfo, "{DF9313AA-DE31-4849-88B5-EBA3ABE80D43} Автономный режим: старт авторизации")
            val res = DBLocal.invoke().todoTUserDao().getCountByLoginPass(pi_login, pi_pass_hash)
            checkAuth = res > 0
            Log.i(
                tagInfo,
                "{789F0386-908C-48F8-A17A-8C27F00D83DB} checkAuth = " + checkAuth
            )
            error.add("")
            return@async error
        }
        val res: ArrayList<String> = launch@ d.await()
        callBack.onReady(checkAuth, error)
    }
}
}
interface ViewLoginActivity {
    fun startMainActivity()
}
