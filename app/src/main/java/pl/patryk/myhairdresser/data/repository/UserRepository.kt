package pl.patryk.myhairdresser.data.repository

import pl.patryk.myhairdresser.data.firebase.FirebaseAuthHelper
import pl.patryk.myhairdresser.data.firebase.FirebaseDatabaseHelper
import pl.patryk.myhairdresser.data.firebase.FirebaseStorageHelper
import pl.patryk.myhairdresser.data.model.Appointment
import pl.patryk.myhairdresser.data.model.Photo
import pl.patryk.myhairdresser.data.model.User

class UserRepository(private val firebase: FirebaseAuthHelper, private val firebaseDB: FirebaseDatabaseHelper, private val storage: FirebaseStorageHelper) {

    fun currentUser() = firebase.currentUser()

    fun currentUserId() = firebase.currentUserId()

    fun register(email: String, password: String) = firebase.register(email, password)

    fun login(email: String, password: String) = firebase.login(email, password)

    fun logout() = firebase.logout()

    fun verifyEmail() = firebase.verifyEmail()

    fun updateUser(uid: String, user: User) = firebaseDB.updateUser(uid, user)

    fun updateAppointment(uid: String, appointment: Appointment) = firebaseDB.updateAppointment(uid, appointment)

    fun deleteAppointment(uid: String, appointment: Appointment) = firebaseDB.deleteAppointment(uid, appointment)

    fun insertPhoto(uid: String, photo: Photo) = firebaseDB.insertPhoto(uid, photo)

    fun getUserReference(uid: String) = firebaseDB.getUserReference(uid)

    fun getUsersReference() = firebaseDB.databaseReference

    fun getPermissionsReference(uid: String) = firebaseDB.getPermissionReference(uid)

    fun getStorageReference() = storage.storageReference

    fun getUserAppointmentsReference(uid: String) = firebaseDB.getUserAppointmentsReference(uid)
}