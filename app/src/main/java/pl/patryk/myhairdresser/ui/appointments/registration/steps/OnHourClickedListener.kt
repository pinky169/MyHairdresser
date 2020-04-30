package pl.patryk.myhairdresser.ui.appointments.registration.steps

import pl.patryk.myhairdresser.data.model.AppointmentDate

interface OnHourClickedListener {
    fun onItemClicked(appointmentDate: AppointmentDate)
}