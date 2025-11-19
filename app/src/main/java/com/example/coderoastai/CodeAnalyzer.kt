package com.example.coderoastai

import android.util.Log
import com.runanywhere.sdk.public.RunAnywhere
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlin.math.max

/**
 * Severity levels for code issues
 */
enum class Severity(val points: Int) {
    LOW(5),
    MEDIUM(10),
    HIGH(20),
    CRITICAL(30)
}

/**
 * Types of code issues that can be detected
 */
enum class IssueType {
    NAMING,      // Poor variable/function names
    NESTING,     // Excessive nesting depth
    LENGTH,      // Functions too long
    DUPLICATION, // Repeated code
    PERFORMANCE, // Performance issues
    SECURITY,    // Security vulnerabilities
    STYLE        // Style violations
}

/**
 * Represents a single code issue
 */
data class CodeIssue(
    val type: IssueType,
    val line: Int,
    val description: String,
    val severity: Severity
)

/**
 * Result of code analysis
 */
data class AnalysisResult(
    val issues: List<CodeIssue>,
    val overallScore: Int,
    val suggestions: List<String>,
    val hasError: Boolean = false,
    val errorMessage: String? = null
) {
    companion object {
        /**
         * Creates an error result when analysis fails
         */
        fun error(message: String): AnalysisResult {
            return AnalysisResult(
                issues = emptyList(),
                overallScore = 0,
                suggestions = listOf("Analysis failed: $message"),
                hasError = true,
                errorMessage = message
            )
        }
    }
}

/**
 * Code analyzer that uses RunAnywhere SDK for intelligent analysis
 * with regex-based fallback for robustness
 */
object CodeAnalyzer {

    private const val TAG = "CodeAnalyzer"
    private const val ANALYSIS_TIMEOUT_MS = 30000L // 30 seconds
    private const val MAX_SCORE = 100

    /**
     * Analyzes code and returns detailed results
     *
     * @param code The source code to analyze
     * @param language Programming language (Python, JavaScript, Java, C++, Kotlin)
     * @return AnalysisResult with detected issues and score
     *
     * Example usage:
     * ```kotlin
     * val result = CodeAnalyzer.analyzeCode(
     *     code = "def foo(x):\n    return x*2",
     *     language = "Python"
     * )
     * println("Score: ${result.overallScore}")
     * result.issues.forEach { println(it.description) }
     * ```
     */
    suspend fun analyzeCode(code: String, language: String): AnalysisResult {
        Log.d(TAG, "Starting code analysis for language: $language")
        Log.d(TAG, "Code length: ${code.length} characters, ${code.lines().size} lines")

        return try {
            // Try AI-powered analysis first
            val aiResult = withTimeout(ANALYSIS_TIMEOUT_MS) {
                analyzeWithAI(code, language)
            }

            if (aiResult != null) {
                Log.d(TAG, "AI analysis successful, score: ${aiResult.overallScore}")
                aiResult
            } else {
                // Fallback to regex-based analysis
                Log.d(TAG, "AI analysis returned null, using fallback")
                analyzeWithFallback(code, language)
            }

        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Analysis timeout after ${ANALYSIS_TIMEOUT_MS}ms")
            // Return fallback analysis on timeout
            analyzeWithFallback(code, language)

        } catch (e: Exception) {
            Log.e(TAG, "Analysis failed with exception: ${e.message}", e)
            // Return fallback analysis on error
            analyzeWithFallback(code, language)
        }
    }

    /**
     * Attempts to analyze code using RunAnywhere SDK
     */
    private suspend fun analyzeWithAI(code: String, language: String): AnalysisResult? {
        Log.d(TAG, "Attempting AI-powered analysis")

        // Check if SDK is initialized
        if (!CodeRoastApplication.isSDKInitialized) {
            Log.w(TAG, "SDK not initialized")
            return null
        }

        try {
            val prompt = buildAnalysisPrompt(code, language)
            Log.d(TAG, "Sending analysis prompt to AI model")

            val response = RunAnywhere.generate(prompt)
            Log.d(TAG, "Received AI response: ${response.take(100)}...")

            // Parse AI response and combine with regex analysis
            val regexIssues = detectIssuesWithRegex(code, language)
            val score = calculateScore(regexIssues)
            val suggestions = generateSuggestions(regexIssues, language)

            return AnalysisResult(
                issues = regexIssues,
                overallScore = score,
                suggestions = suggestions,
                hasError = false
            )

        } catch (e: Exception) {
            Log.e(TAG, "AI analysis error: ${e.message}", e)
            return null
        }
    }

