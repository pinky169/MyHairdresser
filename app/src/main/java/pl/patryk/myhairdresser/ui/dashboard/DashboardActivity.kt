package pl.patryk.myhairdresser.ui.dashboard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.dashboard_layout.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.databinding.DashboardLayoutBinding


class DashboardActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory : DashboardViewModelFactory by instance()

    private lateinit var viewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: DashboardLayoutBinding = DataBindingUtil.setContentView(this, R.layout.dashboard_layout)
        viewModel = ViewModelProvider(this, factory).get(DashboardViewModel::class.java)
        binding.viewmodel = viewModel
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Make an alert asking if user want to sign out or not"
        viewModel.logout()
    }
}
