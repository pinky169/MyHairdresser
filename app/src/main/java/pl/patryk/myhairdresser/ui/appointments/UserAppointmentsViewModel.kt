package pl.patryk.myhairdresser.ui.appointments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import pl.patryk.myhairdresser.data.model.Appointment
import pl.patryk.myhairdresser.data.repository.UserRepository
import java.util.*

class UserAppointmentsViewModel(private val repository: UserRepository) : ViewModel() {

    private val userId by lazy { repository.currentUserId() }
    private val appointmentReference by lazy { repository.getUserAppointmentsReference(userId!!) }

    /**
     * Contains information about User's appointments.
     * Initialization is done once thanks to lazy initialization.
     */
    private val appointmentLiveData: MutableLiveData<List<Appointment>> by lazy {
        MutableLiveData<List<Appointment>>().also {
            loadUserAppointments()
        }
    }

    /**
     * Returns user's LiveData data holder which contains
     * information about user's appointments from firebase database.
     */
    fun getUserAppointments(): LiveData<List<Appointment>> {
        return appointmentLiveData
    }

    /**
     * Loads user's appointments data from firebase database into LivaData data holder.
     */
    fun loadUserAppointments() {

        appointmentReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val appointmentList: ArrayList<Appointment> = arrayListOf()
                if (dataSnapshot.exists()) {
                    for (child in dataSnapshot.children) {
                        val appointment = child.getValue(Appointment::class.java)!!
                        appointmentList.add(appointment)
                    }
                }
                appointmentLiveData.postValue(appointmentList)
            }
        })
    }

    /**
     * Use to update an appointment.
     * @param uid firebase ID of the current user
     * @param appointment Appointment to update. Available states of verification state:
     * VERIFICATION_STATE_IDLE,
     * VERIFICATION_STATE_PENDING,
     * VERIFICATION_STATE_APPROVED,
     * VERIFICATION_STATE_REJECTED
     */
    fun updateAppointment(uid: String, appointment: Appointment) = repository.updateAppointment(uid, appointment)

    /**
     * Removes specified appointment from firebase database
     * @param uid firebase ID of the current user
     * @param appointment Appointment object to remove
     */
    fun deleteAppointment(uid: String, appointment: Appointment) = repository.deleteAppointment(uid, appointment)
}