package com.example.coderoastai.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coderoastai.CodeFixGenerator
import com.example.coderoastai.ui.components.ElevatedGlassCard
import com.example.coderoastai.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Fix Comparison Screen showing side-by-side diff view
 */
@Composable
fun FixComparisonScreen(
    fixResult: CodeFixGenerator.FixResult,
    onApplyFix: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    
    var editedCode by remember { mutableStateOf(fixResult.fixedCode) }
    var canUndo by remember { mutableStateOf(false) }
    var previousCode by remember { mutableStateOf(fixResult.fixedCode) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top: Metrics Card
            ImprovementMetricsCard(
                beforeScore = fixResult.beforeScore,
                afterScore = fixResult.afterScore,
                improvements = fixResult.improvements,
                metricsChange = fixResult.metricsChange
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Middle: Split Screen Code Comparison
            if (isTablet) {
                // Horizontal split for tablets
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CodePanel(
                        title = "ORIGINAL CODE",
                        code = fixResult.originalCode,
                        isReadOnly = true,
                        isDimmed = true,
                        modifier = Modifier.weight(1f)
                    )
                    
                    CodePanel(
                        title = "FIXED CODE",
                        code = editedCode,
                        isReadOnly = false,
                        isDimmed = false,
                        onCodeChange = {
                            previousCode = editedCode
                            editedCode = it
                            canUndo = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                // Vertical split for phones
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CodePanel(
                        title = "ORIGINAL CODE",
                        code = fixResult.originalCode,
                        isReadOnly = true,
                        isDimmed = true,
                        modifier = Modifier.weight(1f)
                    )
                    
                    CodePanel(
                        title = "FIXED CODE",
                        code = editedCode,
                        isReadOnly = false,
                        isDimmed = false,
                        onCodeChange = {
                            previousCode = editedCode
                            editedCode = it
                            canUndo = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Bottom: Action Buttons
            ActionButtons(
                canUndo = canUndo,
                onCopyCode = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    copyToClipboard(context, editedCode)
                },
                onUndo = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    editedCode = previousCode
                    canUndo = false
                },
                onApplyFix = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onApplyFix(editedCode)
                },
                onBack = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onBack()
                }
            )
        }
    }
}

/**
 * Improvement metrics card showing before/after comparison
 */
@Composable
private fun ImprovementMetricsCard(
    beforeScore: Int,
    afterScore: Int,
    improvements: List<CodeFixGenerator.Improvement>,
    metricsChange: CodeFixGenerator.MetricsChange
) {
    var showContent by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(300)
        showContent = true
    }
    
    val improvement = afterScore - beforeScore
    
    ElevatedGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Score Comparison
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Before Score
                ScoreCircle(
                    score = beforeScore,
                    label = "BEFORE",
                    color = NeonRed,
                    animate = showContent
                )
                
                // Arrow
                Text(
                    "→",
                    fontSize = 32.sp,
                    color = NeonCyan
                )
                
                // After Score
                ScoreCircle(
                    score = afterScore,
                    label = "AFTER",
                    color = NeonGreen,
                    animate = showContent
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Improvement Text
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(500)) + expandVertically(tween(500))
            ) {
                Column {
                    Text(
                        text = "IMPROVEMENT: +$improvement POINTS",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        ),
                        color = NeonGreen,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Metrics
                    MetricRow(
                        "Lines",
                        "${metricsChange.linesBefore} → ${metricsChange.linesAfter}",
                        metricsChange.linesAfter < metricsChange.linesBefore
                    )
                    
                    MetricRow(
                        "Complexity",
                        "${metricsChange.complexityBefore} → ${metricsChange.complexityAfter}",
                        metricsChange.complexityAfter < metricsChange.complexityBefore
                    )
                    
                    MetricRow(
                        "Issues Fixed",
                        "${metricsChange.issuesFixed}",
                        true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Fixed Issues Checklist
                    Text(
                        "FIXED ISSUES",
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 1.sp
                        ),
                        color = TextSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    improvements.take(5).forEachIndexed { index, improvement ->
                        ImprovementCheckItem(
                            improvement = improvement,
                            index = index,
                            animate = showContent
                        )
                    }
                    
                    if (improvements.size > 5) {
                        Text(
                            "+ ${improvements.size - 5} more improvements",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary,
                            modifier = Modifier.padding(start = 32.dp, top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Small score circle with animation
 */
@Composable
private fun ScoreCircle(
    score: Int,
    label: String,
    color: Color,
    animate: Boolean
) {
    val animatedScore by animateIntAsState(
        targetValue = if (animate) score else 0,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "score"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(80.dp)
        ) {
            CircularProgressIndicator(
                progress = { animatedScore / 100f },
                modifier = Modifier.fillMaxSize(),
                color = color,
                strokeWidth = 8.dp,
                trackColor = GlassWhite10
            )
            
            Text(
                text = animatedScore.toString(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = color
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

/**
 * Metric comparison row
 */
@Composable
private fun MetricRow(
    label: String,
    value: String,
    isImprovement: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (isImprovement) NeonGreen else TextPrimary
            )
            
            if (isImprovement) {
                Text(
                    "✓",
                    color = NeonGreen,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * Improvement checklist item with animation
 */
@Composable
private fun ImprovementCheckItem(
    improvement: CodeFixGenerator.Improvement,
    index: Int,
    animate: Boolean
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(animate) {
        if (animate) {
            kotlinx.coroutines.delay(index * 100L)
            visible = true
        }
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)) + slideInHorizontally(tween(300))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Animated checkmark
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(NeonGreen.copy(alpha = 0.2f))
                    .border(2.dp, NeonGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "✓",
                    color = NeonGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = improvement.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }
    }
}

/**
 * Code panel with syntax highlighting and scroll
 */
@Composable
private fun CodePanel(
    title: String,
    code: String,
    isReadOnly: Boolean,
    isDimmed: Boolean,
    modifier: Modifier = Modifier,
    onCodeChange: ((String) -> Unit)? = null
) {
    val scrollState = rememberScrollState()
    
    ElevatedGlassCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Surface(
                color = if (isDimmed) MidGray.copy(alpha = 0.5f) else NeonCyan.copy(alpha = 0.2f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 1.sp
                        ),
                        color = if (isDimmed) TextTertiary else NeonCyan
                    )
                    
                    if (!isReadOnly) {
                        Text(
                            "EDITABLE",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonGreen
                        )
                    }
                }
            }
            
            // Code display/editor
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(if (isDimmed) DarkGray.copy(alpha = 0.5f) else DarkGray)
                    .padding(16.dp)
            ) {
                if (isReadOnly) {
                    // Read-only code display
                    Text(
                        text = code,
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = if (isDimmed) TextTertiary else TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .horizontalScroll(rememberScrollState())
                    )
                } else {
                    // Editable code field
                    BasicTextField(
                        value = code,
                        onValueChange = { onCodeChange?.invoke(it) },
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = TextPrimary
                        ),
                        cursorBrush = SolidColor(NeonCyan),
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .horizontalScroll(rememberScrollState())
                    )
                }
            }
            
            // Footer with line count
            Surface(
                color = GlassWhite10,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${code.lines().size} lines",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * Action buttons at bottom
 */
@Composable
private fun ActionButtons(
    canUndo: Boolean,
    onCopyCode: () -> Unit,
    onUndo: () -> Unit,
    onApplyFix: () -> Unit,
    onBack: () -> Unit
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DarkGray.copy(alpha = 0.8f),
                            DarkGray.copy(alpha = 0.6f)
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top row: Copy and Undo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCopyCode,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(2.dp, NeonCyan),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = NeonCyan
                    )
                ) {
                    Text(
                        "📋 COPY CODE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                
                OutlinedButton(
                    onClick = onUndo,
                    enabled = canUndo,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        2.dp,
                        if (canUndo) NeonYellow else GlassWhite20
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (canUndo) NeonYellow else TextTertiary
                    )
                ) {
                    Text(
                        "↶ UNDO",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            
            // Bottom row: Apply and Back
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onApplyFix,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen,
                        contentColor = DeepBlack
                    )
                ) {
                    Text(
                        "✓ APPLY FIX",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(2.dp, NeonRed),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = NeonRed
                    )
                ) {
                    Text(
                        "← BACK",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

/**
 * Copy code to clipboard with toast confirmation
 */
private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Fixed Code", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "✓ Code copied to clipboard", Toast.LENGTH_SHORT).show()
}

// ============================================================================
// PREVIEWS
// ============================================================================

@Preview(showBackground = true, name = "Fix Comparison Screen")
@Composable
fun FixComparisonScreenPreview() {
    val sampleFixResult = CodeFixGenerator.FixResult(
        originalCode = """
            def calc(x, y):
                temp = x + y
                if temp > 0:
                    if temp < 100:
                        return temp
                return 0
        """.trimIndent(),
        fixedCode = """
            def calculate_sum(first_number, second_number):
                # Calculate and validate the sum of two numbers
                total = first_number + second_number
                
                # Validate result is within acceptable range
                if total <= 0:
                    return 0
                if total >= 100:
                    return 0
                    
                return total
        """.trimIndent(),
        improvements = listOf(
            CodeFixGenerator.Improvement("naming", "Renamed 3 variables to be more descriptive", null),
            CodeFixGenerator.Improvement("nesting", "Reduced nesting depth from 3 to 2", null),
            CodeFixGenerator.Improvement("documentation", "Added 1 explanatory comment", null)
        ),
        beforeScore = 45,
        afterScore = 88,
        metricsChange = CodeFixGenerator.MetricsChange(
            linesBefore = 6,
            linesAfter = 9,
            complexityBefore = 3,
            complexityAfter = 2,
            issuesFixed = 3
        )
    )
    
    CodeRoastaiTheme {
        FixComparisonScreen(
            fixResult = sampleFixResult,
            onApplyFix = {},
            onBack = {}
        )
    }
}
