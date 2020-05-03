package pl.patryk.myhairdresser.ui.appointments.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import pl.patryk.myhairdresser.data.model.Appointment
import pl.patryk.myhairdresser.data.model.AppointmentDate
import pl.patryk.myhairdresser.data.repository.UserRepository
import java.text.SimpleDateFormat
import java.util.*

class UserAppointmentsViewModel(private val repository: UserRepository) : ViewModel() {

    private val userId by lazy { repository.currentUserId() }
    private val appointmentReference by lazy { repository.getUserAppointmentsReference(userId!!) }
    private lateinit var query: Query

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
     * Database value event listener object. It is listening to query changes.
     * Loads appointments LiveData.
     */
    private val valueEventListener = object : ValueEventListener {
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
    }

    /**
     * Loads user's appointments data from firebase database into LivaData data holder.
     * Data is ordered by date of an appointment. Takes only the last 30 days.
     */
    fun loadUserAppointments() {

        query = appointmentReference.orderByChild("date").startAt(getDaysAgo(30))
        query.addValueEventListener(valueEventListener)
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

    /**
     * Return reference to bookings at specified day.
     * @param date Day of booking in format yyyy-MM-dd
     */
    fun getAvailableHoursFromDayReference(date: String) = repository.getAvailableHoursFromDayReference(date)

    /**
     * Use to cancel a booking of specified date. Basically used to re-allow bookings at this day and time.
     */
    fun cancelBooking(date: String, key: String, appointmentDate: AppointmentDate) = repository.cancelBooking(date, key, appointmentDate)

    /**
     * Use to return formatted String created from the current date minus selected amount of days passed as an argument.
     * Format pattern: yyyy.MM.dd HH:mm. Allows to orderBy this string in firebase database.
     * @param daysAgo Number of days that we subtract from the current date.
     */
    @Suppress("SameParameterValue")
    private fun getDaysAgo(daysAgo: Int): String {

        val calendar = Calendar.getInstance()

        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)

        return SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(calendar.time)
    }

    // Remove query listener when its not needed any more
    override fun onCleared() {
        query.removeEventListener(valueEventListener)
        super.onCleared()
    }
}