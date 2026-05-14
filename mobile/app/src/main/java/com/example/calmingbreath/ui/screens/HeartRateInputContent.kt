package com.example.calmingbreath.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calmingbreath.R

@Composable
fun HeartRateInputContent(
    title: String,
    subtitle: String,
    buttonText: String,
    onExerciseButtonClick: () -> Unit,
    onAddBpmButtonClick: (Int) -> Unit,
) {
    val context = LocalContext.current

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = subtitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val state = rememberTextFieldState()

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        state = state,
                        label = { Text("Heart rate (bpm)") },
                        lineLimits = TextFieldLineLimits.SingleLine,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    IconButton(onClick = {
                        val bpm = checkInputBpm(state.text.toString())

                        if (bpm != null) {
                            onAddBpmButtonClick(bpm)
                        } else {
                            showToast(context, context.getString(R.string.incorrect_input_data))
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.AddTask,
                            contentDescription = null
                        )
                    }
                }

                Spacer(Modifier.height(5.dp))

                Text(
                    text = "Normal values are in 60..90 bpm",
                    fontWeight = FontWeight.Light
                )
            }

            Spacer(Modifier.height(55.dp))

            Button(
                onClick = onExerciseButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp)
            ) {
                Text(text = buttonText)
            }
        }
    }
}

fun checkInputBpm(str: String): Int? {
    val value = try {
        str.toInt()
    } catch (e: Exception) {
        null
    }

    if (value != null) {
        if (value in 40..200)
            return value
    }
    return null
}

fun showToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}