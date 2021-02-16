package ru.geekbrains.easynotes.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import ru.geekbrains.easynotes.R
import ru.geekbrains.easynotes.viewmodel.BaseViewModel
import kotlin.coroutines.CoroutineContext

abstract class BaseActivity<T> : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.Main + Job() }

    abstract val ui: ViewBinding
    abstract val viewModel: BaseViewModel<T>

    private lateinit var dataJob: Job
    private lateinit var errorJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)
    }

    override fun onStart() {
        dataJob = launch {
            viewModel.getViewState().consumeEach { renderData(it) }
        }

        errorJob = launch {
            viewModel.getErrorChannel().consumeEach { renderError(it) }
        }
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        dataJob.cancel()
        errorJob.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }

    protected open fun renderError(error: Throwable) = error.message?.let { showError(it) }

    abstract fun renderData(data: T)

    protected open fun showError(error: String) {
        Snackbar.make(ui.root, error, Snackbar.LENGTH_INDEFINITE).apply {
            setAction(R.string.snackbar_action) { dismiss() }
            show()
        }
    }
}