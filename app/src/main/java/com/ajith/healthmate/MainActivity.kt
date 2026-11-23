package com.ajith.healthmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.ajith.healthmate.data.HealthDatabase
import com.ajith.healthmate.data.HealthRecord
import com.ajith.healthmate.data.HealthRepository
import com.ajith.healthmate.ui.screens.AddEditScreen
import com.ajith.healthmate.ui.screens.DashboardScreen
import com.ajith.healthmate.ui.screens.RecordListScreen
import com.ajith.healthmate.ui.theme.HealthMateTheme
import com.ajith.healthmate.viewmodel.HealthViewModel
import kotlinx.coroutines.CancellationException

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = HealthDatabase.getDatabase(applicationContext)
        val repository = HealthRepository(database.healthRecordDao())

        val factory = viewModelFactory {
            initializer {
                HealthViewModel(repository)
            }
        }

        setContent {
            HealthMateTheme {
                val healthViewModel: HealthViewModel =
                    viewModel(factory = factory)

                HealthMateApp(viewModel = healthViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HealthMateApp(viewModel: HealthViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route

    val isForm = route == "add" || route == "edit/{recordId}"
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()
    val saveCompleted by viewModel.saveCompleted.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.messages.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(saveCompleted, route) {
        if (saveCompleted && route != null) {
            if (isForm) {
                navController.popBackStack()
            }
            viewModel.consumeSaveCompleted()
        }
    }

    val openAdd: () -> Unit = {
        if (!isSaving) {
            navController.navigate("add") {
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("HealthMate")
                },
                navigationIcon = {
                    if (isForm) {
                        IconButton(
                            enabled = !isSaving,
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (!isForm) {
                NavigationBar {
                    NavigationBarItem(
                        selected = route == "dashboard",
                        enabled = !isSaving,
                        onClick = {
                            navController.openTab("dashboard")
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text("Dashboard")
                        }
                    )

                    NavigationBarItem(
                        selected = route == "records",
                        enabled = !isSaving,
                        onClick = {
                            navController.openTab("records")
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text("Records")
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (!isForm && !isSaving) {
                FloatingActionButton(
                    onClick = openAdd
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add health record"
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            composable("dashboard") {
                DashboardScreen(
                    viewModel = viewModel,
                    onAddRecord = openAdd
                )
            }

            composable("records") {
                RecordListScreen(
                    viewModel = viewModel,
                    onEdit = { recordId ->
                        navController.navigate("edit/$recordId") {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable("add") {
                AddEditScreen(
                    onSave = { record ->
                        viewModel.addRecord(record)
                    },
                    onCancel = {
                        navController.popBackStack()
                    },
                    isSaving = isSaving
                )
            }

            composable(
                route = "edit/{recordId}",
                arguments = listOf(
                    navArgument("recordId") {
                        type = NavType.IntType
                    }
                )
            ) { entry ->
                val recordId = entry.arguments?.getInt("recordId") ?: 0

                EditRecordRoute(
                    viewModel = viewModel,
                    recordId = recordId,
                    isSaving = isSaving,
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }

    BackHandler(enabled = isForm && isSaving) {
        // Wait for the active save to finish.
    }
}

@Composable
private fun EditRecordRoute(
    viewModel: HealthViewModel,
    recordId: Int,
    isSaving: Boolean,
    onCancel: () -> Unit
) {
    var record by remember(recordId) {
        mutableStateOf<HealthRecord?>(null)
    }

    var loading by remember(recordId) {
        mutableStateOf(true)
    }

    LaunchedEffect(recordId, viewModel) {
        try {
            record = viewModel.getRecord(recordId)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            record = null
        } finally {
            loading = false
        }
    }

    when {
        loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        record == null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Unable to load this record.")

                TextButton(onClick = onCancel) {
                    Text("Go back")
                }
            }
        }

        else -> {
            AddEditScreen(
                record = record,
                isSaving = isSaving,
                onSave = { updatedRecord ->
                    viewModel.updateRecord(updatedRecord)
                },
                onCancel = onCancel
            )
        }
    }
}

private fun NavHostController.openTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}