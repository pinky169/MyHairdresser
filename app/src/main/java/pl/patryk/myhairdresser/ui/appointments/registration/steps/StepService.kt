package pl.patryk.myhairdresser.ui.appointments.registration.steps

import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import ernestoyaquello.com.verticalstepperform.Step
import kotlinx.android.synthetic.main.step_service_layout.view.*
import pl.patryk.myhairdresser.R


class StepService(stepTitle: String) : Step<String>(stepTitle) {

    private lateinit var spinner: Spinner
    private lateinit var priceTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var spinnerArrayAdapter: ArrayAdapter<String>
    private var selectedService: String = ""
    private var selectedPosition: Int = 0

    override fun restoreStepData(stepData: String?) {
        // To restore the step after a configuration change.
        spinner.setSelection(selectedPosition)
    }

    override fun isStepDataValid(stepData: String?): IsDataValid {
        val isServiceSelected = selectedPosition > -1
        val errorMessage = if (!isServiceSelected) context.getString(R.string.step_service_title) else ""
        return IsDataValid(isServiceSelected, errorMessage)
    }

    override fun getStepDataAsHumanReadableString(): String {
        return selectedService
    }

    override fun createStepContentLayout(): View {

        val view = LayoutInflater.from(context).inflate(R.layout.step_service_layout, null, false)

        spinner = view.spinner
        priceTextView = view.service_price

        val servicesArray = context.resources.getStringArray(R.array.services)
        val pricesArray = context.resources.getStringArray(R.array.services_prices)

        spinnerArrayAdapter = ArrayAdapter(context,
                android.R.layout.simple_spinner_dropdown_item, servicesArray)
        spinner.adapter = spinnerArrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {

                selectedService = spinner.selectedItem.toString()
                selectedPosition = position
                priceTextView.text = pricesArray[position]

                // Marks the step as completed or uncompleted depending on whether the step data is valid or not.
                // It should be called every time the step data changes.
                markAsCompleted(true)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        return view
    }

    override fun getStepData(): String {
        return selectedService
    }

    override fun onStepOpened(animated: Boolean) {}

    override fun onStepClosed(animated: Boolean) {}

    override fun onStepMarkedAsCompleted(animated: Boolean) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putString("selectedService", selectedService).commit()
    }

    override fun onStepMarkedAsUncompleted(animated: Boolean) {}
}