package com.example.coderoastai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.coderoastai.ui.theme.NeonRed
import kotlinx.coroutines.delay

/**
 * Text with neon glow effect
 */
@Composable
fun NeonText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = NeonRed,
    glowColor: Color = color,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    animated: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "neon_glow")

    val glowRadius by if (animated) {
        infiniteTransition.animateFloat(
            initialValue = 8f,
            targetValue = 16f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glow"
        )
    } else {
        infiniteTransition.animateFloat(
            initialValue = 12f,
            targetValue = 12f,
            animationSpec = infiniteRepeatable(
                animation = tween(1),
                repeatMode = RepeatMode.Restart
            ),
            label = "static_glow"
        )
    }

    Text(
        text = text,
        modifier = modifier.drawBehind {
            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint()
                paint.color = glowColor.toArgb()
                paint.setShadowLayer(
                    glowRadius,
                    0f,
                    0f,
                    glowColor.copy(alpha = 0.8f).toArgb()
                )
                canvas.nativeCanvas.drawText(
                    text,
                    0f,
                    0f,
                    paint
                )
            }
        },
        color = color,
        style = style
    )
}

/**
 * Typewriter effect text animation
 */
@Composable
fun TypewriterText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = Color.White,
    delayMs: Long = 50L
) {
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(text) {
        displayedText = ""
        text.forEachIndexed { index, char ->
            displayedText += char
            delay(delayMs)
        }
    }

    Text(
        text = displayedText,
        modifier = modifier,
        style = style,
        color = color
    )
}
