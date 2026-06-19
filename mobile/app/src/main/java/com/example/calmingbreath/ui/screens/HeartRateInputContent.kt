package com.example.calmingbreath.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calmingbreath.R

private val TitleColor = Color(0xFF2C2C2A)
private val SubtitleColor = Color(0xFF6B6B66)
private val RoseCircle = Color(0xFFEAD9DC)
private val RoseHeart = Color(0xFFB98A95)
private val InfoBoxBg = Color(0xFFF7EFD7)
private val InfoBoxBorder = Color(0xFFE6D9AE)
private val InfoBoxText = Color(0xFF7A6E4E)
private val BackButtonBg = Color(0xFFE6E5DF)
private val BackButtonText = Color(0xFF4A4A45)

@Composable
fun HeartRateInputContent(
    title: String,
    subtitle: String,
    buttonText: String,
    onExerciseButtonClick: () -> Unit,
    onAddBpmButtonClick: (Int) -> Unit,
    infoText: String? = null,
    onBack: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null,
    onViewHistory: (() -> Unit)? = null,
    userName: String? = null,
) {
    val context = LocalContext.current

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            if (!userName.isNullOrBlank()) {
                Text(
                    text = stringResource(R.string.greeting, userName),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TitleColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ScreenBg)
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(RoseCircle, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = RoseHeart,
                    modifier = Modifier.size(40.dp),
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TitleColor,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = subtitle,
                fontSize = 15.sp,
                color = SubtitleColor,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(28.dp))

            Text(
                text = stringResource(R.string.enter_heart_rate_value),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF3A3A37),
            )

            Spacer(Modifier.height(8.dp))

            val state = rememberTextFieldState()

            OutlinedTextField(
                state = state,
                textStyle = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = TitleColor,
                ),
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = FieldBg,
                    unfocusedContainerColor = FieldBg,
                    focusedBorderColor = SageGreen,
                    unfocusedBorderColor = Color(0xFFE3E2DC),
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            if (infoText != null) {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(InfoBoxBg, RoundedCornerShape(12.dp))
                        .border(1.dp, InfoBoxBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WarningAmber,
                        contentDescription = null,
                        tint = InfoBoxText,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.size(10.dp))
                    Text(
                        text = infoText,
                        fontSize = 13.sp,
                        color = InfoBoxText,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val bpm = checkInputBpm(state.text.toString())

                    if (bpm != null) {
                        onAddBpmButtonClick(bpm)
                        onExerciseButtonClick()
                    } else {
                        showToast(context, context.getString(R.string.incorrect_input_data))
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
            ) {
                Text(
                    text = buttonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )
            }

            if (onViewHistory != null) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onViewHistory,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BackButtonBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                ) {
                    Text(
                        text = stringResource(R.string.history_title),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = BackButtonText,
                    )
                }
            }

            if (onLogout != null) {
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onLogout) {
                    Text(
                        text = stringResource(R.string.logout),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = SubtitleColor,
                    )
                }
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
