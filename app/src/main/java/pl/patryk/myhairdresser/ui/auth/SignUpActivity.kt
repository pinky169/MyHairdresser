package pl.patryk.myhairdresser.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.signup_layout.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.databinding.SignupLayoutBinding
import pl.patryk.myhairdresser.utils.startDashboardActivity

class SignUpActivity : AppCompatActivity(), AuthListener, KodeinAware {

    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: SignupLayoutBinding = DataBindingUtil.setContentView(this, R.layout.signup_layout)
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)
        binding.viewmodel = viewModel

        viewModel.authListener = this
    }

    override fun onStarted() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun onSuccess(code: Int) {
        when (code) {
            AuthViewModel.CODE_OK -> {
                progress_bar.visibility = View.GONE
                startDashboardActivity()
            }
        }
    }

    override fun onFailure(message: String) {
        progress_bar.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onIncorrectEmail(errorCode: Int) {
        when (errorCode) {
            AuthViewModel.ERROR_EMPTY_FIELD -> edittext_email_input.error = this.getString(R.string.filed_can_not_be_empty)
        }
    }

    override fun onIncorrectPassword(errorCode: Int) {
        when (errorCode) {
            AuthViewModel.ERROR_EMPTY_FIELD -> edittext_password_input.error = this.getString(R.string.filed_can_not_be_empty)
            AuthViewModel.ERROR_PASSWORD_LENGTH -> edittext_password_input.error = this.getString(R.string.password_must_be_minimum_six_characters)
            AuthViewModel.ERROR_PASSWORDS_DO_NOT_MATCH -> edittext_password_input.error = this.getString(R.string.passwords_dont_match)
        }
    }

    override fun onIncorrect2ndPassword(errorCode: Int) {
        when (errorCode) {
            AuthViewModel.ERROR_EMPTY_FIELD -> edittext_2nd_password_input.error = this.getString(R.string.filed_can_not_be_empty)
            AuthViewModel.ERROR_PASSWORD_LENGTH -> edittext_2nd_password_input.error = this.getString(R.string.password_must_be_minimum_six_characters)
            AuthViewModel.ERROR_PASSWORDS_DO_NOT_MATCH -> edittext_2nd_password_input.error = this.getString(R.string.passwords_dont_match)
        }
    }
}
