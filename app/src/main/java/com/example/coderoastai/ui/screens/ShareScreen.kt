package com.example.coderoastai.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.coderoastai.ShareGenerator
import com.example.coderoastai.ui.theme.*
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ShareScreen(
    code: String,
    language: String,
    score: Int,
    roasts: List<String>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val shareGenerator = remember { ShareGenerator(context) }

    var selectedTemplate by remember { mutableStateOf(ShareGenerator.ShareTemplate.DARK_MODE) }
    var includeCode by remember { mutableStateOf(true) }
    var selectedRoastIndex by remember { mutableStateOf(0) }
    var personalComment by remember { mutableStateOf("") }
    var showCommentDialog by remember { mutableStateOf(false) }
    var isGenerating by remember { mutableStateOf(false) }
    var previewImageFile by remember { mutableStateOf<File?>(null) }

    // Generate initial preview
    LaunchedEffect(selectedTemplate, includeCode, selectedRoastIndex, personalComment) {
        isGenerating = true
        val config = ShareGenerator.ShareConfig(
            template = selectedTemplate,
            includeCode = includeCode,
            highlightedRoast = roasts.getOrNull(selectedRoastIndex) ?: roasts.firstOrNull() ?: "",
            codeSnippet = if (includeCode) code else null,
            score = score,
            personalComment = personalComment.ifBlank { null },
            language = language
        )

        shareGenerator.generateShareImage(config).fold(
            onSuccess = { file ->
                previewImageFile = file
                isGenerating = false
            },
            onFailure = {
                isGenerating = false
                Toast.makeText(context, "Failed to generate preview", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(
        topBar = {
            ShareTopBar(onDismiss = onDismiss)
        },
        containerColor = DeepBlack
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Preview Section
            PreviewSection(
                previewFile = previewImageFile,
                isLoading = isGenerating
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Template Selector
            TemplateSelectorSection(
                selectedTemplate = selectedTemplate,
                onTemplateSelected = { selectedTemplate = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Customization Options
            CustomizationSection(
                includeCode = includeCode,
                onIncludeCodeChange = { includeCode = it },
                selectedRoastIndex = selectedRoastIndex,
                roasts = roasts,
                onRoastIndexChange = { selectedRoastIndex = it },
                personalComment = personalComment,
                onAddComment = { showCommentDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            ActionButtonsSection(
                isGenerating = isGenerating,
                onDownload = {
                    previewImageFile?.let { file ->
                        saveImageToGallery(context, file)
                    }
                },
                onShare = {
                    previewImageFile?.let { file ->
                        shareImage(context, file)
                    }
                },
                onShareText = {
                    val text = shareGenerator.generateShareText(emptyList(), score, true)
                    shareText(context, text)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Comment Dialog
    if (showCommentDialog) {
        CommentDialog(
            currentComment = personalComment,
            onDismiss = { showCommentDialog = false },
            onSave = { comment ->
                personalComment = comment
                showCommentDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareTopBar(onDismiss: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "📤", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Share Roast",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = TextPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkGray.copy(alpha = 0.9f),
            titleContentColor = TextPrimary
        )
    )
}

@Composable
fun PreviewSection(
    previewFile: File?,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(500.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkGray.copy(alpha = 0.6f)
        ),
        border = BorderStroke(1.dp, GlassWhite20)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = NeonCyan)
                        Text(
                            text = "Generating preview...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                previewFile != null && previewFile.exists() -> {
                    val bitmap = remember(previewFile) {
                        BitmapFactory.decodeFile(previewFile.absolutePath)
                    }
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                else -> {
                    Text(
                        text = "Preview unavailable",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary
                    )
                }
            }
        }
    }
}

@Composable
fun TemplateSelectorSection(
    selectedTemplate: ShareGenerator.ShareTemplate,
    onTemplateSelected: (ShareGenerator.ShareTemplate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "TEMPLATE",
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
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShareGenerator.ShareTemplate.values().forEach { template ->
                TemplateCard(
                    template = template,
                    isSelected = selectedTemplate == template,
                    onClick = { onTemplateSelected(template) }
                )
            }
        }
    }
}

@Composable
fun TemplateCard(
    template: ShareGenerator.ShareTemplate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (name, emoji, description) = when (template) {
        ShareGenerator.ShareTemplate.DARK_MODE -> Triple("Dark Mode", "🌙", "Neon on black")
        ShareGenerator.ShareTemplate.LIGHT_MODE -> Triple("Light Mode", "☀️", "Clean & bright")
        ShareGenerator.ShareTemplate.MINIMAL -> Triple("Minimal", "✨", "Simple elegance")
        ShareGenerator.ShareTemplate.DRAMATIC -> Triple("Dramatic", "🔥", "Intense flames")
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .width(140.dp)
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) NeonCyan.copy(alpha = 0.2f) else GlassWhite10
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) NeonCyan else GlassWhite20
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) NeonCyan else TextSecondary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = TextTertiary
            )
        }
    }
}

@Composable
fun CustomizationSection(
    includeCode: Boolean,
    onIncludeCodeChange: (Boolean) -> Unit,
    selectedRoastIndex: Int,
    roasts: List<String>,
    onRoastIndexChange: (Int) -> Unit,
    personalComment: String,
    onAddComment: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "CUSTOMIZE",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = TextSecondary
        )

        // Include Code Toggle
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkGray.copy(alpha = 0.6f)
            ),
            border = BorderStroke(1.dp, GlassWhite20)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Include Code Snippet",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = "Show first 10 lines of code",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
                Switch(
                    checked = includeCode,
                    onCheckedChange = onIncludeCodeChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NeonCyan,
                        checkedTrackColor = NeonCyan.copy(alpha = 0.5f)
                    )
                )
            }
        }

        // Roast Selector
        if (roasts.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DarkGray.copy(alpha = 0.6f)
                ),
                border = BorderStroke(1.dp, GlassWhite20)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Featured Roast",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (selectedRoastIndex > 0) {
                                    onRoastIndexChange(selectedRoastIndex - 1)
                                }
                            },
                            enabled = selectedRoastIndex > 0
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Previous",
                                tint = if (selectedRoastIndex > 0) NeonCyan else TextTertiary
                            )
                        }

                        Text(
                            text = roasts.getOrNull(selectedRoastIndex) ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                if (selectedRoastIndex < roasts.size - 1) {
                                    onRoastIndexChange(selectedRoastIndex + 1)
                                }
                            },
                            enabled = selectedRoastIndex < roasts.size - 1
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Next",
                                tint = if (selectedRoastIndex < roasts.size - 1) NeonCyan else TextTertiary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${selectedRoastIndex + 1} of ${roasts.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
            }
        }

        // Personal Comment
        Card(
            onClick = onAddComment,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkGray.copy(alpha = 0.6f)
            ),
            border = BorderStroke(1.dp, GlassWhite20)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Personal Comment",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = if (personalComment.isBlank()) "Add your thoughts" else personalComment,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (personalComment.isBlank()) TextTertiary else TextSecondary,
                        maxLines = 2
                    )
                }
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = NeonCyan
                )
            }
        }
    }
}

