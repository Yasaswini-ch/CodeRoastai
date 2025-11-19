package com.example.coderoastai.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coderoastai.IssueType
import com.example.coderoastai.Roast
import com.example.coderoastai.Severity
import com.example.coderoastai.ui.components.ElevatedGlassCard
import com.example.coderoastai.ui.components.TypewriterText
import com.example.coderoastai.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

// Data class for confetti particle
private data class ConfettiParticle(
    val id: Int,
    val startX: Float,
    val startY: Float,
    val color: Color,
    val emoji: String
)

/**
 * Main Results Screen with cyberpunk aesthetic
 */
@Composable
fun RoastResultsScreen(
    roasts: List<Roast>,
    score: Int,
    hasError: Boolean,
    onRoastAgain: () -> Unit,
    onShareRoasts: () -> Unit,
    onShowFixes: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Handle error state
    if (hasError) {
        ErrorState(onRoastAgain)
        return
    }

    // Handle perfect score (no roasts)
    if (roasts.isEmpty()) {
        PerfectCodeState(onRoastAgain)
        return
    }

    // Main content
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        // Background celebrations/effects
        if (score > 80) {
            ConfettiAnimation()
        }

        if (roasts.any { it.severity == Severity.CRITICAL }) {
            FlameParticles()
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Section - Overall Score
            ScoreHeroSection(score = score)

            Spacer(modifier = Modifier.height(24.dp))

            // Middle Section - Roasts List
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section header
                item {
                    Text(
                        text = "INDIVIDUAL ROASTS",
                        style = MaterialTheme.typography.labelLarge.copy(
                            letterSpacing = 1.5.sp
                        ),
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Roast cards with staggered animation
                itemsIndexed(roasts) { index, roast ->
                    RoastCardCyberpunk(
                        roast = roast,
                        index = index
                    )
                }
            }

            // Bottom Section - Action Buttons
            ActionButtonBar(
                onRoastAgain = onRoastAgain,
                onShare = onShareRoasts,
                onShowFixes = onShowFixes
            )
        }
    }
}

/**
 * Hero score section with animated circular progress
 */
