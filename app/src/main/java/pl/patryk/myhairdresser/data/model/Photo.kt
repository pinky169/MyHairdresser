package pl.patryk.myhairdresser.data.model

import java.io.Serializable

class Photo : Serializable {

    var name: String? = null
    var photoUrl: String? = null

    constructor(name: String, photoUrl: String) {
        this.name = name
        this.photoUrl = photoUrl
    }

    // Empty constructor for Firebase DB
    constructor()

    fun toMap(): Map<String, Any>? {
        val result: HashMap<String, Any> = HashMap()
        result["name"] = name!!
        result["photoUrl"] = photoUrl!!
        return result
    }
}