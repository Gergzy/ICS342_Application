package com.example.myapplicationpackage

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplicationpackage.ui.theme.My_Application_342Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        val apiService = RetrofitInstance.apiService
        val loginViewModel = LoginViewModel(sharedPreferencesHelper, apiService, this)
        val createAccountViewModel = CreateAccountViewModel(sharedPreferencesHelper, apiService, this)
        val mainViewModel = MainViewModel(sharedPreferencesHelper, apiService, this)

        enableEdgeToEdge()
        setContent {
            My_Application_342Theme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "LoginScreen") {
                    composable("LoginScreen") {
                        LoginScreen(
                            onLoginSuccess = { navController.navigate("ToDoListScreen") },
                            onCreateAccountClick = { navController.navigate("CreateAccountScreen") },
                            viewModel = loginViewModel)
                    }
                    composable("ToDoListScreen") {
                        mainViewModel.loadTodos(sharedPreferencesHelper)
                        ToDoListScreen(mainViewModel, sharedPreferencesHelper)
                    }
                    composable("CreateAccountScreen") {
                        CreateAccountScreen(
                            onCreateAccountSuccess = { navController.navigate("ToDoListScreen") },
                            onLoginClick = { navController.navigate("LoginScreen") },
                            viewModel = createAccountViewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoListScreen(viewModel: MainViewModel, sharedPreferences: SharedPreferencesHelper) {
    val context = LocalContext.current
    val toDoList by viewModel.todos.observeAsState()
    val sheetState = rememberModalBottomSheetState()
    var text by remember { mutableStateOf("") }
    var isSheetOpen by rememberSaveable { mutableStateOf(false) }
    val showError by viewModel.showError.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState("")


    if (showError) {
        AlertDialog(
            onDismissRequest = { viewModel.onShowErrorChange(false) },
            title = { Text(text = context.getString(R.string.error)) },
            text = { Text(text = errorMessage) },
            confirmButton = {
                TextButton(onClick = { viewModel.onShowErrorChange(false) }) {
                    Text(text = context.getString(R.string.okay))
                }
            }
        )
    }
    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { isSheetOpen = false; text = "" }
        ) {
            Column {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 32.dp,
                        end = 32.dp,
                        bottom = 32.dp
                    ),
                    contentAlignment = Alignment.TopCenter
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(context.getString(R.string.new_todo))},
                        value = text,
                        onValueChange = { text = it },
                        trailingIcon = {
                            Icon(imageVector = Icons.Filled.Clear , contentDescription = context.getString(R.string.clear_text))
                            IconButton(onClick = { text = ""}) {
                            }
                        }
                    )

                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 32.dp,
                        end = 32.dp,
                    ),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Button(
                        onClick = {
                            if (text!= "") {
                                val todoitem = TodoItem("", text, false)
                                viewModel.addTodo(todoitem,sharedPreferences)
                                text = ""
                                isSheetOpen = false
                            } else {
                                viewModel.onErrorMessage(context.getString(R.string.blank_error_message))
                                viewModel.onShowErrorChange(true)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    {
                        Text(context.getString(R.string.save))
                    }
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 32.dp,
                        end = 32.dp,
                        top = 16.dp,
                        bottom = 128.dp
                    ),
                    contentAlignment = Alignment.TopCenter
                ) {
                    OutlinedButton(
                        onClick = { isSheetOpen = false
                            text = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    {
                        Text(context.getString(R.string.cancel))
                    }
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .padding(bottom = 16.dp),
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Box(modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {Text(context.getString(R.string.todo))}
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                isSheetOpen = true
            }) {
                Icon(Icons.Default.Add, contentDescription = context.getString(R.string.add))
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            toDoList?.let {list ->
                items(list) { todo ->
                    ItemView(
                        content = todo,
                        onToggle = {
                            viewModel.updateCheckBox(todo, sharedPreferences)
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun ItemView(content: TodoItem, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 12.dp,
                end = 12.dp
            )
            .background(color = Color.LightGray),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = content.description,
            modifier = Modifier
                .padding(start = 8.dp)
        )
        Checkbox(
            checked = content.completed,
            onCheckedChange = {
                onToggle()})
    }
}
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onCreateAccountClick: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val showError by viewModel.showError.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState("")

    if (showError) {
        AlertDialog(
            onDismissRequest = { viewModel.onShowErrorChange(false) },
            title = { Text(text = context.getString(R.string.error)) },
            text = { Text(text = errorMessage) },
            confirmButton = {
                TextButton(onClick = { viewModel.onShowErrorChange(false) }) {
                    Text(text = context.getString(R.string.okay))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = { Text(text = context.getString(R.string.email)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text(text = context.getString(R.string.password)) },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    viewModel.login(
                        onSuccess = onLoginSuccess,
                        onError = { message ->
                            viewModel.onErrorMessage(message)
                            viewModel.onShowErrorChange(true)
                        }
                    )
                } else {
                    viewModel.onErrorMessage(context.getString(R.string.empty_fields_error))
                    viewModel.onShowErrorChange(true)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = context.getString(R.string.login_button))
        }
        TextButton(
            onClick = onCreateAccountClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(text = context.getString(R.string.create_account_button))
        }
    }
}

@Composable
fun CreateAccountScreen(
    onCreateAccountSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: CreateAccountViewModel = viewModel()
) {
    val context = LocalContext.current
    val name by viewModel.name.observeAsState("")
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val showError by viewModel.showError.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState("")
    val showSuccess by viewModel.showSuccess.observeAsState(false)

    if (showError) {
        AlertDialog(
            onDismissRequest = { viewModel.onShowErrorChange(false) },
            title = { Text(text = context.getString(R.string.error)) },
            text = { Text(text = errorMessage) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text(text = context.getString(R.string.okay))
                }
            }
        )
    }

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { viewModel.clearSuccess() },
            title = { Text(text = context.getString(R.string.success)) },
            text = { Text(text = context.getString(R.string.account_creation_success)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearSuccess()
                    onCreateAccountSuccess()
                }) {
                    Text(text = context.getString(R.string.okay))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = {viewModel.onNameChange(it)},
            label = { Text(text = context.getString(R.string.name))},
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = { Text(text = context.getString(R.string.email)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text(text = context.getString(R.string.password)) },
            modifier = Modifier.fillMaxWidth(),
        )
        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank() && name.isNotBlank()) {
                    viewModel.createAccount(
                        onSuccess = { viewModel.onShowSuccessChange(true) },
                        onError = { message ->
                            viewModel.onErrorMessage(message)
                        }
                    )
                } else {
                    viewModel.onErrorMessage(context.getString(R.string.create_account_empty_fields_error))
                    viewModel.onShowErrorChange(true)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = context.getString(R.string.create_account_button))
        }
        TextButton(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(text = context.getString(R.string.login_button))
        }
    }
}







