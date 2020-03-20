package pl.patryk.myhairdresser.data.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import pl.patryk.myhairdresser.data.model.Appointment
import pl.patryk.myhairdresser.data.model.Photo
import pl.patryk.myhairdresser.data.model.User


class FirebaseDatabaseHelper {

    val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    val databaseReference: DatabaseReference by lazy {
        database.getReference("users")
    }

    fun insertUser(uid: String, user: User) {
        databaseReference.child(uid).setValue(user)
    }

    fun updateUser(uid: String, user: User) {
        databaseReference.child(uid).updateChildren(user.toMap()!!)
    }

    fun insertPhoto(uid: String, photo: Photo) {
        databaseReference.child(uid).child("photo").setValue(photo)
    }

    fun registerAppointment(uid: String, appointment: Appointment) {
        databaseReference.child(uid).child("appointment").setValue(appointment)
    }
}