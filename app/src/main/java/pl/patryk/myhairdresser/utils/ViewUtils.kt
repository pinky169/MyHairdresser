package pl.patryk.myhairdresser.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import es.dmoral.toasty.Toasty
import pl.patryk.myhairdresser.FirebaseApplication
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.ui.admin.AdminActivity
import pl.patryk.myhairdresser.ui.auth.LoginActivity
import pl.patryk.myhairdresser.ui.auth.SignUpActivity
import pl.patryk.myhairdresser.ui.profile.UserProfileActivity

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

fun changeDateFormatting(dateToFormat: String): String {

    val date = FirebaseApplication().databaseFormatter.parse(dateToFormat)

    return FirebaseApplication().generalFormatter.format(date)
}