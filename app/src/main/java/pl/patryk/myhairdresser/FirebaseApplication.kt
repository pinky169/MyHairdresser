package pl.patryk.myhairdresser

import android.app.Application
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import pl.patryk.myhairdresser.data.firebase.FirebaseDatabase
import pl.patryk.myhairdresser.data.firebase.FirebaseSource
import pl.patryk.myhairdresser.data.repository.UserRepository
import pl.patryk.myhairdresser.ui.auth.AuthViewModelFactory
import pl.patryk.myhairdresser.ui.profile.UserProfileViewModelFactory

class FirebaseApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@FirebaseApplication))

        bind() from singleton { FirebaseSource() }
        bind() from singleton { FirebaseDatabase() }
        bind() from singleton { UserRepository(instance(), instance()) }
        bind() from provider { AuthViewModelFactory(instance()) }
        bind() from provider { UserProfileViewModelFactory(instance()) }

    }
}