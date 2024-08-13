package com.example.myapplicationpackage


import android.content.Context
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(CoroutinesTestExtension::class, InstantExecutorExtension::class)
class CreateAccountViewModelTest {

    @Test
    fun createAccount_should_show_error_when_fields_are_empty() {
        val sharedPreferencesHelper = mockk<SharedPreferencesHelper>(relaxed = true)
        val apiService = mockk<ApiService>()
        val context = mockk<Context>(relaxed = true)
        val onSuccess = mockk<() -> Unit>(relaxed = true)
        val onError = mockk<(String) -> Unit>(relaxed = true)
        val viewModel = CreateAccountViewModel(sharedPreferencesHelper, apiService, context)
        every { context.getString(R.string.create_account_empty_fields_error) } returns "Fields cannot be empty"
        viewModel.createAccount(onSuccess, onError)
        assertEquals("Fields cannot be empty", viewModel.errorMessage.value)
        assertEquals(true, viewModel.showError.value)
        coVerify(exactly = 0) { apiService.registerUser(any(), any()) }
    }

    @Test
    fun createAccount_should_call_apiService_and_save_data_on_success() {
        val sharedPreferencesHelper = mockk<SharedPreferencesHelper>(relaxed = true)
        val apiService = mockk<ApiService>()
        val context = mockk<Context>(relaxed = true)
        val viewModel = CreateAccountViewModel(sharedPreferencesHelper, apiService, context)
        val userId = "12345"
        val token = "token123"
        val user = User("John Doe", "johndoe@example.com", "password123")
        val response = UserResponse(token, userId)
        coEvery { apiService.registerUser(any(), any()) } returns response
        every { context.getString(R.string.account_creation_error) } returns "Account creation failed"
        viewModel.onNameChange(user.name)
        viewModel.onEmailChange(user.email)
        viewModel.onPasswordChange(user.password)
        viewModel.createAccount(
            onSuccess = { viewModel.onShowSuccessChange(true) },
            onError = { message ->
                viewModel.onErrorMessage(message) })
        coVerify { apiService.registerUser(any(), user) }
        verify { sharedPreferencesHelper.saveUserID(userId) }
        verify { sharedPreferencesHelper.saveUserToken(token) }
        assertEquals(true, viewModel.showSuccess.value)
        assertEquals(false, viewModel.showError.value)
    }

}