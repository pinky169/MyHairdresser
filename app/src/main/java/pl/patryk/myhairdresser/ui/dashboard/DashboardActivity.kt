package pl.patryk.myhairdresser.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.data.firebase.FirebaseDatabase
import pl.patryk.myhairdresser.databinding.DashboardLayoutBinding


class DashboardActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory : DashboardViewModelFactory by instance()
    private val fd: FirebaseDatabase = FirebaseDatabase()

    private lateinit var viewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: DashboardLayoutBinding = DataBindingUtil.setContentView(this, R.layout.dashboard_layout)
        viewModel = ViewModelProvider(this, factory).get(DashboardViewModel::class.java)
        binding.viewmodel = viewModel

        fd.insertUsers()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Make an alert asking if user want to sign out or not"
        viewModel.logout()
    }
}
