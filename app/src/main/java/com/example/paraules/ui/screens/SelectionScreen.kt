package com.example.paraules.ui.screens

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paraules.data.MAX_WORDS
import com.example.paraules.data.MIN_WORDS
import com.example.paraules.data.WordCategory
import com.example.paraules.ui.theme.*
@Composable
fun SelectionScreen(
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
            ActionButton(
                label = "Finalitzar",
                color = ActionGreen,
                onClick = onFinish,
                textColor = Color.White,
                enabled = selectedWords.size >= MIN_WORDS
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryWords(words: List<String>, selectedWords: List<String>, onToggleWord: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        words.forEach { word ->
            val selected = word in selectedWords
            FilterChip(
                selected = selected,
                onClick = { onToggleWord(word) },
                label = { Text(word, fontSize = 17.sp, fontFamily = AccessibilityFont) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
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
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(List(MAX_WORDS) { it }, key = { index, _ ->
                    selectedWords.getOrNull(index) ?: "empty-$index"
                }) { index, _ ->
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

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .pointerInput(total, word, dragThreshold) {
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
            }
            .semantics { contentDescription = "Mantén premut $word per canviar-ne l'ordre" }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.width(48.dp).fillMaxHeight(),
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
private fun ActionButton(
    label: String,
    color: Color,
    onClick: () -> Unit,
    textColor: Color = TitleGreen,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.widthIn(min = 132.dp).height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = textColor),
        contentPadding = PaddingValues(horizontal = 22.dp)
    ) {
        Text(label, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = AccessibilityFont)
    }
}

@Composable
@Preview(showBackground = true, widthDp = 800, heightDp = 360)
fun SelectionScreenPreview() {
    val sampleCategories = listOf(
        WordCategory("Animals", listOf("Gat", "Gos", "Ocell", "Peix")),
        WordCategory("Colors", listOf("Vermell", "Verd", "Blau", "Groc")),
        WordCategory("Fruits", listOf("Poma", "Plàtan", "Taronja", "Raïm"))
    )
    ParaulesTheme {
        SelectionScreen(
            categories = sampleCategories,
            selectedWords = listOf("Gat", "Verd"),
            expandedCategory = "Animals",
            onExpandCategory = {},
            onToggleWord = {},
            onMoveWord = { _, _ -> },
            onRemoveWord = {},
            onClear = {},
            onCopy = {},
            onFinish = {},
        )
    }
}
