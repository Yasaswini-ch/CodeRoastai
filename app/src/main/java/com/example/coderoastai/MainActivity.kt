package com.example.coderoastai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coderoastai.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeRoastaiTheme {
                CodeRoastApp()
            }
        }
    }
}

@Composable
fun CodeRoastMainScreen() {
    var selectedLanguage by remember { mutableStateOf("Python") }
    var selectedPersonality by remember { mutableStateOf("Gordon Ramsay") }
    var roastIntensity by remember { mutableStateOf(3) }
    var codeInput by remember { mutableStateOf("") }
    var showExamplesDialog by remember { mutableStateOf(false) }
    var isRoasting by remember { mutableStateOf(false) }
    var showResults by remember { mutableStateOf(false) }
    var roastResults by remember { mutableStateOf<List<String>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Content - Scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp, bottom = 80.dp)
        ) {
            // ============================================================
            // HEADER - Centered
            // ============================================================
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

            // ============================================================
            // LANGUAGE SECTION
            // ============================================================
            Column(modifier = Modifier.fillMaxWidth()) {
                // Label - Left aligned
                Text(
                    text = "LANGUAGE",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Language Buttons - Centered, horizontally scrollable
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.Center
                ) {
                    LanguageButton(
                        emoji = "🐍",
                        language = "Python",
                        isSelected = selectedLanguage == "Python",
                        onClick = { selectedLanguage = "Python" }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    LanguageButton(
                        emoji = "⚡",
                        language = "JS",
                        isSelected = selectedLanguage == "JavaScript",
                        onClick = { selectedLanguage = "JavaScript" }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    LanguageButton(
                        emoji = "☕",
                        language = "Java",
                        isSelected = selectedLanguage == "Java",
                        onClick = { selectedLanguage = "Java" }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    LanguageButton(
                        emoji = "🎯",
                        language = "Kotlin",
                        isSelected = selectedLanguage == "Kotlin",
                        onClick = { selectedLanguage = "Kotlin" }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    LanguageButton(
                        emoji = "⚙️",
                        language = "C++",
                        isSelected = selectedLanguage == "C++",
                        onClick = { selectedLanguage = "C++" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ============================================================
            // CODE INPUT SECTION
            // ============================================================
            Column(modifier = Modifier.fillMaxWidth()) {
                // Label - Left aligned
                Text(
                    text = "CODE INPUT",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Code Editor - Full width
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = DarkGray.copy(alpha = 0.8f),
                    border = BorderStroke(1.dp, GlassWhite20)
                ) {
                    TextField(
                        value = codeInput,
                        onValueChange = { codeInput = it },
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

            Spacer(modifier = Modifier.height(24.dp))

            // ============================================================
            // PERSONALITY SECTION
            // ============================================================
            Column(modifier = Modifier.fillMaxWidth()) {
                // Label - Left aligned
                Text(
                    text = "PERSONALITY",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Personality Cards - Equal size, horizontally scrollable
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PersonalityCard(
                        emoji = "🔪",
                        name = "Ramsay",
                        isSelected = selectedPersonality == "Gordon Ramsay",
                        onClick = { selectedPersonality = "Gordon Ramsay" }
                    )
                    PersonalityCard(
                        emoji = "🎖️",
                        name = "Sarge",
                        isSelected = selectedPersonality == "Drill Sergeant",
                        onClick = { selectedPersonality = "Drill Sergeant" }
                    )
                    PersonalityCard(
                        emoji = "👔",
                        name = "Dad",
                        isSelected = selectedPersonality == "Disappointed Dad",
                        onClick = { selectedPersonality = "Disappointed Dad" }
                    )
                    PersonalityCard(
                        emoji = "💅",
                        name = "GenZ",
                        isSelected = selectedPersonality == "Gen Z",
                        onClick = { selectedPersonality = "Gen Z" }
                    )
                    PersonalityCard(
                        emoji = "🎭",
                        name = "Shak",
                        isSelected = selectedPersonality == "Shakespeare",
                        onClick = { selectedPersonality = "Shakespeare" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ============================================================
            // ROAST INTENSITY SECTION
            // ============================================================
            Column(modifier = Modifier.fillMaxWidth()) {
                // Label - Left aligned
                Text(
                    text = "ROAST INTENSITY",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Slider - Full width
                Slider(
                    value = roastIntensity.toFloat(),
                    onValueChange = { roastIntensity = it.toInt() },
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

                // Emojis - Evenly spaced below slider
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IntensityEmoji(emoji = "😊", level = 1, currentLevel = roastIntensity)
                    IntensityEmoji(emoji = "😐", level = 2, currentLevel = roastIntensity)
                    IntensityEmoji(emoji = "😠", level = 3, currentLevel = roastIntensity)
                    IntensityEmoji(emoji = "😡", level = 4, currentLevel = roastIntensity)
                    IntensityEmoji(emoji = "💀", level = 5, currentLevel = roastIntensity)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ============================================================
            // ROAST BUTTON - Big, centered, pill-shaped
            // ============================================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Helper text when no code
                if (codeInput.isBlank()) {
                    Text(
                        text = "⬆️ Enter code above or tap 'Examples' below ⬇️",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Button(
                    onClick = {
                        if (!isRoasting && codeInput.isNotBlank()) {
                            isRoasting = true
                            showResults = false
                            scope.launch {
                                delay(2000)
                                roastResults = generateSampleRoasts(selectedPersonality, roastIntensity)
                                isRoasting = false
                                showResults = true
                                delay(300)
                                scope.launch {
                                    scrollState.animateScrollTo(scrollState.maxValue)
                                }
                            }
                        }
                    },
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
                            Text(
                                text = "🔥",
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }

            // ============================================================
            // RESULTS SECTION (if available)
            // ============================================================
            if (showResults && roastResults.isNotEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))
                RoastResultsSection(
                    roasts = roastResults,
                    personality = selectedPersonality,
                    onTryAgain = {
                        showResults = false
                        roastResults = emptyList()
                    }
                )
            }
        }

        // ============================================================
        // BOTTOM BAR - Fixed at bottom
        // ============================================================
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = DarkGray.copy(alpha = 0.95f),
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { showExamplesDialog = true },
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = "Examples",
                        style = MaterialTheme.typography.labelLarge,
                        color = NeonCyan
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = { codeInput = "" },
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = "Clear",
                            style = MaterialTheme.typography.labelLarge,
                            color = NeonRed
                        )
                    }

                    IconButton(
                        onClick = { /* Settings */ },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Text(
                            text = "⚙️",
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }

    // Examples Dialog
    if (showExamplesDialog) {
        ExamplesDialog(
            selectedLanguage = selectedLanguage,
            onDismiss = { showExamplesDialog = false },
            onExampleSelected = { example ->
                codeInput = example.code
                selectedLanguage = example.language
                showExamplesDialog = false
            }
        )
    }
}

// ============================================================
// LANGUAGE BUTTON COMPONENT
// ============================================================
@Composable
fun LanguageButton(
    emoji: String,
    language: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
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
            Text(
                text = emoji,
                fontSize = 18.sp
            )
            Text(
                text = language,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = textColor
            )
        }
    }
}

// ============================================================
// PERSONALITY CARD COMPONENT - Equal size
// ============================================================
@Composable
fun PersonalityCard(
    emoji: String,
    name: String,
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
            Text(
                text = emoji,
                fontSize = 28.sp
            )
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

// ============================================================
// INTENSITY EMOJI COMPONENT
// ============================================================
@Composable
fun IntensityEmoji(emoji: String, level: Int, currentLevel: Int) {
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

// ============================================================
// ROAST RESULTS SECTION
// ============================================================
@Composable
fun RoastResultsSection(
    roasts: List<String>,
    personality: String,
    onTryAgain: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Results Header
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

        // Roast Cards
        roasts.forEachIndexed { index, roast ->
            RoastCard(roast = roast, index = index + 1)
            if (index < roasts.size - 1) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Try Again Button
        OutlinedButton(
            onClick = onTryAgain,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
            border = BorderStroke(2.dp, NeonCyan)
        ) {
            Text(
                text = "↻ Try Different Settings",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun RoastCard(roast: String, index: Int) {
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

// ============================================================
// CODE EXAMPLES
// ============================================================
data class CodeExample(
    val name: String,
    val language: String,
    val code: String,
    val description: String
)

val codeExamples = listOf(
    CodeExample(
        name = "Poor Naming",
        language = "Python",
        code = """
def f(x, y):
    temp = x + y
    data = temp * 2
    return data

var1 = f(5, 3)
print(var1)
        """.trimIndent(),
        description = "Variables with vague names like 'temp', 'data', 'var1'"
    ),
    CodeExample(
        name = "Nested Mess",
        language = "JavaScript",
        code = """
function processUser(user) {
    if (user) {
        if (user.isActive) {
            if (user.age > 18) {
                if (user.hasPermission) {
                    if (user.email) {
                        return true;
                    }
                }
            }
        }
    }
    return false;
}
        """.trimIndent(),
        description = "Excessive nesting - 5 levels deep!"
    ),
    CodeExample(
        name = "Long Function",
        language = "Java",
        code = """
public void handleRequest() {
    System.out.println("Starting...");
    for (int i = 0; i < 100; i++) {
        System.out.println(i);
        if (i % 2 == 0) {
            System.out.println("Even");
        }
    }
    System.out.println("Done");
}
        """.trimIndent(),
        description = "Function longer than 50 lines"
    ),
    CodeExample(
        name = "Copy-Paste Code",
        language = "Python",
        code = """
def calculate_total_1(items):
    total = sum(item.price for item in items)
    return total * 1.1

def calculate_total_2(products):
    total = sum(product.price for product in products)
    return total * 1.1

def calculate_total_3(goods):
    total = sum(good.price for good in goods)
    return total * 1.1
        """.trimIndent(),
        description = "Same code repeated 3 times - DRY principle violation!"
    ),
    CodeExample(
        name = "No Error Handling",
        language = "JavaScript",
        code = """
function readConfig() {
    const data = JSON.parse(userInput);
    const file = fs.readFileSync('config.json');
    const result = parseInt(someValue);
    return database.connect(data);
}
        """.trimIndent(),
        description = "Risky operations without try-catch blocks"
    ),
    CodeExample(
        name = "Style Violations",
        language = "JavaScript",
        code = """
var x=10;
var y=20;
if(x>5)
console.log("yes");
function foo(a,b){return a+b;}
        """.trimIndent(),
        description = "Missing spaces, inconsistent formatting"
    ),
    CodeExample(
        name = "All Issues Combined",
        language = "Python",
        code = """
def process(x):
    if x:
        if x > 0:
            if x < 100:
                temp = x * 2
                data = temp + 5
                result = data / 3
                return result
    return None

var1 = process(10)
var2 = process(20)
var3 = process(30)
        """.trimIndent(),
        description = "Poor naming + nesting + duplication"
    ),
    CodeExample(
        name = "Kotlin Mess",
        language = "Kotlin",
        code = """
fun x(a: Int, b: Int): Int {
    var temp = 0
    if (a > 0) {
        if (b > 0) {
            if (a > b) {
                temp = a + b
            }
        }
    }
    return temp
}
        """.trimIndent(),
        description = "Poor naming and excessive nesting in Kotlin"
    ),
    CodeExample(
        name = "C++ Horror",
        language = "C++",
        code = """
int f(int x,int y){
int temp=x+y;
if(temp>0){
if(temp<100){
return temp*2;
}
}
return 0;
}
        """.trimIndent(),
        description = "No spacing, poor formatting, unclear logic"
    )
)

@Composable
fun ExamplesDialog(
    selectedLanguage: String,
    onDismiss: () -> Unit,
    onExampleSelected: (CodeExample) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    "📝 CODE EXAMPLES",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ),
                    color = NeonCyan
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Try these terrible code snippets!",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                codeExamples.forEach { example ->
                    ExampleCard(
                        example = example,
                        onClick = { onExampleSelected(example) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CLOSE", color = NeonRed)
            }
        },
        containerColor = DarkGray,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun ExampleCard(
    example: CodeExample,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = GlassWhite10,
        border = BorderStroke(1.dp, GlassWhite20)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = example.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NeonCyan.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, NeonCyan)
                ) {
                    Text(
                        text = example.language,
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = example.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

// Generate sample roasts based on personality and intensity
fun generateSampleRoasts(personality: String, intensity: Int): List<String> {
    return when (personality) {
        "Gordon Ramsay" -> when (intensity) {
            1 -> listOf(
                "This code could use some improvement, honestly.",
                "The variable names are a bit unclear.",
                "Maybe consider adding some comments here.",
                "This structure is somewhat confusing."
            )

            2 -> listOf(
                "This code is undercooked! Where's the structure?",
                "These variable names? Not good enough!",
                "You're missing error handling. Not acceptable.",
                "This nesting is getting messy. Clean it up!"
            )

            3 -> listOf(
                "This code is RAW! Completely RAW!",
                "What ARE these variable names?! 'x'? 'temp'? Ridiculous!",
                "Where's the error handling?! You'll poison the users!",
                "This nesting is a mess! Like a Russian doll of incompetence!",
                "These functions are all over the place! ORGANIZE IT!"
            )

            4 -> listOf(
                "This code is DISGUSTING! It's ROTTEN!",
                "You call this a function?! My DEAD GRANDMOTHER writes better code!",
                "These variable names are PATHETIC! What is WRONG with you?!",
                "This is the WORST nesting I've ever seen! IT'S RAW!",
                "NO error handling?! You're going to KILL someone with this code!",
                "SHUT IT DOWN! This entire codebase needs to be thrown in the BIN!"
            )

            5 -> listOf(
                "GET OUT! GET OUT OF MY KITCHEN! This code is an ABOMINATION!",
                "YOU DONKEY! You absolute MUPPET! This is the WORST code I've EVER seen!",
                "These variable names?! 'x'?! 'temp'?! Are you MENTAL?! This is GARBAGE!",
                "This nesting! LOOK AT IT! It's like the NINTH CIRCLE OF HELL!",
                "NO error handling! NO structure! NO brain cells! GET. OUT!",
                "I wouldn't serve this code to my WORST ENEMY! It's DISGUSTING! RAW! PATHETIC!",
                "This is an INSULT to programming! Shut down your computer and NEVER CODE AGAIN!"
            )

            else -> listOf("This code needs work.")
        }

        "Drill Sergeant" -> when (intensity) {
            1 -> listOf(
                "Soldier, this code needs some work.",
                "Not bad, but it could be better.",
                "Add some structure here, recruit.",
                "These variables could be clearer."
            )

            2 -> listOf(
                "This code is unacceptable, recruit!",
                "Your variable names are out of uniform!",
                "Add error handling, NOW!",
                "This nesting is too deep! Flatten it!"
            )

            3 -> listOf(
                "ATTENTION! This code is a DISGRACE!",
                "Your variable names are OUT OF UNIFORM! FIX THEM!",
                "This nesting is DEEPER than enemy territory!",
                "WHERE'S your error handling, soldier?!",
                "Drop and give me 20 refactors, MAGGOT!"
            )

            4 -> listOf(
                "WHAT IS THIS GARBAGE?! This code is UNFIT FOR DUTY!",
                "Your variables are AWOL! They have NO MEANING!",
                "This nesting?! You trying to DIG TO CHINA?!",
                "NO error handling?! That's a COURT MARTIAL offense!",
                "This code is SOFTER than baby powder! PATHETIC!",
                "You call yourself a developer?! My BOOTCAMP WASHOUTS code better!"
            )

            5 -> listOf(
                "WHAT IN THE NAME OF ALL THAT IS HOLY IS THIS PILE OF TRASH?!",
                "MAGGOT! This code is a WAR CRIME against programming!",
                "These variables?! They're DESERTERS! They tell me NOTHING!",
                "This nesting is so DEEP you've reached the Earth's CORE!",
                "NO error handling?! You're trying to SABOTAGE the ENTIRE OPERATION!",
                "This is the WORST code I've seen in 30 YEARS OF SERVICE!",
                "You are DISHONORABLY DISCHARGED from this codebase! GET OUT!"
            )

            else -> listOf("This code needs work, soldier.")
        }

        "Disappointed Dad" -> when (intensity) {
            1 -> listOf(
                "Hey kiddo, maybe add some comments here?",
                "These variable names could be clearer, sport.",
                "Consider some error handling, okay?",
                "The structure is a bit confusing, champ."
            )

            2 -> listOf(
                "Son, we need to talk about this code.",
                "These variable names... they're not great.",
                "Where's the error handling? I expected better.",
                "This nesting... it's getting messy, buddy."
            )

            3 -> listOf(
                "Son, I'm not angry... just disappointed.",
                "You had so much potential. What happened?",
                "Your cousin writes clean code. Why can't you?",
                "Naming variables 'x' and 'y'? Really? That expensive bootcamp taught you this?",
                "This nesting... it's like you're hiding from your problems."
            )

            4 -> listOf(
                "I don't even know what to say anymore. I really don't.",
                "All those nights I stayed up helping you learn to code... for THIS?",
                "Your mother is crying. She saw your code. How could you do this to us?",
                "I told everyone at the BBQ you were a programmer. Now I have to lie to them.",
                "Remember when you said you'd make me proud? I remember. I wish I didn't.",
                "This code... it breaks my heart. Actually breaks it."
            )

            5 -> listOf(
                "I've taken your photo off the mantle. I can't look at it anymore.",
                "Your grandfather... he was a COBOL programmer. He's rolling in his grave.",
                "I'm not mad. I'm beyond mad. I'm... I don't know what I am anymore.",
                "When you were born, I had dreams. Big dreams. And now... this code.",
                "Your sister never wrote code like this. NEVER. And she's a ACCOUNTANT.",
                "I can't... I need to sit down. My chest hurts. Is this what a heart attack feels like?",
                "Don't call me Dad anymore. Don't call me anything."
            )

            else -> listOf("This code needs work, son.")
        }

        "Gen Z" -> when (intensity) {
            1 -> listOf(
                "Bestie, this code needs a glow-up ✨",
                "Not you with the mid variable names 💀",
                "Add some error handling, bestie! 💅",
                "This nesting is giving confused vibes 😅"
            )

            2 -> listOf(
                "Bestie, this code is giving 'I tried' energy 😬",
                "These variable names are so not it, babes.",
                "No error handling? That's sus, bestie 🚩",
                "This nesting is sending me fr fr 💀"
            )

            3 -> listOf(
                "Bestie, this code is giving major ick vibes 💀",
                "These variable names? Giving 'I gave up' energy. Not slay.",
                "This nesting is sending me to another dimension 😵‍💫",
                "No error handling? That's giving YOLO energy 💀",
                "Not you writing code like it's 2010! Bestie, NO."
            )

            4 -> listOf(
                "BESTIE. BESTIE NO. This code is NOT giving what it's supposed to give! 💀",
                "The way these variables have NO NAME?! Girl, bye. 👋",
                "This nesting?! It's serving NOTHING. Absolutely NOTHING! 😤",
                "No cap, this is the most unserious code I've ever seen 💀💀💀",
                "This code really said 'I woke up and chose violence against myself' 😭",
                "Bestie I'm SCREAMING. This is so bad it's giving 'never learned to code' vibes"
            )

            5 -> listOf(
                "JAIL. PRISON. STRAIGHT TO JAIL. DO NOT PASS GO. 🚨🚨🚨",
                "BESTIE I'M ON THE FLOOR. I'VE DIED. YOU'VE KILLED ME WITH THIS CODE 💀⚰️",
                "This is VIOLENCE. This is ASSAULT. I'm calling the POLICE! 👮‍♀️🚔",
                "The way I just SCREAMED at my screen?! My neighbor thinks I'm INSANE! 😱😱😱",
                "NO. NOPE. NEGATIVE. NOT IN THIS LIFETIME OR THE NEXT ONE! 🙅‍♀️❌",
                "I'M CRYING. I'M THROWING UP. This code is the END OF CIVILIZATION! 🤮😭",
                "BESTIE LOG OFF. Delete your IDE. Throw your computer in the OCEAN! 🌊💻",
                "This code is so bad it gave me PHYSICAL PAIN. I need MEDICAL ATTENTION! 🏥💉"
            )

            else -> listOf("This code needs work, bestie.")
        }

        "Shakespeare" -> when (intensity) {
            1 -> listOf(
                "Fair code, yet lacking in clarity.",
                "These names art somewhat unclear, methinks.",
                "Perhaps add guards against errors?",
                "The structure doth confuse mine eyes."
            )

            2 -> listOf(
                "This code lacketh refinement, good sir.",
                "These variable names art most unclear!",
                "Where art thy defenses against errors?",
                "This nesting grows too deep, I fear."
            )

            3 -> listOf(
                "What fresh code hell doth mine eyes behold?!",
                "To refactor, or not to refactor—the answer is TO REFACTOR!",
                "These variables art named most foully! What manner of 'x' is this?",
                "What tangled web of nesting dost thou weave!",
                "Where art thy guards against errors? Thy code lies defenseless!"
            )

            4 -> listOf(
                "O, what a rogue and peasant slave am I to witness such CODE!",
                "These variables! They art but EMPTY VESSELS with no NAME!",
                "This nesting! 'Tis deeper than Hamlet's despair! Deeper than the GRAVE!",
                "No error handling?! This is a TRAGEDY most foul! Most CRUEL!",
                "Out, damned code! Out, I say! This is MURDER most vile!",
                "The quality of this code is NOT strained. 'Tis ABHORRENT!"
            )

            5 -> listOf(
                "AVAUNT! Begone, thou FOUL and PESTILENT code! A POX upon thee!",
                "These variables! They art VILLAINOUS! TREACHEROUS! CURSED be thy naming!",
                "What HELL-BORN nesting is this?! 'Tis deeper than Dante's INFERNO!",
                "Double, double, toil and trouble! This code burns like HELLFIRE!",
                "Is this a dagger I see before me?! NAY! 'Tis code so VILE it WOUNDS mine very SOUL!",
                "THOU ART A KNAVE! A SCOUNDREL! Thy code is fit only for the GALLOWS!",
                "All the perfumes of Arabia could NOT sweeten this ROTTEN code!",
                "Et tu, Developer?! Then fall, Caesar—for this code MURDERS all that is GOOD!"
            )

            else -> listOf("This code doth need improvement.")
        }

        else -> when (intensity) {
            1 -> listOf(
                "This code could be improved.",
                "Consider better naming.",
                "Add error handling."
            )

            2 -> listOf(
                "This code needs work.",
                "Variable naming is unclear.",
                "Missing error handling."
            )

            3 -> listOf("This code has issues.", "Poor variable names.", "No error handling.")
            4 -> listOf("This code is bad.", "Terrible naming.", "No error handling at all.")
            5 -> listOf(
                "This code is awful.",
                "Horrible naming.",
                "Completely missing error handling."
            )

            else -> listOf("This code needs work.")
        }
    }
}
