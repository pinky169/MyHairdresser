package pl.patryk.myhairdresser.data.firebase

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import io.reactivex.Completable
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.data.model.User
import pl.patryk.myhairdresser.utils.startLoginActivity


class FirebaseSource {

    private val db = FirebaseDatabase()

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun login(email: String, password: String) = Completable.create { task ->
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!task.isDisposed) {
                if (it.isSuccessful) {
                    task.onComplete()
                } else {
                    when ((it.exception as FirebaseAuthException).errorCode) {
                        "ERROR_INVALID_EMAIL" -> task.onError(Throwable(firebaseAuth.app.applicationContext.getString(R.string.email_badly_formatted)))
                        "ERROR_WRONG_PASSWORD" -> task.onError(Throwable(firebaseAuth.app.applicationContext.getString(R.string.password_invalid_or_not_existing)))
                    }
                }
            }
        }
    }

    fun register(email: String, password: String) = Completable.create { task ->
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!task.isDisposed) {
                if (it.isSuccessful) {
                    db.insertUser(currentUser()?.uid!!, User(email))
                    Toast.makeText(firebaseAuth.app.applicationContext, firebaseAuth.app.applicationContext.getString(R.string.account_successfully_registered), Toast.LENGTH_LONG).show()
                    task.onComplete()
                } else {
                    when ((it.exception as FirebaseAuthException).errorCode) {
                        "ERROR_INVALID_EMAIL" -> task.onError(Throwable(firebaseAuth.app.applicationContext.getString(R.string.email_badly_formatted)))
                        "ERROR_WEAK_PASSWORD" -> task.onError(Throwable(firebaseAuth.app.applicationContext.getString(R.string.password_too_weak_toast)))
                        "ERROR_EMAIL_ALREADY_IN_USE" -> task.onError(Throwable(firebaseAuth.app.applicationContext.getString(R.string.this_user_already_exists_toast)))
                    }
                }
            }
        }
    }

    fun verifyEmail() {

        // Reload to check the verification state of a user
        // Its because FirebaseUser object is cached within an app session
        currentUser()?.reload()?.addOnSuccessListener {

            // Send verification email only if email linked with account is not verified yet
            if (!currentUser()?.isEmailVerified!!) {
                currentUser()?.sendEmailVerification()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseAuth.signOut()
                        firebaseAuth.app.applicationContext.startLoginActivity()
                        Toast.makeText(firebaseAuth.app.applicationContext, firebaseAuth.app.applicationContext.getString(R.string.verification_send_to, currentUser()?.email), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(firebaseAuth.app.applicationContext, firebaseAuth.app.applicationContext.getString(R.string.failed_to_send_verification_email), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(firebaseAuth.app.applicationContext, firebaseAuth.app.applicationContext.getString(R.string.email_already_verified), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun logout() = firebaseAuth.signOut()

    fun currentUser() = firebaseAuth.currentUser

    fun reloadUser() = firebaseAuth.currentUser?.reload()
}