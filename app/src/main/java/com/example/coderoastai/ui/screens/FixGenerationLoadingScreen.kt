package com.example.coderoastai.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coderoastai.ui.theme.*

/**
 * Loading screen shown while AI generates code fix
 */
@Composable
fun FixGenerationLoadingScreen(
    percentage: Int,
    message: String,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    
    // Rotating animation for the outer circle
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    // Pulsing animation
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    // Animated progress bar fill
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage.toFloat(),
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "percentage"
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Title
            Text(
                text = "GENERATING FIX",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                ),
                color = NeonCyan,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Central loading animation
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                // Outer rotating circle
                CircularProgressIndicator(
                    progress = { 0.75f },
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotation),
                    color = NeonCyan.copy(alpha = 0.3f),
                    strokeWidth = 4.dp,
                    trackColor = Color.Transparent
                )
                
                // Inner pulsing circle
                CircularProgressIndicator(
                    progress = { animatedPercentage / 100f },
                    modifier = Modifier
                        .fillMaxSize(0.8f)
                        .scale(pulseScale),
                    color = NeonCyan,
                    strokeWidth = 8.dp,
                    trackColor = GlassWhite10
                )
                
                // Percentage text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${percentage}%",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = NeonCyan
                    )
                    
                    Text(
                        text = "AI working...",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress bar
            ProgressBarWithLabel(
                percentage = percentage,
                animatedPercentage = animatedPercentage
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Status message with animation
            AnimatedStatusMessage(message = message)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Stages indicator
            StagesIndicator(percentage = percentage)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Cancel button
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, NeonRed.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NeonRed
                )
            ) {
                Text(
                    "CANCEL",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    }
}

/**
 * Progress bar with animated fill and percentage label
 */
@Composable
private fun ProgressBarWithLabel(
    percentage: Int,
    animatedPercentage: Float
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(GlassWhite10)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedPercentage / 100f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                NeonCyan,
                                NeonCyan.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Milestone labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf(0, 25, 50, 75, 100).forEach { milestone ->
                Text(
                    text = "$milestone%",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = if (percentage >= milestone) NeonCyan else TextTertiary
                )
            }
        }
    }
}

/**
 * Animated status message that fades in/out
 */
@Composable
private fun AnimatedStatusMessage(message: String) {
    var displayMessage by remember { mutableStateOf(message) }
    var visible by remember { mutableStateOf(true) }
    
    LaunchedEffect(message) {
        if (message != displayMessage) {
            visible = false
            kotlinx.coroutines.delay(200)
            displayMessage = message
            visible = true
        }
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)) + expandVertically(tween(300)),
        exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkGray.copy(alpha = 0.6f)
            ),
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                Text(
                    text = "⚙️",
                    fontSize = 24.sp,
                    modifier = Modifier.scale(pulsingScale())
                )
                
                // Message
                Text(
                    text = displayMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary
                )
            }
        }
    }
}

/**
 * Stage indicators showing progress through phases
 */
@Composable
private fun StagesIndicator(percentage: Int) {
    val stages = listOf(
        Stage(0..20, "Analyzing", "🔍"),
        Stage(21..50, "Refactoring", "🔧"),
        Stage(51..80, "Optimizing", "⚡"),
        Stage(81..100, "Finalizing", "✨")
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        stages.forEach { stage ->
            StageItem(
                stage = stage,
                isActive = percentage in stage.range,
                isComplete = percentage > stage.range.last
            )
        }
    }
}

/**
 * Individual stage item
 */
@Composable
private fun StageItem(
    stage: Stage,
    isActive: Boolean,
    isComplete: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.scale(scale)
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    when {
                        isComplete -> NeonGreen.copy(alpha = 0.2f)
                        isActive -> NeonCyan.copy(alpha = 0.2f)
                        else -> GlassWhite10
                    }
                )
                .then(
                    if (isActive || isComplete) {
                        Modifier.border(
                            2.dp,
                            if (isComplete) NeonGreen else NeonCyan,
                            RoundedCornerShape(12.dp)
                        )
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isComplete) "✓" else stage.emoji,
                fontSize = 24.sp,
                color = when {
                    isComplete -> NeonGreen
                    isActive -> NeonCyan
                    else -> TextTertiary
                }
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Label
        Text(
            text = stage.name,
            style = MaterialTheme.typography.labelSmall,
            color = when {
                isComplete -> NeonGreen
                isActive -> NeonCyan
                else -> TextTertiary
            },
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * Helper to create pulsing scale animation
 */
@Composable
private fun pulsingScale(): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    return scale
}

/**
 * Data class for stage
 */
private data class Stage(
    val range: IntRange,
    val name: String,
    val emoji: String
)

// ============================================================================
// PREVIEWS
// ============================================================================

@Preview(showBackground = true, name = "Fix Generation Loading - 25%")
@Composable
fun FixGenerationLoadingScreen25Preview() {
    CodeRoastaiTheme {
        FixGenerationLoadingScreen(
            percentage = 25,
            message = "Analyzing code structure...",
            onCancel = {}
        )
    }
}

@Preview(showBackground = true, name = "Fix Generation Loading - 60%")
@Composable
fun FixGenerationLoadingScreen60Preview() {
    CodeRoastaiTheme {
        FixGenerationLoadingScreen(
            percentage = 60,
            message = "Generating improvements...",
            onCancel = {}
        )
    }
}

@Preview(showBackground = true, name = "Fix Generation Loading - 95%")
@Composable
fun FixGenerationLoadingScreen95Preview() {
    CodeRoastaiTheme {
        FixGenerationLoadingScreen(
            percentage = 95,
            message = "Polishing code...",
            onCancel = {}
        )
    }
}
