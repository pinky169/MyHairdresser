package pl.patryk.myhairdresser.data.firebase

import android.widget.Toast
import com.google.firebase.FirebaseError.*
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Completable
import pl.patryk.myhairdresser.data.model.User


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
                    when (it.exception.hashCode()) {
                        ERROR_INVALID_EMAIL -> Toast.makeText(firebaseAuth.app.applicationContext, "The email address is badly formatted.", Toast.LENGTH_LONG).show()
                        ERROR_WRONG_PASSWORD -> Toast.makeText(firebaseAuth.app.applicationContext, "The password is invalid or the user does not have a password.", Toast.LENGTH_LONG).show()
                        else -> task.onError(it.exception!!)
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
                    task.onComplete()
                } else {
                    when (it.exception.hashCode()) {
                        ERROR_INVALID_EMAIL -> Toast.makeText(firebaseAuth.app.applicationContext, "The email address is badly formatted.", Toast.LENGTH_LONG).show()
                        ERROR_WEAK_PASSWORD -> Toast.makeText(firebaseAuth.app.applicationContext, "The given password is too weak. (Minimum 6 characters)", Toast.LENGTH_LONG).show()
                        else -> task.onError(it.exception!!)
                    }
                }
            }
        }
    }

    fun verifyEmail() {
        if (!currentUser()?.isEmailVerified!!) {
            currentUser()?.sendEmailVerification()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(firebaseAuth.app.applicationContext, "Verification email sent to " + currentUser()?.email, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(firebaseAuth.app.applicationContext, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun logout() = firebaseAuth.signOut()

    fun currentUser() = firebaseAuth.currentUser

}