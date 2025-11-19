package com.example.coderoastai

import android.util.Log
import com.runanywhere.sdk.public.RunAnywhere
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlin.random.Random

/**
 * Represents a generated roast for a code issue
 */
data class Roast(
    val issueType: IssueType,
    val lineNumber: Int,
    val roastText: String,
    val severity: Severity
)

/**
 * Personality types for roast generation
 */
enum class RoastPersonality(val displayName: String, val emoji: String) {
    GORDON_RAMSAY("Gordon Ramsay", "🔪"),
    DRILL_SERGEANT("Drill Sergeant", "🎖️"),
    DISAPPOINTED_DAD("Disappointed Dad", "👔"),
    GEN_Z("Gen Z", "💅"),
    SHAKESPEARE("Shakespeare", "🎭");

    companion object {
        /**
         * Converts UI string representation to enum
         * Examples: "🔪 Brutal" -> GORDON_RAMSAY, "🎖️ Drill Sergeant" -> DRILL_SERGEANT
         */
        fun fromDisplayString(display: String): RoastPersonality {
            return when {
                display.contains("Brutal") || display.contains("🔪") -> GORDON_RAMSAY
                display.contains("Drill") || display.contains("🎖️") -> DRILL_SERGEANT
                display.contains("Professional") || display.contains("👔") -> DISAPPOINTED_DAD
                display.contains("Sassy") || display.contains("💅") -> GEN_Z
                display.contains("Theatrical") || display.contains("🎭") -> SHAKESPEARE
                else -> GORDON_RAMSAY // Default
            }
        }
    }
}

/**
 * Generates hilarious, personality-based roasts for code issues
 * using RunAnywhere SDK with intelligent fallbacks
 */
object RoastGenerator {

    private const val TAG = "RoastGenerator"
    private const val ROAST_TIMEOUT_MS = 10000L // 10 seconds per roast
    private const val MAX_CACHE_SIZE = 100 // Limit cache size to prevent memory issues

    // Cache to avoid regenerating identical roasts
    private val roastCache = mutableMapOf<String, String>()

    /**
     * Generates roasts for all issues in the analysis result
     *
     * @param analysisResult The code analysis results
     * @param personality The roasting personality
     * @param intensity Roast harshness (1-5)
     * @return List of generated roasts
     */
    suspend fun generateRoasts(
        analysisResult: AnalysisResult,
        personality: RoastPersonality,
        intensity: Int
    ): List<Roast> {
        // Validate input
        require(intensity in 1..5) { "Intensity must be between 1 and 5, got $intensity" }

        Log.d(TAG, "Generating roasts for ${analysisResult.issues.size} issues")
        Log.d(TAG, "Personality: ${personality.displayName}, Intensity: $intensity")

        val roasts = mutableListOf<Roast>()

        // Generate roasts based on severity (more roasts for severe issues)
        analysisResult.issues.forEach { issue ->
            val roastCount = when (issue.severity) {
                Severity.CRITICAL -> 3
                Severity.HIGH -> 2
                Severity.MEDIUM -> 1
                Severity.LOW -> if (Random.nextBoolean()) 1 else 0 // 50% chance
            }

            repeat(roastCount) { index ->
                try {
                    val roastText = generateSingleRoast(issue, personality, intensity, index)
                    roasts.add(Roast(issue.type, issue.line, roastText, issue.severity))
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to generate roast for ${issue.type}", e)
                    // Use fallback
                    val fallback = getFallbackRoast(issue, personality, intensity)
                    roasts.add(Roast(issue.type, issue.line, fallback, issue.severity))
                }
            }
        }

        Log.d(TAG, "Generated ${roasts.size} total roasts")
        return roasts
    }

