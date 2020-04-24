package pl.patryk.myhairdresser.ui.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.patryk.myhairdresser.data.repository.UserRepository

@Suppress("UNCHECKED_CAST")
class UserAppointmentViewModelFactory(private val repository: UserRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserAppointmentViewModel(repository) as T
    }

}