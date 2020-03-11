package pl.patryk.myhairdresser.data.repository

import pl.patryk.myhairdresser.data.firebase.FirebaseSource

class UserRepository(private val firebase: FirebaseSource) {

    fun login(email: String, password: String) = firebase.login(email, password)

    fun register(email: String, password: String) = firebase.register(email, password)

    fun currentUser() = firebase.currentUser()

    fun logout() = firebase.logout()
}