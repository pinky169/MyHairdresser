package pl.patryk.myhairdresser.ui.appointments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.appointment_item.view.*
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.data.model.Appointment
import pl.patryk.myhairdresser.ui.admin.AppointmentAdapter
import pl.patryk.myhairdresser.utils.PopupMenuListener

class UserAppointmentAdapter : ListAdapter<Appointment, UserAppointmentAdapter.ViewHolder>(AppointmentAdapter.diffCallback) {

    var popupMenuListener: PopupMenuListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val itemContext = itemView.context
        private var state = itemView.appointment_state
        private val date = itemView.appointment_date
        private val service = itemView.appointment_service
        private val popupMenu = itemView.popup_menu
        private val cardView = itemView.appointment_card_view

        fun bind(appointment: Appointment) {

            service.text = appointment.service
            setAppointmentState(appointment)
            setAppointmentDate(appointment)
            setCardBackground(appointment)

            popupMenu.setOnClickListener { popupMenuListener?.createPopupMenu(popupMenu, appointment) }
        }

        private fun setCardBackground(appointment: Appointment) {
            when (appointment.verification_state) {
                Appointment.VERIFICATION_STATE_APPROVED -> cardView.setCardBackgroundColor((ContextCompat.getColor(itemContext, R.color.colorApprovedWith50Transparency)))
                Appointment.VERIFICATION_STATE_REJECTED -> cardView.setCardBackgroundColor((ContextCompat.getColor(itemContext, R.color.colorRejectedWith50Transparency)))
                else -> cardView.setCardBackgroundColor((ContextCompat.getColor(itemContext, R.color.colorPendingWith50Transparency)))
            }
        }

        private fun setAppointmentState(appointment: Appointment) {

            when (appointment.verification_state) {
                Appointment.VERIFICATION_STATE_PENDING -> state.text = itemContext.getString(R.string.appointment_state_pending)
                Appointment.VERIFICATION_STATE_APPROVED -> state.text = itemContext.getString(R.string.appointment_state_approved)
                Appointment.VERIFICATION_STATE_REJECTED -> state.text = itemContext.getString(R.string.appointment_state_rejected)
            }
        }

        private fun setAppointmentDate(appointment: Appointment) {

            when (appointment.verification_state) {
                Appointment.VERIFICATION_STATE_PENDING -> date.text = appointment.date
                Appointment.VERIFICATION_STATE_APPROVED -> date.text = itemContext.getString(R.string.notification_appointment_date_accepted, appointment.date)
                Appointment.VERIFICATION_STATE_REJECTED -> date.text = appointment.date
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.appointment_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))
}