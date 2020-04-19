package pl.patryk.myhairdresser.ui.auth

import android.view.View
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import pl.patryk.myhairdresser.data.repository.UserRepository
import pl.patryk.myhairdresser.utils.ConnectionUtils
import pl.patryk.myhairdresser.utils.startLoginActivity
import pl.patryk.myhairdresser.utils.startSignUpActivity

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    companion object {
        const val CODE_OK = 111
        const val ERROR_EMPTY_FIELD = 100
        const val ERROR_PASSWORD_LENGTH = 101
        const val ERROR_PASSWORDS_DO_NOT_MATCH = 123
    }

    //email and password for the input
    var email: String? = null
    var password: String? = null
    var password2nd: String? = null

    //auth listener
    var authListener: AuthListener? = null

    //disposable to dispose the Completable
    private val disposables = CompositeDisposable()

    val user by lazy { repository.currentUser() }

    val userId by lazy { repository.currentUserId() }

    fun getPermissionsReference(uid: String) = repository.getPermissionsReference(uid)

    fun getUserPermissionLevel(uid: String) {
        repository.getPermissionsReference(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(datasnapshot: DataSnapshot) {
                if (datasnapshot.exists()) {
                    // This contains User's admin filed
                    val isAdmin = datasnapshot.value as? Boolean
                    authListener?.onPermissionGranted(isAdmin!!)
                }
            }
        })
    }

    //function to perform login
    fun login(view: View) {

        if (ConnectionUtils(view.context).isConnected) {

            //validating email and password
            if (email.isNullOrBlank() || password.isNullOrBlank()) {
                authListener?.onIncorrectEmail(ERROR_EMPTY_FIELD)
                authListener?.onIncorrectPassword(ERROR_EMPTY_FIELD)
                return
            } else if (password!!.length < 6) {
                authListener?.onIncorrectPassword(ERROR_PASSWORD_LENGTH)
                return
            }

            //authentication started
            authListener?.onStarted()

            //calling login from repository to perform the actual authentication
            val disposable = repository.login(email!!, password!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        // Do sth that depends on the access lvl
                        getUserPermissionLevel(userId!!)
                    }, {
                        //sending a failure callback
                        authListener?.onFailure(it.message!!)
                    })
            disposables.add(disposable)
        } else {
            authListener?.onNoConnectionAvailable()
        }
    }

    //function to perform sign up
    fun signup(view: View) {

        if (ConnectionUtils(view.context).isConnected) {

            //validating email and password
            if (email.isNullOrBlank() || password.isNullOrBlank() || password2nd.isNullOrBlank()) {
                authListener?.onIncorrectEmail(ERROR_EMPTY_FIELD)
                authListener?.onIncorrectPassword(ERROR_EMPTY_FIELD)
                authListener?.onIncorrect2ndPassword(ERROR_EMPTY_FIELD)
                return
            } else if (!password.equals(password2nd)) {
                authListener?.onIncorrectPassword(ERROR_PASSWORDS_DO_NOT_MATCH)
                authListener?.onIncorrect2ndPassword(ERROR_PASSWORDS_DO_NOT_MATCH)
                return
            } else if (password!!.length < 6 || password2nd!!.length < 6) {
                authListener?.onIncorrectPassword(ERROR_PASSWORD_LENGTH)
                authListener?.onIncorrect2ndPassword(ERROR_PASSWORD_LENGTH)
                return
            }

            authListener?.onStarted()
            val disposable = repository.register(email!!, password!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        // Do sth that depends on the access lvl
                        getUserPermissionLevel(userId!!)
                    }, {
                        authListener?.onFailure(it.message!!)
                    })
            disposables.add(disposable)
        } else {
            authListener?.onNoConnectionAvailable()
        }
    }

    fun logout() = repository.logout()

    fun goToSignup(view: View) {
        view.context.startSignUpActivity()
    }

    fun goToLogin(view: View) {
        view.context.startLoginActivity()
    }

    //disposing the disposables
    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

}