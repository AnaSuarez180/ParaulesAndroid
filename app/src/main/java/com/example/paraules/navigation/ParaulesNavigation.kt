package com.example.paraules.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.paraules.data.MAX_WORDS
import com.example.paraules.data.copyPhrase
import com.example.paraules.data.loadCategories
import com.example.paraules.ui.screens.ResultScreen
import com.example.paraules.ui.screens.SelectionScreen
import com.example.paraules.ui.screens.WelcomeScreen
import com.example.paraules.ui.theme.ScreenBackground
import kotlinx.coroutines.launch

private object Route {
    const val HOME = "home"
    const val SELECTION = "selection"
    const val RESULT = "result"
}

@Composable
fun ParaulesNavigation() {
    val context = LocalContext.current
    val categories = remember { loadCategories(context) }
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var selectedWords by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var expandedCategory by rememberSaveable { mutableStateOf("") }

    Scaffold(
        containerColor = ScreenBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.HOME,
            modifier = Modifier.fillMaxSize().padding(contentPadding)
        ) {
            composable(Route.HOME) {
                WelcomeScreen(
                    onStart = {
                        selectedWords = emptyList()
                        expandedCategory = ""
                        navController.navigate(Route.SELECTION) { launchSingleTop = true }
                    }
                )
            }
            composable(Route.SELECTION) {
                SelectionScreen(
                    categories = categories,
                    selectedWords = selectedWords,
                    expandedCategory = expandedCategory,
                    onExpandCategory = { name ->
                        expandedCategory = if (expandedCategory == name) "" else name
                    },
                    onToggleWord = { word ->
                        when {
                            word in selectedWords -> selectedWords = selectedWords - word
                            selectedWords.size < MAX_WORDS -> selectedWords = selectedWords + word
                            else -> scope.launch {
                                snackbarHostState.showSnackbar("Pots seleccionar fins a sis paraules.")
                            }
                        }
                    },
                    onMoveWord = { from, to ->
                        if (from in selectedWords.indices && to in selectedWords.indices && from != to) {
                            val reordered = selectedWords.toMutableList()
                            val word = reordered.removeAt(from)
                            reordered.add(to, word)
                            selectedWords = reordered
                        }
                    },
                    onRemoveWord = { word -> selectedWords = selectedWords - word },
                    onClear = {
                        selectedWords = emptyList()
                        scope.launch { snackbarHostState.showSnackbar("Selecció esborrada.") }
                    },
                    onCopy = {
                        if (selectedWords.isEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Selecciona alguna paraula abans de copiar.")
                            }
                        } else {
                            copyPhrase(context, selectedWords)
                            scope.launch { snackbarHostState.showSnackbar("Frase copiada.") }
                        }
                    },
                    onFinish = {
                        if (selectedWords.size == MAX_WORDS) {
                            navController.navigate(Route.RESULT) { launchSingleTop = true }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Tria sis paraules per acabar la frase.")
                            }
                        }
                    }
                )
            }
            composable(Route.RESULT) {
                ResultScreen(
                    selectedWords = selectedWords,
                    onPlayAgain = {
                        selectedWords = emptyList()
                        expandedCategory = ""
                        navController.navigate(Route.HOME) {
                            popUpTo(Route.HOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
