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
import pl.patryk.myhairdresser.data.model.Photo
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
    private lateinit var photo: Photo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile_layout)

        viewModel = ViewModelProvider(this, factory).get(UserProfileViewModel::class.java)

        loadUserData()
        setupListeners()
    }

    private fun loadUserData() {

        title = getString(R.string.action_bar_title_edit)

        name = intent.getStringExtra(UserProfileActivity.TAG_USER_NAME)
        surname = intent.getStringExtra(UserProfileActivity.TAG_USER_SURNAME)
        email = intent.getStringExtra(UserProfileActivity.TAG_USER_EMAIL)
        age = intent.getStringExtra(UserProfileActivity.TAG_USER_AGE)
        phone = intent.getStringExtra(UserProfileActivity.TAG_USER_PHONE)
        photo = intent.getSerializableExtra(UserProfileActivity.TAG_USER_PHOTO) as Photo

        email_textview.text = email
        name_editext.setText(name)
        surname_edittext.setText(surname)
        age_editext.setText(age)
        phone_editext.setText(phone)
    }

    private fun saveData() {

        val userId = viewModel.getUserId()
        val newName = name_editext.text.toString().trim()
        val newSurname = surname_edittext.text.toString().trim()
        val newAge = age_editext.text.toString().trim()
        val newPhone = phone_editext.text.toString().trim()

        val updatedUser = User(newName, newSurname, email, newAge, newPhone, photo, false)
        viewModel.updateUser(userId!!, updatedUser)
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setupListeners() {
        button_save_data.setOnClickListener { saveData() }
    }
}