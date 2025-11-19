package com.example.coderoastai

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.coderoastai.data.RoastHistoryEntity
import com.example.coderoastai.data.RoastHistoryRepository
import com.example.coderoastai.navigation.Screen
import com.example.coderoastai.navigation.bottomNavItems
import com.example.coderoastai.ui.screens.*
import com.example.coderoastai.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun CodeRoastApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val repository = remember { RoastHistoryRepository(context) }
    val scope = rememberCoroutineScope()

    // Shared state for code input
    var sharedCodeInput by remember { mutableStateOf("") }
    var sharedLanguage by remember { mutableStateOf("Python") }
    var sharedPersonality by remember { mutableStateOf("Gordon Ramsay") }
    var sharedIntensity by remember { mutableStateOf(3) }
    var sharedRoastResults by remember { mutableStateOf<List<String>>(emptyList()) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        containerColor = DeepBlack
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreenWithNav(
                    codeInput = sharedCodeInput,
                    onCodeInputChange = { sharedCodeInput = it },
                    language = sharedLanguage,
                    onLanguageChange = { sharedLanguage = it },
                    personality = sharedPersonality,
                    onPersonalityChange = { sharedPersonality = it },
                    intensity = sharedIntensity,
                    onIntensityChange = { sharedIntensity = it },
                    roastResults = sharedRoastResults,
                    onRoastResultsChange = { sharedRoastResults = it },
                    onSaveToHistory = { results ->
                        scope.launch {
                            repository.addHistory(
                                RoastHistoryEntity(
                                    code = sharedCodeInput,
                                    language = sharedLanguage,
                                    personality = sharedPersonality,
                                    intensity = sharedIntensity,
                                    roasts = results
                                )
                            )
                        }
                    },
                    onNavigateToExamples = {
                        navController.navigate(Screen.Examples.route)
                    },
                    onNavigateToFix = {
                        navController.navigate(Screen.Fix.route)
                    },
                    onNavigateToShare = {
                        navController.navigate(Screen.Share.route)
                    }
                )
            }

            composable(Screen.Examples.route) {
                ExamplesScreen(
                    onExampleSelected = { example ->
                        sharedCodeInput = example.code
                        sharedLanguage = example.language
                        navController.navigate(Screen.Home.route)
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route)
                    }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    repository = repository,
                    onHistoryItemClick = { historyItem ->
                        sharedCodeInput = historyItem.code
                        sharedLanguage = historyItem.language
                        sharedPersonality = historyItem.personality
                        sharedIntensity = historyItem.intensity
                        sharedRoastResults = historyItem.roasts
                        navController.navigate(Screen.Home.route)
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(repository = repository)
            }

            composable(Screen.Fix.route) {
                // Generate a simple fix result for demonstration
                val fixResult = remember {
                    com.example.coderoastai.CodeFixGenerator.FixResult(
                        originalCode = sharedCodeInput,
                        fixedCode = "// Fixed code will be generated here\n" + sharedCodeInput.replace(
                            "temp",
                            "result"
                        ),
                        improvements = listOf(
                            com.example.coderoastai.CodeFixGenerator.Improvement(
                                "naming",
                                "Improved variable names",
                                null
                            ),
                            com.example.coderoastai.CodeFixGenerator.Improvement(
                                "structure",
                                "Better code organization",
                                null
                            )
                        ),
                        beforeScore = 45,
                        afterScore = 75,
                        metricsChange = com.example.coderoastai.CodeFixGenerator.MetricsChange(
                            linesBefore = sharedCodeInput.lines().size,
                            linesAfter = sharedCodeInput.lines().size + 2,
                            complexityBefore = 5,
                            complexityAfter = 3,
                            issuesFixed = 3
                        )
                    )
                }

                FixComparisonScreen(
                    fixResult = fixResult,
                    onApplyFix = { fixedCode ->
                        sharedCodeInput = fixedCode
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Share.route) {
                ShareScreen(
                    code = sharedCodeInput,
                    language = sharedLanguage,
                    score = 50, // Default score
                    roasts = sharedRoastResults,
                    onDismiss = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = DarkGray.copy(alpha = 0.95f),
        contentColor = TextPrimary,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Text(
                        text = screen.icon,
                        fontSize = 24.sp
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NeonCyan,
                    selectedTextColor = NeonCyan,
                    indicatorColor = NeonCyan.copy(alpha = 0.2f),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}

@Composable
fun HomeScreenWithNav(
    codeInput: String,
    onCodeInputChange: (String) -> Unit,
    language: String,
    onLanguageChange: (String) -> Unit,
    personality: String,
    onPersonalityChange: (String) -> Unit,
    intensity: Int,
    onIntensityChange: (Int) -> Unit,
    roastResults: List<String>,
    onRoastResultsChange: (List<String>) -> Unit,
    onSaveToHistory: (List<String>) -> Unit,
    onNavigateToExamples: () -> Unit,
    onNavigateToFix: () -> Unit = {},
    onNavigateToShare: () -> Unit = {}
) {
    var isRoasting by remember { mutableStateOf(false) }
    var showExamplesDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    CodeRoastMainScreenContent(
        codeInput = codeInput,
        onCodeInputChange = onCodeInputChange,
        language = language,
        onLanguageChange = onLanguageChange,
        personality = personality,
        onPersonalityChange = onPersonalityChange,
        intensity = intensity,
        onIntensityChange = onIntensityChange,
        roastResults = roastResults,
        isRoasting = isRoasting,
        onRoast = {
            isRoasting = true
            scope.launch {
                kotlinx.coroutines.delay(2000)
                val results = generateSampleRoasts(personality, intensity)
                onRoastResultsChange(results)
                onSaveToHistory(results)
                isRoasting = false
            }
        },
        onShowExamples = { showExamplesDialog = true },
        onClearResults = { onRoastResultsChange(emptyList()) },
        onFixCode = onNavigateToFix,
        onShare = onNavigateToShare
    )

    if (showExamplesDialog) {
        ExamplesDialog(
            selectedLanguage = language,
            onDismiss = { showExamplesDialog = false },
            onExampleSelected = { example ->
                onCodeInputChange(example.code)
                onLanguageChange(example.language)
                showExamplesDialog = false
            }
        )
    }
}