    /**
     * Generates a single roast for an issue
     */
    private suspend fun generateSingleRoast(
        issue: CodeIssue,
        personality: RoastPersonality,
        intensity: Int,
        variation: Int
    ): String {
        val cacheKey = "${issue.type}_${personality}_${intensity}_$variation"

        // Check cache
        roastCache[cacheKey]?.let {
            Log.d(TAG, "Using cached roast for ${issue.type}")
            return it
        }

        // Try AI generation with timeout
        return try {
            withTimeout(ROAST_TIMEOUT_MS) {
                val roast = generateWithAI(issue, personality, intensity, variation)

                // Add to cache with size limit
                if (roastCache.size >= MAX_CACHE_SIZE) {
                    // Remove oldest entry (first entry)
                    roastCache.remove(roastCache.keys.first())
                }
                roastCache[cacheKey] = roast

                roast
            }
        } catch (e: TimeoutCancellationException) {
            Log.w(TAG, "AI generation timeout, using fallback")
            getFallbackRoast(issue, personality, intensity)
        } catch (e: Exception) {
            Log.w(TAG, "AI generation failed: ${e.message}, using fallback")
            getFallbackRoast(issue, personality, intensity)
        }
    }

    /**
     * Generates roast using RunAnywhere SDK
     */
    private suspend fun generateWithAI(
        issue: CodeIssue,
        personality: RoastPersonality,
        intensity: Int,
        variation: Int
    ): String {
        Log.d(TAG, "Generating AI roast for ${issue.type} (variation $variation)")

        // Check if SDK is initialized
        if (!CodeRoastApplication.isSDKInitialized) {
            Log.w(TAG, "SDK not initialized, using fallback")
            throw IllegalStateException("SDK not initialized")
        }

        val prompt = buildPrompt(issue, personality, intensity, variation)

        return try {
            val response = RunAnywhere.generate(prompt)
            if (response.isBlank()) {
                Log.w(TAG, "AI returned empty response, using fallback")
                throw IllegalStateException("Empty AI response")
            }
            response.trim()
        } catch (e: Exception) {
            Log.e(TAG, "AI generation error: ${e.message}", e)
            throw e
        }
    }

    /**
     * Builds a personality-specific prompt for the LLM
     */
    private fun buildPrompt(
        issue: CodeIssue,
        personality: RoastPersonality,
        intensity: Int,
        variation: Int
    ): String {
        val intensityDesc = when (intensity) {
            1 -> "gentle and constructive"
            2 -> "mild but honest"
            3 -> "direct and witty"
            4 -> "savage and brutal"
            5 -> "absolutely nuclear and merciless"
            else -> "moderate"
        }

        val variationNote = if (variation > 0) {
            "\nGenerate a DIFFERENT roast than before - use new metaphors and phrasing."
        } else ""

        return when (personality) {
            RoastPersonality.GORDON_RAMSAY -> """
You are Gordon Ramsay reviewing code like you review food in Hell's Kitchen.

Code Issue: ${issue.description} (Line ${issue.line})
Issue Type: ${issue.type.name}
Severity: ${issue.severity.name}

Style: Be $intensityDesc. Use cooking metaphors, British expressions, and Ramsay's signature aggression.
$variationNote

Generate ONE short roast (1-2 sentences, max 50 words). Start directly with the roast:
            """.trimIndent()

            RoastPersonality.DRILL_SERGEANT -> """
You are a military drill sergeant reviewing code like you inspect soldiers.

Code Issue: ${issue.description} (Line ${issue.line})
Issue Type: ${issue.type.name}
Severity: ${issue.severity.name}

Style: Be $intensityDesc. Use military terminology, CAPS for emphasis, drill sergeant tone.
$variationNote

Generate ONE short roast (1-2 sentences, max 50 words). GIVE ME A ROAST, SOLDIER:
            """.trimIndent()

            RoastPersonality.DISAPPOINTED_DAD -> """
You are a disappointed father reviewing your child's code.

Code Issue: ${issue.description} (Line ${issue.line})
Issue Type: ${issue.type.name}
Severity: ${issue.severity.name}

Style: Be $intensityDesc. Use phrases like "I expected better", "You had so much potential", "Your cousin writes clean code".
$variationNote

Generate ONE short roast (1-2 sentences, max 50 words). Express disappointment:
            """.trimIndent()

            RoastPersonality.GEN_Z -> """
You are a Gen Z intern roasting code using modern slang.

Code Issue: ${issue.description} (Line ${issue.line})
Issue Type: ${issue.type.name}
Severity: ${issue.severity.name}

Style: Be $intensityDesc. Use Gen Z slang: 'bestie', 'no cap', 'giving ___ energy', 'bussin', 'ick', 'slay', 'ate', 'periodt'.
$variationNote

Generate ONE short roast (1-2 sentences, max 50 words). Roast it Gen Z style, bestie:
            """.trimIndent()

            RoastPersonality.SHAKESPEARE -> """
You are Shakespeare roasting code in Elizabethan English.

Code Issue: ${issue.description} (Line ${issue.line})
Issue Type: ${issue.type.name}
Severity: ${issue.severity.name}

Style: Be $intensityDesc. Use Old English: 'thy', 'thou', 'doth', 'hath', 'art', poetic insults.
$variationNote

Generate ONE short roast (1-2 sentences, max 50 words). Compose a dramatic roast:
            """.trimIndent()
        }
    }