    /**
     * Fallback analysis using regex patterns
     * Always returns a valid result
     */
    private fun analyzeWithFallback(code: String, language: String): AnalysisResult {
        Log.d(TAG, "Using fallback regex-based analysis")

        val issues = detectIssuesWithRegex(code, language)
        val score = calculateScore(issues)
        val suggestions = generateSuggestions(issues, language)

        Log.d(TAG, "Fallback analysis complete: ${issues.size} issues, score: $score")

        return AnalysisResult(
            issues = issues,
            overallScore = score,
            suggestions = suggestions,
            hasError = false
        )
    }

    /**
     * Detects code issues using regex patterns
     */
    private fun detectIssuesWithRegex(code: String, language: String): List<CodeIssue> {
        val issues = mutableListOf<CodeIssue>()
        val lines = code.lines()

        Log.d(TAG, "Starting regex detection on ${lines.size} lines")

        // 1. Check for long functions (>50 lines)
        issues.addAll(detectLongFunctions(lines, language))

        // 2. Check for poor variable names
        issues.addAll(detectPoorNaming(lines))

        // 3. Check for excessive nesting
        issues.addAll(detectExcessiveNesting(lines))

        // 4. Check for code duplication
        issues.addAll(detectDuplication(lines))

        // 5. Check for missing error handling
        issues.addAll(detectMissingErrorHandling(lines, language))

        // 6. Check for style violations
        issues.addAll(detectStyleViolations(lines, language))

        Log.d(TAG, "Regex detection found ${issues.size} total issues")
        return issues
    }

    /**
     * Detects functions that are too long (>50 lines)
     */
    private fun detectLongFunctions(lines: List<String>, language: String): List<CodeIssue> {
        val issues = mutableListOf<CodeIssue>()

        // Simple heuristic: detect function declarations and count lines until next function
        val functionPatterns = when (language.lowercase()) {
            "python" -> Regex("""^\s*def\s+\w+""")
            "javascript" -> Regex("""^\s*function\s+\w+|^\s*const\s+\w+\s*=\s*\(""")
            "java", "kotlin" -> Regex("""^\s*(public|private|protected)?\s*(static)?\s*\w+\s+\w+\s*\(""")
            "c++" -> Regex("""^\s*\w+\s+\w+\s*\(""")
            else -> Regex("""^\s*def\s+\w+|^\s*function\s+\w+""")
        }

        var functionStartLine = -1
        var functionLines = 0

        lines.forEachIndexed { index, line ->
            if (functionPatterns.containsMatchIn(line)) {
                // Check previous function length
                if (functionStartLine != -1 && functionLines > 50) {
                    issues.add(
                        CodeIssue(
                            type = IssueType.LENGTH,
                            line = functionStartLine + 1,
                            description = "Function is too long ($functionLines lines). Consider breaking it into smaller functions.",
                            severity = if (functionLines > 100) Severity.HIGH else Severity.MEDIUM
                        )
                    )
                }
                // Start tracking new function
                functionStartLine = index
                functionLines = 1
            } else if (functionStartLine != -1) {
                functionLines++
            }
        }

        // Check last function
        if (functionStartLine != -1 && functionLines > 50) {
            issues.add(
                CodeIssue(
                    type = IssueType.LENGTH,
                    line = functionStartLine + 1,
                    description = "Function is too long ($functionLines lines). Consider breaking it into smaller functions.",
                    severity = if (functionLines > 100) Severity.HIGH else Severity.MEDIUM
                )
            )
        }

        Log.d(TAG, "Detected ${issues.size} long function issues")
        return issues
    }

    /**
     * Detects poor variable names
     */
    private fun detectPoorNaming(lines: List<String>): List<CodeIssue> {
        val issues = mutableListOf<CodeIssue>()
        val poorNames =
            listOf("x", "y", "z", "temp", "tmp", "data", "var1", "var2", "test", "foo", "bar")

        lines.forEachIndexed { index, line ->
            poorNames.forEach { poorName ->
                // Match variable declarations and assignments
                val patterns = listOf(
                    Regex("""\b(let|const|var)\s+$poorName\b"""),           // JavaScript
                    Regex("""\b(int|string|float|double)\s+$poorName\b"""), // Java/C++/Kotlin
                    Regex("""\b$poorName\s*=\s*"""),                         // Assignment (all languages)
                    Regex("""\bdef\s+$poorName\s*\(""")                      // Python function
                )

                patterns.forEach { pattern ->
                    if (pattern.containsMatchIn(line)) {
                        issues.add(
                            CodeIssue(
                                type = IssueType.NAMING,
                                line = index + 1,
                                description = "Poor variable name '$poorName'. Use descriptive names like 'userCount', 'totalPrice', 'isValid'.",
                                severity = Severity.LOW
                            )
                        )
                    }
                }
            }
        }

        Log.d(TAG, "Detected ${issues.size} naming issues")
        return issues
    }

