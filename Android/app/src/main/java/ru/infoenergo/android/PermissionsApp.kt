package ru.infoenergo.android

class PermissionsApp private constructor(){

    companion object {
        @Volatile
        private var INSTANCE: PermissionsApp? = null

        fun getInstance(): PermissionsApp {
            return INSTANCE ?: synchronized(this) {
                PermissionsApp().also {
                    INSTANCE = it
                }
            }
        }
    }

    val PERMISSIONS = arrayOf(
        "android.permission.CAMERA",
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.CALL_PHONE)
}