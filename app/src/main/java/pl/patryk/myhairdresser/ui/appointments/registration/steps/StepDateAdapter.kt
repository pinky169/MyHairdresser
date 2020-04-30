package pl.patryk.myhairdresser.ui.appointments.registration.steps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.available_dates_item_layout.view.*
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.data.model.AppointmentDate

class StepDateAdapter : ListAdapter<AppointmentDate, StepDateAdapter.ViewHolder>(diffCallback) {

    var itemClickedListener: OnHourClickedListener? = null
    private var selectedPosition = RecyclerView.NO_POSITION

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val itemContext = itemView.context
        private var hour = itemView.hour
        private val availability = itemView.availability
        private val cardView = itemView.card_view

        fun bind(position: Int, appointmentDate: AppointmentDate) {

            hour.text = appointmentDate.time
            setAvailability(position, appointmentDate)
        }

        private fun setAvailability(position: Int, appointmentDate: AppointmentDate) {
            if (appointmentDate.availability) {
                itemView.setOnClickListener {

                    notifyItemChanged(selectedPosition)
                    selectedPosition = layoutPosition
                    notifyItemChanged(selectedPosition)

                    itemClickedListener?.onItemClicked(appointmentDate)
                }

                availability.text = itemContext.getString(R.string.step_hour_available_label_text)

                if (selectedPosition == position) {
                    cardView.setCardBackgroundColor(ContextCompat.getColor(itemContext, R.color.colorAccent))
                } else {
                    cardView.setCardBackgroundColor(ContextCompat.getColor(itemContext, R.color.colorPrimary))
                }
            } else {
                itemView.apply {
                    isClickable = false
                    isFocusable = false
                }

                cardView.setCardBackgroundColor(ContextCompat.getColor(itemContext, android.R.color.darker_gray))
                availability.text = itemContext.getString(R.string.step_hour_not_available_label_text)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.available_dates_item_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position, getItem(position))

    companion object {

        val diffCallback: DiffUtil.ItemCallback<AppointmentDate> = object : DiffUtil.ItemCallback<AppointmentDate>() {

            override fun areItemsTheSame(oldItem: AppointmentDate, newItem: AppointmentDate): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: AppointmentDate, newItem: AppointmentDate): Boolean {
                return oldItem.date == newItem.date &&
                        oldItem.time == newItem.time &&
                        oldItem.availability == newItem.availability
            }
        }
    }
}