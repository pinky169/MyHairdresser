package pl.patryk.myhairdresser.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import pl.patryk.myhairdresser.data.model.Appointment
import pl.patryk.myhairdresser.data.model.User
import pl.patryk.myhairdresser.data.repository.UserRepository

class AdminViewModel(private val repository: UserRepository) : ViewModel() {

    private val usersReference: DatabaseReference by lazy {
        repository.getUsersReference()
    }

    val userId by lazy { repository.currentUserId() }

    private lateinit var appointments: ArrayList<Appointment>
    var liveAppointments: MutableLiveData<List<Appointment>> = MutableLiveData()

    fun getAppointments(): LiveData<List<Appointment>> {

        if (liveAppointments.value == null) {
            usersReference.orderByChild("appointment/date").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    appointments = arrayListOf()
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val currentUser = userSnapshot.getValue(User::class.java)
                            if (currentUser?.appointment != null && currentUser.appointment!!.verification_state == Appointment.VERIFICATION_STATE_PENDING)
                                appointments.add(currentUser.appointment!!)
                        }
                        liveAppointments.postValue(appointments)
                    }
                }
            })
        }
        return liveAppointments
    }

    fun setAppointmentState(uid: String, verificationState: String) = repository.setAppointmentState(uid, verificationState)

    fun logout() = repository.logout()
}