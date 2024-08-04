package com.example.myapplicationpackage

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(sharedPreferences: SharedPreferencesHelper, private val apiService: ApiService, private val context: Context) : ViewModel() {
    private val apiKey = "a37f8036-f0d2-42f0-9300-d2c9cbdbe723"

    private val _todos = MutableLiveData<List<TodoItem>>()
    val todos: LiveData<List<TodoItem>> = _todos

    private val _showError = MutableLiveData(false)
    val showError: LiveData<Boolean> = _showError

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    fun onShowErrorChange(showErrorChange: Boolean) {
        _showError.value = showErrorChange
    }
    fun onErrorMessage(showErrorMessage: String) {
        _errorMessage.value = showErrorMessage
    }

    fun loadTodos(sharedPreferences: SharedPreferencesHelper) {
        viewModelScope.launch {
            try {
                val bToken = "Bearer " + sharedPreferences.getUserToken()
                val userID = sharedPreferences.getUserID()
                val todos = apiService.getUserTodos(bToken, userID!!, apiKey)
                val todoList = todos.map { item ->
                    val completed = item.completed == 1
                    TodoItem(item.id, item.description, completed)
                }
                _todos.value = todoList
            } catch (e: Exception) {
                _errorMessage.value = context.getString(R.string.failedLoadTodos) + "${e.message}"
                _showError.value = true
            }
        }
    }
    fun addTodo(todo: TodoItem,sharedPreferences: SharedPreferencesHelper) {
        viewModelScope.launch {
            try {
                val bToken = "Bearer " + sharedPreferences.getUserToken()
                val userID = sharedPreferences.getUserID()
                val response = apiService.createUserTodo(bToken, userID!!, apiKey, todo)
                val newList = _todos.value.orEmpty() + response
                _todos.value = newList
            } catch (e: Exception) {
                _errorMessage.value = context.getString(R.string.failedAddTodo) + "${e.message}"
                _showError.value = true
            }
        }
    }

    fun updateCheckBox(todo: TodoItem, sharedPreferences: SharedPreferencesHelper) {
        viewModelScope.launch {
            try {
                val bToken = "Bearer " + sharedPreferences.getUserToken()
                val userID = sharedPreferences.getUserID()
                val todoID = todo.id
                val updatedCompleted = !todo.completed
                apiService.updateUserTodo(userID!!, todoID, apiKey, bToken, todo.copy(completed = updatedCompleted))
                _todos.value = _todos.value?.map { item ->
                    if (item.id == todoID) item.copy(completed = updatedCompleted) else item
                }
            } catch (e: Exception) {
                _errorMessage.value = context.getString(R.string.failedUpdatedCheck) + "${e.message}"
                _showError.value = true
            }
        }
    }
}
class LoginViewModel(private var sharedPreferences: SharedPreferencesHelper, private val apiService: ApiService, private val context: Context ) : ViewModel() {
    private val apiKey = "a37f8036-f0d2-42f0-9300-d2c9cbdbe723"

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _showError = MutableLiveData(false)
    val showError: LiveData<Boolean> = _showError

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }
    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }
    fun onShowErrorChange(showErrorChange: Boolean) {
        _showError.value = showErrorChange
    }
    fun onErrorMessage(showErrorMessage: String) {
        _errorMessage.value = showErrorMessage
    }

    fun login(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (_email.value.isNullOrBlank() || _password.value.isNullOrBlank()) {
            _errorMessage.value = context.getString(R.string.empty_fields_error)
            _showError.value = true
            return
        }

        viewModelScope.launch {
            try {
                val response = apiService.loginUser(apiKey, User("", _email.value!!, _password.value!!))
                sharedPreferences.saveUserID(response.userId)
                sharedPreferences.saveUserToken(response.token)
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = context.getString(R.string.loginFailed)
                _showError.value = true
                onError(context.getString(R.string.loginFailed))
            }
        }
    }
}
class CreateAccountViewModel(var SharedPreferences: SharedPreferencesHelper, private val apiService: ApiService, private val context: Context) : ViewModel() {
    private val apiKey = "a37f8036-f0d2-42f0-9300-d2c9cbdbe723"

    private val _name = MutableLiveData("")
    val name: LiveData<String> = _name

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _showError = MutableLiveData(false)
    val showError: LiveData<Boolean> = _showError

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    private val _showSuccess = MutableLiveData(false)
    val showSuccess: LiveData<Boolean> = _showSuccess

    fun onNameChange(newName: String) {
        _name.value = newName
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onShowErrorChange(showErrorChange: Boolean) {
        _showError.value = showErrorChange
    }

    fun onErrorMessage(showErrorMessage: String) {
        _errorMessage.value = showErrorMessage
    }

    fun onShowSuccessChange(showSuccessChange: Boolean) {
        _showSuccess.value = showSuccessChange
    }

    fun clearError() {
        _showError.value = false
    }

    fun clearSuccess() {
        _showSuccess.value = false
    }

    fun createAccount(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (_email.value.isNullOrBlank() || _password.value.isNullOrBlank() || _name.value.isNullOrBlank()) {
            _errorMessage.value = context.getString(R.string.create_account_empty_fields_error)
            _showError.value = true
            return
        }

        viewModelScope.launch {
            try {
                val response = apiService.registerUser(apiKey, User(_name.value!!,_email.value!!, _password.value!!))
                SharedPreferences.saveUserID(response.userId)
                SharedPreferences.saveUserToken(response.token)
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = context.getString(R.string.account_creation_error)+"${e.message}"
                _showError.value = true
                onError(context.getString(R.string.account_creation_error)+"${e.message}")
            }
        }
    }
}