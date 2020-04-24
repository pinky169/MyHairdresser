package pl.patryk.myhairdresser

import android.app.Application
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import pl.patryk.myhairdresser.data.firebase.FirebaseAuthHelper
import pl.patryk.myhairdresser.data.firebase.FirebaseDatabaseHelper
import pl.patryk.myhairdresser.data.firebase.FirebaseStorageHelper
import pl.patryk.myhairdresser.data.repository.UserRepository
import pl.patryk.myhairdresser.ui.admin.AdminViewModelFactory
import pl.patryk.myhairdresser.ui.appointments.UserAppointmentViewModelFactory
import pl.patryk.myhairdresser.ui.auth.AuthViewModelFactory
import pl.patryk.myhairdresser.ui.profile.UserProfileViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class FirebaseApplication : Application(), KodeinAware {

    val databaseFormatter = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
    val generalFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    override val kodein = Kodein.lazy {

        import(androidXModule(this@FirebaseApplication))

        FirebaseDatabaseHelper().database.setPersistenceEnabled(true)

        bind() from singleton { FirebaseAuthHelper() }
        bind() from singleton { FirebaseDatabaseHelper() }
        bind() from singleton { FirebaseStorageHelper() }
        bind() from singleton { UserRepository(instance(), instance(), instance()) }
        bind() from provider { AuthViewModelFactory(instance()) }
        bind() from provider { AdminViewModelFactory(instance()) }
        bind() from provider { UserProfileViewModelFactory(instance()) }
        bind() from provider { UserAppointmentViewModelFactory(instance()) }

    }
}