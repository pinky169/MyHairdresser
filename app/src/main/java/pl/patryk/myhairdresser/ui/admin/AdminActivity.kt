package pl.patryk.myhairdresser.ui.admin

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.admin_layout.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.data.firebase.FirebaseDatabaseHelper
import pl.patryk.myhairdresser.data.model.Appointment
import pl.patryk.myhairdresser.data.model.User
import pl.patryk.myhairdresser.ui.auth.AuthViewModel
import pl.patryk.myhairdresser.ui.auth.AuthViewModelFactory
import pl.patryk.myhairdresser.utils.startLoginActivity

class AdminActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()
    private lateinit var dbHelper: FirebaseDatabaseHelper
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var appointments: ArrayList<Appointment>

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_layout)

        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)
        dbHelper = FirebaseDatabaseHelper()
        appointments = arrayListOf()

        setupRecycler()
        loadUsers()
    }

    private fun setupRecycler() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        recyclerAdapter = RecyclerAdapter()
        recycler_view.adapter = recyclerAdapter
        recycler_view.setHasFixedSize(true)
    }

    private fun loadUsers() {
        dbHelper.databaseReference.orderByChild("appointment/date").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    recyclerAdapter.clear()
                    for (userSnapshot in dataSnapshot.children) {
                        val currentUser = userSnapshot.getValue(User::class.java)
                        if (currentUser?.appointment != null)
                            appointments.add(currentUser.appointment!!)
                    }
                    recyclerAdapter.setItems(appointments)
                }
            }
        })
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
}
