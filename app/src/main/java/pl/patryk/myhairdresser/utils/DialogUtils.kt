package pl.patryk.myhairdresser.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.appointment_dialog.view.*
import pl.patryk.myhairdresser.FirebaseApplication
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.data.firebase.FirebaseAuthHelper
import pl.patryk.myhairdresser.data.firebase.FirebaseDatabaseHelper
import pl.patryk.myhairdresser.data.model.Appointment
import pl.patryk.myhairdresser.data.model.User


class DialogUtils : DialogFragment() {

    private val dbHelper: FirebaseDatabaseHelper = FirebaseDatabaseHelper()
    private val authHelper: FirebaseAuthHelper = FirebaseAuthHelper()
    private lateinit var inflatedView: View
    private lateinit var spinner: Spinner
    private lateinit var priceTextView: TextView
    private lateinit var appointment: Appointment
    private var selectedDateTime: String? = null
    private var selectedService: String? = null
    private var name: String = ""
    private var phone: String = ""
    private var userID: String? = null

    fun newInstance(): DialogUtils? {
        return DialogUtils()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        initViews()
        setupSpinnerListener()
        loadUser()

        val builder = AlertDialog.Builder(activity!!)
                .setView(inflatedView)
                .setMessage(getString(R.string.appointment_dialog_message))
                .setPositiveButton(getString(R.string.dialog_text_button_positive), dialogListener)
                .setNegativeButton(getString(R.string.dialog_text_button_negative), dialogListener)

        return builder.create()
    }

    @SuppressLint("InflateParams")
    private fun initViews() {
        val inflater = activity!!.layoutInflater
        inflatedView = inflater.inflate(R.layout.appointment_dialog, null)
        spinner = inflatedView.spinner
        priceTextView = inflatedView.service_price
    }

    // Load current user details needed for appointment
    private fun loadUser() {

        userID = authHelper.currentUserId()

        // If any value in db for the user changes, load new content
        dbHelper.databaseReference.child(authHelper.currentUserId()!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)!!
                    name = inflatedView.context.getString(R.string.name_and_surname, user.name, user.surname)
                    phone = user.phone
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private val dialogListener = DialogInterface.OnClickListener { dialog, whichButton ->
        when (whichButton) {
            -1 -> registerAppointment() // -1 Button positive
            -2 -> dialog.dismiss() // -2 Button negative, -3 Button neutral
        }
    }

    private fun setupSpinnerListener() {
        val prices = inflatedView.context.resources.getStringArray(R.array.services_prices)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                selectedService = spinner.selectedItem.toString()
                priceTextView.text = prices[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }
    }

    private fun registerAppointment() {

        val is24HoursView: Boolean = DateFormat.is24HourFormat(context)

        MaterialDialog(context!!).show {

            dateTimePicker(requireFutureDateTime = true, show24HoursView = is24HoursView) { _, dateTime ->
                message(R.string.appointment_dialog_message)
                selectedDateTime = FirebaseApplication().databaseFormatter.format(dateTime.time)
            }.positiveButton(R.string.dialog_text_button_positive)
            {
                if (selectedService != null && selectedDateTime != null) {

                    addNewAppointment(userID!!, selectedService!!, selectedDateTime!!)

                    Toasty.success(context, context.getString(
                            R.string.appointment_registered_successfully,
                            appointment.service,
                            changeDateFormatting(appointment.date)
                    ), Toast.LENGTH_LONG).show()

                } else {
                    Toasty.success(context, context.getString(R.string.sth_went_wrong_try_again), Toast.LENGTH_LONG).show()
                }
            }
                    .negativeButton(R.string.dialog_text_button_negative)
            lifecycleOwner(activity)
        }
    }

    private fun addNewAppointment(user_id: String, service: String, date: String) {

        val newAppointmentReference = dbHelper.getUserAppointmentsReference(user_id).push()
        val appointmentID = newAppointmentReference.key!!

        appointment = Appointment(user_id, name, phone, appointmentID, service, date, Appointment.VERIFICATION_STATE_PENDING)
        newAppointmentReference.setValue(appointment)
    }
}