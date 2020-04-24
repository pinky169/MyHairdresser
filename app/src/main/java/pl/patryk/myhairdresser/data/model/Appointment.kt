package pl.patryk.myhairdresser.data.model

data class Appointment(var userID: String = "",
                       var name: String = "",
                       var phone: String = "",
                       var appointmentID: String = "",
                       var service: String = "",
                       var date: String = "",
                       var verification_state: String = VERIFICATION_STATE_IDLE) {

    companion object {
        const val VERIFICATION_STATE_IDLE = "STATE_IDLE"
        const val VERIFICATION_STATE_PENDING = "STATE_PENDING"
        const val VERIFICATION_STATE_APPROVED = "STATE_APPROVED"
        const val VERIFICATION_STATE_REJECTED = "STATE_REJECTED"
    }

    // Used when calling updateChildren
    // which requires Map<String, Any> argument.
    // Updates only mentioned fields
    fun toMap(): Map<String, Any> {
        val result: HashMap<String, Any> = HashMap()
        result["userID"] = userID
        result["name"] = name
        result["phone"] = phone
        result["appointmentID"] = appointmentID
        result["service"] = service
        result["date"] = date
        result["verification_state"] = verification_state
        return result
    }
}