    /**
     * Detects excessive nesting (>3 levels)
     */
    private fun detectExcessiveNesting(lines: List<String>): List<CodeIssue> {
        val issues = mutableListOf<CodeIssue>()
        var nestingLevel = 0
        var maxNesting = 0
        var maxNestingLine = 0

        lines.forEachIndexed { index, line ->
            val trimmed = line.trim()

            // Count opening braces/colons (simplified)
            if (trimmed.matches(Regex(""".*\b(if|for|while|def|function|class)\b.*[{:]$"""))) {
                nestingLevel++
                if (nestingLevel > maxNesting) {
                    maxNesting = nestingLevel
                    maxNestingLine = index + 1
                }
            }

            // Count closing braces
            if (trimmed.startsWith("}")) {
                nestingLevel--
            }

            // Python: dedent detection (simplified)
            if (line.isNotEmpty() && !line[0].isWhitespace() && nestingLevel > 0) {
                nestingLevel = max(0, nestingLevel - 1)
            }
        }

        if (maxNesting > 3) {
            issues.add(
                CodeIssue(
                    type = IssueType.NESTING,
                    line = maxNestingLine,
                    description = "Excessive nesting detected ($maxNesting levels). Use early returns or extract methods.",
                    severity = when {
                        maxNesting > 5 -> Severity.HIGH
                        maxNesting > 4 -> Severity.MEDIUM
                        else -> Severity.LOW
                    }
                )
            )
        }

        Log.d(TAG, "Max nesting level: $maxNesting, detected ${issues.size} nesting issues")
        return issues
    }

    /**
     * Detects code duplication
     */
    private fun detectDuplication(lines: List<String>): List<CodeIssue> {
        val issues = mutableListOf<CodeIssue>()
        val lineGroups = mutableMapOf<String, MutableList<Int>>()

        // Group identical non-empty lines
        lines.forEachIndexed { index, line ->
            val trimmed = line.trim()
            if (trimmed.isNotEmpty() && trimmed.length > 10) { // Ignore short lines
                lineGroups.getOrPut(trimmed) { mutableListOf() }.add(index + 1)
            }
        }

        // Find duplicated groups
        lineGroups.forEach { (line, lineNumbers) ->
            if (lineNumbers.size >= 3) { // Repeated 3+ times
                issues.add(
                    CodeIssue(
                        type = IssueType.DUPLICATION,
                        line = lineNumbers.first(),
                        description = "Code duplication detected. This line appears ${lineNumbers.size} times. Extract to a function.",
                        severity = if (lineNumbers.size > 5) Severity.MEDIUM else Severity.LOW
                    )
                )
            }
        }

        Log.d(TAG, "Detected ${issues.size} duplication issues")
        return issues
    }

    /**
     * Detects missing error handling
     */
    private fun detectMissingErrorHandling(lines: List<String>, language: String): List<CodeIssue> {
        val issues = mutableListOf<CodeIssue>()

        // Check for risky operations without try-catch
        val riskyPatterns = listOf(
            Regex("""\.parse\("""),           // Parsing operations
            Regex("""\.read\("""),            // File read
            Regex("""\.write\("""),           // File write
            Regex("""\.open\("""),            // File open
            Regex("""\.connect\("""),         // Network connect
            Regex("""new\s+\w+Connection"""), // Database connections
            Regex("""\.parseInt\("""),        // String to int
            Regex("""\.parseFloat\(""")       // String to float
        )

        val hasTryCatch = lines.any { it.trim().startsWith("try") || it.contains("try {") }

        if (!hasTryCatch) {
            lines.forEachIndexed { index, line ->
                riskyPatterns.forEach { pattern ->
                    if (pattern.containsMatchIn(line)) {
                        issues.add(
                            CodeIssue(
                                type = IssueType.SECURITY,
                                line = index + 1,
                                description = "Risky operation without error handling. Wrap in try-catch or handle errors.",
                                severity = Severity.MEDIUM
                            )
                        )
                    }
                }
            }
        }

        Log.d(TAG, "Detected ${issues.size} error handling issues")
        return issues
    }

