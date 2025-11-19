package com.example.coderoastai.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coderoastai.CodeExample
import com.example.coderoastai.ui.theme.*

/**
 * Examples Screen - Shows pre-loaded terrible code examples
 */
@Composable
fun ExamplesScreen(
    onExampleSelected: (CodeExample) -> Unit,
    onNavigateToHome: () -> Unit
) {
    val examples = remember { getAllCodeExamples() }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Python", "JavaScript", "Java", "Kotlin", "C++")

    val filteredExamples = if (selectedCategory == "All") {
        examples
    } else {
        examples.filter { it.language == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        // Header
        ExamplesHeader()

        // Category Tabs
        CategoryTabs(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        // Examples List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredExamples) { example ->
                ExampleDetailedCard(
                    example = example,
                    onClick = {
                        onExampleSelected(example.toCodeExample())
                        onNavigateToHome()
                    }
                )
            }

            // Bottom padding for nav bar
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun ExamplesHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkGray.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📚",
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Code Examples",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = NeonCyan
                )
                Text(
                    text = "Tap to load and roast",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun CategoryTabs(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkGray.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelected(category) },
                    label = {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    enabled = true,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                        selectedLabelColor = NeonCyan,
                        containerColor = GlassWhite10,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedCategory == category,
                        borderColor = if (selectedCategory == category) NeonCyan else GlassWhite20,
                        selectedBorderColor = NeonCyan,
                        borderWidth = 1.dp,
                        selectedBorderWidth = 2.dp
                    )
                )
            }
        }
    }
}

