package com.example.coderoastai

import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Generates shareable images and formatted text for social media
 */
class ShareGenerator(private val context: Context) {

    companion object {
        private const val TAG = "ShareGenerator"

        // Image dimensions (Instagram Story format)
        private const val IMAGE_WIDTH = 1080
        private const val IMAGE_HEIGHT = 1920

        // Compression quality
        private const val JPEG_QUALITY = 90
        private const val MAX_FILE_SIZE = 2 * 1024 * 1024 // 2MB
    }

    /**
     * Share template styles
     */
    enum class ShareTemplate {
        DARK_MODE,      // Black background, neon accents
        LIGHT_MODE,     // White background, red accents
        MINIMAL,        // Clean, lots of whitespace
        DRAMATIC        // Flames, intense colors
    }

    /**
     * Configuration for share image
     */
    data class ShareConfig(
        val template: ShareTemplate = ShareTemplate.DARK_MODE,
        val includeCode: Boolean = true,
        val highlightedRoast: String,
        val codeSnippet: String? = null,
        val score: Int,
        val personalComment: String? = null,
        val language: String = "Code"
    )

    /**
     * Generates a shareable image
     */
    suspend fun generateShareImage(config: ShareConfig): Result<File> =
        withContext(Dispatchers.IO) {
            try {
                // Create bitmap
                val bitmap = Bitmap.createBitmap(
                    IMAGE_WIDTH,
                    IMAGE_HEIGHT,
                    Bitmap.Config.ARGB_8888
                )

                val canvas = Canvas(bitmap)

                // Draw based on template
                when (config.template) {
                    ShareTemplate.DARK_MODE -> drawDarkModeTemplate(canvas, config)
                    ShareTemplate.LIGHT_MODE -> drawLightModeTemplate(canvas, config)
                    ShareTemplate.MINIMAL -> drawMinimalTemplate(canvas, config)
                    ShareTemplate.DRAMATIC -> drawDramaticTemplate(canvas, config)
                }

                // Save to file
                val file = saveBitmapToFile(bitmap, config.template)

                // Recycle bitmap
                bitmap.recycle()

                Result.success(file)
            } catch (e: Exception) {
                Log.e(TAG, "Error generating share image", e)
                Result.failure(e)
            }
        }

    /**
     * Generates formatted text for sharing
     */
    fun generateShareText(
        roasts: List<Roast>,
        score: Int,
        includeHashtags: Boolean = true
    ): String {
        val sb = StringBuilder()

        // Header
        sb.appendLine("🔥 CodeRoast.ai Analysis Results 🔥")
        sb.appendLine()

        // Score
        sb.appendLine("Overall Score: $score/100")
        val grade = when {
            score >= 96 -> "A+"
            score >= 86 -> "A"
            score >= 76 -> "B"
            score >= 61 -> "C"
            score >= 51 -> "D"
            else -> "F"
        }
        sb.appendLine("Grade: $grade")
        sb.appendLine()

        // Top roasts
        sb.appendLine("Top Roasts:")
        roasts.take(5).forEachIndexed { index, roast ->
            sb.appendLine("${index + 1}. ${roast.roastText}")
            sb.appendLine("   ├─ Type: ${roast.issueType.name}")
            sb.appendLine("   └─ Line: ${roast.lineNumber}")
            sb.appendLine()
        }

        // Call to action
        sb.appendLine("Want to improve your code?")
        sb.appendLine("Get roasted at CodeRoast.ai")

        // Hashtags
        if (includeHashtags) {
            sb.appendLine()
            sb.append("#CodeRoast #CleanCode #Programming #CodeReview #SoftwareEngineering")
        }

        return sb.toString()
    }

