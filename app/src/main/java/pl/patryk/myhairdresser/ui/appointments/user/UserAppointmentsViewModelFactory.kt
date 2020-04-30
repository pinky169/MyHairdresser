package pl.patryk.myhairdresser.ui.appointments.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.patryk.myhairdresser.data.repository.UserRepository

@Suppress("UNCHECKED_CAST")
class UserAppointmentsViewModelFactory(private val repository: UserRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserAppointmentsViewModel(repository) as T
    }

}