package ru.geekbrains.easynotes.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import ru.geekbrains.easynotes.R
import ru.geekbrains.easynotes.databinding.ActivitySplashBinding
import ru.geekbrains.easynotes.exceptions.NoAuthException
import ru.geekbrains.easynotes.model.FirebaseStoreProvider
import ru.geekbrains.easynotes.viewmodel.SplashViewModel

private const val RC_SIGN_IN = 458
private const val START_DELAY = 2000L

class SplashActivity : BaseActivity<Boolean?, SplashViewState>() {

    private val CLASS = "${SplashActivity::class.java.simpleName}"

    override val ui: ActivitySplashBinding by lazy { ActivitySplashBinding.inflate(layoutInflater) }
    override val viewModel: SplashViewModel by lazy { ViewModelProvider(this).get(SplashViewModel::class.java) }

    override fun onResume() {
        Log.d(CLASS, "onResume")
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({ viewModel.requestUser() }, START_DELAY)
    }

    override fun renderData(data: Boolean?) {
        Log.d(CLASS, "renderData")
        data?.takeIf { it }?.let { startMainActivity() }
    }

    override fun renderError(error: Throwable) =
        when (error) {
            is NoAuthException -> {
                Log.d(CLASS, "renderError")
                startLoginActivity()
            }
            else -> error.message?.let {
                Log.d(CLASS, "renderError")
                showError(it)
            }
        }

    private fun startLoginActivity() {
        Log.d(CLASS, "startLoginActivity")

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
        Log.d(CLASS, "startMainActivity")
        MainActivity.getStartIntent(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(CLASS, "onActivityResult")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode != Activity.RESULT_OK) {
            finish()
        }
    }
}