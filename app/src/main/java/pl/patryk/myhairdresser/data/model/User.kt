package pl.patryk.myhairdresser.data.model

data class User(var name: String = "",
                var surname: String = "",
                var email: String = "",
                var age: String = "",
                var phone: String = "",
                var photo: Photo? = null,
                var appointments: HashMap<String, Appointment>? = null,
                var admin: Boolean = false) {

    // Constructor for updating a User's data in database
    constructor(name: String, surname: String, age: String, phone: String) : this() {
        this.name = name
        this.surname = surname
        this.age = age
        this.phone = phone
    }

    // Constructor for registering a new User
    // when we have only an email
    constructor(email: String) : this() {
        this.email = email
    }

    // Used when calling updateChildren
    // which requires Map<String, Any> argument
    // Updates only mentioned fields
    fun toMap(): Map<String, Any> {
        val result: HashMap<String, Any> = HashMap()
        result["name"] = name
        result["surname"] = surname
        result["age"] = age
        result["phone"] = phone
        return result
    }
}