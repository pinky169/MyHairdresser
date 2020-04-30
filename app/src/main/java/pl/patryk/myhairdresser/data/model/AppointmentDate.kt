package pl.patryk.myhairdresser.data.model

data class AppointmentDate(var date: String = "", var time: String = "", var availability: Boolean = true) {

    fun toMap(): Map<String, Any> {
        val result: HashMap<String, Any> = HashMap()
        result["date"] = date
        result["time"] = time
        result["availability"] = availability
        return result
    }

}