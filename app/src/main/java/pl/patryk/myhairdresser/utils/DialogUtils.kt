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
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.appointment_dialog.view.*
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.data.firebase.FirebaseAuthHelper
import pl.patryk.myhairdresser.data.firebase.FirebaseDatabaseHelper
import pl.patryk.myhairdresser.data.model.Appointment
import java.text.SimpleDateFormat
import java.util.*


class DialogUtils : DialogFragment() {

    private val dbHelper: FirebaseDatabaseHelper = FirebaseDatabaseHelper()
    private val authHelper: FirebaseAuthHelper = FirebaseAuthHelper()
    private lateinit var selectedService: String
    private lateinit var inflatedView: View
    private lateinit var mSpinner: Spinner
    private lateinit var mPriceTextView: TextView
    private lateinit var selectedDateTime: String
    private lateinit var appointment: Appointment

    fun newInstance(): DialogUtils? {
        return DialogUtils()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        initViews()
        setupSpinnerListener()

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
        mSpinner = inflatedView.spinner
        mPriceTextView = inflatedView.service_price
    }

    private val dialogListener = DialogInterface.OnClickListener { dialog, whichButton ->
        when (whichButton) {
            -1 -> registerAppointment() // -1 Button positive
            -2 -> dialog.dismiss() // -2 Button negative, -3 Button neutral
        }
    }

    private fun setupSpinnerListener() {
        val prices = inflatedView.context.resources.getStringArray(R.array.services_prices)
        mSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                selectedService = mSpinner.selectedItem.toString()
                mPriceTextView.text = prices[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }
    }

    private fun registerAppointment() {

        val is24HoursView: Boolean = DateFormat.is24HourFormat(context)
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

        MaterialDialog(context!!).show {
            dateTimePicker(requireFutureDateTime = true, show24HoursView = is24HoursView) { _, dateTime ->
                message(R.string.appointment_dialog_message)
                selectedDateTime = formatter.format(dateTime.time)
            }.positiveButton(R.string.dialog_text_button_positive) {
                appointment = Appointment(selectedService, selectedDateTime)
                dbHelper.registerAppointment(authHelper.currentUserId()!!, appointment)
                Toasty.success(it.context, it.context.getString(R.string.appointment_registered_successfully, appointment.service, appointment.date), Toast.LENGTH_LONG).show()
            }.negativeButton(R.string.dialog_text_button_negative)
            lifecycleOwner(activity)
        }
    }
}