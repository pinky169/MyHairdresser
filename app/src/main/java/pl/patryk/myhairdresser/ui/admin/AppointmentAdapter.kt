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
import pl.patryk.myhairdresser.utils.changeDateFormatting


class AppointmentAdapter : ListAdapter<Appointment, AppointmentAdapter.ViewHolder>(diffCallback) {

    var adminListener: AdminListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val itemContext = itemView.context
        private val person = itemView.person
        private val date = itemView.date
        private val service = itemView.service
        private val phone = itemView.phone
        private val popupMenu = itemView.popup_menu
        private val cardView = itemView.card_view

        fun bind(appointment: Appointment) {

            person.text = appointment.person
            date.text = changeDateFormatting(appointment.date)
            service.text = appointment.service
            phone.text = appointment.contact_phone

            setCardBackground(appointment)

            popupMenu.setOnClickListener { adminListener?.createPopupMenu(itemView.context, itemView, appointment) }
        }

        private fun setCardBackground(appointment: Appointment) {
            when {
                appointment.verification_state.equals(Appointment.VERIFICATION_STATE_APPROVED) -> cardView.setCardBackgroundColor((ContextCompat.getColor(itemContext, R.color.colorApproved)))
                appointment.verification_state.equals(Appointment.VERIFICATION_STATE_REJECTED) -> cardView.setCardBackgroundColor((ContextCompat.getColor(itemContext, R.color.colorRejected)))
                else -> cardView.setCardBackgroundColor((ContextCompat.getColor(itemContext, R.color.colorPending)))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {

        private val diffCallback: DiffUtil.ItemCallback<Appointment> = object : DiffUtil.ItemCallback<Appointment>() {

            override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
                return oldItem.userID.equals(newItem.userID)
            }

            override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
                return oldItem.person.equals(newItem.person) &&
                        oldItem.date.equals(newItem.date) &&
                        oldItem.contact_phone.equals(newItem.contact_phone) &&
                        oldItem.service.equals(newItem.service) &&
                        oldItem.verification_state.equals(newItem.verification_state)
            }
        }
    }
}