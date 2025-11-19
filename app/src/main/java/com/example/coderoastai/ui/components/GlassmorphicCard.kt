package com.example.coderoastai.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.coderoastai.ui.theme.GlassWhite10
import com.example.coderoastai.ui.theme.GlassWhite20

/**
 * Glassmorphic card with frosted glass effect
 *
 * Features:
 * - Semi-transparent background with blur
 * - Subtle border
 * - Gradient overlay
 * - Rounded corners
 */
@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    borderColor: Color = GlassWhite20,
    borderWidth: Dp = 1.dp,
    backgroundColor: Color = GlassWhite10,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        color = Color.Transparent,
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Box(
            modifier = Modifier
                .background(backgroundColor)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            content()
        }
    }
}

/**
 * Elevated glassmorphic card with shadow effect
 */
@Composable
fun ElevatedGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    content: @Composable BoxScope.() -> Unit
) {
    androidx.compose.material3.Card(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = GlassWhite10
        ),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            content()
        }
    }
}