    /**
     * Dark mode template - black background with neon accents
     */
    private fun drawDarkModeTemplate(canvas: Canvas, config: ShareConfig) {
        // Background gradient
        val gradient = LinearGradient(
            0f, 0f, 0f, IMAGE_HEIGHT.toFloat(),
            intArrayOf(
                Color.parseColor("#0a0a0a"), // Deep black
                Color.parseColor("#1a1a1a"), // Dark gray
                Color.parseColor("#0a0a0a")  // Deep black
            ),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        val bgPaint = Paint().apply {
            shader = gradient
        }
        canvas.drawRect(0f, 0f, IMAGE_WIDTH.toFloat(), IMAGE_HEIGHT.toFloat(), bgPaint)

        // Neon glow effect overlay
        drawNeonOverlay(canvas)

        var currentY = 100f

        // App branding
        currentY = drawBranding(canvas, currentY, Color.parseColor("#00f0ff"))
        currentY += 80f

        // Score badge
        drawScoreBadge(canvas, config.score, Color.parseColor("#ff0844"))

        // Code snippet (if included)
        if (config.includeCode && config.codeSnippet != null) {
            currentY = drawCodeSnippet(
                canvas,
                config.codeSnippet,
                currentY,
                Color.parseColor("#00f0ff"),
                Color.parseColor("#1a1a1a")
            )
            currentY += 60f
        }

        // Highlighted roast
        currentY = drawRoastQuote(
            canvas,
            config.highlightedRoast,
            currentY,
            Color.parseColor("#ff0844")
        )
        currentY += 60f

        // Personal comment (if provided)
        if (!config.personalComment.isNullOrBlank()) {
            currentY = drawPersonalComment(
                canvas,
                config.personalComment,
                currentY,
                Color.parseColor("#00ff88")
            )
            currentY += 40f
        }

        // Footer
        drawFooter(canvas, Color.parseColor("#00f0ff"))
    }

    /**
     * Light mode template - white background with red accents
     */
    private fun drawLightModeTemplate(canvas: Canvas, config: ShareConfig) {
        // Background
        canvas.drawColor(Color.WHITE)

        // Subtle pattern
        drawLightPattern(canvas)

        var currentY = 100f

        // App branding
        currentY = drawBranding(canvas, currentY, Color.parseColor("#ff0844"))
        currentY += 80f

        // Score badge
        drawScoreBadge(canvas, config.score, Color.parseColor("#ff0844"))

        // Code snippet
        if (config.includeCode && config.codeSnippet != null) {
            currentY = drawCodeSnippet(
                canvas,
                config.codeSnippet,
                currentY,
                Color.parseColor("#333333"),
                Color.parseColor("#f5f5f5")
            )
            currentY += 60f
        }

        // Highlighted roast
        currentY = drawRoastQuote(
            canvas,
            config.highlightedRoast,
            currentY,
            Color.parseColor("#d32f2f")
        )
        currentY += 60f

        // Personal comment
        if (!config.personalComment.isNullOrBlank()) {
            currentY = drawPersonalComment(
                canvas,
                config.personalComment,
                currentY,
                Color.parseColor("#666666")
            )
        }

        // Footer
        drawFooter(canvas, Color.parseColor("#ff0844"))
    }

    /**
     * Minimal template - clean with lots of whitespace
     */
    private fun drawMinimalTemplate(canvas: Canvas, config: ShareConfig) {
        // Background
        canvas.drawColor(Color.parseColor("#fafafa"))

        var currentY = 150f

        // Simple branding
        val brandPaint = TextPaint().apply {
            color = Color.parseColor("#333333")
            textSize = 48f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val brandText = "CODEROAST"
        val brandWidth = brandPaint.measureText(brandText)
        canvas.drawText(brandText, (IMAGE_WIDTH - brandWidth) / 2, currentY, brandPaint)

        currentY += 200f

        // Large score
        val scorePaint = TextPaint().apply {
            color = Color.parseColor("#ff0844")
            textSize = 200f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val scoreText = "${config.score}"
        val scoreWidth = scorePaint.measureText(scoreText)
        canvas.drawText(scoreText, (IMAGE_WIDTH - scoreWidth) / 2, currentY, scorePaint)

        currentY += 100f

        // Roast quote (centered, large)
        currentY = drawCenteredQuote(
            canvas,
            config.highlightedRoast,
            currentY,
            Color.parseColor("#333333")
        )

        // Simple footer
        val footerPaint = TextPaint().apply {
            color = Color.parseColor("#999999")
            textSize = 32f
            isAntiAlias = true
        }
        val footerText = "CodeRoast.ai"
        val footerWidth = footerPaint.measureText(footerText)
        canvas.drawText(
            footerText,
            (IMAGE_WIDTH - footerWidth) / 2,
            IMAGE_HEIGHT - 100f,
            footerPaint
        )
    }

    /**
     * Dramatic template - flames and intense colors
     */
    private fun drawDramaticTemplate(canvas: Canvas, config: ShareConfig) {
        // Gradient background (red to black)
        val gradient = LinearGradient(
            0f, 0f, 0f, IMAGE_HEIGHT.toFloat(),
            intArrayOf(
                Color.parseColor("#ff0844"),
                Color.parseColor("#cc0034"),
                Color.parseColor("#0a0a0a")
            ),
            floatArrayOf(0f, 0.3f, 1f),
            Shader.TileMode.CLAMP
        )
        val bgPaint = Paint().apply {
            shader = gradient
        }
        canvas.drawRect(0f, 0f, IMAGE_WIDTH.toFloat(), IMAGE_HEIGHT.toFloat(), bgPaint)

        // Fire emoji decorations
        val firePaint = TextPaint().apply {
            textSize = 120f
            isAntiAlias = true
        }
        canvas.drawText("🔥", 50f, 200f, firePaint)
        canvas.drawText("🔥", IMAGE_WIDTH - 150f, 300f, firePaint)
        canvas.drawText("🔥", 100f, IMAGE_HEIGHT - 300f, firePaint)
        canvas.drawText("🔥", IMAGE_WIDTH - 200f, IMAGE_HEIGHT - 500f, firePaint)

        var currentY = 250f

        // Dramatic branding
        currentY = drawBranding(canvas, currentY, Color.WHITE)
        currentY += 100f

        // Large score
        val scorePaint = TextPaint().apply {
            color = Color.WHITE
            textSize = 180f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            setShadowLayer(10f, 0f, 0f, Color.BLACK)
        }
        val scoreText = "${config.score}/100"
        val scoreWidth = scorePaint.measureText(scoreText)
        canvas.drawText(scoreText, (IMAGE_WIDTH - scoreWidth) / 2, currentY, scorePaint)

        currentY += 150f

        // Roast in white with shadow
        val roastPaint = TextPaint().apply {
            color = Color.WHITE
            textSize = 56f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            setShadowLayer(8f, 0f, 0f, Color.parseColor("#cc0034"))
        }

        val maxWidth = IMAGE_WIDTH - 160
        val layout = StaticLayout.Builder.obtain(
            config.highlightedRoast,
            0,
            config.highlightedRoast.length,
            roastPaint,
            maxWidth
        ).build()

        canvas.save()
        canvas.translate(80f, currentY)
        layout.draw(canvas)
        canvas.restore()

        // Footer
        drawFooter(canvas, Color.WHITE)
    }

    // ========================================================================
    // Helper drawing methods
    // ========================================================================

    private fun drawNeonOverlay(canvas: Canvas) {
        val overlayPaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = Color.parseColor("#00f0ff")
            alpha = 30
        }

        // Draw some accent lines
        canvas.drawLine(100f, 80f, IMAGE_WIDTH - 100f, 80f, overlayPaint)
        canvas.drawLine(
            100f,
            IMAGE_HEIGHT - 80f,
            IMAGE_WIDTH - 100f,
            IMAGE_HEIGHT - 80f,
            overlayPaint
        )
    }

    private fun drawLightPattern(canvas: Canvas) {
        val patternPaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 1f
            color = Color.parseColor("#eeeeee")
        }

        // Draw grid pattern
        for (i in 0..20) {
            val y = i * 100f
            canvas.drawLine(0f, y, IMAGE_WIDTH.toFloat(), y, patternPaint)
        }
    }

    private fun drawBranding(canvas: Canvas, y: Float, color: Int): Float {
        // App name
        val titlePaint = TextPaint().apply {
            this.color = color
            textSize = 72f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            letterSpacing = 0.15f
            isAntiAlias = true
        }

        val title = "CODEROAST.AI"
        val titleWidth = titlePaint.measureText(title)
        val titleX = (IMAGE_WIDTH - titleWidth) / 2

        canvas.drawText(title, titleX, y, titlePaint)

        return y + 20f
    }

    private fun drawScoreBadge(canvas: Canvas, score: Int, color: Int) {
        val badgeSize = 200f
        val badgeX = IMAGE_WIDTH - badgeSize - 50f
        val badgeY = 100f

        // Circle background
        val circlePaint = Paint().apply {
            this.color = color
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(
            badgeX + badgeSize / 2,
            badgeY + badgeSize / 2,
            badgeSize / 2,
            circlePaint
        )

        // Score text
        val scorePaint = TextPaint().apply {
            this.color = Color.WHITE
            textSize = 80f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText(
            "$score",
            badgeX + badgeSize / 2,
            badgeY + badgeSize / 2 + 30f,
            scorePaint
        )
    }

    private fun drawCodeSnippet(
        canvas: Canvas,
        code: String,
        y: Float,
        textColor: Int,
        bgColor: Int
    ): Float {
        val codeLines = code.lines().take(10)
        val padding = 40f
        val lineHeight = 45f
        val codeHeight = codeLines.size * lineHeight + padding * 2

        // Background card
        val cardPaint = Paint().apply {
            color = bgColor
            isAntiAlias = true
        }
        val cardRect = RectF(
            80f,
            y,
            IMAGE_WIDTH - 80f,
            y + codeHeight
        )
        canvas.drawRoundRect(cardRect, 20f, 20f, cardPaint)

        // Code text
        val codePaint = TextPaint().apply {
            color = textColor
            textSize = 32f
            typeface = Typeface.MONOSPACE
            isAntiAlias = true
        }

        var currentCodeY = y + padding + 35f
        codeLines.forEach { line ->
            val trimmedLine = if (line.length > 50) line.take(47) + "..." else line
            canvas.drawText(trimmedLine, 120f, currentCodeY, codePaint)
            currentCodeY += lineHeight
        }

        return y + codeHeight
    }

    private fun drawRoastQuote(
        canvas: Canvas,
        quote: String,
        y: Float,
        color: Int
    ): Float {
        val quotePaint = TextPaint().apply {
            this.color = color
            textSize = 52f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val formattedQuote = "\"$quote\""
        val maxWidth = IMAGE_WIDTH - 160

        val layout = StaticLayout.Builder.obtain(
            formattedQuote,
            0,
            formattedQuote.length,
            quotePaint,
            maxWidth
        ).build()

        canvas.save()
        canvas.translate(80f, y)
        layout.draw(canvas)
        canvas.restore()

        return y + layout.height
    }

    private fun drawCenteredQuote(
        canvas: Canvas,
        quote: String,
        y: Float,
        color: Int
    ): Float {
        val quotePaint = TextPaint().apply {
            this.color = color
            textSize = 48f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        val formattedQuote = "\"$quote\""
        val maxWidth = IMAGE_WIDTH - 200

        val layout = StaticLayout.Builder.obtain(
            formattedQuote,
            0,
            formattedQuote.length,
            quotePaint,
            maxWidth
        ).setAlignment(Layout.Alignment.ALIGN_CENTER).build()

        canvas.save()
        canvas.translate((IMAGE_WIDTH / 2).toFloat(), y)
        layout.draw(canvas)
        canvas.restore()

        return y + layout.height
    }

    private fun drawPersonalComment(
        canvas: Canvas,
        comment: String,
        y: Float,
        color: Int
    ): Float {
        val commentPaint = TextPaint().apply {
            this.color = color
            textSize = 38f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            isAntiAlias = true
        }

        val formattedComment = "— $comment"
        val maxWidth = IMAGE_WIDTH - 160

        val layout = StaticLayout.Builder.obtain(
            formattedComment,
            0,
            formattedComment.length,
            commentPaint,
            maxWidth
        ).build()

        canvas.save()
        canvas.translate(80f, y)
        layout.draw(canvas)
        canvas.restore()

        return y + layout.height
    }

    private fun drawFooter(canvas: Canvas, color: Int) {
        val footerPaint = TextPaint().apply {
            this.color = color
            textSize = 36f
            isAntiAlias = true
        }

        val footerText = "Roasted by CodeRoast.ai"
        val footerWidth = footerPaint.measureText(footerText)
        val footerX = (IMAGE_WIDTH - footerWidth) / 2
        val footerY = IMAGE_HEIGHT - 80f

        canvas.drawText(footerText, footerX, footerY, footerPaint)

        // Watermark line
        val linePaint = Paint().apply {
            this.color = color
            strokeWidth = 2f
            alpha = 100
        }
        canvas.drawLine(
            IMAGE_WIDTH / 4f,
            IMAGE_HEIGHT - 120f,
            IMAGE_WIDTH * 3 / 4f,
            IMAGE_HEIGHT - 120f,
            linePaint
        )
    }

    /**
     * Saves bitmap to file
     */
    private fun saveBitmapToFile(bitmap: Bitmap, template: ShareTemplate): File {
        val cacheDir = File(context.cacheDir, "share_images")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }

        val fileName = "coderoast_${template.name.lowercase()}_${System.currentTimeMillis()}.jpg"
        val file = File(cacheDir, fileName)

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
        }

        // Check file size
        if (file.length() > MAX_FILE_SIZE) {
            Log.w(TAG, "File size exceeds max: ${file.length()} bytes")
        }

        return file
    }
}
