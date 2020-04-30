package pl.patryk.myhairdresser.ui.appointments.registration

import androidx.lifecycle.ViewModel
import pl.patryk.myhairdresser.data.model.AppointmentDate
import pl.patryk.myhairdresser.data.repository.UserRepository

class AppointmentRegistrationViewModel(private val repository: UserRepository) : ViewModel() {

    val userId by lazy { repository.currentUserId() }
    val userReference by lazy { repository.getUserReference(userId!!) }

    fun getUserAppointmentsReference(uid: String) = repository.getUserAppointmentsReference(uid)
    fun getAvailableHoursFromDayReference(date: String) = repository.getAvailableHoursFromDayReference(date)
    fun bookADate(date: String, key: String, appointmentDate: AppointmentDate) = repository.bookADate(date, key, appointmentDate)
}