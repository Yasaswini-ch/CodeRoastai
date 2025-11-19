package com.example.coderoastai.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coderoastai.data.RoastHistoryEntity
import com.example.coderoastai.data.RoastHistoryRepository
import com.example.coderoastai.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

@Composable
fun HistoryScreen(
    repository: RoastHistoryRepository,
    onHistoryItemClick: (RoastHistoryEntity) -> Unit
) {
    val scope = rememberCoroutineScope()
    val historyList by repository.getRecentHistory().collectAsState(initial = emptyList())
    var showClearDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        // Header
        HistoryHeader(
            onSearchClick = { isSearching = !isSearching },
            onClearAllClick = { showClearDialog = true },
            hasHistory = historyList.isNotEmpty()
        )

        // Search Bar (if searching)
        AnimatedVisibility(
            visible = isSearching,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onClearSearch = {
                    searchQuery = ""
                    isSearching = false
                }
            )
        }

        // History List or Empty State
        if (historyList.isEmpty()) {
            EmptyHistoryState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = if (searchQuery.isBlank()) historyList
                    else historyList.filter {
                        it.code.contains(searchQuery, ignoreCase = true) ||
                                it.language.contains(searchQuery, ignoreCase = true)
                    },
                    key = { it.id }
                ) { historyItem ->
                    HistoryCard(
                        historyItem = historyItem,
                        onClick = { onHistoryItemClick(historyItem) },
                        onDelete = {
                            scope.launch {
                                repository.deleteHistory(historyItem)
                            }
                        }
                    )
                }

                // Bottom padding for nav bar
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Clear All Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(
                    "Clear All History?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    "This will permanently delete all your roast history. This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            repository.clearAllHistory()
                            showClearDialog = false
                        }
                    }
                ) {
                    Text("DELETE ALL", color = NeonRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("CANCEL", color = NeonCyan)
                }
            },
            containerColor = DarkGray,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun HistoryHeader(
    onSearchClick: () -> Unit,
    onClearAllClick: () -> Unit,
    hasHistory: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkGray.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "🕐", fontSize = 28.sp)
                Column {
                    Text(
                        text = "Roast History",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = NeonCyan
                    )
                    Text(
                        text = "Your past roasts",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            if (hasHistory) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = NeonCyan
                        )
                    }
                    IconButton(onClick = onClearAllClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear All",
                            tint = NeonRed
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        color = DarkGray,
        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("Search code or language...", color = TextTertiary)
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = NeonCyan
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
                singleLine = true
            )
            if (query.isNotBlank()) {
                IconButton(onClick = onClearSearch) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = TextTertiary
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryCard(
    historyItem: RoastHistoryEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkGray.copy(alpha = 0.6f)
        ),
        border = BorderStroke(1.dp, GlassWhite20)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time and Language
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Language Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = getLanguageColorForHistory(historyItem.language).copy(alpha = 0.15f),
                        border = BorderStroke(
                            1.dp,
                            getLanguageColorForHistory(historyItem.language)
                        )
                    ) {
                        Text(
                            text = historyItem.language,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = getLanguageColorForHistory(historyItem.language),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Score Badge (if available)
                    historyItem.score?.let { score ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = getScoreColor(score).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, getScoreColor(score))
                        ) {
                            Text(
                                text = "$score/100",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = getScoreColor(score),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Timestamp
                    Text(
                        text = getRelativeTime(historyItem.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary,
                        fontSize = 10.sp
                    )
                }

                // Delete Button
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = NeonRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Code Preview (first 3 lines)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DeepBlack.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, GlassWhite10)
            ) {
                Text(
                    text = historyItem.code.lines().take(3).joinToString("\n"),
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextSecondary,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Personality Used
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getPersonalityEmoji(historyItem.personality),
                    fontSize = 14.sp
                )
                Text(
                    text = "Roasted by ${historyItem.personality}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    fontSize = 11.sp
                )
                Text(
                    text = "•",
                    color = TextTertiary,
                    fontSize = 11.sp
                )
                Text(
                    text = "Level ${historyItem.intensity}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonRed,
                    fontSize = 11.sp
                )
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Delete this roast?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    "This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("DELETE", color = NeonRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("CANCEL", color = NeonCyan)
                }
            },
            containerColor = DarkGray,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun EmptyHistoryState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "📜", fontSize = 64.sp)
            Text(
                text = "No Roast History Yet",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary
            )
            Text(
                text = "Roast some code to see it here!",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

// Helper Functions
fun getRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        diff < 604_800_000 -> "${diff / 86_400_000}d ago"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
    }
}

fun getLanguageColorForHistory(language: String): Color {
    return when (language) {
        "Python" -> NeonYellow
        "JavaScript", "JS" -> NeonYellow
        "Java" -> NeonRed
        "Kotlin" -> NeonPurple
        "C++" -> NeonCyan
        else -> NeonGreen
    }
}

fun getScoreColor(score: Int): Color {
    return when {
        score >= 90 -> NeonGreen
        score >= 70 -> NeonYellow
        score >= 50 -> Color(0xFFFF9800) // Orange
        score >= 30 -> Color(0xFFFF5722) // Deep Orange
        else -> NeonRed
    }
}

fun getPersonalityEmoji(personality: String): String {
    return when (personality) {
        "Gordon Ramsay" -> "🔪"
        "Drill Sergeant" -> "🎖️"
        "Disappointed Dad" -> "👔"
        "Gen Z" -> "💅"
        "Shakespeare" -> "🎭"
        else -> "🤖"
    }
}
