package ru.infoenergo.android

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel

class MainActivityModel(application: Application) : AndroidViewModel(application) {

    var context: Context = application.applicationContext

}