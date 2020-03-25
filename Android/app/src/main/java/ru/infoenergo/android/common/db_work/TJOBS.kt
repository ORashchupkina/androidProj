package ru.infoenergo.android.common.db_work

import androidx.annotation.Nullable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "TJOBS")
data class TJOBS(
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "ID")
    var uuid: String,
    @NonNull
    @ColumnInfo(name = "DTC") var dtc: Date,
//    @ColumnInfo(name = "ID_AUTHOR") var idAuthor: Long, //TODO: нужно ли?
    @NonNull
    @ColumnInfo(name = "ID_EXECUTOR") var idExecutor: Long,
//    @ColumnInfo(name = "ID_USER") var ID_USER: Long, //TODO: нужно ли?
    @Nullable
    @ColumnInfo(name = "ID_JOBS_METERING_TYPE") var idJobsMeteringType: String?, //TODO: нужно ли?
    @Nullable
    @ColumnInfo(name = "GPS_POINT") var gpsPoint: String?,
//    @ColumnInfo(name = "ID_USER_JOBS_METERING_TYPE") var idUserJobsMeteringType: Long,
    @NonNull
    @ColumnInfo(name = "ID_JOBS_ADDR_EXT") var idJobsAddrExt: String, //TODO: нужно ли?
//    @ColumnInfo(name = "ADDR_CODE_LATIT") var addrCodeLatit: String, //TODO: нужно ли?
//    @ColumnInfo(name = "ADDR_CODE_LONGIT") var addrCodeLongit: String, //TODO: нужно ли?
    @NonNull
    @ColumnInfo(name = "ADDR_NAME") var addrName: String,
//    @ColumnInfo(name = "ROOM") var room: String, //TODO: нужно ли?
//    @ColumnInfo(name = "ID_USER_JOBS_ADDR_EXT") var idUserJobsAddrExt: Long,
    @NonNull
    @ColumnInfo(name = "ID_JOBS_STATE_EXT") var idJobsStateExt: String,
    @NonNull
    @ColumnInfo(name = "TO_F_R") var tofr: Date,
    @NonNull
    @ColumnInfo(name = "TO_F_W") var tofw: Date,
    @NonNull
    @ColumnInfo(name = "TO_F_C") var tofc: Date,
    @NonNull
    @ColumnInfo(name = "TO_M_W") var tomw: Date,
    @Nullable
    @ColumnInfo(name = "TO_M_C") var tomc: Date?,
    @Nullable
    @ColumnInfo(name = "FR_MC_R") var frMcR: Date?,
    @Nullable
    @ColumnInfo(name = "FR_M_C") var frmc: Date?,
    @Nullable
    @ColumnInfo(name = "TO_H_W") var tohw: Date?,
    @Nullable
    @ColumnInfo(name = "TO_H_C") var tohc: Date?,
    @Nullable
    @ColumnInfo(name = "END_VALID") var endValid: Date?,
    @Nullable
    @ColumnInfo(name = "END_INVALID") var endInvalid: Date?,
    @Nullable
    @ColumnInfo(name = "CANCELED") var canceled: Date?,
//    @ColumnInfo(name = "ID_USER_JOBS_STATE_EXT") var idUserJobsStateExt: Long,
    @NonNull
    @ColumnInfo(name = "ID_JOBS_PROPERTIES_EXT") var idJobsPropertiesExt: String,
    @NonNull
    @ColumnInfo(name = "CLIENT_NAME") var clientName: String,
    @NonNull
    @ColumnInfo(name = "CLIENT_PHONE") var clientPhone: String,
    @NonNull
    @ColumnInfo(name = "ZAYAVITEL") var zayavitel: String,
    @NonNull
    @ColumnInfo(name = "KOD_ZAYAV") var kodZayav: String,
    @NonNull
    @ColumnInfo(name = "NUM_ZAYAV") var numZayav: String,
    @NonNull
    @ColumnInfo(name = "UVTU") var uvtu: String,
    @NonNull
    @ColumnInfo(name = "ID_UVTU") var idUvtu: String, // TODO: такой тип данных?
    @NonNull
    @ColumnInfo(name = "DT_VISIT_PLAN") var dtVisitPlan: Date,
    //    @ColumnInfo(name = "ID_USER_JOBS_PROPERTIES_EXT") var idUserJobsPropertiesExt: Long,
    @Nullable
    @ColumnInfo(name = "DATE_VISIT_FACT") var dateVisitFact: String?,
    @Nullable
    @ColumnInfo(name = "NOTE") var note: String?,
    @Nullable
    @ColumnInfo(name = "WORK_RESUME") var workResume: Int?

//    // на будущее:
//    @ColumnInfo(name = "ID_POWER") var idPower: Long,
//    @ColumnInfo(name = "POWER") var power: String,
//    @ColumnInfo(name = "ID_POINT_EQUIP") var idPointEquip: Long,
//    @ColumnInfo(name = "POINT_EQUIP") var pointEquip: String,
//    @ColumnInfo(name = "POINT") var point: String,
//    @ColumnInfo(name = "FL1") var fl1: Int,
//    @ColumnInfo(name = "FL2") var fl2: Int,
//    @ColumnInfo(name = "FL3") var fl3: Int,
//    @ColumnInfo(name = "FL4") var fl4: Int,
//    @ColumnInfo(name = "FL5") var fl5: Int,
//    @ColumnInfo(name = "FL6") var fl6: Int,
//    @ColumnInfo(name = "FL_ATP") var flAtp: Int,
//    @ColumnInfo(name = "IDs_ZAMECHANIE") var idsZamechanie: Int,
//    @ColumnInfo(name = "DOP_ZAMECHANIE") var dopZamechanie: Int,
//    @ColumnInfo(name = "INDIRECT_JOINING") var indirectJoining: Int
)
