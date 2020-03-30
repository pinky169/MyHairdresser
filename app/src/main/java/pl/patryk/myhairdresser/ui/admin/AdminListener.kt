package pl.patryk.myhairdresser.ui.admin

import android.content.Context
import android.view.View
import pl.patryk.myhairdresser.data.model.Appointment

interface AdminListener {
    fun createPopupMenu(context: Context, view: View, appointment: Appointment)
}