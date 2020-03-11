package pl.patryk.myhairdresser.ui.dashboard

import android.view.View
import androidx.lifecycle.ViewModel
import pl.patryk.myhairdresser.data.repository.UserRepository
import pl.patryk.myhairdresser.utils.startLoginActivity

class DashboardViewModel(private val repository: UserRepository) : ViewModel() {

    val user by lazy {
        repository.currentUser()
    }

    fun logout() {
        repository.logout()
    }

    fun logout(view: View){
        repository.logout()
        view.context.startLoginActivity()
    }
}