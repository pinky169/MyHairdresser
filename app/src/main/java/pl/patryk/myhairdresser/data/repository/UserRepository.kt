package pl.patryk.myhairdresser.data.repository

import pl.patryk.myhairdresser.data.firebase.FirebaseAuthHelper
import pl.patryk.myhairdresser.data.firebase.FirebaseDatabaseHelper
import pl.patryk.myhairdresser.data.model.Appointment
import pl.patryk.myhairdresser.data.model.User

class UserRepository(private val firebase: FirebaseAuthHelper, private val firebaseDB: FirebaseDatabaseHelper) {

    fun currentUser() = firebase.currentUser()

    fun currentUserId() = firebase.currentUserId()

    fun register(email: String, password: String) = firebase.register(email, password)

    fun login(email: String, password: String) = firebase.login(email, password)

    fun logout() = firebase.logout()

    fun verifyEmail() = firebase.verifyEmail()

    fun reloadUser() = firebase.reloadUser()

    fun updateUser(uid: String, user: User) = firebaseDB.updateUser(uid, user)

    fun updateAppointment(uid: String, appointment: Appointment) = firebaseDB.updateAppointment(uid, appointment)

}