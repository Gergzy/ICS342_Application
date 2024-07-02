package com.example.myapplicationpackage

import android.os.Bundle
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp
import com.example.myapplicationpackage.ui.theme.My_Application_342Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            My_Application_342Theme {
                ScaffoldExample()
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldExample() {
    val toDoList = remember {
        mutableStateListOf<String>()
    }
    val sheetState = rememberModalBottomSheetState()
    var text by remember { mutableStateOf("") }
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var showAlertDialog by remember {
        mutableStateOf(false)
    }

    if (showAlertDialog) {
        ExampleAlertDialog(
            onCancel = {showAlertDialog = false},
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
                        label = { Text("New Todo")},
                        value = text,
                        onValueChange = { text = it },
                        trailingIcon = {
                            Icon(imageVector = Icons.Filled.Clear , contentDescription = "Clear Text")
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
                                toDoList.add(text)
                                text = ""
                                isSheetOpen = false
                            } else {
                                showAlertDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    {
                        Text("Save")
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
                        Text("Cancel")
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
                    ) {Text("Todo")}
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                isSheetOpen = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            items(toDoList) {item ->
                ItemView(content = item)
            }
        }
    }
}


@Composable
fun ItemView(content: String) {
    val viewModel: MainViewModel = viewModel()
    val checkedState = viewModel.currentCheckState.observeAsState(false)
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
        Text(text = content,
            modifier = Modifier
                .padding(start = 8.dp)
            )
        Checkbox(checked = checkedState.value, onCheckedChange = {viewModel.onChange(it)})
    }
}

@Composable
fun ExampleAlertDialog(
    onCancel: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {  },
        confirmButton = {
            TextButton(onClick = onCancel) {
                Text(text = "Okay")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = "Cancel")
            }
        },
        title = {
            Text(text = "Error")
        },
        text = {
            Text(text = "An entry on the to do list can not be blank.")
        }
    )
}