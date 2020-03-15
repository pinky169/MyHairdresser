package pl.patryk.myhairdresser.data.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import pl.patryk.myhairdresser.data.model.User


class FirebaseDatabase {

    var userInfo = arrayListOf<User>()

    val databaseReference: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("users")
    }

    fun insertUser(uid: String, user: User) {
        databaseReference.child(uid).setValue(user)
    }

    fun updateUser(uid: String, user: User) {
        databaseReference.child(uid).updateChildren(user.toMap()!!)
    }
}