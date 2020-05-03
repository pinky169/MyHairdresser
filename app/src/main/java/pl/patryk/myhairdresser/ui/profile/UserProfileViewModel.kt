package pl.patryk.myhairdresser.ui.profile

import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import pl.patryk.myhairdresser.data.model.Photo
import pl.patryk.myhairdresser.data.model.User
import pl.patryk.myhairdresser.data.repository.UserRepository
import pl.patryk.myhairdresser.utils.startLoginActivity

class UserProfileViewModel(private val repository: UserRepository) : ViewModel() {

    var userListener: UserListener? = null
    val user by lazy { repository.currentUser() }
    val userId by lazy { repository.currentUserId() }
    val userReference by lazy { repository.getUserReference(userId!!) }
    private val storageReference by lazy { repository.getStorageReference() }

    /**
     * Contains information about User.
     * Initialization is done once thanks to lazy initialization.
     */
    private val userLiveData: MutableLiveData<User> by lazy {
        MutableLiveData<User>().also {
            loadUser()
        }
    }

    /**
     * Returns user's LiveData data holder which contains
     * information about user from firebase database.
     */
    fun getUser(): LiveData<User> {
        return userLiveData
    }

    val valueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                val user: User = dataSnapshot.getValue(User::class.java)!!
                userLiveData.postValue(user)
                userListener?.onSuccess()
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            userListener?.onCanceled()
        }
    }

    /**
     * Loads user's data from firebase database into LivaData data holder.
     */
    private fun loadUser() {
        userListener?.onStarted()

        // If any value in db for the user changes, load new content
        userReference.addValueEventListener(valueEventListener)
    }

    /**
     * Updates user data in firebase database
     * @param uid firebase ID of the current user
     * @param user User object
     */
    fun updateUser(uid: String, user: User) = repository.updateUser(uid, user)

    /**
     * Use to log out the user
     */
    fun logout() = repository.logout()

    /**
     * Use to logout the user using some view
     * and navigate to login screen
     * @param view View which is used to call logout
     */
    fun logout(view: View) {
        repository.logout()
        view.context.startLoginActivity()
    }

    /**
     * Function to call firebase email verification.
     * Sends email to current user and logout automatically.
     */
    fun verifyEmail() = repository.verifyEmail()

    /**
     * Uploads a selected by user photo to firebase **storage** and
     * if UploadTask was successful a Photo object with img URL
     * is inserted into firebase **database** for the current user.
     * @param imgUri Uri to a File selected by the user
     * @param fileExtension Extension of the file selected by the user (.jpg, .png, .gif, etc.)
     */
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

    /**
     * Use to insert user's device token into database.
     */
    fun insertToken(uid: String, token: String) = repository.insertToken(uid, token)

    override fun onCleared() {
        userReference.removeEventListener(valueEventListener)
        super.onCleared()
    }
}