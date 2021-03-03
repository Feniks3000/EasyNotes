package ru.geekbrains.easynotes.viewmodel

import kotlinx.coroutines.launch
import ru.geekbrains.easynotes.exceptions.NoAuthException
import ru.geekbrains.easynotes.model.Repository

class SplashViewModel(private val repository: Repository) : BaseViewModel<Boolean>() {

    fun requestUser() {
        launch {
            repository.getCurrentUser()?.let { setData(true) }
                ?: setError(NoAuthException())
        }
    }
}