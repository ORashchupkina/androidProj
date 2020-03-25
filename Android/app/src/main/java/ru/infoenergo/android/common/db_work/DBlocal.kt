package ru.infoenergo.android.common.db_work

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.infoenergo.android.common.App
import ru.infoenergo.android.common.DateSqlite
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration



@Database(entities = arrayOf(TUSER::class, TSCRIPTSFRSERV::class, TJOBS::class), version = 1, exportSchema = false)
@TypeConverters(DateSqlite::class)
abstract class DBLocal : RoomDatabase() {
    abstract fun todoTUserDao(): TUser_dao
    abstract fun todoTJobsDao(): TJobs_dao
    abstract fun todoTScriptsFrServDao(): TScriptsFrServ_dao

    internal var fullNameClass = this.javaClass.toString()
    internal var tagError = "error." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)
    internal var tagInfo =
        "information." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)
    internal var tagWarn = "warning." + fullNameClass.substring(fullNameClass.lastIndexOf(".") + 1)

    companion object {
        @Volatile
        private var instance: DBLocal? = null
        private val LOCK = Any()

        operator fun invoke() = instance ?: synchronized(LOCK) {
            val context = App.instance
            instance ?: buildDatabase(context).also { instance = it }
        }

        fun close() {
//            synchronized(instance as Any) {
                if (instance != null) {
                    instance!!.close()
                    instance = null
                }
//            }
        }



        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            DBLocal::class.java, "local.db"//"user/database_1.db"//
        )
//            .addMigrations(MIGRATION_1_2)
            .build() // enableMultiInstanceInvalidation()
    }


//    fun tuserGetCountByLoginPass(pi_login: String, pi_pass_hash: String, callBack: OnReadyCallbackBoolean) {
//        var checkAuth = false
//        var error = ArrayList<String>()
//        uiScope.launch(Dispatchers.Main) {
//            val d = uiScope.async(Dispatchers.IO) {
//                Log.i(tagInfo, "{DF9313AA-DE31-4849-88B5-EBA3ABE80D43} Автономный режим: старт авторизации")
//                val res = instance?.todoDao()?.getCountByLoginPass(pi_login, pi_pass_hash)
//                checkAuth = res!! > 0
//                Log.i(
//                    tagInfo,
//                    "{789F0386-908C-48F8-A17A-8C27F00D83DB} checkAuth = " + checkAuth
//                )
//                error.add("")
//                return@async error
//            }
//            val res: ArrayList<String> = launch@ d.await()
//            callBack.onReady(checkAuth, error)
//        }
//    }
}

//val MIGRATION_1_2: Migration = object : Migration(1, 2) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        database.execSQL("ALTER TABLE TSCRIPTSFRSERV ADD COLUMN IS_SEND TEXT")
//        database.execSQL("BEGIN TRANSACTION;" +
//                "CREATE TABLE t1_backup AS SELECT UUID, DTC, SCRIPT, TYPE_SCRIPT, DB_NAME, IS_EXECUTE, IS_SEND FROM TSCRIPTSFRSERV; DROP TABLE TSCRIPTSFRSERV; ALTER TABLE t1_backup RENAME TO TSCRIPTSFRSERV; COMMIT;")
//    }

    /*
    BEGIN TRANSACTION;
CREATE TABLE t1_backup AS SELECT UUID, DTC, SCRIPT, TYPE_SCRIPT, DB_NAME, IS_EXECUTE, IS_SEND FROM TSCRIPTSFRSERV; DROP TABLE TSCRIPTSFRSERV; ALTER TABLE t1_backup RENAME TO TSCRIPTSFRSERV;
COMMIT;
     */
    //миграция бд: https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929

