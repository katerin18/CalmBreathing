package com.example.calmingbreath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calmingbreath.data.ExerciseDatabase
import com.example.calmingbreath.ui.navigation.ExerciseScreen
import com.example.calmingbreath.ui.navigation.FirstHeartRateInputScreen
import com.example.calmingbreath.ui.navigation.HistoryScreen
import com.example.calmingbreath.ui.navigation.LoginRoute
import com.example.calmingbreath.ui.navigation.RegisterRoute
import com.example.calmingbreath.ui.navigation.ResultsScreen
import com.example.calmingbreath.ui.navigation.SecondHeartRateInputScreen
import com.example.calmingbreath.ui.screens.ExerciseScreenView
import com.example.calmingbreath.ui.screens.FirstHeartRateInputScreenView
import com.example.calmingbreath.ui.screens.HistoryScreenView
import com.example.calmingbreath.ui.screens.LoginScreen
import com.example.calmingbreath.ui.screens.RegisterScreen
import com.example.calmingbreath.ui.screens.ResultsScreenView
import com.example.calmingbreath.ui.screens.SecondHeartRateInputScreenView
import com.example.calmingbreath.ui.viewmodel.ExerciseViewModel
import com.example.calmingbreath.ui.viewmodel.HeartRateInputViewModel
import com.example.calmingbreath.ui.viewmodel.HeartRateInputViewModelFactory
import com.example.calmingbreath.ui.viewmodel.HistoryViewModel
import com.example.calmingbreath.ui.viewmodel.HistoryViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = ExerciseDatabase.getDatabase(applicationContext)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            val heartRateViewModel: HeartRateInputViewModel = viewModel(
                factory = HeartRateInputViewModelFactory(db.exerciseDao(), applicationContext)
            )

            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(applicationContext)
            )

            // Авто-вход: если refresh-токен сохранён с прошлого сеанса — стартуем сразу с главного экрана.
            val tokenManager = remember { TokenManager(applicationContext) }
            val startDestination: Any = remember {
                if (tokenManager.getRefreshToken() != null) FirstHeartRateInputScreen else LoginRoute
            }

            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable<LoginRoute> {
                    val authState by authViewModel.state.collectAsState()

                    LaunchedEffect(authState.isAuthenticated) {
                        if (authState.isAuthenticated) {
                            navController.navigate(FirstHeartRateInputScreen) {
                                popUpTo(LoginRoute) { inclusive = true }
                            }
                        }
                    }

                    LoginScreen(
                        state = authState,
                        onAction = { action ->
                            when (action) {
                                AuthAction.NavigateToRegister ->
                                    navController.navigate(RegisterRoute)
                                else -> authViewModel.onAction(action)
                            }
                        }
                    )
                }

                composable<RegisterRoute> {
                    val authState by authViewModel.state.collectAsState()

                    LaunchedEffect(authState.isAuthenticated) {
                        if (authState.isAuthenticated) {
                            navController.navigate(FirstHeartRateInputScreen) {
                                popUpTo(LoginRoute) { inclusive = true }
                            }
                        }
                    }

                    RegisterScreen(
                        state = authState,
                        onAction = { action ->
                            when (action) {
                                AuthAction.NavigateToLogin -> navController.popBackStack()
                                else -> authViewModel.onAction(action)
                            }
                        }
                    )
                }

                composable<FirstHeartRateInputScreen> {
                    val authState by authViewModel.state.collectAsState()
                    FirstHeartRateInputScreenView(
                        viewModel = heartRateViewModel,
                        userName = authState.user?.firstName,
                        onNavigateToExercise = {
                            navController.navigate(ExerciseScreen)
                        },
                        onBack = { navController.popBackStack() },
                        onViewHistory = { navController.navigate(HistoryScreen) },
                        onLogout = {
                            authViewModel.onAction(AuthAction.LogoutClicked)
                            navController.navigate(LoginRoute) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable<ExerciseScreen> {
                    val exerciseViewModel: ExerciseViewModel = viewModel()
                    ExerciseScreenView(
                        viewModel = exerciseViewModel,
                        onNavigateNext = {
                            heartRateViewModel.onExerciseDone()
                            navController.navigate(SecondHeartRateInputScreen)
                        }
                    )
                }

                composable<SecondHeartRateInputScreen> {
                    SecondHeartRateInputScreenView(
                        viewModel = heartRateViewModel,
                        onNavigateToResults = {
                            navController.navigate(ResultsScreen)
                        }
                    )
                }

                composable<ResultsScreen> {
                    ResultsScreenView(
                        viewModel = heartRateViewModel,
                        onNavigateHome = {
                            navController.navigate(FirstHeartRateInputScreen) {
                                popUpTo(FirstHeartRateInputScreen) { inclusive = true }
                            }
                        },
                        onViewHistory = { navController.navigate(HistoryScreen) }
                    )
                }

                composable<HistoryScreen> {
                    val historyViewModel: HistoryViewModel = viewModel(
                        factory = HistoryViewModelFactory(applicationContext)
                    )
                    HistoryScreenView(
                        viewModel = historyViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}