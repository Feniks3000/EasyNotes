package ru.geekbrains.easynotes.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import ru.geekbrains.easynotes.viewmodel.BaseViewModel

abstract class BaseActivity<T, VS : BaseViewState<T>> : AppCompatActivity() {

    abstract val ui: ViewBinding
    abstract val viewModel: BaseViewModel<T, VS>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        viewModel.getViewState().observe(this, object : Observer<VS> {
            override fun onChanged(t: VS) {
                if (t == null) return
                if (t.data != null) renderData(t.data)
                if (t.error != null) renderError(t.error)
            }
        })
    }

    protected fun renderError(error: Throwable) = error.message?.let { showError(error.message!!) }

    abstract fun renderData(data: T)

    fun showError(error: String) {
        val snackbar = Snackbar.make(ui.root, error, Snackbar.LENGTH_INDEFINITE)
        // TODO: Нужно инициировать дейтсвие?
        snackbar.show()
    }
}