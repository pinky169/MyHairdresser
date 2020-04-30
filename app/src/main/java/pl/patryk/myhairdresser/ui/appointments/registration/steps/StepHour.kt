package pl.patryk.myhairdresser.ui.appointments.registration.steps

import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.*
import ernestoyaquello.com.verticalstepperform.Step
import kotlinx.android.synthetic.main.admin_layout.view.*
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.data.firebase.FirebaseDatabaseHelper
import pl.patryk.myhairdresser.data.model.AppointmentDate

class StepHour(stepTitle: String) : Step<String>(stepTitle), OnHourClickedListener {

    companion object {
        val AVAILABLE_HOURS_ARRAY = arrayListOf("9:00-10:00", "10:00-11:00", "11:00-12:00", "14:00-15:00", "15:00-16:00", "16:00-17:00")
    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recyclerAdapter: StepDateAdapter
    private lateinit var allDates: ArrayList<AppointmentDate>
    private lateinit var dateReference: DatabaseReference
    private lateinit var childEventListener: ChildEventListener
    private val dbHelper: FirebaseDatabaseHelper = FirebaseDatabaseHelper()
    private var selectedHour: String = ""
    private var selectedDate: String? = null

    override fun restoreStepData(data: String?) {
        selectedHour = data!!
    }

    override fun isStepDataValid(stepData: String?): IsDataValid {

        val isDatePicked = selectedHour != ""
        val errorMessage = if (!isDatePicked) context.getString(R.string.step_hour_title) else selectedHour

        return IsDataValid(isDatePicked, errorMessage)
    }

    override fun onStepMarkedAsCompleted(animated: Boolean) {
    }

    override fun getStepDataAsHumanReadableString(): String {
        return selectedHour
    }

    override fun createStepContentLayout(): View {

        val view = LayoutInflater.from(context).inflate(R.layout.step_hour_layout, null, false)

        val recyclerView = view.recycler_view
        recyclerAdapter = StepDateAdapter()
        recyclerAdapter.itemClickedListener = this
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2)
            adapter = recyclerAdapter
        }

        return view
    }

    private fun getAppointmentDatesFromDatabase(selectedDate: String) {

        dateReference = dbHelper.getAvailableHoursFromDayReference(selectedDate)

        dateReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(snapshot: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    generateDateData(selectedDate)
                }
            }
        })

        childEventListener = dateReference.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(snapshot: DatabaseError) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()) {
                    val appointmentHour = snapshot.getValue(AppointmentDate::class.java)!!
                    allDates.add(appointmentHour)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })

        recyclerAdapter.submitList(allDates)
        markAsCompletedOrUncompleted(true)
    }

    private fun generateDateData(date: String): List<AppointmentDate> {

        val listOfAppointmentDate: ArrayList<AppointmentDate> = arrayListOf()
        for (value in AVAILABLE_HOURS_ARRAY) {
            val currentAppointmentDate = AppointmentDate(date, value, true)
            dbHelper.insertAppointmentDate(date, currentAppointmentDate)
        }

        return listOfAppointmentDate
    }

    override fun getStepData(): String {
        return selectedHour
    }

    override fun onStepOpened(animated: Boolean) {
        allDates = arrayListOf()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        selectedDate = sharedPreferences.getString("selectedDate", null)
        getAppointmentDatesFromDatabase(selectedDate!!)
        markAsUncompleted(context.getString(R.string.step_hour_title), true)
    }

    override fun onStepMarkedAsUncompleted(animated: Boolean) {}

    override fun onStepClosed(animated: Boolean) {
        dateReference.removeEventListener(childEventListener)
    }

    override fun onItemClicked(appointmentDate: AppointmentDate) {
        selectedHour = appointmentDate.time
        sharedPreferences.edit().putString("selectedHour", selectedHour).commit()
        markAsCompleted(true)
    }

}