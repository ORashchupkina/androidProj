package ru.infoenergo.android

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import ru.infoenergo.android.common.OnReadyCallbackArray
import ru.infoenergo.android.common.db_work.DBLocal
import ru.infoenergo.android.common.db_work.TSCRIPTSFRSERV
import java.lang.Exception

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4ClassRunner ::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("ru.infoenergo.android", appContext.packageName)
    }

    @Test
    fun execQuery(){
        val context = androidx.test.InstrumentationRegistry.getInstrumentation().targetContext
        var scriptsFrServ : TmExecScriptFrServ = TmExecScriptFrServ()
//        var tempExec : ScriptsFrServ = ScriptsFrServ(context)
        var temp : Array<Any>? = null
            try{
//                temp = scriptsFrServ.execQuery("1000", "local.db", "SELECT", "SELECT * FROM T_USER")
//                temp = scriptsFrServ.execQuery("1000", "local.db", "UPDATE", "UPDATE T_USER SET PASS_HASH = 'pass_2' WHERE LOGIN = 'login_1'")
        temp = scriptsFrServ.execQuery("1000", "local.db", "INSERT", "INSERT INTO T_USER VALUES('login_1', 'pass_1')")
        }catch (e:Exception){
            Log.e("tagError","{EAE88E13-8B66-49AD-89B0-D6CC90348C58} e = " + e)
        }
        Log.i("{BD45C732-91EC-481D-8760-E57A103E53B5}", " " + temp!![0] + " " + temp[1] + " " + temp[2])
//        Logger.getLogger(" " + temp[0] + " " + temp[1] + " " + temp[2])
//        assertEquals("ru.infoenergo.mobiaapp", context.packageName)
    }

//    @Test
//    fun timertickTest(){
//        var callDataSend : TmExecScriptFrServ = TmExecScriptFrServ()
//        val context = androidx.test.InstrumentationRegistry.getInstrumentation().targetContext
//
//
//        callDataSend.timertick(object : OnReadyCallbackArray {
//            override fun onReady(result: Array<Any>) {
//                Log.i("tagInfo", "{C579638C-74FA-40EC-A657-D5A740149440}:  " + result[0] + result[1])
//                assertEquals("ru.infoenergo.mobiaapp", context.packageName)
//            }
//        })
//    }

    @Test
    fun tmGetScriptFrServ(){
        var tmGetScriptFrServ : TmGetScriptFrServ = TmGetScriptFrServ()
        val context = androidx.test.InstrumentationRegistry.getInstrumentation().targetContext


        tmGetScriptFrServ.timertick()
//            override fun onReady(result: Array<Any>) {
//                Log.i("tagInfo", "{C579638C-74FA-40EC-A657-D5A740149440}:  " + result[0] + result[1])
//                assertEquals("ru.infoenergo.mobiaapp", context.packageName)
//            }
//        })
    }
    @Test
    fun tmExecScriptFrServ() {
        var tmExecScriptFrServ: TmExecScriptFrServ = TmExecScriptFrServ()
        val context = androidx.test.InstrumentationRegistry.getInstrumentation().targetContext

            tmExecScriptFrServ.timertick()
    }

    @Test
    fun tmSendScriptAnswerToServ() {
        var tmSendScriptAnswerToServ: TmSendScriptAnswerToServ = TmSendScriptAnswerToServ()
        val context = androidx.test.InstrumentationRegistry.getInstrumentation().targetContext

        tmSendScriptAnswerToServ.timertick()
    }

//    @Test
//    fun getScript(){
//        val context = androidx.test.InstrumentationRegistry.getInstrumentation().targetContext
//        val scriptsFrServ: ScriptsFrServ = ScriptsFrServ(context)
//        var login = "user"
//
//        scriptsFrServ.getScriptsFrServ(login, object : OnReadyCallbackArray {
//            override fun onReady(result: Array<Any>){
//                Log.i("tagInfo", "{47FA8289-AA5A-4E95-8A1D-3D0759733122} context.packageName = " + context.packageName);
//                assertEquals("ru.infoenergo.mobiaapp", context.packageName)
//            }
//        })
//    }


//    @Test
//    fun test_createDb() {
//        val context = InstrumentationRegistry.getInstrumentation().targetContext
//        // Context of the app under test.
//
//
//        // СОЗДАНИЕ БД:
//        var buffer = ByteArray(1024)
//        val path_outPut: String = "/data/data/ru.infoenergo.mobiaapp.android/databases"//context.getString(R.string.DB_PATH);
//        val db_name: String = "local.db"//context.getString(R.string.DB_NAME);
//        val myOutput: OutputStream
//        var myInput: InputStream
//        var length: Int
//        var log = 0
//        try {
//            myInput = context.getAssets().open("local.db"/*context.getString(R.string.DB_NAME_TEMPLATE)*/)
//
//            log = 1
//            // transfer bytes from the inputfile to the outputfile
//            val dir = File(path_outPut)
//            log = 2
//            dir.mkdirs()
//
//            log = 3
//            val file = File(path_outPut +'/'+ db_name)
//            file.createNewFile()
//            myOutput = FileOutputStream(file)
//
//            log = 4
//            length = myInput.read(buffer)
//            while (length > 0) {
//                log += 2
//                myOutput.write(buffer, 0, length)
//                length = myInput.read(buffer)
//            }
//            log = 5
//            myOutput.close()
//            log = 7
//            myOutput.flush()
//            log = 9
//            myInput.close()
//            log = 11
//            Log.i(
//                "i",
//                "{B9C564F1-52AB-4F1E-9036-1BD78A662BAC} New database has been copied to device!"
//            )
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.w("w", "{D76CC722-D9CA-40FE-B3BB-343970AC1995} $log ERROR: $e")
//        }
//    }
}