    /**
     * Fallback roasts when AI generation fails
     * Pre-written roasts with dynamic issue insertion
     */
    private fun getFallbackRoast(
        issue: CodeIssue,
        personality: RoastPersonality,
        intensity: Int
    ): String {
        val templates = getFallbackTemplates(personality, issue.type)

        // Ensure we have templates (should never be empty, but safety check)
        if (templates.isEmpty()) {
            Log.e(
                TAG,
                "No fallback templates found for ${personality.displayName} and ${issue.type}"
            )
            return "This code needs improvement. Please review line ${issue.line}."
        }

        val template = templates.random()

        // Insert issue details
        return template
            .replace("{description}", issue.description)
            .replace("{line}", issue.line.toString())
            .replace("{type}", issue.type.name.lowercase())
            .replace("{severity}", issue.severity.name.lowercase())
    }

    /**
     * Get fallback roast templates for each personality and issue type
     */
    private fun getFallbackTemplates(personality: RoastPersonality, type: IssueType): List<String> {
        return when (personality) {
            RoastPersonality.GORDON_RAMSAY -> getGordonRamsayTemplates(type)
            RoastPersonality.DRILL_SERGEANT -> getDrillSergeantTemplates(type)
            RoastPersonality.DISAPPOINTED_DAD -> getDisappointedDadTemplates(type)
            RoastPersonality.GEN_Z -> getGenZTemplates(type)
            RoastPersonality.SHAKESPEARE -> getShakespeareTemplates(type)
        }
    }

    /**
     * Gordon Ramsay roast templates
     */
    private fun getGordonRamsayTemplates(type: IssueType): List<String> {
        val common = listOf(
            "This code is so RAW, it's still asking for code reviews!",
            "You call this a function? My GRANDMOTHER writes better code!",
            "This is DISGUSTING! Absolutely REVOLTING code!",
            "It's BLAND! Where's the SEASONING?! Where's the STRUCTURE?!",
            "This code is so undercooked, it's still MOOING!",
            "GET OUT! Get out of my codebase!",
            "You're cooking code like you're making a DOG'S DINNER!",
            "It's DRY! It's OVERDONE! It's RUBBISH!"
        )

        val specific = when (type) {
            IssueType.NAMING -> listOf(
                "These variable names are PATHETIC! What is 'x'? What is 'temp'? TELL ME!",
                "You named this 'data'? DATA?! That's like calling all food 'FOOD'!",
                "These names are so vague, Gordon can't even taste what they're supposed to be!",
                "Variable names should SING! These ones STINK!"
            )

            IssueType.NESTING -> listOf(
                "Look at this nesting! It's like a RUSSIAN DOLL of incompetence!",
                "Four levels deep?! I've seen cleaner spaghetti in a TRASH CAN!",
                "This nesting is so deep, I need SCUBA GEAR to debug it!",
                "FLATTEN IT! This isn't LASAGNA!"
            )

            IssueType.LENGTH -> listOf(
                "This function is LONGER than my shopping list! CUT IT DOWN!",
                "60 lines?! My patience is SHORTER than your attention span!",
                "This function's so long, I AGED reading it!",
                "REDUCE! EXTRACT! Don't give me this NOVEL!"
            )

            IssueType.DUPLICATION -> listOf(
                "You've copied this code FIVE TIMES?! Ever heard of a FUNCTION?!",
                "This duplication is making me SICK! Extract it, you DONKEY!",
                "DRY! Don't Repeat Yourself! It's not ROCKET SCIENCE!",
                "This copy-paste job is LAZIER than a SLOTH!"
            )

            IssueType.SECURITY -> listOf(
                "Where's the error handling?! You'll POISON the users!",
                "No try-catch? That's like serving RAW CHICKEN!",
                "This security hole is BIG ENOUGH to drive a TRUCK through!",
                "You're gonna get HACKED with code like this!"
            )

            IssueType.STYLE -> listOf(
                "The presentation is AWFUL! Where's the STYLING?!",
                "This code looks like it was formatted by a BLINDFOLDED MONKEY!",
                "Style matters! This looks like you don't CARE!",
                "CLEAN IT UP! I wouldn't serve this to my DOG!"
            )

            IssueType.PERFORMANCE -> listOf(
                "This is SLOWER than my nan on a SUNDAY!",
                "Performance? More like POOR-FORMANCE!",
                "This runs like MOLASSES! SPEED IT UP!",
                "You're WASTING cycles like you waste my TIME!"
            )
        }

        return common + specific
    }

