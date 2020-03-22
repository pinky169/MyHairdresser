package pl.patryk.myhairdresser.ui.profile

import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import pl.patryk.myhairdresser.data.model.Appointment
import pl.patryk.myhairdresser.data.model.Photo
import pl.patryk.myhairdresser.data.model.User
import pl.patryk.myhairdresser.data.repository.UserRepository
import pl.patryk.myhairdresser.utils.startLoginActivity

class UserProfileViewModel(private val repository: UserRepository) : ViewModel() {

    var userListener: UserListener? = null
    val user by lazy { repository.currentUser() }
    val userId by lazy { repository.currentUserId() }
    val userReference by lazy { repository.getUserReference(userId!!) }
    val storageReference by lazy { repository.getStorageReference() }
    var userLiveData: MutableLiveData<User> = MutableLiveData()

    fun logout() = repository.logout()

    fun logout(view: View) {
        repository.logout()
        view.context.startLoginActivity()
    }

    fun updateUser(uid: String, user: User) = repository.updateUser(uid, user)

    fun updateAppointment(uid: String, appointment: Appointment) = repository.updateAppointment(uid, appointment)

    fun verifyEmail() = repository.verifyEmail()

    fun reloadUser() = repository.reloadUser()

    fun getUserReference(uid: String) = repository.getUserReference(uid)

    fun loadUser(): LiveData<User> {
        userListener?.onStarted()
        // If any value in db for the user changes, load new content
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    userLiveData.postValue(user)
                    userListener?.onSuccess()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                userListener?.onCanceled()
            }
        })
        return userLiveData
    }

    fun uploadPhoto(imgUri: Uri?, fileExtension: String?) {

        userListener?.onUploadStarted()

        if (imgUri != null) {
            val imgReference = storageReference.child(userId!!).child("$userId.$fileExtension")
            imgReference.putFile(imgUri).addOnSuccessListener {
                imgReference.downloadUrl.addOnSuccessListener {
                    val imgURL = it.toString()
                    val photo = Photo(userId!!, imgURL)
                    repository.insertPhoto(userId!!, photo)
                    userListener?.onUploadSuccessful()
                }
            }
        } else {
            userListener?.onUploadFailed()
        }
    }
}