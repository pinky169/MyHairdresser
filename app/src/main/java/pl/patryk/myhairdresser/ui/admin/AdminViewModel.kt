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
    private lateinit var pendingAppointments: ArrayList<Appointment>
    private lateinit var approvedAppointments: ArrayList<Appointment>
    private lateinit var rejectedAppointments: ArrayList<Appointment>
    private lateinit var sections: ArrayList<AppointmentSection>
    private var liveAppointments: MutableLiveData<List<AppointmentSection>> = MutableLiveData()
    private var pendingAppointmentsLiveData: MutableLiveData<List<Appointment>> = MutableLiveData()

    fun getAppointments(): LiveData<List<AppointmentSection>> {

        if (liveAppointments.value == null) {
            usersReference.orderByChild("appointment/date").startAt(getDaysAgo(7)).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    pendingAppointments = arrayListOf()
                    approvedAppointments = arrayListOf()
                    rejectedAppointments = arrayListOf()
                    sections = arrayListOf()

                    if (dataSnapshot.exists()) {
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
        return liveAppointments
    }

    fun setAppointmentState(uid: String, verificationState: String) = repository.setAppointmentState(uid, verificationState)

    fun logout() = repository.logout()

    @Suppress("SameParameterValue")
    private fun getDaysAgo(daysAgo: Int): String {

        val calendar = Calendar.getInstance()

        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)

        return FirebaseApplication().databaseFormatter.format(calendar.time)
    }
}