    /**
     * Drill Sergeant roast templates
     */
    private fun getDrillSergeantTemplates(type: IssueType): List<String> {
        val common = listOf(
            "ATTENTION! This code is a DISGRACE to the codebase!",
            "Drop and give me 20 refactors, MAGGOT!",
            "This code is OUT OF FORMATION!",
            "You call this CODE?! I call it GARBAGE!",
            "UNACCEPTABLE! Report to my office for REMEDIAL TRAINING!",
            "This code is SOFTER than baby powder!",
            "YOUR CODE JUST FAILED INSPECTION, SOLDIER!",
            "I've seen BETTER code from RECRUITS!"
        )

        val specific = when (type) {
            IssueType.NAMING -> listOf(
                "Your variable names are OUT OF UNIFORM! FIX THEM!",
                "What's this 'x' garbage?! PROPER NAMES, SOLDIER!",
                "These names are as WEAK as your PUSH-UPS!",
                "DESCRIPTIVE NAMES! That's an ORDER!"
            )

            IssueType.NESTING -> listOf(
                "This nesting is DEEPER than enemy territory! FLATTEN IT!",
                "TOO MANY LEVELS! Extract and CONQUER!",
                "Your code structure is as MESSY as your BUNK!",
                "STRAIGHTEN OUT these conditionals, PRIVATE!"
            )

            IssueType.LENGTH -> listOf(
                "This function is LONGER than basic training!",
                "CUT IT DOWN to size, SOLDIER!",
                "TOO LONG! Break it into TACTICAL units!",
                "BREVITY is a VIRTUE! LEARN IT!"
            )

            IssueType.DUPLICATION -> listOf(
                "You're REPEATING yourself like a BROKEN RECORD!",
                "ELIMINATE redundancy! That's BASIC TRAINING!",
                "One function, ONE PURPOSE! HOOAH!",
                "CONSOLIDATE your forces, SOLDIER!"
            )

            IssueType.SECURITY -> listOf(
                "WHERE'S your defensive perimeter?! ADD ERROR HANDLING!",
                "This security is WEAKER than tissue paper!",
                "FORTIFY your code! This is a WARZONE!",
                "NO PROTECTION?! You're DEFENSELESS!"
            )

            IssueType.STYLE -> listOf(
                "Your code's APPEARANCE is SUBSTANDARD!",
                "POLISH this code until it SHINES!",
                "ATTENTION TO DETAIL! That's what I DEMAND!",
                "This looks like a PRIVATE wrote it! UNSAT!"
            )

            IssueType.PERFORMANCE -> listOf(
                "This code moves SLOWER than a SLOTH! SPEED UP!",
                "OPTIMIZE! Time is PRECIOUS in COMBAT!",
                "Your algorithm is as SLOW as YOU!",
                "EFFICIENCY! That's what WINS battles!"
            )
        }

        return common + specific
    }

