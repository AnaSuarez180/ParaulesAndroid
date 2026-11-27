package com.example.paraules.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paraules.data.phraseFor
import com.example.paraules.ui.theme.*
@Composable
fun ResultScreen(selectedWords: List<String>, onPlayAgain: () -> Unit) {
    val phrase = remember(selectedWords) { phraseFor(selectedWords) }
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(6.dp))
        Text("Bon treball!", color = Leaf, fontSize = 42.sp, fontWeight = FontWeight.Bold, fontFamily = AccessibilityFont, textAlign = TextAlign.Center)
        Text("Has creat aquesta frase", color = Leaf, fontSize = 25.sp, fontWeight = FontWeight.Bold, fontFamily = AccessibilityFont, textAlign = TextAlign.Center)
        Spacer(Modifier.height(14.dp))
        Card(
            modifier = Modifier.weight(1f).widthIn(max = 940.dp).fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PanelGreen)
        ) {
            Column(Modifier.fillMaxSize().padding(18.dp)) {
                Text("La teva frase", color = Leaf, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = AccessibilityFont)
                Text(
                    phrase,
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 12.dp),
                    color = TitleGreen,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AccessibilityFont,
                    textAlign = TextAlign.Center
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    selectedWords.forEachIndexed { index, word ->
                        ResultWordRow(index + 1, word)
                    }
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Button(
            onClick = onPlayAgain,
            modifier = Modifier.widthIn(max = 940.dp).fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ActionGreen, contentColor = Color.White)
        ) {
            Text("Torna a jugar", fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily = AccessibilityFont)
        }
    }
}

@Composable
private fun ResultWordRow(number: Int, word: String) {
    Surface(color = Color.White, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().height(48.dp)) {
        Row(Modifier.padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = PaleGreen, shape = RoundedCornerShape(7.dp), modifier = Modifier.size(30.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(number.toString(), color = Leaf, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = AccessibilityFont)
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(word, color = Leaf, fontSize = 18.sp, fontFamily = AccessibilityFont)
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 800, heightDp = 360)
fun ResultScreenPreview() {
    ParaulesTheme { 
        ResultScreen(
            selectedWords = listOf("Vull", "aigua")
        ) { }
    }
}
