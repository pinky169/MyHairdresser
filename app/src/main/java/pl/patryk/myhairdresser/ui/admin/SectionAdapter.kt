package pl.patryk.myhairdresser.ui.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.section_layout.view.*
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.data.model.AppointmentSection


class SectionAdapter : ListAdapter<AppointmentSection, SectionAdapter.ViewHolder>(diffCallback) {

    // Use to optimize nested recycler views
    private val viewPool = RecyclerView.RecycledViewPool()

    // Listener for buttons click events
    var listener: AdminListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var appointmentAdapter = AppointmentAdapter()
        private val header = itemView.header
        private val recyclerView = itemView.section_recycler_view

        fun bind(section: AppointmentSection) {

            // Set section header
            header.text = section.header

            // Setup onClick listener
            appointmentAdapter.adminListener = listener

            // Decide if we want to show section
            // if its empty or not
            showOrHideSection(section)

            // Setup recycler view
            setupRecyclerForSection()

            // Submit section data to show
            appointmentAdapter.submitList(section.appointments)
        }

        private fun setupRecyclerForSection() {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(recyclerView.context, RecyclerView.HORIZONTAL, false)
                adapter = appointmentAdapter
                setRecycledViewPool(viewPool)
            }
        }

        private fun showOrHideSection(section: AppointmentSection) {
            // Check if we have any data to populate views
            if (section.appointments.isNullOrEmpty()) {
                header.visibility = View.GONE
                recyclerView.visibility = View.GONE
            } else {
                header.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.section_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SectionAdapter.ViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {

        private val diffCallback: DiffUtil.ItemCallback<AppointmentSection> = object : DiffUtil.ItemCallback<AppointmentSection>() {

            override fun areItemsTheSame(oldItem: AppointmentSection, newItem: AppointmentSection): Boolean {
                return oldItem.header.equals(newItem.header)
            }

            override fun areContentsTheSame(oldItem: AppointmentSection, newItem: AppointmentSection): Boolean {
                return oldItem.header.equals(newItem.header) && oldItem.appointments.equals(newItem.appointments)
            }
        }
    }


}