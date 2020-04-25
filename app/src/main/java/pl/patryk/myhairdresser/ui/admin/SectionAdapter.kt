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
import pl.patryk.myhairdresser.data.model.Appointment
import pl.patryk.myhairdresser.data.model.AppointmentSection
import pl.patryk.myhairdresser.utils.PopupMenuListener


class SectionAdapter : ListAdapter<AppointmentSection, SectionAdapter.ViewHolder>(diffCallback) {

    // Use to optimize nested recycler views
    private val viewPool = RecyclerView.RecycledViewPool()

    // Listener for buttons click events
    var listener: PopupMenuListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val itemContext = itemView.context
        private var appointmentAdapter = AppointmentAdapter()
        private val header = itemView.header
        private val recyclerView = itemView.section_recycler_view

        fun bind(section: AppointmentSection) {

            // Decide if we want to show section
            // whether its empty or not
            showOrHideSection(section)
        }

        private fun showOrHideSection(section: AppointmentSection) {

            // Check if we have any data to populate views
            if (section.sectionData.isNullOrEmpty()) {

                header.visibility = View.GONE
                recyclerView.visibility = View.GONE

            }

            // Setup views if we any section to show
            else {

                header.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE

                // Set section header
                setSectionTitle(section)

                // Setup recycler view
                setupRecyclerForSection()

                // Setup onClick listener
                appointmentAdapter.popupMenuListener = listener

                // Submit section data to show
                appointmentAdapter.submitList(section.sectionData)
            }
        }

        private fun setupRecyclerForSection() {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(recyclerView.context, RecyclerView.HORIZONTAL, false)
                adapter = appointmentAdapter
                setRecycledViewPool(viewPool)
            }
        }

        private fun setSectionTitle(section: AppointmentSection) {
            when (section.sectionType) {
                Appointment.VERIFICATION_STATE_PENDING -> header.text = itemContext.getString(R.string.section_header_pending)
                Appointment.VERIFICATION_STATE_APPROVED -> header.text = itemContext.getString(R.string.section_header_approved)
                Appointment.VERIFICATION_STATE_REJECTED -> header.text = itemContext.getString(R.string.section_header_rejected)
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
                return oldItem.sectionType == newItem.sectionType
            }

            override fun areContentsTheSame(oldItem: AppointmentSection, newItem: AppointmentSection): Boolean {
                return oldItem.sectionType == newItem.sectionType && oldItem.sectionData == newItem.sectionData
            }
        }
    }


}