package ru.geekbrains.easynotes.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import ru.geekbrains.easynotes.R
import ru.geekbrains.easynotes.viewmodel.BaseViewModel

abstract class BaseActivity<T, VS : BaseViewState<T>> : AppCompatActivity() {

    abstract val ui: ViewBinding
    abstract val viewModel: BaseViewModel<T, VS>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        viewModel.getViewState().observe(this) { t ->
            t?.apply {
                data?.let { data -> renderData(data) }
                error?.let { error -> renderError(error) }
            }
        }
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