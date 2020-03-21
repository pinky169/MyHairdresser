package pl.patryk.myhairdresser.data.model

data class Appointment(var person: String = "",
                       var service: String = "",
                       var date: String = "",
                       var contactPhone: String = "") {

    // Used when calling updateChildren
    // which requires Map<String, Any> argument.
    // Updates only mentioned fields
    fun toMap(): Map<String, Any>? {
        val result: HashMap<String, Any> = HashMap()
        result["person"] = person
        result["contactPhone"] = contactPhone
        return result
    }
}