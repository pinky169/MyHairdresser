package pl.patryk.myhairdresser.data.model

data class User(var name: String = "",
                var surname: String = "",
                var email: String = "",
                var age: String = "",
                var phone: String = "",
                var photo: Photo? = null,
                var admin: Boolean = false) {

    constructor(name: String, surname: String, email: String, age: String, phone: String, admin: Boolean) : this() {
        this.name = name
        this.surname = surname
        this.email = email
        this.age = age
        this.phone = phone
        this.admin = admin
    }

    constructor(email: String) : this() {
        this.email = email
    }

    fun toMap(): Map<String, Any>? {
        val result: HashMap<String, Any> = HashMap()
        result["name"] = name
        result["surname"] = surname
        result["email"] = email
        result["age"] = age
        result["phone"] = phone
        result["admin"] = admin
        return result
    }
}