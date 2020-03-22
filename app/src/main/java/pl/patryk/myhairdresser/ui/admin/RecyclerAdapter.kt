package pl.patryk.myhairdresser.ui.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.item_layout.view.*
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.data.model.Appointment

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var itemList = mutableListOf<Appointment>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val person = itemView.person
        private val date = itemView.date
        private val service = itemView.service
        private val phone = itemView.phone

        fun bind(appointment: Appointment) {
            person.text = appointment.person
            date.text = appointment.date
            service.text = appointment.service
            phone.text = appointment.contactPhone
            itemView.appointment_done_button.setOnClickListener { Toasty.success(itemView.context, "Pomyślnie zatwierdzono", Toast.LENGTH_LONG).show() }
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
}