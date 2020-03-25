package ru.infoenergo.android.common

interface OnReadyCallbackOnlyBoolean {
    fun onReady(error: Boolean)
}

interface OnReadyCallbackBoolean {
    fun onReady(result: Boolean, errors: ArrayList<String>)
}

interface OnReadyCallbackOnlyString {
    fun onReady(error: String)
}

interface OnReadyCallbackArray{
    fun onReady(result: Array<Any>)
}

interface OnReadyCallbackArrayNull{
    fun onReady(result: Array<Any>?)
}

interface OnReadyCallbackString{
    fun onReady(result: String, errors: String)
}

interface OnReadyCallback {
    fun onReady()
}