package com.example.calmingbreath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calmingbreath.data.ExerciseDatabase
import com.example.calmingbreath.ui.navigation.ExerciseScreen
import com.example.calmingbreath.ui.navigation.FirstHeartRateInputScreen
import com.example.calmingbreath.ui.navigation.ResultsScreen
import com.example.calmingbreath.ui.navigation.SecondHeartRateInputScreen
import com.example.calmingbreath.ui.screens.ExerciseScreenView
import com.example.calmingbreath.ui.screens.FirstHeartRateInputScreenView
import com.example.calmingbreath.ui.screens.ResultsScreenView
import com.example.calmingbreath.ui.screens.SecondHeartRateInputScreenView
import com.example.calmingbreath.ui.viewmodel.ExerciseViewModel
import com.example.calmingbreath.ui.viewmodel.HeartRateInputViewModel
import com.example.calmingbreath.ui.viewmodel.HeartRateInputViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = ExerciseDatabase.getDatabase(applicationContext)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            val heartRateViewModel: HeartRateInputViewModel = viewModel(
                factory = HeartRateInputViewModelFactory(db.exerciseDao())
            )

            NavHost(
                navController = navController,
                startDestination = FirstHeartRateInputScreen
            ) {
                composable<FirstHeartRateInputScreen> {
                    FirstHeartRateInputScreenView(
                        viewModel = heartRateViewModel,
                        onNavigateToExercise = {
                            navController.navigate(ExerciseScreen)
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
                        viewModel = heartRateViewModel
                    )
                }
            }
        }
    }
}