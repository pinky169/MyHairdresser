package pl.patryk.myhairdresser.data.firebase

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseStorageHelper {

    private val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    val storageReference: StorageReference by lazy {
        storage.getReference("uploads")
    }
}