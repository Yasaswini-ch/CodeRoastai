package com.example.coderoastai

import android.util.Log
import com.runanywhere.sdk.public.RunAnywhere
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Generates fixed/refactored code using RunAnywhere SDK
 *
 * Takes original code with analysis results and produces improved version
 * that fixes all detected issues while preserving original logic.
 */
class CodeFixGenerator {

    companion object {
        private const val TAG = "CodeFixGenerator"
    }

    /**
     * Result of code fix generation
     */
    data class FixResult(
        val originalCode: String,
        val fixedCode: String,
        val improvements: List<Improvement>,
        val beforeScore: Int,
        val afterScore: Int,
        val metricsChange: MetricsChange
    )

    /**
     * Individual improvement made
     */
    data class Improvement(
        val type: String, // "naming", "nesting", "duplication", etc.
        val description: String,
        val lineNumber: Int?
    )

    /**
     * Code metrics before/after
     */
    data class MetricsChange(
        val linesBefore: Int,
        val linesAfter: Int,
        val complexityBefore: Int,
        val complexityAfter: Int,
        val issuesFixed: Int
    )

    /**
     * Progress update during generation
     */
    data class ProgressUpdate(
        val percentage: Int,
        val message: String
    )

    /**
     * Generates fixed code with progress updates
     */
    suspend fun generateFix(
        originalCode: String,
        roasts: List<Roast>,
        language: String,
        beforeScore: Int
    ): Flow<GenerationState> = flow {
        emit(GenerationState.Loading(0, "Analyzing code structure..."))

        try {
            // Build comprehensive prompt for code fixing
            val fixPrompt = buildFixPrompt(originalCode, roasts, language)

            emit(GenerationState.Loading(20, "Generating improvements..."))

            // Generate fixed code using RunAnywhere SDK
            var fixedCode = ""
            var tokenCount = 0

            RunAnywhere.generateStream(fixPrompt).collect { token ->
                fixedCode += token
                tokenCount++

                // Update progress based on token generation
                val progress = 20 + (tokenCount / 10).coerceAtMost(60)
                if (tokenCount % 50 == 0) {
                    emit(GenerationState.Loading(progress, "Polishing code..."))
                }
            }

            emit(GenerationState.Loading(90, "Validating fixes..."))

            // Extract code block if wrapped in markdown
            val cleanedCode = extractCodeBlock(fixedCode)

            // Validate the fixed code
            if (!isValidCode(cleanedCode, language)) {
                Log.w(TAG, "Generated code failed validation")
                emit(GenerationState.Error("Generated code is not syntactically valid. Please try again."))
                return@flow
            }

            emit(GenerationState.Loading(95, "Calculating improvements..."))

            // Analyze improvements
            val improvements = detectImprovements(originalCode, cleanedCode, roasts)
            val metricsChange = calculateMetrics(originalCode, cleanedCode, roasts.size)
            val afterScore = estimateScore(cleanedCode, language)

            // Create result
            val result = FixResult(
                originalCode = originalCode,
                fixedCode = cleanedCode,
                improvements = improvements,
                beforeScore = beforeScore,
                afterScore = afterScore,
                metricsChange = metricsChange
            )

            emit(GenerationState.Success(result))

        } catch (e: Exception) {
            Log.e(TAG, "Error generating fix", e)
            emit(GenerationState.Error("Failed to generate fix: ${e.message}"))
        }
    }

    /**
     * Builds the AI prompt for code fixing
     */
    private fun buildFixPrompt(
        originalCode: String,
        roasts: List<Roast>,
        language: String
    ): String {
        val issuesList = roasts.joinToString("\n") { roast ->
            "- Line ${roast.lineNumber}: ${roast.issueType.name} - ${roast.roastText}"
        }

        return """
            You are an expert $language developer. Refactor the following code to fix all detected issues.
            
            DETECTED ISSUES:
            $issuesList
            
            REQUIREMENTS:
            1. Fix all naming issues - use meaningful, descriptive variable and function names
            2. Break down long functions into smaller, focused functions
            3. Reduce nesting depth using early returns and guard clauses
            4. Add proper error handling with try-catch blocks where appropriate
            5. Remove all code duplication by extracting common logic
            6. Add clear, concise comments explaining complex logic
            7. Follow $language best practices and conventions
            8. Preserve the original functionality completely - do not change the logic
            9. Format code properly with consistent indentation
            
            ORIGINAL CODE:
            ```$language
            $originalCode
            ```
            
            Provide ONLY the refactored code wrapped in a code block. Do not include explanations.
            
            REFACTORED CODE:
        """.trimIndent()
    }

