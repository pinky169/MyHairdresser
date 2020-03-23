package pl.patryk.myhairdresser.ui.admin

interface AdminListener {
    fun confirm(userID: String)
    fun reject(userID: String)
    fun phoneCall(contactPhone: String)
}