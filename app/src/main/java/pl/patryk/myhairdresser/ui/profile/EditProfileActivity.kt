package pl.patryk.myhairdresser.ui.profile

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.edit_profile_layout.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.data.model.Appointment
import pl.patryk.myhairdresser.data.model.User

class EditProfileActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: UserProfileViewModelFactory by instance()
    private lateinit var viewModel: UserProfileViewModel
    private lateinit var name: String
    private lateinit var surname: String
    private lateinit var email: String
    private lateinit var age: String
    private lateinit var phone: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile_layout)

        // Toolbar title
        title = getString(R.string.action_bar_title_edit)

        viewModel = ViewModelProvider(this, factory).get(UserProfileViewModel::class.java)

        loadUserData()
        setupListeners()
    }

    private fun loadUserData() {

        name = intent.getStringExtra(UserProfileActivity.TAG_USER_NAME)
        surname = intent.getStringExtra(UserProfileActivity.TAG_USER_SURNAME)
        email = intent.getStringExtra(UserProfileActivity.TAG_USER_EMAIL)
        age = intent.getStringExtra(UserProfileActivity.TAG_USER_AGE)
        phone = intent.getStringExtra(UserProfileActivity.TAG_USER_PHONE)

        email_textview.text = email
        name_editext.setText(name)
        surname_edittext.setText(surname)
        age_editext.setText(age)
        phone_editext.setText(phone)
    }

    private fun saveData() {

        val userId = viewModel.userId
        val newName = name_editext.text.toString().trim()
        val newSurname = surname_edittext.text.toString().trim()
        val newAge = age_editext.text.toString().trim()
        val newPhone = phone_editext.text.toString().trim()
        val appointment = Appointment()
        appointment.person = "$newName $newSurname"
        appointment.contact_phone = newPhone

        val updatedUser = User(newName, newSurname, newAge, newPhone)
        viewModel.updateUser(userId!!, updatedUser)
        viewModel.updateAppointment(userId, appointment)
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setupListeners() {
        button_save_data.setOnClickListener { saveData() }
    }
}