package pl.patryk.myhairdresser.ui.appointments.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.patryk.myhairdresser.data.repository.UserRepository

@Suppress("UNCHECKED_CAST")
class AppointmentRegistrationViewModelFactory(private val repository: UserRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AppointmentRegistrationViewModel(repository) as T
    }

}