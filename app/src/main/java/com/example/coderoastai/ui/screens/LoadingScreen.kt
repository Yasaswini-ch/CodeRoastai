package com.example.coderoastai.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coderoastai.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Loading screen with rotating funny messages
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoadingScreen() {
    val loadingMessages = listOf(
        "🔥 Heating up the roasting pan...",
        "🔪 Sharpening the knives...",
        "👨‍🍳 Summoning Gordon Ramsay...",
        "💀 Analyzing your crimes against code...",
        "🎯 Finding all the bugs you missed...",
        "🔬 Running code smell detector...",
        "⚡ Charging the roast-o-matic 3000...",
        "🎭 Preparing dramatic insults...",
        "🧨 Loading explosive criticism...",
        "🌶️ Adding extra spice..."
    )
    
    var currentMessageIndex by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            currentMessageIndex = (currentMessageIndex + 1) % loadingMessages.size
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated loading indicator
            NeonLoadingIndicator()
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Rotating message with fade animation
            androidx.compose.animation.AnimatedContent(
                targetState = loadingMessages[currentMessageIndex],
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith
                            fadeOut(animationSpec = tween(500))
                },
                label = "message"
            ) { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium.copy(
                        letterSpacing = 0.5.sp
                    ),
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Shimmer effect bar
            ShimmerLoadingBar()
        }
    }
}

@Composable
fun NeonLoadingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    val colorPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color"
    )
    
    val color = androidx.compose.ui.graphics.lerp(NeonRed, NeonCyan, colorPhase)
    
    CircularProgressIndicator(
        modifier = Modifier.size(80.dp),
        color = color,
        strokeWidth = 6.dp
    )
}

@Composable
fun ShimmerLoadingBar() {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    
    Box(
        modifier = Modifier
            .width(200.dp)
            .height(4.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        NeonRed.copy(alpha = 0.5f),
                        NeonCyan.copy(alpha = 0.5f),
                        Color.Transparent
                    ),
                    start = Offset(shimmerOffset * 1000, 0f),
                    end = Offset((shimmerOffset + 0.5f) * 1000, 0f)
                ),
                shape = RoundedCornerShape(2.dp)
            )
    )
}

/**
 * Skeleton loading card with shimmer effect
 */
@Composable
fun SkeletonLoadingCard(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                GlassWhite10.copy(alpha = shimmerAlpha),
                RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Title skeleton
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(20.dp)
                    .background(
                        GlassWhite20.copy(alpha = shimmerAlpha),
                        RoundedCornerShape(4.dp)
                    )
            )
            
            // Description skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(
                        GlassWhite20.copy(alpha = shimmerAlpha),
                        RoundedCornerShape(4.dp)
                    )
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
                    .background(
                        GlassWhite20.copy(alpha = shimmerAlpha),
                        RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

/**
 * Pulsing dot loader
 */
@Composable
fun PulsingDots(
    modifier: Modifier = Modifier,
    color: Color = NeonRed
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(3) { index ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 600,
                        delayMillis = index * 200,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_$index"
            )
            
            Box(
                modifier = Modifier
                    .size(12.dp * scale)
                    .background(color, androidx.compose.foundation.shape.CircleShape)
            )
        }
    }
}
