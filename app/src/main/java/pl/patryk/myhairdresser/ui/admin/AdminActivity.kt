package pl.patryk.myhairdresser.ui.admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.admin_layout.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.data.model.Appointment
import pl.patryk.myhairdresser.utils.PopupMenuListener
import pl.patryk.myhairdresser.utils.startLoginActivity


class AdminActivity : AppCompatActivity(), PopupMenuListener, KodeinAware {

    override val kodein by kodein()
    private val factory: AdminViewModelFactory by instance()
    private lateinit var recyclerAdapter: SectionAdapter
    private lateinit var userID: String

    private lateinit var viewModel: AdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_layout)
        title = getString(R.string.admin_activity_title)

        viewModel = ViewModelProvider(this, factory).get(AdminViewModel::class.java)
        userID = viewModel.userId!!

        setupRecycler()
        observeAppointments(viewModel)
    }

    private fun observeAppointments(viewModel: AdminViewModel) {
        viewModel.getAppointments().observe(this, Observer { appointments ->
            recyclerAdapter.submitList(appointments)
        })
    }

    private fun setupRecycler() {

        recyclerAdapter = SectionAdapter()
        recyclerAdapter.listener = this

        val recyclerLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerLayoutManager.apply { initialPrefetchItemCount = 3 }

        recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = recyclerLayoutManager
            adapter = recyclerAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_logout -> {
                viewModel.logout()
                finish()
                startLoginActivity()
                Toasty.info(this, getString(R.string.log_out_confirmation), Toast.LENGTH_LONG).show()
            }
        }
        return true
    }

    override fun createPopupMenu(view: View, appointment: Appointment) {

        //creating a popup menu
        val popup = PopupMenu(this, view)

        //inflating menu from xml resource
        popup.inflate(R.menu.admin_popup_menu)
        popup.menu.getItem(2).title = getString(R.string.appointment_phone_call_button_text, appointment.name)

        //adding click listener
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_confirm -> {
                    viewModel.updateAppointment(appointment.userID, appointment.copy(verification_state = Appointment.VERIFICATION_STATE_APPROVED))
                    Toasty.success(this, getString(R.string.appointment_approved_toast), Toast.LENGTH_LONG).show()
                    true
                }
                R.id.menu_reject -> {
                    viewModel.updateAppointment(appointment.userID, appointment.copy(verification_state = Appointment.VERIFICATION_STATE_REJECTED))
                    Toasty.error(this, getString(R.string.appointment_rejected_toast), Toast.LENGTH_LONG).show()
                    true
                }
                R.id.menu_phone_call -> {
                    val callIntent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", appointment.phone, null))
                    startActivity(callIntent)
                    true
                }
                else -> false
            }
        }

        //displaying the popup
        popup.show()
    }
}
