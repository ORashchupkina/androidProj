package ru.infoenergo.android.common.db_work

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TUser_dao {
    @Query("SELECT COUNT(*) FROM T_USER WHERE LOGIN=:login AND PASS_HASH=:passHash") // 1 use
    fun getCountByLoginPass(login: String, passHash: String): Long

    @Query("SELECT COUNT(*) FROM T_USER WHERE LOGIN=:login") // 1 use
    fun getCountByLogin(login: String): Long

    @Query("UPDATE T_USER SET PASS_HASH=:passHash WHERE LOGIN=:login")
    fun updPassByLogin(passHash: String, login: String): Int // 1 use

    @Insert
    fun insUser(user: TUSER) // 1 use


    @Query("SELECT * FROM T_USER")
    fun getAll(): List<TUSER>

    @Insert
    fun insertAll(vararg params: TUSER)

    @Query("DELETE FROM T_USER")
    fun deleteAll()
}