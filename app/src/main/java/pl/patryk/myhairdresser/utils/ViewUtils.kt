package pl.patryk.myhairdresser.utils

import android.content.Context
import android.content.Intent
import pl.patryk.myhairdresser.ui.auth.LoginActivity
import pl.patryk.myhairdresser.ui.auth.SignUpActivity
import pl.patryk.myhairdresser.ui.dashboard.DashboardActivity

fun Context.startDashboardActivity() =
    Intent(this, DashboardActivity::class.java).also {
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
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }