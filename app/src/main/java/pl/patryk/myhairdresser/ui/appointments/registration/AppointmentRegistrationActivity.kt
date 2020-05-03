package pl.patryk.myhairdresser.ui.appointments.registration

import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
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
import pl.patryk.myhairdresser.ui.appointments.registration.steps.StepDate
import pl.patryk.myhairdresser.ui.appointments.registration.steps.StepHour
import pl.patryk.myhairdresser.ui.appointments.registration.steps.StepService
import pl.patryk.myhairdresser.utils.changeBackFromQueryFormatting
import pl.patryk.myhairdresser.utils.changeToUserReadableFormatting

class AppointmentRegistrationActivity : AppCompatActivity(), StepperFormListener, KodeinAware {

    override val kodein by kodein()
    private val factory: AppointmentRegistrationViewModelFactory by instance()
    private lateinit var viewModel: AppointmentRegistrationViewModel
    private lateinit var appointment: Appointment
    private lateinit var serviceStep: StepService
    private lateinit var dateStep: StepDate
    private lateinit var hourStep: StepHour
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
        observeUser(viewModel)
    }

    private fun initViews() {

        serviceStep = StepService(getString(R.string.step_service_title))
        dateStep = StepDate(getString(R.string.step_date_title))
        hourStep = StepHour(getString(R.string.step_hour_title))

        stepper_form
                .setup(this, serviceStep, dateStep, hourStep)
                .allowNonLinearNavigation(false)
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
    }

    override fun onCancelledForm() {
        finish()
    }

    // Loads user realtime data from firebase database into views
    private fun observeUser(viewModel: AppointmentRegistrationViewModel) {
        viewModel.getUser().observe(this, Observer { user ->
            userID = viewModel.userId
            name = getString(R.string.name_and_surname, user.name, user.surname)
            phone = user.phone
        })
    }

    private fun registerAppointment() {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        selectedService = sharedPreferences.getString("selectedService", null)
        selectedDate = sharedPreferences.getString("selectedDate", null)
        selectedHour = sharedPreferences.getString("selectedHour", null)

        bookADate(selectedDate!!, selectedHour!!)
    }

    private fun bookNewAppointment() {

        val newAppointmentReference = viewModel.getUserAppointmentsReference(userID!!).push()
        val appointmentID = newAppointmentReference.key!!

        val formattedDate = changeBackFromQueryFormatting(selectedDate!!)

        appointment = Appointment(userID!!, name, phone, appointmentID, selectedService!!, formattedDate, selectedHour!!, Appointment.VERIFICATION_STATE_PENDING)
        newAppointmentReference.setValue(appointment)

        Toasty.success(this, getString(R.string.appointment_registered_successfully, selectedService, changeToUserReadableFormatting(formattedDate), selectedHour), Toast.LENGTH_LONG).show()
    }

    private fun bookADate(date: String, time: String) {
        viewModel.getAvailableHoursFromDayReference(date).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {

                    val appointmentDate = childSnapshot.getValue(AppointmentDate::class.java)!!

                    if (appointmentDate.time == time) {
                        if (appointmentDate.availability) {
                            val appointmentDateKey = childSnapshot.key!!
                            val updatedAppointmentDate = AppointmentDate(date, time, false)
                            viewModel.bookADate(date, appointmentDateKey, updatedAppointmentDate)
                            bookNewAppointment()
                            finish()
                        } else {
                            Toasty.warning(applicationContext, getString(R.string.book_appointment_date_not_available_message), Toast.LENGTH_LONG).show()
                            stepper_form.cancelFormCompletionOrCancellationAttempt()
                        }
                    }
                }
            }
        })
    }

    private fun cleanListeners() {
        if (hourStep.dateReference != null) {
            hourStep.dateReference!!.keepSynced(false)
            hourStep.dateReference!!.removeEventListener(hourStep.childEventListener)
        }
    }

    override fun onDestroy() {
        cleanListeners()
        super.onDestroy()
    }
}