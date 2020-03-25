package ru.infoenergo.android

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ru.infoenergo.android.common.MyUtils
import ru.infoenergo.android.databinding.ActivityLoginBinding
import android.app.AlarmManager
import android.app.PendingIntent
import ru.infoenergo.android.common.db_work.DBLocal


class LoginActivity : AppCompatActivity() {
    internal var fullNameClass = this.javaClass.toString()
    internal var tagError = "error." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)
    internal var tagInfo =
        "information." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)
    internal var tagWarn = "warning." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)

    private var mPassView: EditText? = null
    private var mLoginView: EditText? = null
    private var mButton: Button? = null

    var alertDialogBuilder : AlertDialog.Builder? = null

    val REQUEST_ALL = 12

    var activity : LoginActivity?= null

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this@LoginActivity

        setContentView(R.layout.activity_login)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        val viewModel = ViewModelProviders.of(this).get(LoginActivityModel::class.java)
        binding.viewModel = viewModel

        mPassView = findViewById(R.id.password)
        mLoginView = findViewById(R.id.login)
        mButton = findViewById(R.id.btnEnter)

        viewModel.showMessage.observe(this, Observer { res ->
            showDiag(res[0], res[1])
        })

        viewModel.viewLoginActivity = object : ViewLoginActivity {
            override fun startMainActivity() {
                activity!!.startActivity(Intent(activity, MainActivity::class.java))
            }
        }

        val alarmIntentTmGetScriptFrServ = Intent(this, TmGetScriptFrServ::class.java)
        val pendingIntentTmGetScriptFrServ = PendingIntent.getBroadcast(this, 0, alarmIntentTmGetScriptFrServ, PendingIntent.FLAG_CANCEL_CURRENT)
        val alarmManagerTmGetScriptFrServ = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManagerTmGetScriptFrServ.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 1000,
            (1 * 60 * 1000).toLong(),
            pendingIntentTmGetScriptFrServ
        )

        val alarmIntentTmExecScriptFrServ = Intent(this, TmExecScriptFrServ::class.java)
        val pendingIntentTmExecScriptFrServ = PendingIntent.getBroadcast(this, 0, alarmIntentTmExecScriptFrServ, PendingIntent.FLAG_CANCEL_CURRENT)
        val alarmManagerTmExecScriptFrServ = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManagerTmExecScriptFrServ.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 1000,
            (1 * 60 * 1000).toLong(),
            pendingIntentTmExecScriptFrServ
        )

        val alarmIntentTmSendScriptAnswerToServ = Intent(this, TmSendScriptAnswerToServ::class.java)
        val pendingIntentTmSendScriptAnswerToServ = PendingIntent.getBroadcast(this, 0, alarmIntentTmSendScriptAnswerToServ, PendingIntent.FLAG_CANCEL_CURRENT)
        val alarmManagerTmSendScriptAnswerToServ = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManagerTmSendScriptAnswerToServ.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 1000,
            (1 * 60 * 1000).toLong(),
            pendingIntentTmSendScriptAnswerToServ
        )
    }

    fun showDiag(title: String, message: String) {
        try {
            if (title != "") alertDialogBuilder!!.setTitle(title)
            alertDialogBuilder!!.setMessage(MyUtils.fromHtml(message))
            alertDialogBuilder!!.setPositiveButton(
                "Закрыть"
            ) { dialog, i -> dialog.dismiss() }
            alertDialogBuilder!!.show()
        } catch (e: Exception) {
            Log.e(tagError, "{8324C6D4-9B5F-4C0B-B293-A7D162160C7F} e = " + e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        DBLocal.close()
        Log.i(tagInfo, "{7525FB76-C4B5-47A1-AC53-5AAA55E2C5CF}")
    }
}