@Composable
fun ExampleDetailedCard(
    example: ExtendedCodeExample,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkGray.copy(alpha = 0.6f)
        ),
        border = BorderStroke(1.dp, getLanguageColor(example.language).copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = example.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = getLanguageColor(example.language).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = example.language,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = getLanguageColor(example.language),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = example.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Issues in a simple row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                example.issues.forEach { issue ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = NeonRed.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "• $issue",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonRed,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Load button - simplified
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = getLanguageColor(example.language)
                )
            ) {
                Text(
                    text = "LOAD EXAMPLE",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = DeepBlack,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

fun getLanguageColor(language: String): Color {
    return when (language) {
        "Python" -> NeonYellow
        "JavaScript" -> NeonYellow
        "Java" -> NeonRed
        "Kotlin" -> NeonPurple
        "C++" -> NeonCyan
        else -> NeonGreen
    }
}

// Extended CodeExample with issues
data class ExtendedCodeExample(
    val name: String,
    val language: String,
    val code: String,
    val description: String,
    val issues: List<String>,
    val expectedScore: String
)

fun getAllCodeExamples(): List<ExtendedCodeExample> {
    return listOf(
        ExtendedCodeExample(
            name = "Spaghetti Python",
            language = "Python",
            code = """
# Everything in global scope, no functions
x = 5
y = 10
if x > 3:
    z = x + y
    if z > 10:
        result = z * 2
        if result > 20:
            final = result / 2
            print(final)
else:
    print("nope")
            """.trimIndent(),
            description = "No functions, everything in global scope. Logic scattered everywhere with no structure.",
            issues = listOf("No Functions", "Global Variables", "Deep Nesting"),
            expectedScore = "15-25"
        ),

        ExtendedCodeExample(
            name = "Variable Hell",
            language = "JavaScript",
            code = """
function f(a, b, c) {
    let x = a + b;
    let y = x * c;
    let z = y / 2;
    let w = z + 10;
    let q = w - 5;
    let r = q * 3;
    return r;
}
            """.trimIndent(),
            description = "Single-letter variable names everywhere. Impossible to understand what the function does.",
            issues = listOf("Poor Naming", "Magic Numbers", "No Documentation"),
            expectedScore = "20-30"
        ),

        ExtendedCodeExample(
            name = "Nested Nightmare",
            language = "Java",
            code = """
public void processData(Data data) {
    if (data != null) {
        if (data.isValid()) {
            if (data.hasPermission()) {
                if (data.getUser() != null) {
                    if (data.getUser().isActive()) {
                        if (data.getUser().getAge() > 18) {
                            if (data.getUser().hasConsent()) {
                                // Finally do something
                                process(data);
                            }
                        }
                    }
                }
            }
        }
    }
}
            """.trimIndent(),
            description = "7 levels of nested if statements. The arrow anti-pattern in its full glory.",
            issues = listOf("Deep Nesting", "No Early Returns", "Complexity"),
            expectedScore = "10-20"
        ),

        ExtendedCodeExample(
            name = "Memory Leaker",
            language = "C++",
            code = """
void createData() {
    int* data = new int[1000];
    // Process data
    for(int i = 0; i < 1000; i++) {
        data[i] = i * 2;
    }
    // Oops, forgot to delete[]!
}

void processFile(string filename) {
    FILE* file = fopen(filename.c_str(), "r");
    // Read file
    char buffer[100];
    fgets(buffer, 100, file);
    // Never closed!
}
            """.trimIndent(),
            description = "Memory leaks and resource leaks everywhere. No cleanup, no RAII.",
            issues = listOf("Memory Leak", "Resource Leak", "No RAII"),
            expectedScore = "5-15"
        ),

        ExtendedCodeExample(
            name = "SQL Injection Special",
            language = "JavaScript",
            code = """
function loginUser(username, password) {
    const query = "SELECT * FROM users WHERE " +
                  "username = '" + username + "' AND " +
                  "password = '" + password + "'";
    return db.execute(query);
}

function searchProducts(term) {
    return db.query("SELECT * FROM products WHERE name LIKE '%" + term + "%'");
}
            """.trimIndent(),
            description = "Classic SQL injection vulnerability. User input directly concatenated into queries.",
            issues = listOf("SQL Injection", "Security", "No Sanitization"),
            expectedScore = "0-10"
        ),

        ExtendedCodeExample(
            name = "The Copy-Paster",
            language = "Python",
            code = """
def calculate_price_1(items):
    total = 0
    for item in items:
        total += item.price * item.quantity
    return total * 1.1

def calculate_price_2(products):
    total = 0
    for product in products:
        total += product.price * product.quantity
    return total * 1.1

def calculate_price_3(goods):
    total = 0
    for good in goods:
        total += good.price * good.quantity
    return total * 1.1
            """.trimIndent(),
            description = "Same function copy-pasted three times with minimal changes. DRY principle completely ignored.",
            issues = listOf("Duplication", "No Abstraction", "Maintenance Nightmare"),
            expectedScore = "15-25"
        ),

        ExtendedCodeExample(
            name = "Performance Disaster",
            language = "Python",
            code = """
def find_duplicates(arr):
    duplicates = []
    for i in range(len(arr)):
        for j in range(len(arr)):
            if i != j and arr[i] == arr[j]:
                if arr[i] not in duplicates:
                    for k in range(len(duplicates)):
                        if duplicates[k] == arr[i]:
                            break
                    else:
                        duplicates.append(arr[i])
    return duplicates
            """.trimIndent(),
            description = "O(n³) algorithm when O(n) is possible. Triple nested loops for a simple task.",
            issues = listOf("O(n³)", "Performance", "Could use Set"),
            expectedScore = "10-20"
        ),

        ExtendedCodeExample(
            name = "Style Violator",
            language = "JavaScript",
            code = """
var x=10;var y=20;
function foo(a,b){return a+b;}
if(x>5){console.log("yes");}else{console.log("no");}
const arr=[1,2,3,4,5];arr.forEach(function(item){console.log(item);});
            """.trimIndent(),
            description = "Every style rule violated: no spaces, var instead of let/const, inconsistent formatting.",
            issues = listOf("Style", "var usage", "No Spacing"),
            expectedScore = "20-30"
        ),

        ExtendedCodeExample(
            name = "Comment-Less Code",
            language = "Kotlin",
            code = """
fun p(l: List<Int>): Int {
    var r = 0
    val m = mutableListOf<Int>()
    for (i in l) {
        if (i % 2 == 0) {
            m.add(i)
        }
    }
    for (j in m) {
        r += j * 2
    }
    return r / m.size
}
            """.trimIndent(),
            description = "Zero documentation, cryptic variable names, no explanation of logic. Good luck maintaining this!",
            issues = listOf("No Comments", "Poor Naming", "Unclear Logic"),
            expectedScore = "15-25"
        ),

        ExtendedCodeExample(
            name = "The God Function",
            language = "Java",
            code = """
public void handleUserRequest(Request req) {
    // Validate (30 lines)
    if (req == null) throw new Exception();
    // ... 25 more validation lines
    
    // Parse (40 lines)  
    String data = req.getData();
    // ... 35 more parsing lines
    
    // Process (50 lines)
    ProcessResult result = new ProcessResult();
    // ... 45 more processing lines
    
    // Transform (40 lines)
    TransformedData transformed = transform(result);
    // ... 35 more transformation lines
    
    // Validate again (30 lines)
    // ... validation code
    
    // Save (40 lines)
    // ... database operations
    
    // Send notifications (30 lines)
    // ... notification code
    
    // Log everything (20 lines)
    // ... logging code
    
    // Cleanup (30 lines)
    // ... cleanup code
}
            """.trimIndent(),
            description = "300+ line function that does EVERYTHING. Validation, parsing, processing, saving, notifications, logging. Should be 10+ separate functions.",
            issues = listOf("300+ Lines", "God Function", "No SRP"),
            expectedScore = "0-10"
        )
    )
}

// Convert to original CodeExample format
fun ExtendedCodeExample.toCodeExample(): CodeExample {
    return CodeExample(
        name = this.name,
        language = this.language,
        code = this.code,
        description = this.description
    )
}
            