package pl.patryk.myhairdresser.ui.auth

interface AuthListener {

    fun onStarted()
    fun onSuccess()
    fun onFailure(message: String)
    fun onIncorrectEmail(message: String)
    fun onIncorrectPassword(message: String)
    fun onIncorrect2ndPassword(message: String)
}