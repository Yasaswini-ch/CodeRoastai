package com.example.coderoastai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coderoastai.ui.theme.NeonRed
import com.example.coderoastai.ui.theme.TextPrimary
import com.example.coderoastai.ui.theme.TextSecondary

/**
 * Empty state component with illustration and CTA
 */
@Composable
fun EmptyState(
    emoji: String = "📝",
    title: String = "No code to roast",
    message: String = "Paste some code above and let's see what you've done wrong.",
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "float")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Floating emoji
        Text(
            text = emoji,
            fontSize = 80.sp,
            modifier = Modifier.offset(y = offsetY.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Message
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        // Optional action button
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonRed
                )
            ) {
                Text(text = actionText)
            }
        }
    }
}

/**
 * Error state component
 */
@Composable
fun ErrorState(
    emoji: String = "💥",
    title: String = "Something went wrong",
    message: String = "We couldn't roast your code. Maybe it's too perfect? (Unlikely)",
    actionText: String = "Try Again",
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        emoji = emoji,
        title = title,
        message = message,
        actionText = actionText,
        onAction = onAction,
        modifier = modifier
    )
}
