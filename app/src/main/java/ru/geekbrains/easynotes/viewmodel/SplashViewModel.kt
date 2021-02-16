package ru.geekbrains.easynotes.viewmodel

import ru.geekbrains.easynotes.exceptions.NoAuthException
import ru.geekbrains.easynotes.model.Repository
import ru.geekbrains.easynotes.ui.splash.SplashViewState

class SplashViewModel(private val repository: Repository) :
    BaseViewModel<Boolean?, SplashViewState>() {

    fun requestUser() {
        repository.getCurrentUser().observeForever { user ->
            viewStateLiveData.value = user?.let {
                SplashViewState(isAuth = true)
            } ?: SplashViewState(error = NoAuthException())
        }
    }

}