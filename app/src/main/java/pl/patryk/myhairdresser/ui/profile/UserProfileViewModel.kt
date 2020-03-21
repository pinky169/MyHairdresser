package pl.patryk.myhairdresser.ui.profile

import android.view.View
import androidx.lifecycle.ViewModel
import pl.patryk.myhairdresser.data.model.Appointment
import pl.patryk.myhairdresser.data.model.User
import pl.patryk.myhairdresser.data.repository.UserRepository
import pl.patryk.myhairdresser.utils.startLoginActivity

class UserProfileViewModel(private val repository: UserRepository) : ViewModel() {

    val user by lazy {
        repository.currentUser()
    }

    fun getUserId(): String? {
        return repository.currentUser()?.uid
    }

    fun logout() = repository.logout()

    fun logout(view: View) {
        repository.logout()
        view.context.startLoginActivity()
    }

    fun updateUser(uid: String, user: User) = repository.updateUser(uid, user)

    fun updateAppointment(uid: String, appointment: Appointment) = repository.updateAppointment(uid, appointment)

    fun verifyEmail() = repository.verifyEmail()

    fun reloadUser() = repository.reloadUser()
}