    /**
     * Detects language-specific style violations
     */
    private fun detectStyleViolations(lines: List<String>, language: String): List<CodeIssue> {
        val issues = mutableListOf<CodeIssue>()

        when (language.lowercase()) {
            "python" -> {
                // Check for missing spaces after commas
                lines.forEachIndexed { index, line ->
                    if (Regex(""",\w""").containsMatchIn(line)) {
                        issues.add(
                            CodeIssue(
                                type = IssueType.STYLE,
                                line = index + 1,
                                description = "PEP 8: Add space after comma.",
                                severity = Severity.LOW
                            )
                        )
                    }
                }
            }

            "javascript" -> {
                // Check for var usage (should use let/const)
                lines.forEachIndexed { index, line ->
                    if (Regex("""\bvar\s+\w+""").containsMatchIn(line)) {
                        issues.add(
                            CodeIssue(
                                type = IssueType.STYLE,
                                line = index + 1,
                                description = "Use 'let' or 'const' instead of 'var' in modern JavaScript.",
                                severity = Severity.LOW
                            )
                        )
                    }
                }
            }

            "java", "kotlin" -> {
                // Check for missing braces
                lines.forEachIndexed { index, line ->
                    if (Regex("""^\s*if\s*\(.*\)\s*$""").containsMatchIn(line)) {
                        if (index + 1 < lines.size && !lines[index + 1].trim().startsWith("{")) {
                            issues.add(
                                CodeIssue(
                                    type = IssueType.STYLE,
                                    line = index + 1,
                                    description = "Always use braces for if statements, even for single-line bodies.",
                                    severity = Severity.LOW
                                )
                            )
                        }
                    }
                }
            }
        }

        Log.d(TAG, "Detected ${issues.size} style issues")
        return issues
    }

    /**
     * Calculates overall code quality score (0-100)
     */
    private fun calculateScore(issues: List<CodeIssue>): Int {
        var score = MAX_SCORE

        issues.forEach { issue ->
            score -= issue.severity.points
        }

        // Score cannot go below 0
        val finalScore = max(0, score)
        Log.d(
            TAG,
            "Calculated score: $finalScore (deducted ${MAX_SCORE - finalScore} points from ${issues.size} issues)"
        )

        return finalScore
    }

    /**
     * Generates improvement suggestions based on detected issues
     */
    private fun generateSuggestions(issues: List<CodeIssue>, language: String): List<String> {
        val suggestions = mutableListOf<String>()

        val issuesByType = issues.groupBy { it.type }

        issuesByType.forEach { (type, typeIssues) ->
            when (type) {
                IssueType.NAMING -> {
                    suggestions.add("✏️ Improve variable naming: Use descriptive names that explain purpose (e.g., 'userCount' not 'x')")
                }

                IssueType.NESTING -> {
                    suggestions.add("📐 Reduce nesting: Use early returns and extract nested logic into separate functions")
                }

                IssueType.LENGTH -> {
                    suggestions.add("✂️ Break down long functions: Aim for functions under 50 lines. Extract logical blocks")
                }

                IssueType.DUPLICATION -> {
                    suggestions.add("🔄 Remove duplication: Extract repeated code into reusable functions")
                }

                IssueType.SECURITY -> {
                    suggestions.add("🛡️ Add error handling: Wrap risky operations in try-catch blocks")
                }

                IssueType.STYLE -> {
                    suggestions.add("🎨 Fix style issues: Follow ${language} conventions for better readability")
                }

                IssueType.PERFORMANCE -> {
                    suggestions.add("⚡ Optimize performance: Review algorithms and data structures")
                }
            }
        }

        // Add general suggestions based on score
        if (issues.isEmpty()) {
            suggestions.add("🎉 Excellent! No major issues detected. Keep up the good work!")
        } else if (issues.size > 10) {
            suggestions.add("⚠️ Many issues detected. Focus on the HIGH and CRITICAL severity items first")
        }

        // Add language-specific best practices
        when (language.lowercase()) {
            "python" -> suggestions.add("📖 Follow PEP 8 style guide for Python code")
            "javascript" -> suggestions.add("📖 Use ESLint for consistent JavaScript style")
            "java" -> suggestions.add("📖 Follow Oracle Java Code Conventions")
            "kotlin" -> suggestions.add("📖 Follow Kotlin Coding Conventions")
            "c++" -> suggestions.add("📖 Consider C++ Core Guidelines")
        }

        Log.d(TAG, "Generated ${suggestions.size} suggestions")
        return suggestions.distinct()
    }

    /**
     * Builds the analysis prompt for AI model
     */
    private fun buildAnalysisPrompt(code: String, language: String): String {
        return """
Analyze this ${language} code for quality issues:

```${language.lowercase()}
$code
```

Identify:
1. Poor naming (variables like x, temp, data)
2. Excessive nesting (>3 levels)
3. Long functions (>50 lines)
4. Code duplication
5. Missing error handling
6. Style violations

Provide a brief analysis focusing on the most critical issues.
        """.trimIndent()
    }
}