    /**
     * Extracts code from markdown code block
     */
    private fun extractCodeBlock(text: String): String {
        // Look for ```language ... ``` pattern
        val codeBlockRegex = "```\\w*\\n([\\s\\S]*?)```".toRegex()
        val match = codeBlockRegex.find(text)

        return if (match != null) {
            match.groupValues[1].trim()
        } else {
            // If no code block found, return as-is (might be plain code)
            text.trim()
        }
    }

    /**
     * Basic validation of generated code
     */
    private fun isValidCode(code: String, language: String): Boolean {
        if (code.isBlank()) return false

        // Basic syntax checks based on language
        return when (language.lowercase()) {
            "python" -> {
                // Python: Check for basic structure
                val hasIndentation =
                    code.lines().any { it.startsWith("    ") || it.startsWith("\t") }
                val hasValidSyntax = !code.contains("```") && code.trim().isNotEmpty()
                hasIndentation && hasValidSyntax
            }

            "javascript", "typescript" -> {
                // JS/TS: Check for balanced braces
                val openBraces = code.count { it == '{' }
                val closeBraces = code.count { it == '}' }
                openBraces == closeBraces && code.isNotEmpty()
            }

            "java", "kotlin" -> {
                // Java/Kotlin: Check for class/function structure
                val hasClassOrFunction = code.contains("class ") ||
                        code.contains("fun ") ||
                        code.contains("void ") ||
                        code.contains("public ")
                val openBraces = code.count { it == '{' }
                val closeBraces = code.count { it == '}' }
                hasClassOrFunction && openBraces == closeBraces
            }

            "c++", "c" -> {
                // C/C++: Check for basic structure
                val hasIncludes = code.contains("#include") || code.contains("int main")
                val openBraces = code.count { it == '{' }
                val closeBraces = code.count { it == '}' }
                hasIncludes && openBraces == closeBraces
            }

            else -> {
                // Unknown language: basic check
                code.trim().isNotEmpty() && code.length > 10
            }
        }
    }

    /**
     * Detects specific improvements made
     */
    private fun detectImprovements(
        original: String,
        fixed: String,
        roasts: List<Roast>
    ): List<Improvement> {
        val improvements = mutableListOf<Improvement>()

        // Check for naming improvements
        val originalVars = extractVariableNames(original)
        val fixedVars = extractVariableNames(fixed)
        val renamedCount =
            originalVars.count { it.length <= 2 } - fixedVars.count { it.length <= 2 }
        if (renamedCount > 0) {
            improvements.add(
                Improvement(
                    "naming",
                    "Renamed $renamedCount variables to be more descriptive",
                    null
                )
            )
        }

        // Check for reduced lines
        val originalLines = original.lines().filter { it.isNotBlank() }.size
        val fixedLines = fixed.lines().filter { it.isNotBlank() }.size
        if (fixedLines < originalLines) {
            improvements.add(
                Improvement(
                    "length",
                    "Reduced code from $originalLines to $fixedLines lines",
                    null
                )
            )
        }

        // Check for added error handling
        val originalTryCatch = original.split("try").size - 1
        val fixedTryCatch = fixed.split("try").size - 1
        if (fixedTryCatch > originalTryCatch) {
            improvements.add(
                Improvement(
                    "error_handling",
                    "Added ${fixedTryCatch - originalTryCatch} error handling blocks",
                    null
                )
            )
        }

        // Check for reduced nesting
        val originalMaxNesting = calculateMaxNesting(original)
        val fixedMaxNesting = calculateMaxNesting(fixed)
        if (fixedMaxNesting < originalMaxNesting) {
            improvements.add(
                Improvement(
                    "nesting",
                    "Reduced maximum nesting depth from $originalMaxNesting to $fixedMaxNesting",
                    null
                )
            )
        }

        // Check for added comments
        val originalComments = original.split("//").size - 1 + original.split("/*").size - 1
        val fixedComments = fixed.split("//").size - 1 + fixed.split("/*").size - 1
        if (fixedComments > originalComments) {
            improvements.add(
                Improvement(
                    "documentation",
                    "Added ${fixedComments - originalComments} explanatory comments",
                    null
                )
            )
        }

        // Add fixed issues from roasts
        roasts.groupBy { it.issueType }.forEach { (type, issues) ->
            improvements.add(
                Improvement(
                    type.name.lowercase(),
                    "Fixed ${issues.size} ${type.name.lowercase()} issue(s)",
                    null
                )
            )
        }

        return improvements
    }

