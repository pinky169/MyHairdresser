package pl.patryk.myhairdresser.ui.appointments.registration

import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.appointment_registration_layout.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.data.model.Appointment
import pl.patryk.myhairdresser.data.model.AppointmentDate
import pl.patryk.myhairdresser.data.model.User
import pl.patryk.myhairdresser.ui.appointments.registration.steps.StepDate
import pl.patryk.myhairdresser.ui.appointments.registration.steps.StepHour
import pl.patryk.myhairdresser.ui.appointments.registration.steps.StepService
import pl.patryk.myhairdresser.utils.changeFormatting
import pl.patryk.myhairdresser.utils.changeToUserReadableFormatting

class AppointmentRegistrationActivity : AppCompatActivity(), StepperFormListener, KodeinAware {

    override val kodein by kodein()
    private val factory: AppointmentRegistrationViewModelFactory by instance()
    private lateinit var viewModel: AppointmentRegistrationViewModel
    private lateinit var appointment: Appointment
    private var selectedDate: String? = null
    private var selectedService: String? = null
    private var selectedHour: String? = null
    private var name: String = ""
    private var phone: String = ""
    private var userID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.appointment_registration_layout)

        viewModel = ViewModelProvider(this, factory).get(AppointmentRegistrationViewModel::class.java)

        initViews()
        loadUser()
    }

    private fun initViews() {

        val serviceStep = StepService(getString(R.string.step_service_title))
        val dateStep = StepDate(getString(R.string.step_date_title))
        val hourStep = StepHour(getString(R.string.step_hour_title))

        stepper_form
                .setup(this, serviceStep, dateStep, hourStep)
                .displayBottomNavigation(false)
                .stepNextButtonText(getString(R.string.stepper_form_button_next_text))
                .displayCancelButtonInLastStep(true)
                .lastStepCancelButtonText(getString(R.string.stepper_last_step_cancel_button_text))
                .lastStepNextButtonText(getString(R.string.stepper_form_last_step_next_button_text))
                .confirmationStepTitle(getString(R.string.stepper_form_confirmation_button_text))
                .init()
    }

    override fun onCompletedForm() {
        registerAppointment()
        finish()
    }

    override fun onCancelledForm() {
        finish()
    }

    // Load current user details needed for appointment
    private fun loadUser() {

        userID = viewModel.userId

        viewModel.userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)!!
                    name = getString(R.string.name_and_surname, user.name, user.surname)
                    phone = user.phone
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun registerAppointment() {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        selectedService = sharedPreferences.getString("selectedService", null)
        selectedDate = sharedPreferences.getString("selectedDate", null)
        selectedHour = sharedPreferences.getString("selectedHour", null)

        val date = "${changeFormatting(selectedDate!!)} $selectedHour"

        bookNewAppointment(userID!!, selectedService!!, date)
    }

    private fun bookNewAppointment(user_id: String, service: String, date: String) {

        val newAppointmentReference = viewModel.getUserAppointmentsReference(user_id).push()
        val appointmentID = newAppointmentReference.key!!

        appointment = Appointment(user_id, name, phone, appointmentID, service, date, Appointment.VERIFICATION_STATE_PENDING)
        newAppointmentReference.setValue(appointment)

        bookADate(selectedDate!!, selectedHour!!)

        Toasty.success(this, getString(R.string.appointment_registered_successfully, service, changeToUserReadableFormatting(date)), Toast.LENGTH_LONG).show()
    }

    private fun bookADate(date: String, time: String) {
        viewModel.getAvailableHoursFromDayReference(date).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val appointmentDate = childSnapshot.getValue(AppointmentDate::class.java)!!

                    if (appointmentDate.time == time) {
                        val appointmentDateKey = childSnapshot.key!!
                        val updatedAppointmentDate = AppointmentDate(date, time, false)
                        viewModel.bookADate(date, appointmentDateKey, updatedAppointmentDate)
                    }
                }
            }
        })
    }
}