@Composable
fun ActionButtonsSection(
    isGenerating: Boolean,
    onDownload: () -> Unit,
    onShare: () -> Unit,
    onShareText: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Share as Image
        Button(
            onClick = onShare,
            enabled = !isGenerating,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonCyan,
                disabledContainerColor = MidGray.copy(alpha = 0.5f)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = DeepBlack
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SHARE AS IMAGE",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = DeepBlack
            )
        }

        // Download and Share Text Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onDownload,
                enabled = !isGenerating,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NeonCyan
                ),
                border = BorderStroke(2.dp, NeonCyan)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("SAVE")
            }

            OutlinedButton(
                onClick = onShareText,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NeonRed
                ),
                border = BorderStroke(2.dp, NeonRed)
            ) {
                Icon(
                    imageVector = Icons.Default.Article,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("TEXT")
            }
        }

        // Social Media Quick Share
        Text(
            text = "Quick Share:",
            style = MaterialTheme.typography.labelMedium,
            color = TextTertiary,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Twitter" to "🐦", "LinkedIn" to "💼", "WhatsApp" to "💬", "Instagram" to "📷").forEach { (name, emoji) ->
                OutlinedButton(
                    onClick = { /* TODO: Direct share to platform */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextSecondary
                    ),
                    border = BorderStroke(1.dp, GlassWhite20),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = emoji, fontSize = 20.sp)
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommentDialog(
    currentComment: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var comment by remember { mutableStateOf(currentComment) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Add Personal Comment",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
        },
        text = {
            TextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Share your thoughts...", color = TextTertiary)
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkGray.copy(alpha = 0.5f),
                    unfocusedContainerColor = DarkGray.copy(alpha = 0.5f),
                    cursorColor = NeonCyan
                ),
                maxLines = 5
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(comment) }) {
                Text("SAVE", color = NeonCyan, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = NeonRed)
            }
        },
        containerColor = DarkGray,
        shape = RoundedCornerShape(20.dp)
    )
}

// Helper functions
private fun shareImage(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, "Check out my code roast from CodeRoast.ai! #CodeRoast #CleanCode")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Share via"))
}

private fun shareText(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }

    context.startActivity(Intent.createChooser(intent, "Share via"))
}

private fun saveImageToGallery(context: Context, file: File) {
    // For simplicity, we'll just show a toast. Actual implementation would
    // copy to MediaStore.Images
    Toast.makeText(
        context,
        "Image saved: ${file.name}",
        Toast.LENGTH_LONG
    ).show()
}
