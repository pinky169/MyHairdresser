package pl.patryk.myhairdresser.ui.auth

import android.view.View
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import pl.patryk.myhairdresser.data.repository.UserRepository
import pl.patryk.myhairdresser.utils.startLoginActivity
import pl.patryk.myhairdresser.utils.startSignUpActivity

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    //email and password for the input
    var email: String? = null
    var password: String? = null
    var password2nd: String? = null

    //auth listener
    var authListener: AuthListener? = null


    //disposable to dispose the Completable
    private val disposables = CompositeDisposable()

    val user by lazy {
        repository.currentUser()
    }

    //function to perform login
    fun login() {

        //validating email and password
        if (email.isNullOrBlank() || password.isNullOrBlank()) {
            authListener?.onIncorrectEmail("Please enter valid email adress")
            authListener?.onIncorrectPassword("Please enter valid password")
            return
        } else if (password!!.length < 6) {
            authListener?.onIncorrectPassword("Minimum password length is 6")
            return
        }

        //authentication started
        authListener?.onStarted()

        //calling login from repository to perform the actual authentication
        val disposable = repository.login(email!!, password!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //sending a success callback
                    authListener?.onSuccess()
                }, {
                    //sending a failure callback
                    authListener?.onFailure(it.message!!)
                })
        disposables.add(disposable)
    }

    //function to perform sign up
    fun signup() {

        //validating email and password
        if (email.isNullOrBlank() || password.isNullOrBlank() || password2nd.isNullOrBlank()) {
            authListener?.onIncorrectEmail("Please enter valid email adress")
            authListener?.onIncorrectPassword("Please enter valid password")
            authListener?.onIncorrect2ndPassword("Please enter valid password")
            return
        } else if (!password.equals(password2nd)) {
            authListener?.onIncorrectPassword("Passwords do not match")
            authListener?.onIncorrect2ndPassword("Passwords do not match")
            return
        } else if (password!!.length < 6 || password2nd!!.length < 6) {
            authListener?.onIncorrectPassword("Minimum password length is 6")
            authListener?.onIncorrect2ndPassword("Minimum password length is 6")
            return
        }

        authListener?.onStarted()
        val disposable = repository.register(email!!, password!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    authListener?.onSuccess()
                }, {
                    authListener?.onFailure(it.message!!)
                })
        disposables.add(disposable)
    }

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