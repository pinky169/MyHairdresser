package pl.patryk.myhairdresser.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.patryk.myhairdresser.data.repository.UserRepository

@Suppress("UNCHECKED_CAST")
class AdminViewModelFactory(private val repository: UserRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AdminViewModel(repository) as T
    }

}