package ru.geekbrains.easynotes.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.geekbrains.easynotes.exceptions.NoAuthException
import ru.geekbrains.easynotes.model.Repository
import ru.geekbrains.easynotes.model.User

class SplashViewModelTest {

    @get:Rule   // Правило для использования LiveData
    val taskExecuteRule = InstantTaskExecutorRule()

    private val mockRepository = mockk<Repository>()
    private val userLiveData = MutableLiveData<User>()
    private lateinit var viewModel: SplashViewModel

    private val user = User("user", "")

    @Before
    fun setUp() {
        every { mockRepository.getCurrentUser() } returns userLiveData
        viewModel = SplashViewModel(mockRepository)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `should requestUser return User`() {
        var result: Boolean? = null

        viewModel.getViewState().observeForever {
            result = it?.data
        }
        viewModel.requestUser()
        userLiveData.value = user

        assertTrue("Для пользователя state с isAuth!=true", result!!)
    }

    @Test
    fun `should requestUser return NoAuthException`() {
        var result: Throwable? = null

        viewModel.getViewState().observeForever {
            result = it?.error
        }
        viewModel.requestUser()
        userLiveData.value = null

        assertTrue("Для null-пользователя нет исключения", result is NoAuthException)
    }

}