    /**
     * Disappointed Dad roast templates
     */
    private fun getDisappointedDadTemplates(type: IssueType): List<String> {
        val common = listOf(
            "Son, we need to talk about this code...",
            "I'm not angry, just... disappointed.",
            "You had so much potential. What happened?",
            "Your cousin writes clean code. Why can't you?",
            "I expected better from you. I really did.",
            "This isn't the developer I raised you to be.",
            "We sacrificed so much for your education, and this is what you produce?",
            "I don't even know what to say anymore..."
        )

        val specific = when (type) {
            IssueType.NAMING -> listOf(
                "Naming variables 'x' and 'y'? Is that what they taught you at that expensive bootcamp?",
                "We talked about meaningful names. Remember? At dinner last week?",
                "Your sister names her variables properly. Just saying.",
                "I thought you understood the importance of clear communication..."
            )

            IssueType.NESTING -> listOf(
                "This nesting... it's like you're hiding from your problems.",
                "Remember when I taught you about simplicity? You were seven.",
                "Four levels deep? That's deeper than our family issues.",
                "I raised you to face things directly, not nest them away."
            )

            IssueType.LENGTH -> listOf(
                "60 lines? You know better than this. I taught you about modular design.",
                "When I was your age, I kept functions under 50 lines. But that's just me.",
                "This function is longer than your excuses.",
                "We talked about the Single Responsibility Principle. Were you even listening?"
            )

            IssueType.DUPLICATION -> listOf(
                "You copied the same code five times? That's just lazy, son.",
                "Remember when I told you 'work smarter, not harder'? This is working harder.",
                "Your mother and I didn't raise a copy-paster.",
                "DRY principle. We discussed this. Multiple times. Like this code."
            )

            IssueType.SECURITY -> listOf(
                "No error handling? What if this fails in production? Did you think about that?",
                "Safety first. That's what I always said. This isn't safe.",
                "You're putting users at risk. Is that the legacy you want?",
                "I thought you cared about doing things right..."
            )

            IssueType.STYLE -> listOf(
                "The formatting... it's like you didn't even try.",
                "Presentation matters, son. In code, in life, in everything.",
                "I taught you to take pride in your work. Where's the pride here?",
                "Your first code was so neat. What changed?"
            )

            IssueType.PERFORMANCE -> listOf(
                "This runs slower than you completing chores. And that's saying something.",
                "Efficiency was one of our family values. Remember?",
                "You could optimize this. You just chose not to. Why?",
                "Your cousin's code runs in milliseconds. Just thought you should know."
            )
        }

        return common + specific
    }

    /**
     * Gen Z roast templates
     */
    private fun getGenZTemplates(type: IssueType): List<String> {
        val common = listOf(
            "Bestie, this code is giving major ick vibes 💀",
            "Not you writing code like it's 2010 💅",
            "This ain't it, chief. This ain't it at all.",
            "The way this code is giving me secondhand embarrassment 😬",
            "No cap, this code needs to be deleted and tried again fr fr",
            "This code? Not bussin. Not bussin at ALL.",
            "Respectfully, this is unhinged behavior 💀",
            "Bestie woke up and chose violence (against clean code) 😭"
        )

        val specific = when (type) {
            IssueType.NAMING -> listOf(
                "These variable names are giving 'I gave up' energy 💀",
                "Naming variables 'x' and 'temp'? That's so not slay bestie",
                "'Data' as a variable name? Babe, no. Just no. 🚩",
                "These names are giving placeholder vibes. Did you forget to change them? 💀"
            )

            IssueType.NESTING -> listOf(
                "This nesting is sending me to another dimension 😵‍💫",
                "Four levels deep? Bestie, that's giving overthinking energy 💀",
                "The way this nesting is absolutely unhinged periodt",
                "This code structure is NOT giving what it needs to give 🚫"
            )

            IssueType.LENGTH -> listOf(
                "This function said 'I'm gonna be long' and ATE (in a bad way) 💀",
                "60 lines? Bestie, nobody's reading all that. Break it up fr fr",
                "The length of this function is giving essay energy 📄",
                "This function is longer than my Starbucks order. That's concerning. ☕"
            )

            IssueType.DUPLICATION -> listOf(
                "Copy-paste-copy-paste? That's giving lazy Sunday energy 😴",
                "Why are we copying code like it's a group chat message? 💀",
                "This duplication is NOT the vibe, bestie 🚩",
                "You really said 'ctrl+c ctrl+v' five times and called it a day? 💅"
            )

            IssueType.SECURITY -> listOf(
                "No error handling? That's giving 'YOLO' energy 💀",
                "Bestie, where's the try-catch? This is lowkey dangerous fr",
                "This security is giving 'what could go wrong?' vibes 🚩",
                "Not you leaving the back door open like that 💀"
            )

            IssueType.STYLE -> listOf(
                "The formatting? Oof. It's giving 'I don't care' energy 💀",
                "This style is NOT it. Not giving professional vibes AT ALL 💅",
                "The way this looks like it was written in the dark 😭",
                "Bestie said 'formatting is optional' and it shows 🚩"
            )

            IssueType.PERFORMANCE -> listOf(
                "This code is slower than me getting ready. That's BAD. 💀",
                "Performance? More like poor-formance (I'll see myself out) 🚪",
                "This runs slower than my motivation on a Monday fr fr 😴",
                "The way this algorithm is NOT giving efficient vibes 💅"
            )
        }

        return common + specific
    }

