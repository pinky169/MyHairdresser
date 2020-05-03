package pl.patryk.myhairdresser.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import es.dmoral.toasty.Toasty
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.ui.admin.AdminActivity
import pl.patryk.myhairdresser.ui.appointments.registration.AppointmentRegistrationActivity
import pl.patryk.myhairdresser.ui.appointments.user.UserAppointmentsActivity
import pl.patryk.myhairdresser.ui.auth.LoginActivity
import pl.patryk.myhairdresser.ui.auth.SignUpActivity
import pl.patryk.myhairdresser.ui.profile.UserProfileActivity
import java.text.SimpleDateFormat
import java.util.*

fun Context.startUserProfileActivity() =
        Intent(this, UserProfileActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
            Toasty.success(this, getString(R.string.welcome_message), Toast.LENGTH_LONG, false).show()
        }

fun Context.startAdminActivity() =
        Intent(this, AdminActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }

fun Context.startLoginActivity() =
        Intent(this, LoginActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }

fun Context.startSignUpActivity() =
        Intent(this, SignUpActivity::class.java).also {
            startActivity(it)
        }

fun Context.startUserAppointmentsActivity() =
        Intent(this, UserAppointmentsActivity::class.java).also {
            startActivity(it)
        }

fun Context.startAppointmentRegistrationActivity() =
        Intent(this, AppointmentRegistrationActivity::class.java).also {
            startActivity(it)
        }

fun changeToUserReadableFormatting(dateToFormat: String): String {

    val date = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).parse(dateToFormat)

    return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
}

fun changeToDatabaseFormatting(dateToFormat: String): String {

    val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).parse(dateToFormat)

    return SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(date)
}

fun changeToQueryFormatting(dateToFormat: String): String {

    val date = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).parse(dateToFormat)

    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
}

fun changeBackFromQueryFormatting(dateToFormat: String): String {

    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateToFormat)

    return SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(date)
}