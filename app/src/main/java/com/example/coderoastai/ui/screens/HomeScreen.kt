package com.example.coderoastai.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coderoastai.ui.theme.*
import com.example.coderoastai.CodeExample
import com.example.coderoastai.codeExamples
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape

@Composable
fun CodeRoastMainScreenContent(
    codeInput: String,
    onCodeInputChange: (String) -> Unit,
    language: String,
    onLanguageChange: (String) -> Unit,
    personality: String,
    onPersonalityChange: (String) -> Unit,
    intensity: Int,
    onIntensityChange: (Int) -> Unit,
    roastResults: List<String>,
    isRoasting: Boolean,
    onRoast: () -> Unit,
    onShowExamples: () -> Unit,
    onClearResults: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp, bottom = 100.dp)
    ) {
        // Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CODEROAST.AI",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontSize = 28.sp
                ),
                color = NeonRed,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Your code is RAW...",
                style = MaterialTheme.typography.bodyMedium.copy(
                    letterSpacing = 0.5.sp
                ),
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Language Selection
        LanguageSection(
            selectedLanguage = language,
            onLanguageSelected = onLanguageChange
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Code Input
        CodeInputSection(
            codeInput = codeInput,
            onCodeChanged = onCodeInputChange
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Personality Selection
        PersonalitySection(
            selectedPersonality = personality,
            onPersonalitySelected = onPersonalityChange
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Intensity Slider
        IntensitySection(
            intensity = intensity,
            onIntensityChanged = onIntensityChange
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Roast Button
        RoastButtonSection(
            codeInput = codeInput,
            isRoasting = isRoasting,
            onRoast = onRoast,
            onShowExamples = onShowExamples
        )

        // Results
        if (roastResults.isNotEmpty() && !isRoasting) {
            Spacer(modifier = Modifier.height(32.dp))
            RoastResultsSection(
                roasts = roastResults,
                personality = personality,
                onTryAgain = onClearResults
            )
        }
    }
}

@Composable
private fun LanguageSection(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "LANGUAGE",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Center
        ) {
            listOf(
                "🐍" to "Python",
                "⚡" to "JavaScript",
                "☕" to "Java",
                "🎯" to "Kotlin",
                "⚙️" to "C++"
            ).forEach { (emoji, lang) ->
                LanguageButton(
                    emoji = emoji,
                    language = lang,
                    isSelected = selectedLanguage == lang,
                    onClick = { onLanguageSelected(lang) }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
private fun CodeInputSection(
    codeInput: String,
    onCodeChanged: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "CODE INPUT",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = DarkGray.copy(alpha = 0.8f),
            border = BorderStroke(1.dp, GlassWhite20)
        ) {
            TextField(
                value = codeInput,
                onValueChange = onCodeChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp, max = 350.dp),
                placeholder = {
                    Text(
                        text = "// Paste your code here...\n// Let's roast it! 🔥",
                        color = TextTertiary,
                        style = MaterialTheme.typography.bodyMedium
                    )
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
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

@Composable
private fun PersonalitySection(
    selectedPersonality: String,
    onPersonalitySelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "PERSONALITY",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "🔪" to "Gordon Ramsay",
                "🎖️" to "Drill Sergeant",
                "👔" to "Disappointed Dad",
                "💅" to "Gen Z",
                "🎭" to "Shakespeare"
            ).forEach { (emoji, name) ->
                PersonalityCard(
                    emoji = emoji,
                    name = name.split(" ").last(),
                    fullName = name,
                    isSelected = selectedPersonality == name,
                    onClick = { onPersonalitySelected(name) }
                )
            }
        }
    }
}

@Composable
private fun IntensitySection(
    intensity: Int,
    onIntensityChanged: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "ROAST INTENSITY",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Slider(
            value = intensity.toFloat(),
            onValueChange = { onIntensityChanged(it.toInt()) },
            valueRange = 1f..5f,
            steps = 3,
            colors = SliderDefaults.colors(
                thumbColor = NeonRed,
                activeTrackColor = NeonRed,
                inactiveTrackColor = GlassWhite20
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("😊", "😐", "😠", "😡", "💀").forEachIndexed { index, emoji ->
                IntensityEmoji(emoji = emoji, level = index + 1, currentLevel = intensity)
            }
        }
    }
}

@Composable
private fun RoastButtonSection(
    codeInput: String,
    isRoasting: Boolean,
    onRoast: () -> Unit,
    onShowExamples: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (codeInput.isBlank()) {
            Text(
                text = "⬆️ Enter code above or tap 'Examples' tab ⬇️",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = onRoast,
            enabled = codeInput.isNotBlank() && !isRoasting,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (codeInput.isNotBlank() && !isRoasting) NeonRed else MidGray,
                disabledContainerColor = MidGray.copy(alpha = 0.5f)
            )
        ) {
            if (isRoasting) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = TextPrimary,
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "ROASTING...",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        ),
                        color = TextPrimary
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ROAST MY CODE",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        ),
                        color = if (codeInput.isNotBlank()) TextPrimary else TextTertiary
                    )
                    Text(text = "🔥", fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
private fun LanguageButton(
    emoji: String,
    language: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val displayName = if (language == "JavaScript") "JS" else language
    val backgroundColor = if (isSelected) NeonRed.copy(alpha = 0.2f) else GlassWhite10
    val borderColor = if (isSelected) NeonRed else GlassWhite20
    val textColor = if (isSelected) NeonRed else TextSecondary

    Surface(
        onClick = onClick,
        modifier = Modifier
            .heightIn(min = 48.dp)
            .widthIn(min = 48.dp),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 18.sp)
            Text(
                text = displayName,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = textColor
            )
        }
    }
}

@Composable
private fun PersonalityCard(
    emoji: String,
    name: String,
    fullName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) NeonRed.copy(alpha = 0.2f) else GlassWhite10
    val borderColor = if (isSelected) NeonRed else GlassWhite20
    val textColor = if (isSelected) NeonRed else TextSecondary

    Surface(
        onClick = onClick,
        modifier = Modifier
            .width(90.dp)
            .height(90.dp),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                ),
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun IntensityEmoji(emoji: String, level: Int, currentLevel: Int) {
    val emojiScale by animateFloatAsState(
        targetValue = if (level == currentLevel) 1.3f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Text(
        text = emoji,
        fontSize = (18 * emojiScale).sp,
        modifier = Modifier.scale(emojiScale),
        color = if (level == currentLevel) NeonRed else TextTertiary
    )
}

@Composable
fun RoastResultsSection(
    roasts: List<String>,
    personality: String,
    onTryAgain: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = NeonRed.copy(alpha = 0.15f),
            border = BorderStroke(2.dp, NeonRed)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🔥", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ROAST RESULTS",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ),
                    color = NeonRed
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Roasted by: $personality",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        roasts.forEachIndexed { index, roast ->
            RoastCard(roast = roast, index = index + 1)
            if (index < roasts.size - 1) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action buttons row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Fix Code button
            Button(
                onClick = { /* TODO: Navigate to Fix screen */ },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen,
                    contentColor = DeepBlack
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🔧", fontSize = 18.sp)
                    Text(
                        text = "FIX CODE",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Share button
            OutlinedButton(
                onClick = { /* TODO: Share functionality */ },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                border = BorderStroke(2.dp, NeonCyan)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "📤", fontSize = 18.sp)
                    Text(
                        text = "SHARE",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onTryAgain,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonRed),
            border = BorderStroke(2.dp, NeonRed)
        ) {
            Text(
                text = "↻ Try Different Settings",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun RoastCard(roast: String, index: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = GlassWhite10,
        border = BorderStroke(1.dp, GlassWhite20)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = NeonRed.copy(alpha = 0.2f),
                border = BorderStroke(2.dp, NeonRed)
            ) {
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$index",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = NeonRed
                    )
                }
            }
            Text(
                text = roast,
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 22.sp),
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
