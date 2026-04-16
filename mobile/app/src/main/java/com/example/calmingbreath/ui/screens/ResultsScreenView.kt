package com.example.calmingbreath.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calmingbreath.R
import com.example.calmingbreath.ui.theme.afterBorderColor
import com.example.calmingbreath.ui.theme.afterColor
import com.example.calmingbreath.ui.theme.beforeBorderColor
import com.example.calmingbreath.ui.theme.beforeColor
import com.example.calmingbreath.ui.viewmodel.HeartRateInputViewModel

@Composable
fun ResultsScreenView(viewModel: HeartRateInputViewModel) {
    val state = viewModel.state.collectAsState()
    ResultsScreenContent(
        heartRateBefore = state.value.heartRateBefore,
        heartRateAfter = state.value.heartRateAfter
    )
}

@Composable
fun ResultsScreenContent(heartRateBefore: Int, heartRateAfter: Int) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.finally_results),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BpmCard(
                    label = stringResource(R.string.before),
                    bpm = heartRateBefore,
                    backgroundColor = beforeColor,
                    borderColor = beforeBorderColor
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(28.dp)
                )

                BpmCard(
                    label = stringResource(R.string.after),
                    bpm = heartRateAfter,
                    backgroundColor = afterColor,
                    borderColor = afterBorderColor
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultsScreenPreview() {
    ResultsScreenContent(heartRateBefore = 80, heartRateAfter = 65)
}

@Composable
private fun BpmCard(
    label: String,
    bpm: Int,
    backgroundColor: Color,
    borderColor: Color
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(backgroundColor)
            .border(width = 2.dp, color = borderColor, shape = shape)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = borderColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.bpm).replace("#", bpm.toString()),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
        }
    }
}
