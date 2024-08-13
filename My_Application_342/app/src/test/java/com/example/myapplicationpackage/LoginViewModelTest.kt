package com.example.myapplicationpackage
import android.content.Context
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(CoroutinesTestExtension::class, InstantExecutorExtension::class)
class LoginViewModelTest {
    @Test
    fun login_should_be_successful_with_correct_information() {
        val sharedPreferencesHelper = mockk<SharedPreferencesHelper>(relaxed = true)
        val apiService = mockk<ApiService>()
        val context = mockk<Context>(relaxed = true)
        val viewModel = LoginViewModel(sharedPreferencesHelper, apiService, context)
        val userId = "12345"
        val token = "token123"
        val user = User("John Doe", "johndoe@example.com", "password123")
        val response = UserResponse(token, userId)
        coEvery { apiService.loginUser(any(), any()) } returns response
        coEvery { apiService.loginUser(any(), any()) } returns response
        viewModel.onEmailChange(user.email)
        viewModel.onPasswordChange(user.password)
        viewModel.login(
            onSuccess = { viewModel.onShowSuccessChange(true) },
            onError = { message ->
                viewModel.onErrorMessage(message)
                viewModel.onShowErrorChange(true)
            })
        assertEquals(false, viewModel.showError.value)
        assertEquals(true, viewModel.showSuccess.value)
    }

    @Test
    fun createAccount_should_show_error_when_fields_are_empty() {
        val sharedPreferencesHelper = mockk<SharedPreferencesHelper>(relaxed = true)
        val apiService = mockk<ApiService>()
        val context = mockk<Context>(relaxed = true)
        val onSuccess = mockk<() -> Unit>(relaxed = true)
        val onError = mockk<(String) -> Unit>(relaxed = true)
        val viewModel = LoginViewModel(sharedPreferencesHelper, apiService, context)
        every { context.getString(R.string.empty_fields_error) } returns "Email and password need to be entered."
        viewModel.login(onSuccess, onError)
        assertEquals("Email and password need to be entered.", viewModel.errorMessage.value)
        assertEquals(true, viewModel.showError.value)
        assertEquals(false, viewModel.showSuccess.value)
        coVerify(exactly = 0) { apiService.registerUser(any(), any()) }
    }
}