package pl.patryk.myhairdresser.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import pl.patryk.myhairdresser.FirebaseApplication
import pl.patryk.myhairdresser.data.model.Appointment
import pl.patryk.myhairdresser.data.model.AppointmentSection
import pl.patryk.myhairdresser.data.model.User
import pl.patryk.myhairdresser.data.repository.UserRepository
import java.util.*

class AdminViewModel(private val repository: UserRepository) : ViewModel() {

    val userId by lazy { repository.currentUserId() }
    private val usersReference: DatabaseReference by lazy { repository.getUsersReference() }


    /**
     * Contains information about all appointments split into sections.
     * Initialization is done once thanks to lazy initialization.
     */
    private val liveAppointments: MutableLiveData<List<AppointmentSection>> by lazy {
        MutableLiveData<List<AppointmentSection>>().also {
            loadAppointments()
        }
    }

    /**
     * Returns a list of AppointmentSections LiveData which contains
     * information about all the appointments from firebase database.
     */
    fun getAppointments(): LiveData<List<AppointmentSection>> {
        return liveAppointments
    }

    /**
     * Loads all appointments data from firebase database into LivaData data holder.
     */
    private fun loadAppointments() {

        usersReference.orderByChild("appointment/date").startAt(getDaysAgo(7)).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {

                    val pendingAppointments: ArrayList<Appointment> = arrayListOf()
                    val approvedAppointments: ArrayList<Appointment> = arrayListOf()
                    val rejectedAppointments: ArrayList<Appointment> = arrayListOf()
                    val sections: ArrayList<AppointmentSection> = arrayListOf()

                    for (userSnapshot in dataSnapshot.children) {
                        val currentUser = userSnapshot.getValue(User::class.java)

                        if (currentUser?.appointment != null) {
                            when (currentUser.appointment!!.verification_state) {
                                Appointment.VERIFICATION_STATE_PENDING -> pendingAppointments.add(currentUser.appointment!!)
                                Appointment.VERIFICATION_STATE_APPROVED -> approvedAppointments.add(currentUser.appointment!!)
                                Appointment.VERIFICATION_STATE_REJECTED -> rejectedAppointments.add(currentUser.appointment!!)
                            }
                        }
                    }

                    sections.add(AppointmentSection("OCZEKUJĄCE", pendingAppointments))
                    sections.add(AppointmentSection("ZATWIERDZONE", approvedAppointments))
                    sections.add(AppointmentSection("ODRZUCONE", rejectedAppointments))

                    liveAppointments.postValue(sections)
                }
            }
        })
    }

    /**
     * Use to set a state for an appointment.
     * @param uid firebase ID of the current user
     * @param verificationState one of three available states of the appointments:
     * VERIFICATION_STATE_IDLE,
     * VERIFICATION_STATE_PENDING,
     * VERIFICATION_STATE_APPROVED,
     * VERIFICATION_STATE_REJECTED
     */
    fun setAppointmentState(uid: String, verificationState: String) = repository.setAppointmentState(uid, verificationState)

    /**
     * Use to log out the user
     */
    fun logout() = repository.logout()

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

        return FirebaseApplication().databaseFormatter.format(calendar.time)
    }
}