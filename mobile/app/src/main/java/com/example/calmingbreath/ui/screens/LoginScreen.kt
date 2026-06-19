package com.example.calmingbreath.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calmingbreath.AuthAction
import com.example.calmingbreath.AuthState

val ScreenBg = Color(0xFFEFEEE8)
val SageGreen = Color(0xFF7C9A77)
val HeartBlue = Color(0xFF4F8FE0)
val FieldBg = Color(0xFFFDFDFB)

@Composable
fun LoginScreen(
    state: AuthState,
    onAction: (AuthAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = null,
            tint = HeartBlue,
            modifier = Modifier.size(56.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text("Вход", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2C2C2A))
        Spacer(Modifier.height(4.dp))
        Text("Войдите в свой аккаунт", fontSize = 16.sp, color = Color(0xFF6B6B66))

        Spacer(Modifier.height(32.dp))

        FieldLabel("Email")
        AppTextField(
            value = state.email,
            onValueChange = { onAction(AuthAction.EmailChanged(it)) },
            placeholder = "your@email.com",
            leadingIcon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email,
        )

        Spacer(Modifier.height(16.dp))

        FieldLabel("Пароль")
        AppTextField(
            value = state.password,
            onValueChange = { onAction(AuthAction.PasswordChanged(it)) },
            placeholder = "••••••••",
            leadingIcon = Icons.Filled.Lock,
            isPassword = true,
        )

        Spacer(Modifier.height(8.dp))
        Text(
            "Забыли пароль?",
            fontSize = 14.sp,
            color = HeartBlue,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { onAction(AuthAction.ForgotPasswordClicked) },
        )

        if (state.errorMessage != null) {
            Spacer(Modifier.height(12.dp))
            Text(state.errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { onAction(AuthAction.LoginClicked) },
            enabled = !state.isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
            } else {
                Text("Войти", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
            }
        }

        Spacer(Modifier.height(20.dp))

        Row {
            Text("Нет аккаунта? ", fontSize = 14.sp, color = Color(0xFF6B6B66))
            Text(
                "Зарегистрироваться",
                fontSize = 14.sp,
                color = HeartBlue,
                modifier = Modifier.clickable { onAction(AuthAction.NavigateToRegister) },
            )
        }
    }
}

@Composable
fun FieldLabel(text: String) {
    Text(
        text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF3A3A37),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
    )
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xFF9A9A93)) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = Color(0xFF9A9A93)) },
        singleLine = true,
        visualTransformation =
            if (isPassword) PasswordVisualTransformation()
            else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = FieldBg,
            unfocusedContainerColor = FieldBg,
            focusedBorderColor = SageGreen,
            unfocusedBorderColor = Color(0xFFE3E2DC),
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}