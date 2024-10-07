package org.omsi.demoproject.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import demoproject.composeapp.generated.resources.Res
import demoproject.composeapp.generated.resources.app_name
import demoproject.composeapp.generated.resources.back_button
import demoproject.composeapp.generated.resources.title_parameters
import demoproject.composeapp.generated.resources.title_settings
import demoproject.composeapp.generated.resources.title_summary
import io.kamel.core.DataSource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.omsi.demoproject.viewmodel.MainViewModel
import org.koin.compose.koinInject



/**
 * enum values that represent the screens in the app--> Lists Routes
 */
enum class AppScreen(val title: StringResource) {
    Start(title = Res.string.app_name),
    Parameter(title = Res.string.title_parameters),
    Settings(title = Res.string.title_settings),
    Summary(title = Res.string.title_summary)
}

@Composable
fun TestApp(/*viewModel: MainViewModel = MainViewModel(), */navController: NavHostController = rememberNavController()){
    val viewModel: MainViewModel= koinInject()
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.Start.name
    )

    Scaffold(
        topBar = {
            MainAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        //  val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = AppScreen.Start.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = AppScreen.Start.name) {
                HomeScreen(navigateTo = {destination->
                    navController.navigate(destination)
                })
            }

            composable(route = AppScreen.Parameter.name) {
                ParameterScreen()
            }
            composable(route = AppScreen.Settings.name) {
                SettingScreen()
            }

            composable(route = AppScreen.Summary.name) {
                SettingScreen()
            }
        }
    }

}





/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar(
    currentScreen: AppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.back_button)
                    )
                }
            }
        }
    )
}
