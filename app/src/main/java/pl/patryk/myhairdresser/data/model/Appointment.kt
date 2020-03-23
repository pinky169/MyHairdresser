package pl.patryk.myhairdresser.data.model

data class Appointment(var userID: String = "",
                       var person: String = "",
                       var service: String = "",
                       var date: String = "",
                       var contact_phone: String = "",
                       var verification_state: String = VERIFICATION_STATE_IDLE) {

    // Used when calling updateChildren
    // which requires Map<String, Any> argument.
    // Updates only mentioned fields
    fun toMap(): Map<String, Any>? {
        val result: HashMap<String, Any> = HashMap()
        result["person"] = person
        result["contactPhone"] = contact_phone
        return result
    }

    companion object {
        const val VERIFICATION_STATE_IDLE = "STATE_IDLE"
        const val VERIFICATION_STATE_PENDING = "STATE_PENDING"
        const val VERIFICATION_STATE_APPROVED = "STATE_APPROVED"
        const val VERIFICATION_STATE_REJECTED = "STATE_REJECTED"
    }
}