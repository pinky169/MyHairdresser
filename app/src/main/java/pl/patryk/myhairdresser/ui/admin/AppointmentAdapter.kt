package pl.patryk.myhairdresser.ui.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_layout.view.*
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.data.model.Appointment
import pl.patryk.myhairdresser.utils.PopupMenuListener
import pl.patryk.myhairdresser.utils.changeToUserReadableFormatting


class AppointmentAdapter : ListAdapter<Appointment, AppointmentAdapter.ViewHolder>(diffCallback) {

    var popupMenuListener: PopupMenuListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val itemContext = itemView.context
        private val person = itemView.person
        private val date = itemView.date
        private val service = itemView.service
        private val phone = itemView.phone
        private val popupMenu = itemView.popup_menu
        private val cardView = itemView.card_view

        fun bind(appointment: Appointment) {

            person.text = appointment.name
            date.text = "${changeToUserReadableFormatting(appointment.date)} ${appointment.time}"
            service.text = appointment.service
            phone.text = appointment.phone

            setCardBackground(appointment)

            popupMenu.setOnClickListener { popupMenuListener?.createPopupMenu(popupMenu, appointment) }
        }

        private fun setCardBackground(appointment: Appointment) {
            when (appointment.verification_state) {
                Appointment.VERIFICATION_STATE_APPROVED -> cardView.setCardBackgroundColor((ContextCompat.getColor(itemContext, R.color.colorApprovedWith30Transparency)))
                Appointment.VERIFICATION_STATE_REJECTED -> cardView.setCardBackgroundColor((ContextCompat.getColor(itemContext, R.color.colorRejectedWith30Transparency)))
                else -> cardView.setCardBackgroundColor((ContextCompat.getColor(itemContext, R.color.colorPendingWith30Transparency)))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {

        val diffCallback: DiffUtil.ItemCallback<Appointment> = object : DiffUtil.ItemCallback<Appointment>() {

            override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
                return oldItem.appointmentID == newItem.appointmentID
            }

            override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
                return oldItem.userID == newItem.userID &&
                        oldItem.service == newItem.service &&
                        oldItem.date == newItem.date &&
                        oldItem.verification_state == newItem.verification_state &&
                        oldItem.name == newItem.name &&
                        oldItem.phone == newItem.phone
            }
        }
    }
}