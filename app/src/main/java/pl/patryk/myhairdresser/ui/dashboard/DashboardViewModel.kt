package pl.patryk.myhairdresser.ui.dashboard

import android.view.View
import androidx.lifecycle.ViewModel
import pl.patryk.myhairdresser.data.model.User
import pl.patryk.myhairdresser.data.repository.UserRepository
import pl.patryk.myhairdresser.utils.startLoginActivity

class DashboardViewModel(private val repository: UserRepository) : ViewModel() {

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

    fun verifyEmail() = repository.verifyEmail()
}