    /**
     * Calculates metrics change
     */
    private fun calculateMetrics(
        original: String,
        fixed: String,
        issuesCount: Int
    ): MetricsChange {
        val originalLines = original.lines().filter { it.isNotBlank() }.size
        val fixedLines = fixed.lines().filter { it.isNotBlank() }.size
        val originalComplexity = calculateMaxNesting(original)
        val fixedComplexity = calculateMaxNesting(fixed)

        return MetricsChange(
            linesBefore = originalLines,
            linesAfter = fixedLines,
            complexityBefore = originalComplexity,
            complexityAfter = fixedComplexity,
            issuesFixed = issuesCount
        )
    }

    /**
     * Estimates score for fixed code (simple heuristic)
     */
    private fun estimateScore(code: String, language: String): Int {
        var score = 100

        // Deduct for long functions (>50 lines)
        val functionLengths = estimateFunctionLengths(code)
        score -= functionLengths.count { it > 50 } * 5

        // Deduct for high nesting
        val maxNesting = calculateMaxNesting(code)
        score -= (maxNesting - 2).coerceAtLeast(0) * 3

        // Deduct for short variable names
        val vars = extractVariableNames(code)
        score -= vars.count { it.length <= 2 } * 2

        // Bonus for comments
        val commentCount = code.split("//").size - 1 + code.split("/*").size - 1
        score += (commentCount * 2).coerceAtMost(10)

        return score.coerceIn(0, 100)
    }

    /**
     * Helper: Extract variable names from code
     */
    private fun extractVariableNames(code: String): List<String> {
        val varRegex = "\\b[a-z][a-zA-Z0-9_]*\\b".toRegex()
        return varRegex.findAll(code)
            .map { it.value }
            .filter { it !in setOf("if", "for", "while", "else", "return", "var", "let", "const") }
            .distinct()
            .toList()
    }

    /**
     * Helper: Calculate maximum nesting depth
     */
    private fun calculateMaxNesting(code: String): Int {
        var maxNesting = 0
        var currentNesting = 0

        for (char in code) {
            when (char) {
                '{' -> {
                    currentNesting++
                    maxNesting = maxOf(maxNesting, currentNesting)
                }

                '}' -> {
                    currentNesting = maxOf(0, currentNesting - 1)
                }
            }
        }

        return maxNesting
    }

    /**
     * Helper: Estimate function lengths
     */
    private fun estimateFunctionLengths(code: String): List<Int> {
        val lengths = mutableListOf<Int>()
        var inFunction = false
        var functionLines = 0
        var braceCount = 0

        for (line in code.lines()) {
            if (line.contains("function ") || line.contains("def ") ||
                line.contains("fun ") || line.contains("void ")
            ) {
                inFunction = true
                functionLines = 0
            }

            if (inFunction) {
                functionLines++
                braceCount += line.count { it == '{' }
                braceCount -= line.count { it == '}' }

                if (braceCount == 0 && functionLines > 1) {
                    lengths.add(functionLines)
                    inFunction = false
                }
            }
        }

        return lengths
    }

    /**
     * Generation state
     */
    sealed class GenerationState {
        data class Loading(val percentage: Int, val message: String) : GenerationState()
        data class Success(val result: FixResult) : GenerationState()
        data class Error(val message: String) : GenerationState()
    }

    /**
     * Generates manual suggestions if AI generation fails
     */
    fun generateManualSuggestions(roasts: List<Roast>): List<String> {
        val suggestions = mutableListOf<String>()

        roasts.groupBy { it.issueType }.forEach { (type, issues) ->
            when (type) {
                IssueType.NAMING -> {
                    suggestions.add("Rename variables to be more descriptive (e.g., 'temp' → 'temperatureValue')")
                }

                IssueType.LENGTH -> {
                    suggestions.add("Break long functions into smaller, focused functions (aim for <50 lines)")
                }

                IssueType.NESTING -> {
                    suggestions.add("Reduce nesting using early returns and guard clauses")
                }

                IssueType.PERFORMANCE -> {
                    suggestions.add("Optimize loops and algorithms for better performance")
                }

                IssueType.SECURITY -> {
                    suggestions.add("Add input validation and sanitization")
                }

                IssueType.STYLE -> {
                    suggestions.add("Format code consistently with proper indentation")
                }

                IssueType.DUPLICATION -> {
                    suggestions.add("Extract duplicated code into reusable functions")
                }
            }
        }

        // Add general suggestions
        suggestions.add("Add error handling with try-catch blocks")
        suggestions.add("Add comments explaining complex logic")
        suggestions.add("Follow language-specific best practices")

        return suggestions.distinct()
    }
}
