package com.example.calmingbreath.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calmingbreath.AuthAction
import com.example.calmingbreath.AuthState

@Composable
fun RegisterScreen(
    state: AuthState,
    onAction: (AuthAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(24.dp))
        Icon(Icons.Filled.Favorite, null, tint = HeartBlue, modifier = Modifier.size(56.dp))
        Spacer(Modifier.height(16.dp))
        Text(
            "Регистрация",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C2C2A)
        )
        Spacer(Modifier.height(4.dp))
        Text("Создайте свой аккаунт", fontSize = 16.sp, color = Color(0xFF6B6B66))

        Spacer(Modifier.height(28.dp))

        FieldLabel("Имя")
        AppTextField(
            state.firstName,
            { onAction(AuthAction.FirstNameChanged(it)) },
            "Ваше имя",
            Icons.Filled.Person
        )
        Spacer(Modifier.height(16.dp))

        FieldLabel("Email")
        AppTextField(
            state.email,
            { onAction(AuthAction.EmailChanged(it)) },
            "your@email.com",
            Icons.Filled.Email,
            keyboardType = KeyboardType.Email
        )
        Spacer(Modifier.height(16.dp))

        FieldLabel("Пароль")
        AppTextField(
            state.password,
            { onAction(AuthAction.PasswordChanged(it)) },
            "Не менее 6 символов",
            Icons.Filled.Lock,
            isPassword = true
        )
        Spacer(Modifier.height(16.dp))

        FieldLabel("Подтвердите пароль")
        AppTextField(
            state.confirmPassword,
            { onAction(AuthAction.ConfirmPasswordChanged(it)) },
            "Повторите пароль",
            Icons.Filled.Lock,
            isPassword = true
        )

        Spacer(Modifier.height(12.dp))

        if (state.errorMessage != null) {
            Spacer(Modifier.height(8.dp))
            Text(state.errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { onAction(AuthAction.RegisterClicked) },
            enabled = !state.isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    "Зарегистрироваться",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Row {
            Text(
                text = "Уже есть аккаунт?",
                fontSize = 14.sp,
                color = Color(0xFF6B6B66)
            )
            Text(
                "Войти",
                fontSize = 14.sp,
                color = HeartBlue,
                modifier = Modifier.clickable { onAction(AuthAction.NavigateToLogin) })
        }
    }
}