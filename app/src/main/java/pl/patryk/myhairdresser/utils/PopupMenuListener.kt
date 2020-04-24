package pl.patryk.myhairdresser.utils

import android.view.View
import pl.patryk.myhairdresser.data.model.Appointment

interface PopupMenuListener {
    fun createPopupMenu(view: View, appointment: Appointment)
}