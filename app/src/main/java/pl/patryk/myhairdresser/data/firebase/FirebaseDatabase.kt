package pl.patryk.myhairdresser.data.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import pl.patryk.myhairdresser.data.model.User

class FirebaseDatabase {

    private val storageReference: StorageReference by lazy {
        FirebaseStorage.getInstance().getReference("users")
    }

    private val databaseReference: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("users")
    }

    fun insertUsers() {
        val users = HashMap<String, User>()
        users["user_id"] = User("Patryk", "Pinkowski", "test123@gmail.com", true)
        users["another_id"] = User("Ania", "Owerko", "pepega@gmail.com", false)

        databaseReference.setValue(users)
    }
}