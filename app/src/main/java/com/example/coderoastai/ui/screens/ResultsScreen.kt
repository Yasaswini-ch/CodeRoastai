package com.example.coderoastai.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coderoastai.ui.components.ElevatedGlassCard
import com.example.coderoastai.ui.components.TypewriterText
import com.example.coderoastai.ui.theme.*
import kotlinx.coroutines.delay

data class RoastResult(
    val score: Int,
    val grade: String,
    val verdict: String,
    val roasts: List<RoastItem>
)

data class RoastItem(
    val severity: String, // "critical", "warning", "info"
    val title: String,
    val description: String,
    val lineNumber: Int?,
    val flames: Int // 1-5
)

@Composable
fun ResultsScreen(
    result: RoastResult,
    onRoastAgain: () -> Unit,
    onShare: () -> Unit,
    onFixCode: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepBlack)
        ) {
            // Hero Score Section
            HeroScoreSection(
                score = result.score,
                grade = result.grade,
                verdict = result.verdict,
                visible = showContent
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Roasts List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(result.roasts) { index, roast ->
                    AnimatedVisibility(
                        visible = showContent,
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 500,
                                delayMillis = index * 100
                            )
                        ) + slideInVertically(
                            animationSpec = tween(
                                durationMillis = 500,
                                delayMillis = index * 100
                            ),
                            initialOffsetY = { it / 2 }
                        )
                    ) {
                        RoastCard(roast = roast)
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // Floating Action Bar
        AnimatedVisibility(
            visible = showContent,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            enter = slideInVertically(
                animationSpec = tween(500),
                initialOffsetY = { it }
            )
        ) {
            ActionBar(
                onRoastAgain = onRoastAgain,
                onShare = onShare,
                onFixCode = onFixCode
            )
        }
    }
}

@Composable
fun HeroScoreSection(
    score: Int,
    grade: String,
    verdict: String,
    visible: Boolean
) {
    val animatedScore by animateIntAsState(
        targetValue = if (visible) score else 0,
        animationSpec = tween(
            durationMillis = 2000,
            easing = FastOutSlowInEasing
        ),
        label = "score"
    )

    val ringProgress by animateFloatAsState(
        targetValue = if (visible) score / 100f else 0f,
        animationSpec = tween(
            durationMillis = 2000,
            easing = FastOutSlowInEasing
        ),
        label = "ring"
    )

    val ringColor = when {
        score >= 75 -> NeonGreen
        score >= 50 -> NeonOrange
        else -> NeonRed
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ringColor.copy(alpha = 0.3f),
                        DeepBlack
                    )
                )
            )
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circular Score Indicator
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                // Background ring
                CircularProgressIndicator(
                    progress = 1f,
                    modifier = Modifier.fillMaxSize(),
                    color = GlassWhite10,
                    strokeWidth = 12.dp
                )

                // Animated ring
                CircularProgressIndicator(
                    progress = ringProgress,
                    modifier = Modifier.fillMaxSize(),
                    color = ringColor,
                    strokeWidth = 12.dp
                )

                // Score text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = animatedScore.toString(),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 72.sp,
                            fontWeight = FontWeight.Black
                        ),
                        color = TextPrimary
                    )

                    Text(
                        text = grade,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ringColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Verdict with typewriter effect
            if (visible) {
                TypewriterText(
                    text = verdict,
                    style = MaterialTheme.typography.titleLarge.copy(
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.5.sp
                    ),
                    color = TextSecondary,
                    delayMs = 30L,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }
    }
}

@Composable
fun RoastCard(roast: RoastItem) {
    var expanded by remember { mutableStateOf(false) }

    val borderColor = when (roast.severity) {
        "critical" -> NeonRed
        "warning" -> NeonOrange
        "info" -> NeonCyan
        else -> NeonPurple
    }

    ElevatedGlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Severity badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = borderColor.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Text(
                        text = roast.severity.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = borderColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Flame indicators
                Text(
                    text = "🔥".repeat(roast.flames),
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Left border
            Row {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(if (expanded) 100.dp else 60.dp)
                        .background(borderColor, RoundedCornerShape(2.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    // Title
                    Text(
                        text = roast.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Description
                    Text(
                        text = roast.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        maxLines = if (expanded) Int.MAX_VALUE else 2
                    )

                    // Line number chip
                    if (roast.lineNumber != null) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NeonCyan.copy(alpha = 0.2f),
                            onClick = { /* TODO: Jump to line */ }
                        ) {
                            Text(
                                text = "Line ${roast.lineNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionBar(
    onRoastAgain: () -> Unit,
    onShare: () -> Unit,
    onFixCode: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        color = GlassWhite10,
        border = BorderStroke(1.dp, GlassWhite20),
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            DarkGray.copy(alpha = 0.8f),
                            DarkGray.copy(alpha = 0.6f)
                        )
                    )
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Roast Again button
            OutlinedButton(
                onClick = onRoastAgain,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, NeonRed),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NeonRed
                )
            ) {
                Text("🔄 AGAIN")
            }

            // Share button
            Button(
                onClick = onShare,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = DeepBlack
                )
            ) {
                Text("📤 SHARE", fontWeight = FontWeight.Bold)
            }

            // Fix Code button
            Button(
                onClick = onFixCode,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonRed,
                    contentColor = TextPrimary
                )
            ) {
                Text("🔧 FIX", fontWeight = FontWeight.Bold)
            }
        }
    }
}
