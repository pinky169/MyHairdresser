package pl.patryk.myhairdresser.ui.admin

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.admin_layout.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.data.model.Appointment
import pl.patryk.myhairdresser.utils.startLoginActivity

class AdminActivity : AppCompatActivity(), AdminListener, KodeinAware {

    override val kodein by kodein()
    private val factory: AdminViewModelFactory by instance()
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var userID: String

    private lateinit var viewModel: AdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_layout)

        viewModel = ViewModelProvider(this, factory).get(AdminViewModel::class.java)
        userID = viewModel.userId!!

        setupRecycler()
        observeAppointments(viewModel)
        setupBackgroundAnimation()
    }

    private fun observeAppointments(viewModel: AdminViewModel) {
        viewModel.getAppointments().observe(this, Observer { appointments ->
            // If there are no appointments just show empty view
            if (appointments.isNotEmpty())
                empty_view.visibility = View.GONE
            else
                empty_view.visibility = View.VISIBLE

            recyclerAdapter.setItems(appointments)
        })
    }

    private fun setupRecycler() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        recyclerAdapter = RecyclerAdapter()
        recyclerAdapter.adminListener = this
        recycler_view.adapter = recyclerAdapter
        recycler_view.setHasFixedSize(true)
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

    private fun setupBackgroundAnimation() {
        val animationDrawable = admin_layout.background as? AnimationDrawable
        animationDrawable?.setEnterFadeDuration(2000)
        animationDrawable?.setExitFadeDuration(4000)
        animationDrawable?.start()
    }

    override fun confirm(userID: String) {
        viewModel.setAppointmentState(userID, Appointment.VERIFICATION_STATE_APPROVED)
        Toasty.success(this, "Potwierdzono!", Toast.LENGTH_LONG).show()
    }

    override fun reject(userID: String) {
        viewModel.setAppointmentState(userID, Appointment.VERIFICATION_STATE_REJECTED)
        Toasty.error(this, "Odrzucono!", Toast.LENGTH_LONG).show()
    }

    override fun phoneCall(contactPhone: String) {
        val callIntent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", contactPhone, null))
        startActivity(callIntent)
    }
}
