package pl.patryk.myhairdresser.ui.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_layout.view.*
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.data.model.Appointment


class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var itemList = mutableListOf<Appointment>()
    private var lastPosition = -1
    var adminListener: AdminListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val person = itemView.person
        private val date = itemView.date
        private val service = itemView.service
        private val phone = itemView.phone

        fun bind(appointment: Appointment) {
            person.text = appointment.person
            date.text = appointment.date
            service.text = appointment.service
            phone.text = appointment.contact_phone

            itemView.confirm_appointment_button.setOnClickListener { adminListener?.confirm(appointment.userID) }
            itemView.reject_appointment_button.setOnClickListener { adminListener?.reject(appointment.userID) }
            itemView.phone_call_button.setOnClickListener { adminListener?.phoneCall(appointment.contact_phone) }

            setAnimation(itemView, adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.bind(itemList[position])

    override fun getItemCount() = itemList.size

    fun setItems(items: List<Appointment>) {
        if (itemList != items) {
            this.itemList = items as MutableList<Appointment>
            notifyDataSetChanged()
        }
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation: Animation = AnimationUtils.loadAnimation(viewToAnimate.context, android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }
}