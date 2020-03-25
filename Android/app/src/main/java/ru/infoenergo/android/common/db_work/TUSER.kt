package ru.infoenergo.android.common.db_work

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "T_USER")
data class TUSER(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "LOGIN")
    var login: String,
    @ColumnInfo(name = "PASS_HASH") var passHash: String
)