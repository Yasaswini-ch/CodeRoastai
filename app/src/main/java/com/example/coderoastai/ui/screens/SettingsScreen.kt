package com.example.coderoastai.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coderoastai.data.RoastHistoryRepository
import com.example.coderoastai.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(repository: RoastHistoryRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var defaultPersonality by remember { mutableStateOf("Gordon Ramsay") }
    var animationSpeed by remember { mutableStateOf("Normal") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        // Header
        SettingsHeader()

        // Settings Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Preferences Section
            item {
                SettingsSectionHeader("PREFERENCES")
            }

            item {
                SettingsOption(
                    icon = Icons.Default.Person,
                    title = "Default Personality",
                    subtitle = defaultPersonality,
                    onClick = { /* TODO: Show personality picker */ }
                )
            }

            item {
                SettingsOption(
                    icon = Icons.Default.Speed,
                    title = "Animation Speed",
                    subtitle = animationSpeed,
                    onClick = { /* TODO: Show speed picker */ }
                )
            }

            // Data Section
            item {
                Spacer(modifier = Modifier.height(12.dp))
                SettingsSectionHeader("DATA")
            }

            item {
                SettingsOption(
                    icon = Icons.Default.FileDownload,
                    title = "Export History",
                    subtitle = "Save your roast history as JSON",
                    onClick = {
                        scope.launch {
                            val file = repository.exportHistoryToJson(context)
                            if (file != null) {
                                Toast.makeText(
                                    context,
                                    "Exported to ${file.name}",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Export failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )
            }

            item {
                SettingsOption(
                    icon = Icons.Default.Delete,
                    title = "Clear All History",
                    subtitle = "Permanently delete all roasts",
                    onClick = {
                        scope.launch {
                            repository.clearAllHistory()
                            Toast.makeText(
                                context,
                                "History cleared",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    isDestructive = true
                )
            }

            // About Section
            item {
                Spacer(modifier = Modifier.height(12.dp))
                SettingsSectionHeader("ABOUT")
            }

            item {
                SettingsOption(
                    icon = Icons.Default.Info,
                    title = "App Version",
                    subtitle = "1.0.0",
                    onClick = {}
                )
            }

            item {
                SettingsOption(
                    icon = Icons.Default.Code,
                    title = "GitHub Repository",
                    subtitle = "View source code",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com"))
                        context.startActivity(intent)
                    }
                )
            }

            item {
                SettingsOption(
                    icon = Icons.Default.Favorite,
                    title = "Rate This App",
                    subtitle = "Show some love ❤️",
                    onClick = {
                        Toast.makeText(context, "Thanks for the support!", Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            }

            // Bottom padding
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun SettingsHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkGray.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "⚙️", fontSize = 28.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = NeonCyan
                )
                Text(
                    text = "Customize your experience",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        ),
        color = TextTertiary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingsOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkGray.copy(alpha = 0.6f)
        ),
        border = BorderStroke(
            1.dp,
            if (isDestructive) NeonRed.copy(alpha = 0.3f) else GlassWhite20
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) NeonRed else NeonCyan,
                modifier = Modifier.size(24.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isDestructive) NeonRed else TextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
