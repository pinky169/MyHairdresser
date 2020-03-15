package pl.patryk.myhairdresser.data.repository

import pl.patryk.myhairdresser.data.firebase.FirebaseDatabase
import pl.patryk.myhairdresser.data.firebase.FirebaseSource
import pl.patryk.myhairdresser.data.model.User

class UserRepository(private val firebase: FirebaseSource, private val firebaseDB: FirebaseDatabase) {

    fun currentUser() = firebase.currentUser()

    fun register(email: String, password: String) = firebase.register(email, password)

    fun login(email: String, password: String) = firebase.login(email, password)

    fun logout() = firebase.logout()

    fun verifyEmail() = firebase.verifyEmail()

    fun updateUser(uid: String, user: User) = firebaseDB.updateUser(uid, user)

}