@Composable
private fun ScoreHeroSection(score: Int) {
    var showContent by remember { mutableStateOf(false) }

    // Animated score counting
    val animatedScore by animateIntAsState(
        targetValue = if (showContent) score else 0,
        animationSpec = tween(
            durationMillis = 2000,
            easing = FastOutSlowInEasing
        ),
        label = "score"
    )

    // Ring progress animation
    val ringProgress by animateFloatAsState(
        targetValue = if (showContent) score / 100f else 0f,
        animationSpec = tween(
            durationMillis = 2000,
            easing = FastOutSlowInEasing
        ),
        label = "ring"
    )

    // Shake animation for low scores
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(300)
        showContent = true

        // Trigger shake for terrible scores
        if (score < 30) {
            delay(2100) // After count animation
            repeat(3) {
                shakeOffset.animateTo(
                    targetValue = 20f,
                    animationSpec = tween(50)
                )
                shakeOffset.animateTo(
                    targetValue = -20f,
                    animationSpec = tween(50)
                )
            }
            shakeOffset.animateTo(0f, animationSpec = tween(50))
        }
    }

    // Determine colors based on score
    val ringColor = when {
        score >= 75 -> NeonGreen
        score >= 50 -> NeonOrange
        else -> NeonRed
    }

    val grade = when {
        score >= 96 -> "A+"
        score >= 86 -> "A"
        score >= 76 -> "B"
        score >= 61 -> "C"
        score >= 51 -> "D"
        else -> "F"
    }

    val verdict = when {
        score >= 86 -> "ACTUALLY GOOD CODE! 🎉"
        score >= 71 -> "DECENT, BUT NOT GREAT 👍"
        score >= 51 -> "MEDIOCRE AT BEST 😐"
        score >= 31 -> "BARELY FUNCTIONAL GARBAGE 🗑️"
        else -> "THIS CODE IS A CRIME SCENE 🚨"
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
            // Circular Score Indicator with shake
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(220.dp)
                    .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
            ) {
                // Background ring
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = GlassWhite10,
                    strokeWidth = 14.dp,
                    strokeCap = StrokeCap.Round
                )

                // Animated colored ring
                CircularProgressIndicator(
                    progress = { ringProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = ringColor,
                    strokeWidth = 14.dp,
                    strokeCap = StrokeCap.Round
                )

                // Score and grade in center
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = animatedScore.toString(),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 72.sp,
                            fontWeight = FontWeight.Black
                        ),
                        color = ringColor
                    )

                    Text(
                        text = grade,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Verdict with typewriter effect
            if (showContent) {
                TypewriterText(
                    text = verdict,
                    style = MaterialTheme.typography.titleLarge.copy(
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ringColor,
                    delayMs = 30L,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }
    }
}

/**
 * Individual roast card with cyberpunk styling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoastCardCyberpunk(
    roast: Roast,
    index: Int
) {
    var visible by remember { mutableStateOf(false) }
    var isDismissed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index * 150L) // Staggered delay
        visible = true
    }

    if (isDismissed) return

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(500)
        ) + slideInVertically(
            animationSpec = tween(500),
            initialOffsetY = { it / 2 }
        )
    ) {
        val borderColor = when (roast.severity) {
            Severity.CRITICAL -> NeonRed
            Severity.HIGH -> NeonOrange
            Severity.MEDIUM -> NeonYellow
            Severity.LOW -> NeonCyan
        }

        val severityEmoji = when (roast.severity) {
            Severity.LOW -> "🔥"
            Severity.MEDIUM -> "🔥🔥"
            Severity.HIGH -> "🔥🔥🔥"
            Severity.CRITICAL -> "💀"
        }

        val issueColor = when (roast.issueType) {
            IssueType.SECURITY -> NeonRed
            IssueType.PERFORMANCE -> NeonOrange
            IssueType.NAMING -> NeonCyan
            IssueType.STYLE -> NeonPurple
            IssueType.NESTING -> NeonYellow
            IssueType.LENGTH -> NeonGreen
            IssueType.DUPLICATION -> Color(0xFFff6600)
        }

        ElevatedGlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header row with badges and severity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Issue type badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = issueColor.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, issueColor)
                        ) {
                            Text(
                                text = roast.issueType.name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = issueColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // Line number chip (clickable)
                        Surface(
                            onClick = { /* TODO: Highlight line in editor */ },
                            shape = RoundedCornerShape(12.dp),
                            color = NeonCyan.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, NeonCyan)
                        ) {
                            Text(
                                text = "Line ${roast.lineNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // Severity flames
                    Text(
                        text = severityEmoji,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Left border indicator
                Row {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(60.dp)
                            .background(borderColor, RoundedCornerShape(2.dp))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Roast text (selectable for copying)
                    SelectionContainer {
                        Text(
                            text = buildAnnotatedString {
                                // Make certain words bold for emphasis
                                val words = roast.roastText.split(" ")
                                words.forEachIndexed { idx, word ->
                                    if (word.length > 8 || word.any { it.isUpperCase() }) {
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(word)
                                        }
                                    } else {
                                        append(word)
                                    }
                                    if (idx < words.size - 1) append(" ")
                                }
                            },
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = 24.sp
                            ),
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Action button bar at the bottom
 */
@Composable
private fun ActionButtonBar(
    onRoastAgain: () -> Unit,
    onShare: () -> Unit,
    onShowFixes: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

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
            // Roast Again button (outline style)
            OutlinedButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onRoastAgain()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, NeonRed),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NeonRed
                )
            ) {
                Text(
                    "🔄 AGAIN",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Share button (cyan)
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onShare()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = DeepBlack
                )
            ) {
                Text(
                    "📤 SHARE",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Show Fixes button (red)
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onShowFixes()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonRed,
                    contentColor = TextPrimary
                )
            ) {
                Text(
                    "🔧 FIXES",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

/**
 * Confetti animation for high scores
 */
@Composable
private fun ConfettiAnimation() {
    val particles = remember {
        List(30) { index ->
            ConfettiParticle(
                id = index,
                startX = Random.nextFloat(),
                startY = -0.1f,
                color = listOf(NeonRed, NeonCyan, NeonYellow, NeonGreen, NeonPurple).random(),
                emoji = listOf("🎉", "🎊", "✨", "⭐", "🌟").random()
            )
        }
    }

    var animationStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(2200) // After score animation
        animationStarted = true
    }

    if (animationStarted) {
        particles.forEach { particle ->
            var offsetY by remember { mutableStateOf(0f) }

            LaunchedEffect(particle.id) {
                while (true) {
                    offsetY += 2f
                    if (offsetY > 1.2f) {
                        offsetY = -0.1f
                    }
                    delay(16)
                }
            }

            Text(
                text = particle.emoji,
                fontSize = 32.sp,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (particle.startX * 1000).toInt(),
                            (offsetY * 2000).toInt()
                        )
                    }
                    .graphicsLayer {
                        rotationZ = offsetY * 360f
                        alpha = (1f - offsetY).coerceIn(0f, 1f)
                    }
            )
        }
    }
}

/**
 * Flame particles for critical issues
 */
@Composable
private fun FlameParticles() {
    val particles = remember {
        List(15) { index ->
            index to (Random.nextFloat() to Random.nextFloat())
        }
    }

    particles.forEach { (id, position) ->
        var offsetY by remember { mutableStateOf(1f) }

        LaunchedEffect(id) {
            while (true) {
                offsetY -= 0.01f
                if (offsetY < -0.1f) {
                    offsetY = 1f
                }
                delay(16)
            }
        }

        Text(
            text = "🔥",
            fontSize = 24.sp,
            modifier = Modifier
                .offset {
                    IntOffset(
                        (position.first * 1000).toInt(),
                        (offsetY * 2000).toInt()
                    )
                }
                .graphicsLayer {
                    alpha = offsetY.coerceIn(0f, 0.7f)
                }
        )
    }
}

/**
 * Error state with friendly message
 */
@Composable
private fun ErrorState(onRetry: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500)) + scaleIn(tween(500))
        ) {
            ElevatedGlassCard(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(32.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text("💥", fontSize = 80.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "ANALYSIS FAILED",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        ),
                        color = NeonRed
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Something went wrong while roasting your code. Even I have my limits.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onRetry,
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonRed
                        ),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text(
                            "TRY AGAIN",
                            style = MaterialTheme.typography.labelLarge.copy(
                                letterSpacing = 2.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Perfect code state with trophy animation
 */
@Composable
private fun PerfectCodeState(onRoastAgain: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(300)
        visible = true

        // Trophy bounce animation
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(1000))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    "🏆",
                    fontSize = 120.sp,
                    modifier = Modifier.scale(scale.value)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "PERFECT CODE!",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp
                    ),
                    color = Color(0xFFFFD700) // Gold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "I COULDN'T FIND A SINGLE THING TO ROAST",
                    style = MaterialTheme.typography.titleMedium.copy(
                        letterSpacing = 1.sp
                    ),
                    color = NeonCyan,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Congratulations! Your code is actually good. This is rare. Very rare.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onRoastAgain,
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen
                    ),
                    modifier = Modifier.height(60.dp)
                ) {
                    Text(
                        "🔄 ROAST AGAIN",
                        style = MaterialTheme.typography.labelLarge.copy(
                            letterSpacing = 2.sp
                        ),
                        color = DeepBlack
                    )
                }
            }
        }
    }
}

