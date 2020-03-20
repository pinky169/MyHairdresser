package pl.patryk.myhairdresser.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.user_profile_layout.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import pl.patryk.myhairdresser.R
import pl.patryk.myhairdresser.data.firebase.FirebaseDatabaseHelper
import pl.patryk.myhairdresser.data.firebase.FirebaseStorageHelper
import pl.patryk.myhairdresser.data.model.Photo
import pl.patryk.myhairdresser.data.model.User
import pl.patryk.myhairdresser.databinding.UserProfileLayoutBinding
import pl.patryk.myhairdresser.utils.DialogUtils
import pl.patryk.myhairdresser.utils.startLoginActivity


class UserProfileActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: UserProfileViewModelFactory by instance()
    private lateinit var dbHelper: FirebaseDatabaseHelper
    private lateinit var storageHelper: FirebaseStorageHelper
    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userID: String
    private var user: User? = null

    private lateinit var viewModel: UserProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: UserProfileLayoutBinding = DataBindingUtil.setContentView(this, R.layout.user_profile_layout)
        viewModel = ViewModelProvider(this, factory).get(UserProfileViewModel::class.java)
        binding.viewmodel = viewModel

        init()
        setupListeners()
        loadUser(databaseReference.child(userID))
        setupBackgroundAnimation()
    }

    private fun init() {

        userID = viewModel.getUserId()!!

        dbHelper = FirebaseDatabaseHelper()
        databaseReference = dbHelper.databaseReference

        storageHelper = FirebaseStorageHelper()
        storageReference = storageHelper.storageReference
    }

    // Loads user realtime data from firebase database into views
    private fun loadUser(userReference: DatabaseReference) {

        // Loading started, show progress bar
        progress_bar.visibility = View.VISIBLE

        // If any value in db for the user changes, load new content
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {

                    user = dataSnapshot.getValue(User::class.java)

                    // If user uploaded any photo before just load it
                    // else don't load anything and hide progress bar
                    if (dataSnapshot.child("photo").exists())
                        loadPhoto(user?.photo!!.photoUrl)
                    else
                        photo_progress_bar.visibility = View.GONE

                    // Load user data into views
                    setupContent()

                    //Done loading, hide progress bar
                    progress_bar.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                progress_bar.visibility = View.GONE
            }
        })
    }

    private fun loadPhoto(photoUrl: String) {

        // Loading started, show progress bar
        photo_progress_bar.visibility = View.VISIBLE

        Glide.with(applicationContext)
                .load(photoUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_profile_picture)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        photo_progress_bar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        photo_progress_bar.visibility = View.GONE
                        return false
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(profile_photo)
    }

    private fun setupContent() {

        if (viewModel.user!!.isEmailVerified) {
            textview_email_label.text = getString(R.string.email_label)
            verified_icon.setImageResource(R.drawable.ic_user_verified)
        } else {
            textview_email_label.text = getString(R.string.verify_your_email)
            verified_icon.setImageResource(R.drawable.ic_send_email)
            verified_icon.setOnClickListener { viewModel.verifyEmail() }
        }

        verified_icon.visibility = View.VISIBLE
        edit_profile_button.visibility = View.VISIBLE
        name_textview.text = user?.name
        surname_textview.text = user?.surname
        email_textview.text = user?.email
        age_textview.text = user?.age
        phone_textview.text = user?.phone
    }

    private fun setupListeners() {
        profile_photo.setOnClickListener { openFileChooser() }
        edit_profile_button.setOnClickListener { startEditProfileActivity() }
        register_appointment_button.setOnClickListener { startAppointmentActivity() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_logout -> {
                viewModel.logout()
                finish()
                startLoginActivity()
                Toasty.info(this, getString(R.string.log_out_confirmation), Toast.LENGTH_LONG).show()
            }
        }
        return true
    }

    private fun startEditProfileActivity() {
        if (user != null) {
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra(TAG_USER_NAME, user?.name)
            intent.putExtra(TAG_USER_SURNAME, user?.surname)
            intent.putExtra(TAG_USER_EMAIL, user?.email)
            intent.putExtra(TAG_USER_AGE, user?.age)
            intent.putExtra(TAG_USER_PHONE, user?.phone)
            startActivityForResult(intent, UPDATE_PROFILE_REQUEST)
        }
    }

    private fun startAppointmentActivity() {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        val prev: Fragment? = supportFragmentManager.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        // Create and show the dialog.
        val newFragment: DialogFragment = DialogUtils().newInstance()!!
        newFragment.show(ft, "dialog")
    }

    private fun openFileChooser() {
        Toasty.info(this, getString(R.string.pick_a_photo), Toast.LENGTH_LONG).show()
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.data != null) {
            uploadPhoto(data.data)
        }

        if (requestCode == UPDATE_PROFILE_REQUEST && resultCode == Activity.RESULT_OK) {
            Toasty.success(this, getString(R.string.profile_updated_successfully), Toast.LENGTH_LONG).show()
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun uploadPhoto(imgUri: Uri?) {

        photo_progress_bar.visibility = View.VISIBLE

        if (imgUri != null) {
            val imgReference = storageReference.child(userID).child("$userID.${getFileExtension(imgUri)}")
            imgReference.putFile(imgUri).addOnSuccessListener {
                imgReference.downloadUrl.addOnSuccessListener {
                    val imgURL = it.toString()
                    val photo = Photo(userID, imgURL)
                    dbHelper.insertPhoto(userID, photo)
                    photo_progress_bar.visibility = View.GONE
                    Toasty.success(this, getString(R.string.photo_uploaded_successfully), Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toasty.warning(this, getString(R.string.no_file_selected), Toast.LENGTH_LONG).show()
        }
    }

    private fun setupBackgroundAnimation() {
        val animationDrawable = constraintLayout.background as? AnimationDrawable
        animationDrawable?.setEnterFadeDuration(2000)
        animationDrawable?.setExitFadeDuration(4000)
        animationDrawable?.start()
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1337
        private const val UPDATE_PROFILE_REQUEST = 777
        const val TAG_USER_NAME = "name"
        const val TAG_USER_SURNAME = "surname"
        const val TAG_USER_EMAIL = "email"
        const val TAG_USER_AGE = "age"
        const val TAG_USER_PHONE = "phone"

    }
}