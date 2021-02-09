package ru.geekbrains.easynotes.ui.splash

import ru.geekbrains.easynotes.ui.base.BaseViewState

class SplashViewState(isAuth: Boolean? = null, error: Throwable? = null) :
    BaseViewState<Boolean?>(isAuth, error)