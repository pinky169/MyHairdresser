package pl.patryk.myhairdresser.data.model

class User {

    var name: String = ""
    var surname: String = ""
    var email: String = ""
    var age: String = ""
    var phone: String = ""
    var photo: Photo = Photo()
    var isAdmin: Boolean = false

    constructor(name: String, surname: String, email: String, age: String, phone: String, photo: Photo, isAdmin: Boolean) {
        this.name = name
        this.surname = surname
        this.email = email
        this.age = age
        this.phone = phone
        this.photo = photo
        this.isAdmin = isAdmin
    }

    constructor(email: String) {
        this.email = email
    }

    // Empty constructor for Firebase DB
    constructor()

    fun toMap(): Map<String, Any>? {
        val result: HashMap<String, Any> = HashMap()
        result["name"] = name
        result["surname"] = surname
        result["email"] = email
        result["age"] = age
        result["phone"] = phone
        result["photo"] = photo
        result["isAdmin"] = isAdmin
        return result
    }
}