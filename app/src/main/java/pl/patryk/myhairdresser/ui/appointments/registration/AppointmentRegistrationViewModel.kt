package pl.patryk.myhairdresser.ui.appointments.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import pl.patryk.myhairdresser.data.model.AppointmentDate
import pl.patryk.myhairdresser.data.model.User
import pl.patryk.myhairdresser.data.repository.UserRepository

class AppointmentRegistrationViewModel(private val repository: UserRepository) : ViewModel() {

    val userId by lazy { repository.currentUserId() }
    val userReference by lazy { repository.getUserReference(userId!!) }

    private val userLiveData: MutableLiveData<User> by lazy {
        MutableLiveData<User>().also {
            loadUser()
        }
    }

    fun getUser(): LiveData<User> {
        return userLiveData
    }

    private fun loadUser() {
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user: User = dataSnapshot.getValue(User::class.java)!!
                    userLiveData.postValue(user)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun getUserAppointmentsReference(uid: String) = repository.getUserAppointmentsReference(uid)
    fun getAvailableHoursFromDayReference(date: String) = repository.getAvailableHoursFromDayReference(date)
    fun bookADate(date: String, key: String, appointmentDate: AppointmentDate) = repository.bookADate(date, key, appointmentDate)
}