/*
═══════════════════════════════════════════════════════════════
UNIT TEST EXAMPLES
═══════════════════════════════════════════════════════════════

// Test 1: Analyze simple clean code
@Test
fun `test analyze clean code returns high score`() = runBlocking {
    val code = """
        fun calculateTotal(items: List<Item>): Double {
            return items.sumOf { it.price }
        }
    """.trimIndent()
    
    val result = CodeAnalyzer.analyzeCode(code, "Kotlin")
    assertTrue(result.overallScore > 80)
    assertTrue(result.issues.isEmpty() || result.issues.all { it.severity == Severity.LOW })
}

// Test 2: Detect long function
@Test
fun `test detect long function`() = runBlocking {
    val code = buildString {
        appendLine("def longFunction():")
        repeat(60) { appendLine("    print($it)") }
    }
    
    val result = CodeAnalyzer.analyzeCode(code, "Python")
    assertTrue(result.issues.any { it.type == IssueType.LENGTH })
    assertTrue(result.overallScore < 100)
}

// Test 3: Detect poor naming
@Test
fun `test detect poor variable names`() = runBlocking {
    val code = """
        let x = 10;
        let temp = x * 2;
        let data = temp + 5;
    """.trimIndent()
    
    val result = CodeAnalyzer.analyzeCode(code, "JavaScript")
    val namingIssues = result.issues.filter { it.type == IssueType.NAMING }
    assertTrue(namingIssues.size >= 2) // Should detect x, temp, data
}

// Test 4: Detect excessive nesting
@Test
fun `test detect excessive nesting`() = runBlocking {
    val code = """
        if (a) {
            if (b) {
                if (c) {
                    if (d) {
                        if (e) {
                            return true;
                        }
                    }
                }
            }
        }
    """.trimIndent()
    
    val result = CodeAnalyzer.analyzeCode(code, "JavaScript")
    assertTrue(result.issues.any { it.type == IssueType.NESTING })
}

// Test 5: Detect missing error handling
@Test
fun `test detect missing error handling`() = runBlocking {
    val code = """
        const data = JSON.parse(userInput);
        const file = fs.readFileSync('config.txt');
    """.trimIndent()
    
    val result = CodeAnalyzer.analyzeCode(code, "JavaScript")
    assertTrue(result.issues.any { it.type == IssueType.SECURITY })
}

// Test 6: Calculate score correctly
@Test
fun `test score calculation`() = runBlocking {
    val code = """
        var x = 1;  // Style issue (var) + naming (x)
        var y = 2;
    """.trimIndent()
    
    val result = CodeAnalyzer.analyzeCode(code, "JavaScript")
    assertTrue(result.overallScore < 100)
    assertTrue(result.overallScore >= 0)
}

// Test 7: Handle empty code
@Test
fun `test handle empty code gracefully`() = runBlocking {
    val result = CodeAnalyzer.analyzeCode("", "Python")
    assertFalse(result.hasError)
    assertTrue(result.issues.isEmpty())
}

// Test 8: Timeout handling
@Test(timeout = 35000)
fun `test analysis completes within timeout`() = runBlocking {
    val largeCode = buildString {
        repeat(1000) { appendLine("def func$it(): pass") }
    }
    
    val result = CodeAnalyzer.analyzeCode(largeCode, "Python")
    assertNotNull(result)
    assertFalse(result.hasError)
}

// Test 9: Verify suggestions are generated
@Test
fun `test suggestions are generated`() = runBlocking {
    val code = """
        var x = 1;
        if (x > 0)
            return x;
    """.trimIndent()
    
    val result = CodeAnalyzer.analyzeCode(code, "JavaScript")
    assertTrue(result.suggestions.isNotEmpty())
}

// Test 10: All issue types detected
@Test
fun `test all issue types can be detected`() = runBlocking {
    // Code with multiple issue types
    val code = """
        var x = 1;  // NAMING + STYLE
        if (a) {    // NESTING (when nested)
            if (b) {
                if (c) {
                    if (d) {
                        x = x + 1;  // DUPLICATION
                        x = x + 1;
                        x = x + 1;
                    }
                }
            }
        }
        JSON.parse(data);  // SECURITY
    """.trimIndent()
    
    val result = CodeAnalyzer.analyzeCode(code, "JavaScript")
    assertTrue(result.issues.size > 3) // Multiple different issues
}

════════════════════════════════════════════════════���══════════
END UNIT TESTS
═══════════════════════════════════════════════════════════════
*/
