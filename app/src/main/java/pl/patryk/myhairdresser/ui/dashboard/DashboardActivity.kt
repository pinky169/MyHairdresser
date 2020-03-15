package pl.patryk.myhairdresser.ui.dashboard

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.dashboard_layout.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.data.firebase.FirebaseDatabase
import pl.patryk.myhairdresser.data.model.User
import pl.patryk.myhairdresser.databinding.DashboardLayoutBinding
import pl.patryk.myhairdresser.utils.startLoginActivity


class DashboardActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: DashboardViewModelFactory by instance()
    private val database: FirebaseDatabase = FirebaseDatabase()
    private val dbReference: DatabaseReference = database.databaseReference
    private var user: User = User()

    private lateinit var viewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: DashboardLayoutBinding = DataBindingUtil.setContentView(this, R.layout.dashboard_layout)
        viewModel = ViewModelProvider(this, factory).get(DashboardViewModel::class.java)
        binding.viewmodel = viewModel

        setupListeners()
        loadUser()
    }

    // Loads user realtime data from firebase database into views
    private fun loadUser() {

        progress_bar.visibility = View.VISIBLE

        dbReference.child(viewModel.getUserId()!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    user = dataSnapshot.getValue(User::class.java)!!
                }

                if (viewModel.user!!.isEmailVerified) {
                    email_textview.text = user.email
                    verified_icon.setImageResource(R.drawable.ic_user_verified)
                } else {
                    email_textview.text = "Click an icon to verify your email"
                    verified_icon.setImageResource(R.drawable.ic_send_email)
                    verified_icon.setOnClickListener { viewModel.verifyEmail() }
                }

                name_textview.text = user.name
                surname_textview.text = user.surname
                age_textview.text = user.age.toString()

                progress_bar.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                progress_bar.visibility = View.GONE
            }
        })
    }

//    private fun saveData() {
//
//        val userId = viewModel.getUserId()
//        val name = name_input.text.toString().trim()
//        val surname = surname_input.text.toString().trim()
//        val email = viewModel.user?.email
//
//        val updatedUser = User(name, surname, email!!, false)
//        viewModel.updateUser(userId!!, updatedUser)
//    }

    private fun setupListeners() {
        button_save_data.setOnClickListener {
            //saveData()
            Toast.makeText(this, "Dane zostały zaktualizowane", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_logout -> {
                viewModel.logout()
                finish()
                startLoginActivity()
            }
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Make an alert asking if user want to sign out or not"
        viewModel.logout()
    }
}