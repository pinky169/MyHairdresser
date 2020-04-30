package pl.patryk.myhairdresser.ui.appointments.registration.steps

import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import ernestoyaquello.com.verticalstepperform.Step
import kotlinx.android.synthetic.main.step_date_layout.view.*
import pl.patryk.myhairdresser.R
import java.text.SimpleDateFormat
import java.util.*


class StepDate(stepTitle: String) : Step<String>(stepTitle) {

    private var selectedDate: String = ""
    private var humanReadableSelectedDate: String = ""
    private lateinit var selectedDayTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun restoreStepData(data: String?) {
        selectedDate = data!!
        selectedDayTextView.text = data
    }

    override fun isStepDataValid(stepData: String?): IsDataValid {

        val isDatePicked = selectedDate != ""
        val errorMessage = if (!isDatePicked) context.getString(R.string.step_date_title) else humanReadableSelectedDate

        return IsDataValid(isDatePicked, errorMessage)
    }

    override fun onStepMarkedAsCompleted(animated: Boolean) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putString("selectedDate", selectedDate).commit()
    }

    override fun getStepDataAsHumanReadableString(): String {
        return humanReadableSelectedDate
    }

    override fun createStepContentLayout(): View {

        val view = LayoutInflater.from(context).inflate(R.layout.step_date_layout, null, false)

        val button = view.button_select_day
        selectedDayTextView = view.selectedDay

        button.setOnClickListener {

            MaterialDialog(context!!).show {

                datePicker(requireFutureDate = true) { _, dateTime ->
                    selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dateTime.time)
                    humanReadableSelectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(dateTime.time)
                }.positiveButton(R.string.dialog_text_button_positive)
                {
                    selectedDayTextView.text = humanReadableSelectedDate
                    markAsCompleted(true)
                }.negativeButton(R.string.dialog_text_button_negative)
            }
        }

        return view
    }

    override fun getStepData(): String {
        return selectedDate
    }

    override fun onStepOpened(animated: Boolean) {}

    override fun onStepMarkedAsUncompleted(animated: Boolean) {}

    override fun onStepClosed(animated: Boolean) {}
}