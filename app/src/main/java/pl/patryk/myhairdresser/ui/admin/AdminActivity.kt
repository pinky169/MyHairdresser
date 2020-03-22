package pl.patryk.myhairdresser.ui.admin

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import pl.patryk.myhairdresser.utils.startLoginActivity

class AdminActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: AdminViewModelFactory by instance()
    private lateinit var recyclerAdapter: RecyclerAdapter

    private lateinit var viewModel: AdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_layout)

        viewModel = ViewModelProvider(this, factory).get(AdminViewModel::class.java)

        setupRecycler()
        observeAppointments(viewModel)
    }

    private fun observeAppointments(viewModel: AdminViewModel) {
        viewModel.getAppointments().observe(this, Observer { appointments ->
            recyclerAdapter.setItems(appointments)
        })
    }

    private fun setupRecycler() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        recyclerAdapter = RecyclerAdapter()
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
}
