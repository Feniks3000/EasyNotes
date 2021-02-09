package ru.geekbrains.easynotes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.geekbrains.easynotes.ui.base.BaseViewState

open class BaseViewModel<T, VS : BaseViewState<T>> : ViewModel() {
    open val viewStateLiveData = MutableLiveData<VS>()

    open fun getViewState(): LiveData<VS> = viewStateLiveData
}