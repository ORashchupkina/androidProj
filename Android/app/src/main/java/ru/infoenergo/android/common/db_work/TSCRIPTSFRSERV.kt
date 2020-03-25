package ru.infoenergo.android.common.db_work

import androidx.room.*
import java.util.*

//@TypeConverters(Converters::class)
@Entity(tableName = "TSCRIPTSFRSERV")
data class TSCRIPTSFRSERV/*(val uuid : String, val dtc : Date, val script : String, val typeScript : String, val typeScript : String, val dbName : String, val isExecute : String)*/(
    @PrimaryKey(autoGenerate = false)
    val uuid: String,
    @ColumnInfo(name = "DTC")
    var dtc: Date?,
    @ColumnInfo(name = "SCRIPT") var script: String,
    @ColumnInfo(name = "TYPE_SCRIPT") var typeScript: String,
    @ColumnInfo(name = "DB_NAME") var dbName: String,
//    @ColumnInfo(name = "T_NAME") var tName: String,
    @ColumnInfo(name = "IS_EXECUTE") var isExecute: String?,
    @ColumnInfo(name = "IS_SEND") var isSend: String?
)
