package pl.patryk.myhairdresser.ui.auth

interface AuthListener {

    fun onStarted()
    fun onSuccess(code: Int)
    fun onFailure(message: String)
    fun onIncorrectEmail(errorCode: Int)
    fun onIncorrectPassword(errorCode: Int)
    fun onIncorrect2ndPassword(errorCode: Int)
    fun onPermissionGranted(isAdmin: Boolean?)
    fun onNoConnectionAvailable()
}