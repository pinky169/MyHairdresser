package pl.patryk.myhairdresser.ui.appointments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.user_appointments_layout.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.data.model.Appointment
import pl.patryk.myhairdresser.utils.PopupMenuListener
import java.util.*

class UserAppointmentsActivity : AppCompatActivity(), PopupMenuListener, KodeinAware {

    override val kodein by kodein()
    private val factory: UserAppointmentsViewModelFactory by instance()
    private lateinit var viewModel: UserAppointmentsViewModel
    private lateinit var recyclerAdapter: UserAppointmentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_appointments_layout)
        title = getString(R.string.user_appointment_activity_title)

        viewModel = ViewModelProvider(this, factory).get(UserAppointmentsViewModel::class.java)

        setupRecyclerView()
        observeUserAppointments(viewModel)
    }

    private fun observeUserAppointments(viewmodel: UserAppointmentsViewModel) {
        viewmodel.getUserAppointments().observe(this, Observer { appointments ->
            recyclerAdapter.submitList(appointments)
            showOrHideEmptyView(appointments)
        })
    }

    private fun setupRecyclerView() {

        recyclerAdapter = UserAppointmentsAdapter()
        recyclerAdapter.popupMenuListener = this

        val recyclerLayoutManager = LinearLayoutManager(this)

        appointment_recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = recyclerLayoutManager
            adapter = recyclerAdapter
        }
    }

    @ExperimentalStdlibApi
    override fun createPopupMenu(view: View, appointment: Appointment) {

        // Creating a popup menu
        val popup = PopupMenu(view.context, view)

        // Inflating menu from xml resource
        popup.inflate(R.menu.user_popup_menu)

        // Setting title for a 2nd item in menu
        if (appointment.verification_state == Appointment.VERIFICATION_STATE_PENDING)
            popup.menu.getItem(1).title = getString(R.string.menu_appointment_cancel_txt, appointment.service.decapitalize(Locale.getDefault()))
        else
            popup.menu.getItem(1).title = getString(R.string.menu_appointment_remove_txt, appointment.service.decapitalize(Locale.getDefault()))

        // Showing call button when the appointment has been rejected
        if (appointment.verification_state == Appointment.VERIFICATION_STATE_REJECTED) {
            popup.menu.getItem(0).isVisible = true
        }

        // Adding click listener
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_remove -> {
                    viewModel.deleteAppointment(appointment.userID, appointment)
                    Toasty.success(this, getString(R.string.appointment_deleted_toast, appointment.service), Toast.LENGTH_LONG).show()
                    true
                }
                R.id.menu_call -> {
                    val callIntent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", getString(R.string.hairdresser_phone_number), null))
                    startActivity(callIntent)
                    true
                }
                else -> false
            }
        }

        //displaying the popup
        popup.show()
    }

    private fun showOrHideEmptyView(appointments: List<Appointment>) {
        if (appointments.isNullOrEmpty()) {
            empty_view.visibility = View.VISIBLE
        } else {
            empty_view.visibility = View.GONE
        }
    }
}