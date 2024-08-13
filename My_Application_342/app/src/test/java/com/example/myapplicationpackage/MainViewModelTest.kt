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
class MainViewModelTest {
    @Test
    fun loadTodos_should_show_success()  {
        val sharedPreferencesHelper = mockk<SharedPreferencesHelper>(relaxed = true)
        val apiService = mockk<ApiService>()
        val context = mockk<Context>(relaxed = true)
        val viewModel = MainViewModel(sharedPreferencesHelper, apiService, context)
        val todos = listOf(TodoItem("1", "Test Todo", true))
        val todos2 = listOf(TodoItemReceiver("1", "Test Todo", 1))
        coEvery { sharedPreferencesHelper.getUserToken() } returns "token"
        coEvery { sharedPreferencesHelper.getUserID() } returns "12345"
        coEvery { apiService.getUserTodos(any(), any(), any()) } returns todos2
        viewModel.loadTodos(sharedPreferencesHelper)
        coVerify { apiService.getUserTodos(any(), any(), any()) }
        assertEquals(todos, viewModel.todos.value)
        assertEquals(false, viewModel.showError.value)
    }

    @Test
    fun loadTodos_should_show_failure() {
        val errorMessage = "Error"
        val sharedPreferencesHelper = mockk<SharedPreferencesHelper>(relaxed = true)
        val apiService = mockk<ApiService>()
        val context = mockk<Context>(relaxed = true)
        val viewModel = MainViewModel(sharedPreferencesHelper, apiService, context)
        every { context.getString(R.string.failedLoadTodos) } returns "Failed to load todos"
        coEvery { sharedPreferencesHelper.getUserToken() } returns "token"
        coEvery { sharedPreferencesHelper.getUserID() } returns "12345"
        coEvery { apiService.getUserTodos(any(), any(), any()) } throws Exception(errorMessage)
        viewModel.loadTodos(sharedPreferencesHelper)
        assertEquals("Failed to load todos$errorMessage", viewModel.errorMessage.value)
        assertEquals(true, viewModel.showError.value)
    }

    @Test
    fun addTodo_should_show_success() {
        val sharedPreferencesHelper = mockk<SharedPreferencesHelper>(relaxed = true)
        val apiService = mockk<ApiService>()
        val context = mockk<Context>(relaxed = true)
        val viewModel = MainViewModel(sharedPreferencesHelper, apiService, context)
        val todo = TodoItem("1", "Test Todo", false)
        coEvery { sharedPreferencesHelper.getUserToken() } returns "token"
        coEvery { sharedPreferencesHelper.getUserID() } returns "12345"
        coEvery { apiService.createUserTodo(any(), any(), any(), any()) } returns todo
        viewModel.addTodo(todo, sharedPreferencesHelper)
        coVerify { apiService.createUserTodo(any(), any(), any(), todo) }
        assertEquals(listOf(todo), viewModel.todos.value)
        assertEquals(false, viewModel.showError.value)

    }

    @Test
    fun addTodo_should_show_failure() {
        val errorMessage = "Error"
        val sharedPreferencesHelper = mockk<SharedPreferencesHelper>(relaxed = true)
        val apiService = mockk<ApiService>()
        val context = mockk<Context>(relaxed = true)
        val viewModel = MainViewModel(sharedPreferencesHelper, apiService, context)
        val todo = TodoItem("1", "Test Todo", false)
        every { context.getString(R.string.failedAddTodo) } returns "Failed to add todo"
        coEvery { sharedPreferencesHelper.getUserToken() } returns "token"
        coEvery { sharedPreferencesHelper.getUserID() } returns "12345"
        coEvery { apiService.createUserTodo(any(), any(), any(), any()) } throws Exception(errorMessage)
        viewModel.addTodo(todo, sharedPreferencesHelper)
        assertEquals("Failed to add todo$errorMessage", viewModel.errorMessage.value)
        assertEquals(true, viewModel.showError.value)
    }
    @Test
    fun updateCheckBox_should_not_show_failure() {
        val sharedPreferencesHelper = mockk<SharedPreferencesHelper>(relaxed = true)
        val apiService = mockk<ApiService>()
        val context = mockk<Context>(relaxed = true)
        val viewModel = MainViewModel(sharedPreferencesHelper, apiService, context)
        val todo = TodoItem("1", "Test Todo", false)
        coEvery { sharedPreferencesHelper.getUserToken() } returns "token"
        coEvery { sharedPreferencesHelper.getUserID() } returns "12345"
        coEvery { apiService.updateUserTodo(any(), any(), any(), any(), any()) } returns todo
        viewModel.updateCheckBox(todo, sharedPreferencesHelper)
        coVerify { apiService.updateUserTodo(any(), todo.id, any(), any(), todo.copy(completed = true)) }
        assertEquals(false, viewModel.showError.value)
    }

    @Test
    fun updateCheckbox_should_show_failure() {
        val errorMessage = "Error"
        val sharedPreferencesHelper = mockk<SharedPreferencesHelper>(relaxed = true)
        val apiService = mockk<ApiService>()
        val context = mockk<Context>(relaxed = true)
        val viewModel = MainViewModel(sharedPreferencesHelper, apiService, context)
        every { context.getString(R.string.failedUpdatedCheck) } returns "Failed to update todo"
        coEvery { sharedPreferencesHelper.getUserToken() } returns "token"
        coEvery { sharedPreferencesHelper.getUserID() } returns "12345"
        coEvery { apiService.updateUserTodo(any(), any(), any(), any(), any()) } throws Exception(errorMessage)
        val todo = TodoItem("1", "Test Todo", false)
        viewModel.updateCheckBox(todo, sharedPreferencesHelper)
        assertEquals("Failed to update todo$errorMessage", viewModel.errorMessage.value)
        assertEquals(true, viewModel.showError.value)
    }

}