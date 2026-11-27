package com.example.paraules.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paraules.ui.theme.AccessibilityFont
import com.example.paraules.ui.theme.ActionGreen
import com.example.paraules.ui.theme.Leaf
import com.example.paraules.ui.theme.ParaulesTheme
import com.example.paraules.ui.theme.TitleGreen
@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Paraules",
            color = TitleGreen,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = AccessibilityFont,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = "Construeix la teva frase",
            color = Leaf,
            fontSize = 24.sp,
            fontFamily = AccessibilityFont,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(36.dp))
        Button(
            onClick = onStart,
            modifier = Modifier.width(240.dp).height(96.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ActionGreen),
            contentPadding = PaddingValues(horizontal = 28.dp)
        ) {
            Text("▶", fontSize = 38.sp, fontFamily = AccessibilityFont, color = Color.White)
            Spacer(Modifier.width(14.dp))
            Text("Començar", fontSize = 23.sp, fontWeight = FontWeight.Bold, fontFamily = AccessibilityFont)
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 800,
    heightDp = 360)
fun WelcomeScreenPreview() {
    ParaulesTheme {
        WelcomeScreen(onStart = {})
    }
}

