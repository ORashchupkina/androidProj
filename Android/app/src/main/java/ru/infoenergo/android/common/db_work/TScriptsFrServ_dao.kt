package ru.infoenergo.android.common.db_work

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.util.*
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.room.RawQuery



@Dao
interface TScriptsFrServ_dao {
    @Query("SELECT * FROM TSCRIPTSFRSERV WHERE IS_EXECUTE is null and IS_SEND is null") // 1 use
    fun getSqriptNotExec(): List<TSCRIPTSFRSERV>

    @Query("SELECT * FROM TSCRIPTSFRSERV WHERE IS_EXECUTE is not null and IS_SEND is null") // 1 use
    fun getSqriptExec(): List<TSCRIPTSFRSERV>

    @Query("SELECT * FROM TSCRIPTSFRSERV WHERE UUID = :uuid and IS_EXECUTE is null") // 1 use
    fun getSqriptNotExecByUUID(uuid: String): TSCRIPTSFRSERV

    @Query("SELECT COUNT(*) FROM TSCRIPTSFRSERV WHERE UUID = :uuid") // 2 use
    fun getCountByUUID(uuid: String): Long

    @Query("SELECT COUNT(*) FROM TSCRIPTSFRSERV WHERE UUID = :uuid and IS_EXECUTE is null") // 1 use
    fun isSqriptNotExec(uuid: String): Long

    @Query("UPDATE TSCRIPTSFRSERV SET IS_EXECUTE=:isExecute WHERE uuid=:uuid")
    fun updIsExecuteByIdScript(isExecute: String, uuid: String): Int // 5 use

    @Query("UPDATE TSCRIPTSFRSERV SET IS_SEND=:isSend WHERE uuid=:uuid")
    fun updIsSendByIdScript(isSend: String, uuid: String): Int // 1 use

//    @Query("INSERT INTO TSCRIPTSFRSERV VALUES (:uuid, :dtc, :script, :typeScript, :dbName, :isExecute);")//(UUID, DTC, SCRIPT, TYPE_SCRIPT, DB_NAME, T_NAME, IS_EXECUTE)
//    fun insScriptAll(uuid: String, dtc: Date, script: String, typeScript: String, dbName: String, isExecute: String?)

    @Insert
    fun insScript(script: TSCRIPTSFRSERV) // 1 use


//    @RawQuery
//    fun getUserViaQuery(query: SupportSQLiteQuery): User
}