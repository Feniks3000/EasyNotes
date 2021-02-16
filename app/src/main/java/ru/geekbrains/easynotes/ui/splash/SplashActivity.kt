package ru.geekbrains.easynotes.ui.splash

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.firebase.ui.auth.AuthUI
import org.koin.android.viewmodel.ext.android.viewModel
import ru.geekbrains.easynotes.R
import ru.geekbrains.easynotes.databinding.ActivitySplashBinding
import ru.geekbrains.easynotes.exceptions.NoAuthException
import ru.geekbrains.easynotes.ui.main.MainActivity
import ru.geekbrains.easynotes.ui.base.BaseActivity
import ru.geekbrains.easynotes.viewmodel.SplashViewModel

private const val RC_SIGN_IN = 460
private const val START_DELAY = 1000L

class SplashActivity : BaseActivity<Boolean?, SplashViewState>() {

    private val CLASS = "${SplashActivity::class.java.simpleName}"

    override val ui: ActivitySplashBinding by lazy { ActivitySplashBinding.inflate(layoutInflater) }
    override val viewModel: SplashViewModel by viewModel()

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({ viewModel.requestUser() }, START_DELAY)
    }

    override fun renderData(data: Boolean?) {
        data?.takeIf { it }?.let { startMainActivity() }
    }

    override fun renderError(error: Throwable) = when (error) {
        is NoAuthException -> {
            startLoginActivity()
        }
        else -> error.message?.let {
            showError(it)
        }
    }

    private fun startLoginActivity() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.drawable.common_google_signin_btn_icon_dark_normal)
                .setTheme(R.style.LoginStyle)
                .setAvailableProviders(
                    listOf(
                        AuthUI.IdpConfig.AnonymousBuilder().build(),
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        AuthUI.IdpConfig.GoogleBuilder().build()
                    )
                )
                .build(),
            RC_SIGN_IN
        )
    }

    private fun startMainActivity() {
        startActivity(MainActivity.getStartIntent(this))
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode != Activity.RESULT_OK) {
            finish()
        }
    }
}