    /**
     * Shakespeare roast templates
     */
    private fun getShakespeareTemplates(type: IssueType): List<String> {
        val common = listOf(
            "What fresh code hell doth mine eyes behold?!",
            "To refactor, or not to refactor—the answer is TO REFACTOR!",
            "This code art a most grievous tragedy!",
            "Methinks this script was penned by one who hath never glimpsed clean architecture!",
            "Alas! What foul code doth assault mine senses!",
            "By my troth, this code offends mine very soul!",
            "Out, out, damned code! Out, I say!",
            "These lines art more tangled than a tragic plot!"
        )

        val specific = when (type) {
            IssueType.NAMING -> listOf(
                "These variables art named most foully! What manner of 'x' is this?",
                "Thy names lack meaning, like a play without plot!",
                "'Temp' and 'data'? Fie! Give them names of substance!",
                "These appellations art vaguer than a prophecy!"
            )

            IssueType.NESTING -> listOf(
                "What tangled web of nesting dost thou weave!",
                "These nested ifs art deeper than Hamlet's despair!",
                "Thy conditionals art more layered than a tragic misunderstanding!",
                "Flatten thy code, lest it collapse under its own weight!"
            )

            IssueType.LENGTH -> listOf(
                "This function stretcheth longer than a five-act play!",
                "Brevity is the soul of wit! This hath none!",
                "Thy function runneth on like a verbose soliloquy!",
                "Prithee, trim this verbosity! 'Tis most excessive!"
            )

            IssueType.DUPLICATION -> listOf(
                "Thou repeatest thyself like a maddened player!",
                "This redundancy wouldst bore even the groundlings!",
                "Extract thy repeated lines into a function, good sir!",
                "Such repetition doth maketh for tedious theater!"
            )

            IssueType.SECURITY -> listOf(
                "Where art thy guards against errors? Thy code lies defenseless!",
                "Without error handling, thy program courts disaster!",
                "Thy code is as unprotected as Juliet's balcony!",
                "Fortify thy functions with try-catch, I beseech thee!"
            )

            IssueType.STYLE -> listOf(
                "The presentation of thy code offends the eye!",
                "Format thy lines as one would format a sonnet!",
                "This style is most unseemly and unbecoming!",
                "Even a tavern brawl hath better structure than this!"
            )

            IssueType.PERFORMANCE -> listOf(
                "Thy algorithm moveth slower than a tragic denouement!",
                "This performance lacketh the speed of wit!",
                "Optimize, lest thy users fall asleep mid-execution!",
                "This code runneth like a player with stage fright!"
            )
        }

        return common + specific
    }

    /**
     * Clears the roast cache (useful for testing)
     */
    fun clearCache() {
        roastCache.clear()
        Log.d(TAG, "Roast cache cleared")
    }

    /**
     * Gets cache statistics for monitoring
     */
    fun getCacheStats(): String {
        return "Cache size: ${roastCache.size}/$MAX_CACHE_SIZE entries"
    }

    /**
     * Gets the number of cached roasts
     */
    fun getCacheSize(): Int = roastCache.size
}
