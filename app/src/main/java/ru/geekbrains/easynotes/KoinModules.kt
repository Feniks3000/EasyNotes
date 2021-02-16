package ru.geekbrains.easynotes

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.geekbrains.easynotes.model.FirebaseStoreProvider
import ru.geekbrains.easynotes.model.RemoteDataProvider
import ru.geekbrains.easynotes.model.Repository
import ru.geekbrains.easynotes.viewmodel.MainViewModel
import ru.geekbrains.easynotes.viewmodel.NoteViewModel
import ru.geekbrains.easynotes.viewmodel.SplashViewModel

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStoreProvider(get(), get()) } bind RemoteDataProvider::class
    single { Repository(get()) }
}

val splashModule = module { viewModel { SplashViewModel(get()) } }

val mainModule = module { viewModel { MainViewModel(get()) } }

val noteModule = module { viewModel { NoteViewModel(get()) } }

