package pl.patryk.myhairdresser.ui.profile

interface UserListener {
    fun onStarted()
    fun onSuccess()
    fun onCanceled()
    fun onUploadStarted()
    fun onUploadSuccessful()
    fun onUploadFailed()
}