// ============================================================================
// PREVIEWS
// ============================================================================

@Preview(showBackground = true, name = "Good Score (88)")
@Composable
fun RoastResultsScreenPreview_Good() {
    val sampleRoasts = listOf(
        Roast(
            IssueType.NAMING,
            42,
            "Variable 'x' is so generic, it could be used in algebra class. Give it a proper name!",
            Severity.LOW
        ),
        Roast(
            IssueType.STYLE,
            88,
            "This indentation is all over the place. Did you let your cat walk on the keyboard?",
            Severity.MEDIUM
        ),
        Roast(
            IssueType.PERFORMANCE,
            156,
            "This nested loop is slower than my grandmother's dial-up internet. Optimize it!",
            Severity.HIGH
        )
    )

    CodeRoastaiTheme {
        RoastResultsScreen(
            roasts = sampleRoasts,
            score = 88,
            hasError = false,
            onRoastAgain = {},
            onShareRoasts = {},
            onShowFixes = {}
        )
    }
}

@Preview(showBackground = true, name = "Bad Score (25)")
@Composable
fun RoastResultsScreenPreview_Bad() {
    val sampleRoasts = listOf(
        Roast(
            IssueType.SECURITY,
            25,
            "You left a SQL injection vulnerability. Hope you like unexpected database modifications!",
            Severity.CRITICAL
        ),
        Roast(
            IssueType.NESTING,
            72,
            "This is nested deeper than a Russian doll collection. Flatten this mess immediately.",
            Severity.HIGH
        ),
        Roast(
            IssueType.LENGTH,
            110,
            "This function is 500 lines long. I've read shorter novellas. Break it up!",
            Severity.HIGH
        ),
        Roast(
            IssueType.DUPLICATION,
            145,
            "You copied and pasted this code block 7 times. Ever heard of functions?",
            Severity.MEDIUM
        )
    )

    CodeRoastaiTheme {
        RoastResultsScreen(
            roasts = sampleRoasts,
            score = 25,
            hasError = false,
            onRoastAgain = {},
            onShareRoasts = {},
            onShowFixes = {}
        )
    }
}

@Preview(showBackground = true, name = "Perfect Score")
@Composable
fun PerfectScorePreview() {
    CodeRoastaiTheme {
        RoastResultsScreen(
            roasts = emptyList(),
            score = 100,
            hasError = false,
            onRoastAgain = {},
            onShareRoasts = {},
            onShowFixes = {}
        )
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun ErrorStatePreview() {
    CodeRoastaiTheme {
        RoastResultsScreen(
            roasts = emptyList(),
            score = 0,
            hasError = true,
            onRoastAgain = {},
            onShareRoasts = {},
            onShowFixes = {}
        )
    }
}
