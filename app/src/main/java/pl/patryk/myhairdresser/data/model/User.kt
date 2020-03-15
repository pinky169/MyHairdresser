package pl.patryk.myhairdresser.data.model

class User {

    var name: String? = null
    var surname: String? = null
    var email: String? = null
    var age: Int? = 0
    var isAdmin: Boolean = false

    constructor(name: String, surname: String, email: String, age: Int, isAdmin: Boolean) {
        this.name = name
        this.surname = surname
        this.email = email
        this.age = age
        this.isAdmin = isAdmin
    }

    constructor(email: String?) {
        this.email = email
    }

    constructor()

    fun toMap(): Map<String, Any>? {
        val result: HashMap<String, Any> = HashMap()
        result["name"] = name!!
        result["surname"] = surname!!
        result["email"] = email!!
        result["age"] = age!!
        result["isAdmin"] = isAdmin
        return result
    }
}