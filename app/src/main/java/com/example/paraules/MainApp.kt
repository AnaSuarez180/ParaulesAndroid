package com.example.paraules

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.json.JSONObject

private val Canvas = Color(0xFFF1F7E9)
private val PanelGreen = Color(0xFFDDEBCF)
private val Leaf = Color(0xFF035122)
private val TitleGreen = Color(0xFF304A38)
private val ActionGreen = Color(0xFF347A50)
private val ActionYellow = Color(0xFFF4D66D)
private val ActionCoral = Color(0xFFF28C78)
private val PaleGreen = Color(0xFFCEEED1)
private val AccessibilityFont = FontFamily(
    Font(R.font.atkinson_hyperlegible_regular, FontWeight.Normal),
    Font(R.font.atkinson_hyperlegible_bold, FontWeight.Bold)
)
private const val MAX_WORDS = 2

private enum class AppScreen { Welcome, Selection, Result }
private data class WordCategory(val name: String, val words: List<String>)
@Composable
fun ParaulesApp() {
    val context = LocalContext.current
    val categories = remember { loadCategories(context) }
    var screenName by rememberSaveable { mutableStateOf(AppScreen.Welcome.name) }
    var selectedWords by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var expandedCategory by rememberSaveable { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val screen = AppScreen.valueOf(screenName)

    BackHandler(enabled = screen != AppScreen.Welcome) {
        screenName = if (screen == AppScreen.Result) AppScreen.Selection.name else AppScreen.Welcome.name
    }

    MaterialTheme(colorScheme = lightColorScheme(
        primary = ActionGreen,
        onPrimary = Color.White,
        secondary = ActionYellow,
        background = Canvas,
        surface = PanelGreen,
        onSurface = Leaf
    )) {
        Scaffold(
            containerColor = Canvas,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { insets ->
            Box(Modifier.fillMaxSize().padding(insets)) {
                when (screen) {
                    AppScreen.Welcome -> WelcomeScreen(
                        onStart = {
                            selectedWords = emptyList()
                            expandedCategory = ""
                            screenName = AppScreen.Selection.name
                        }
                    )
                    AppScreen.Selection -> SelectionScreen(
                        categories = categories,
                        selectedWords = selectedWords,
                        expandedCategory = expandedCategory,
                        onExpandCategory = { expandedCategory = if (expandedCategory == it) "" else it },
                        onToggleWord = { word ->
                            selectedWords = when {
                                word in selectedWords -> selectedWords - word
                                selectedWords.size < MAX_WORDS -> selectedWords + word
                                else -> selectedWords
                            }
                            if (word !in selectedWords && selectedWords.size >= MAX_WORDS) {
                                scope.launch { snackbarHostState.showSnackbar("Pots seleccionar fins a sis paraules.") }
                            }
                        },
                        onMoveWord = { from, to ->
                            if (from in selectedWords.indices && to in selectedWords.indices && from != to) {
                                val next = selectedWords.toMutableList()
                                val word = next.removeAt(from)
                                next.add(to, word)
                                selectedWords = next
                            }
                        },
                        onRemoveWord = { word -> selectedWords = selectedWords - word },
                        onClear = {
                            selectedWords = emptyList()
                            scope.launch { snackbarHostState.showSnackbar("Selecció esborrada.") }
                        },
                        onCopy = {
                            if (selectedWords.isEmpty()) {
                                scope.launch { snackbarHostState.showSnackbar("Selecciona alguna paraula abans de copiar.") }
                            } else {
                                copyPhrase(context, selectedWords)
                                scope.launch { snackbarHostState.showSnackbar("Frase copiada.") }
                            }
                        },
                        onFinish = {
                            if (selectedWords.size == MAX_WORDS) screenName = AppScreen.Result.name
                            else scope.launch {
                                snackbarHostState.showSnackbar("Tria sis paraules per acabar la frase.")
                            }
                        }
                    )
                    AppScreen.Result -> ResultScreen(
                        selectedWords = selectedWords,
                        onPlayAgain = {
                            selectedWords = emptyList()
                            expandedCategory = ""
                            screenName = AppScreen.Welcome.name
                        }
                    )
                }
            }
        }
    }
}

private fun loadCategories(context: Context): List<WordCategory> {
    val json = context.assets.open("substantius_300_ca.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
    val objectJson = JSONObject(json)
    return objectJson.keys().asSequence().map { category ->
        val words = objectJson.getJSONArray(category)
        WordCategory(category, List(words.length()) { index -> words.getString(index) })
    }.toList()
}

private fun copyPhrase(context: Context, words: List<String>) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Paraules", phraseFor(words)))
}

private fun phraseFor(words: List<String>): String = words.joinToString(" ").replaceFirstChar { it.uppercase() } + "."

@Composable
private fun WelcomeScreen(onStart: () -> Unit) {
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
private fun SelectionScreen(
    categories: List<WordCategory>,
    selectedWords: List<String>,
    expandedCategory: String,
    onExpandCategory: (String) -> Unit,
    onToggleWord: (String) -> Unit,
    onMoveWord: (Int, Int) -> Unit,
    onRemoveWord: (String) -> Unit,
    onClear: () -> Unit,
    onCopy: () -> Unit,
    onFinish: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Paraules", color = Leaf, fontSize = 25.sp, fontWeight = FontWeight.Bold, fontFamily = AccessibilityFont)
            Spacer(Modifier.width(18.dp))
            Text("Escull les paraules per comunicar-te", color = TitleGreen, fontSize = 18.sp, fontFamily = AccessibilityFont)
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            CategoriesPanel(
                categories = categories,
                expandedCategory = expandedCategory,
                selectedWords = selectedWords,
                onExpandCategory = onExpandCategory,
                onToggleWord = onToggleWord,
                modifier = Modifier.weight(1f)
            )
            SelectedWordsPanel(
                selectedWords = selectedWords,
                onMoveWord = onMoveWord,
                onRemoveWord = onRemoveWord,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(label = "Netejar", color = ActionCoral, onClick = onClear)
            Spacer(Modifier.width(12.dp))
            ActionButton(label = "Copiar", color = ActionYellow, onClick = onCopy, textColor = TitleGreen)
            Spacer(Modifier.width(12.dp))
            ActionButton(label = "Finalitzar", color = ActionGreen, onClick = onFinish, textColor = Color.White)
        }
    }
}

@Composable
private fun CategoriesPanel(
    categories: List<WordCategory>,
    expandedCategory: String,
    selectedWords: List<String>,
    onExpandCategory: (String) -> Unit,
    onToggleWord: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxHeight(),
        color = PanelGreen,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.fillMaxSize().padding(20.dp)) {
            Text("Categories", color = Leaf, fontSize = 21.sp, fontWeight = FontWeight.Bold, fontFamily = AccessibilityFont)
            Spacer(Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(categories, key = { it.name }) { category ->
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        CategoryHeader(
                            name = category.name,
                            expanded = expandedCategory == category.name,
                            onClick = { onExpandCategory(category.name) }
                        )
                        if (expandedCategory == category.name) {
                            CategoryWords(
                                words = category.words,
                                selectedWords = selectedWords,
                                onToggleWord = onToggleWord
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(name: String, expanded: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Leaf),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(name, modifier = Modifier.weight(1f), fontSize = 19.sp, fontWeight = FontWeight.Bold, fontFamily = AccessibilityFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(if (expanded) "⌃" else "⌄", fontSize = 27.sp, color = Leaf, fontFamily = AccessibilityFont)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun CategoryWords(words: List<String>, selectedWords: List<String>, onToggleWord: (String) -> Unit) {
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        words.forEach { word ->
            val selected = word in selectedWords
            FilterChip(
                selected = selected,
                onClick = { onToggleWord(word) },
                label = { Text(word, fontSize = 17.sp, fontFamily = AccessibilityFont) },
                modifier = Modifier.heightIn(min = 50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    labelColor = Leaf,
                    selectedContainerColor = ActionGreen,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun SelectedWordsPanel(
    selectedWords: List<String>,
    onMoveWord: (Int, Int) -> Unit,
    onRemoveWord: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxHeight(),
        color = PanelGreen,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.fillMaxSize().padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Paraules seleccionades",
                    modifier = Modifier.weight(1f),
                    color = Leaf,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AccessibilityFont
                )
                Text("${selectedWords.size}/$MAX_WORDS", color = Leaf, fontSize = 19.sp, fontWeight = FontWeight.Bold, fontFamily = AccessibilityFont)
            }
            Spacer(Modifier.height(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(MAX_WORDS) { index ->
                    val word = selectedWords.getOrNull(index)
                    if (word == null) EmptyWordSlot(index + 1)
                    else key(word) {
                        SelectedWordRow(
                            index = index,
                            total = selectedWords.size,
                            word = word,
                            onMoveWord = onMoveWord,
                            onRemove = { onRemoveWord(word) }
                        )
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Text(
                "Mantén premut el mànec per arrossegar. També pots fer servir les fletxes.",
                modifier = Modifier.padding(top = 8.dp),
                color = TitleGreen,
                fontSize = 15.sp,
                fontFamily = AccessibilityFont
            )
        }
    }
}

@Composable
private fun EmptyWordSlot(number: Int) {
    Surface(color = Color.White.copy(alpha = 0.72f), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().height(56.dp)) {
        Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = PaleGreen, shape = RoundedCornerShape(8.dp), modifier = Modifier.size(32.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(number.toString(), color = Leaf, fontSize = 17.sp, fontWeight = FontWeight.Bold, fontFamily = AccessibilityFont)
                }
            }
            Spacer(Modifier.width(14.dp))
            Text("Paraula $number", color = TitleGreen.copy(alpha = 0.7f), fontSize = 18.sp, fontFamily = AccessibilityFont)
        }
    }
}

@Composable
private fun SelectedWordRow(
    index: Int,
    total: Int,
    word: String,
    onMoveWord: (Int, Int) -> Unit,
    onRemove: () -> Unit
) {
    val density = LocalDensity.current
    val dragThreshold = with(density) { 48.dp.toPx() }
    var dragDistance by remember(word) { mutableStateOf(0f) }
    val currentIndex by rememberUpdatedState(index)
    val moveWord by rememberUpdatedState(onMoveWord)

    Surface(color = Color.White, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().height(56.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.width(48.dp).fillMaxHeight().pointerInput(total, word, dragThreshold) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { dragDistance = 0f },
                        onDragEnd = { dragDistance = 0f },
                        onDragCancel = { dragDistance = 0f },
                        onDrag = { change, amount ->
                            change.consume()
                            dragDistance += amount.y
                            if (dragDistance >= dragThreshold && currentIndex < total - 1) {
                                moveWord(currentIndex, currentIndex + 1)
                                dragDistance = 0f
                            } else if (dragDistance <= -dragThreshold && currentIndex > 0) {
                                moveWord(currentIndex, currentIndex - 1)
                                dragDistance = 0f
                            }
                        }
                    )
                }.semantics { contentDescription = "Mantén premut per arrossegar $word i canviar-ne l'ordre" },
                contentAlignment = Alignment.Center
            ) {
                Text("⋮⋮", color = Leaf, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = AccessibilityFont)
            }
            Text(
                text = word,
                modifier = Modifier.weight(1f),
                color = Leaf,
                fontSize = 18.sp,
                fontFamily = AccessibilityFont,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            ReorderButton(label = "↑", description = "Mou $word amunt", enabled = index > 0) { onMoveWord(index, index - 1) }
            ReorderButton(label = "↓", description = "Mou $word avall", enabled = index < total - 1) { onMoveWord(index, index + 1) }
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(48.dp).semantics { contentDescription = "Eliminar $word" }
            ) {
                Text("×", color = Leaf, fontSize = 30.sp, fontWeight = FontWeight.Normal, fontFamily = AccessibilityFont)
            }
        }
    }
}

@Composable
private fun ReorderButton(label: String, description: String, enabled: Boolean, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(48.dp).semantics { contentDescription = description }
    ) {
        Text(label, color = if (enabled) Leaf else Leaf.copy(alpha = 0.28f), fontSize = 23.sp, fontWeight = FontWeight.Bold, fontFamily = AccessibilityFont)
    }
}

@Composable
private fun ActionButton(label: String, color: Color, onClick: () -> Unit, textColor: Color = TitleGreen) {
    Button(
        onClick = onClick,
        modifier = Modifier.widthIn(min = 132.dp).height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = textColor),
        contentPadding = PaddingValues(horizontal = 22.dp)
    ) {
        Text(label, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = AccessibilityFont)
    }
}

@Composable
private fun ResultScreen(selectedWords: List<String>, onPlayAgain: () -> Unit) {
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
