# Code Fix Feature - Complete Guide

## 🎯 Overview

The **Code Fix Feature** uses AI (RunAnywhere SDK) to automatically refactor and improve code while
preserving original logic. It provides a split-screen diff view, improvement metrics, and actionable
buttons.

---

## ✨ Features Implemented

### 1. **CodeFixGenerator** (`CodeFixGenerator.kt`)

Intelligent code fixing engine that:

#### Capabilities

- ✅ Renames variables meaningfully (`temp` → `temperatureValue`)
- ✅ Breaks long functions into smaller ones
- ✅ Reduces nesting with early returns
- ✅ Adds error handling (try-catch blocks)
- ✅ Removes code duplication
- ✅ Adds meaningful comments
- ✅ **Preserves original logic completely**
- ✅ Validates generated code syntax

#### AI Prompt Engineering

```kotlin
// Comprehensive prompt includes:
- List of detected issues
- Specific fixing requirements
- Language-specific best practices
- Original code for context
- Output format specification
```

#### Progress Tracking

```kotlin
sealed class GenerationState {
    data class Loading(percentage: Int, message: String)
    data class Success(result: FixResult)
    data class Error(message: String)
}
```

**Progress Messages:**

- 0-20%: "Analyzing code structure..."
- 20-80%: "Generating improvements..."
- 80-95%: "Polishing code..."
- 95%+: "Validating fixes..."

#### Code Validation

Performs basic syntax checks:

- **Python**: Indentation, no markdown artifacts
- **JavaScript/TypeScript**: Balanced braces `{}`
- **Java/Kotlin**: Class/function structure, balanced braces
- **C/C++**: Includes, balanced braces
- **Generic**: Min length, non-empty

#### Improvement Detection

Automatically detects:

- Variable renaming (short → descriptive)
- Line reduction
- Error handling additions
- Nesting depth reduction
- Comment additions
- Fixed issues from roasts

#### Metrics Calculation

```kotlin
data class MetricsChange(
    linesBefore: Int,
    linesAfter: Int,
    complexityBefore: Int,
    complexityAfter: Int,
    issuesFixed: Int
)
```

#### Score Estimation

Heuristic-based scoring:

- Deduct for long functions (>50 lines): -5 per function
- Deduct for high nesting: -3 per level above 2
- Deduct for short variable names: -2 per var
- Bonus for comments: +2 per comment (max +10)
- Range: 0-100

---

### 2. **FixComparisonScreen** (`FixComparisonScreen.kt`)

Beautiful split-screen comparison with cyberpunk styling.

#### Layout

**Phone (< 600dp):**

```
┌─────────────────────────┐
│  Metrics Card (Top)     │
├─────────────────────────┤
│  Original Code (Top)    │
│  - Read-only, dimmed    │
├─────────────────────────┤
│  Fixed Code (Bottom)    │
│  - Editable, highlighted│
├─────────────────────────┤
│  Action Buttons         │
└─────────────────────────┘
```

**Tablet (≥ 600dp):**

```
┌──────────────────────────────────────┐
│       Metrics Card (Top)             │
├──────────────┬───────────────────────┤
│              │                       │
│  Original    │    Fixed Code         │
│  Code (Left) │    (Right)            │
│  - Read-only │    - Editable         │
│              │                       │
├──────────────┴───────────────────────┤
│       Action Buttons                 │
└──────────────────────────────────────┘
```

#### Components

**Improvement Metrics Card:**

- Before/After score circles (80dp)
- Animated counting (1.5s)
- Improvement text ("+32 POINTS" in green)
- Metrics comparison:
    - Lines: 87 → 54
    - Complexity: 5 → 2
    - Issues Fixed: 8
- Fixed issues checklist (animated checkmarks)
- Staggered fade-in (100ms delay per item)

**Code Panels:**

- **Left (Original)**:
    - Read-only text display
    - Dimmed appearance (alpha 0.5)
    - Gray header
    - Monospace font
    - Line count in footer

- **Right (Fixed)**:
    - Editable BasicTextField
    - Bright colors
    - Cyan header ("EDITABLE" badge)
    - Monospace font
    - Line count in footer

**Features:**

- Both panels scrollable (vertical + horizontal)
- Glassmorphic design
- Color-coded headers
- Monospace code font (13sp, 20sp line height)

#### Action Buttons

**4 Buttons in glassmorphic bar:**

1. **📋 COPY CODE**
    - Copies fixed code to clipboard
    - Shows toast: "✓ Code copied to clipboard"
    - Cyan outline style
    - Haptic feedback

2. **↶ UNDO**
    - Reverts last edit
    - Only enabled if edits made
    - Yellow outline style
    - Haptic feedback

3. **✓ APPLY FIX**
    - Applies fixed code
    - Green filled button
    - Black text
    - Haptic feedback

4. **← BACK**
    - Returns to roasts screen
    - Red outline style
    - Haptic feedback

**Layout:**

- Top row: Copy + Undo
- Bottom row: Apply + Back
- 12dp spacing between buttons
- Equal widths (weight = 1f)

---

### 3. **Loading States**

#### FixGenerationLoadingScreen

**Design:**

```
┌──────────────────────────┐
│                          │
│    🔧 (rotating icon)    │
│                          │
│   GENERATING FIX         │
│                          │
│   [████████░░] 60%       │
│                          │
│ Generating improvements  │
│                          │
│ This may take 10-30s...  │
│                          │
└──────────────────────────┘
```

**Features:**

- Rotating loading icon (2s cycle)
- Color transition (Cyan ↔ Purple, 3s)
- Linear progress bar (8dp height)
- Percentage display (large)
- Rotating message with fade
- Glassmorphic card
- Centered layout

**Messages Cycle:**

1. "Analyzing code structure..."
2. "Generating improvements..."
3. "Polishing code..."
4. "Validating fixes..."

#### ManualSuggestionsScreen (Fallback)

**When AI fails:**

```
┌──────────────────────────┐
│          💡              │
│  MANUAL SUGGESTIONS      │
│                          │
│ Automatic fix unavailable│
│ Here's what you can do:  │
│                          │
│ 1. Rename variables...   │
│ 2. Break long functions  │
│ 3. Reduce nesting...     │
│ 4. Add error handling    │
│ 5. Add comments          │
│                          │
│  [← BACK TO ROASTS]      │
└──────────────────────────┘
```

**Features:**

- Large lightbulb emoji (64sp)
- Yellow title
- Numbered suggestions
- Staggered animation (100ms delay)
- Each item in glassmorphic card
- Yellow back button

**Generated Suggestions:**

- Based on issue types from roasts
- Generic best practices added
- Deduplicated
- 5-10 suggestions typically

---

## 🎬 Animation Details

### Metrics Card Animations

**Score Circles:**

```kotlin
// Animated counting: 0 → final
Duration: 1500ms
Easing: FastOutSlowInEasing
```

**Ring Fill:**

```kotlin
// Synchronized with counting
Progress: 0 → score/100
Duration: 1500ms
```

**Improvement Text:**

```kotlin
// Fade + Expand
Enter: fadeIn(500ms) + expandVertically(500ms)
```

**Checkmarks:**

```kotlin
// Staggered entry
Delay: index * 100ms
Enter: fadeIn(300ms) + slideInHorizontally(300ms)
```

### Loading Screen Animations

**Icon Rotation:**

```kotlin
// Continuous spin
0° → 360° in 2000ms
Linear easing
Infinite repeat
```

**Color Cycle:**

```kotlin
// Cyan ↔ Purple
3000ms per cycle
Reverse repeat
```

**Message Fade:**

```kotlin
// Cross-fade
FadeIn: 500ms
FadeOut: 500ms
```

---

## 💻 Usage Examples

### Generate Fix

```kotlin
val generator = CodeFixGenerator()
val scope = viewModelScope

scope.launch {
    generator.generateFix(
        originalCode = userCode,
        roasts = analysisRoasts,
        language = "Python",
        beforeScore = 45
    ).collect { state ->
        when (state) {
            is GenerationState.Loading -> {
                // Show loading screen
                showLoading(state.percentage, state.message)
            }
            is GenerationState.Success -> {
                // Navigate to comparison
                navigateToComparison(state.result)
            }
            is GenerationState.Error -> {
                // Show error or fallback
                showManualSuggestions()
            }
        }
    }
}
```

### Show Comparison Screen

```kotlin
FixComparisonScreen(
    fixResult = fixResult,
    onApplyFix = { fixedCode ->
        // Save or replace code
        saveFixedCode(fixedCode)
        navigateBack()
    },
    onBack = {
        navigateBack()
    }
)
```

### Show Loading

```kotlin
FixGenerationLoadingScreen(
    percentage = 60,
    message = "Generating improvements..."
)
```

### Show Manual Suggestions

```kotlin
val suggestions = generator.generateManualSuggestions(roasts)

ManualSuggestionsScreen(
    suggestions = suggestions,
    onBack = { navigateToRoasts() }
)
```

---

## 🎨 Visual Design

### Color Scheme

```kotlin
// Metrics
Before Score     → NeonRed (#ff0844)
After Score      → NeonGreen (#00ff88)
Improvement      → NeonGreen
Checkmarks       → NeonGreen

// Code Panels
Original Header  → MidGray (dimmed)
Fixed Header     → NeonCyan (#00f0ff)
Code Background  → DarkGray (#1a1a1a)
Dimmed Text      → TextTertiary (50% opacity)

// Buttons
Copy Button      → NeonCyan outline
Undo Button      → NeonYellow outline
Apply Button     → NeonGreen filled
Back Button      → NeonRed outline

// Loading
Icon Color       → Cyan ↔ Purple gradient
Progress Bar     → NeonCyan
Message          → TextSecondary
```

### Typography

```kotlin
// Metrics Card
Score Numbers    → TitleLarge, Bold (≈24sp)
Improvement      → TitleLarge, Black, +1.5sp spacing
Metric Labels    → BodyMedium
Metric Values    → BodyMedium, Bold
Checkmark Text   → BodyMedium

// Code Panels
Header Title     → LabelMedium, +1sp spacing
Code Text        → Monospace, 13sp, 20sp line height
Footer Text      → LabelSmall

// Buttons
Button Text      → LabelMedium, Bold

// Loading
Title            → HeadlineSmall, Black, +2sp spacing
Percentage       → TitleLarge, Bold
Message          → BodyLarge, +0.5sp spacing
```

### Spacing

```kotlin
// Metrics Card
Internal Padding → 20dp
Score Spacing    → 16dp
Metric Rows      → 4dp vertical padding
Checklist Spacing→ 4dp vertical

// Code Panels
Panel Padding    → 16dp
Header Padding   → 12dp
Code Padding     → 16dp
Footer Padding   → 12dp horizontal, 6dp vertical

// Buttons
Bar Padding      → 16dp
Button Spacing   → 12dp
Internal Padding → Varies by button

// Loading
Card Padding     → 32dp
Section Spacing  → 16-32dp
```

---

## 🔧 Technical Implementation

### Key Classes

**CodeFixGenerator**

```kotlin
class CodeFixGenerator {
    suspend fun generateFix(...): Flow<GenerationState>
    fun generateManualSuggestions(...): List<String>
    
    // Helper methods
    private fun buildFixPrompt(...)
    private fun extractCodeBlock(...)
    private fun isValidCode(...)
    private fun detectImprovements(...)
    private fun calculateMetrics(...)
    private fun estimateScore(...)
}
```

**Data Classes**

```kotlin
data class FixResult(
    originalCode: String,
    fixedCode: String,
    improvements: List<Improvement>,
    beforeScore: Int,
    afterScore: Int,
    metricsChange: MetricsChange
)

data class Improvement(
    type: String,
    description: String,
    lineNumber: Int?
)

data class MetricsChange(
    linesBefore: Int,
    linesAfter: Int,
    complexityBefore: Int,
    complexityAfter: Int,
    issuesFixed: Int
)
```

### RunAnywhere SDK Integration

**Streaming Generation:**

```kotlin
RunAnywhere.generateStream(fixPrompt).collect { token ->
    fixedCode += token
    tokenCount++
    
    // Update progress
    val progress = 20 + (tokenCount / 10).coerceAtMost(60)
    emit(GenerationState.Loading(progress, "Polishing code..."))
}
```

**Timeout Handling:**

- Typical: 10-30 seconds
- Max tokens: Variable (depends on code length)
- Error handling: Fallback to manual suggestions

---

## 📱 Responsive Behavior

### Phone Layout

- **Vertical split** (stacked)
- Original code on top
- Fixed code on bottom
- Full-width panels
- 16dp padding

### Tablet Layout

- **Horizontal split** (side-by-side)
- Original code on left
- Fixed code on right
- Equal weights (1f each)
- 16dp spacing between

### Detection

```kotlin
val configuration = LocalConfiguration.current
val isTablet = configuration.screenWidthDp >= 600
```

---

## ♿ Accessibility

### Implemented

- ✅ Haptic feedback on all buttons
- ✅ Copy to clipboard with toast
- ✅ Editable text fields
- ✅ Clear visual hierarchy
- ✅ High contrast text
- ✅ Large touch targets (≥48dp)

### To Add (Future)

- [ ] Content descriptions for screen readers
- [ ] Keyboard navigation
- [ ] Focus indicators
- [ ] Zoom support for code
- [ ] Voice announcements

---

## 🎯 Edge Cases Handled

### Invalid AI Output

- Extract code from markdown blocks
- Validate syntax before showing
- Fallback to manual suggestions if invalid

### Empty Code

- Check for blank results
- Minimum length validation
- Retry option

### Very Long Code

- Streaming handles large outputs
- Progress updates every 50 tokens
- Scrollable code panels

### Edit Conflicts

- Undo tracks previous state
- Can undo once (simple history)
- Apply uses latest edited version

### Network Issues

- Error state with retry
- Manual suggestions fallback
- Clear error messages

---

## 🚀 Performance Optimization

### Implemented

- ✅ `LaunchedEffect` for side effects
- ✅ `remember` for expensive calculations
- ✅ Streaming for large outputs
- ✅ Lazy evaluation
- ✅ Proper coroutine scopes

### Tips

- Debounce rapid edits
- Cache validation results
- Optimize regex operations
- Limit improvement list to 5-10 items

---

## 🐛 Known Limitations

### Current

1. **Single undo** - No multi-level undo stack
2. **No diff highlighting** - Just side-by-side comparison
3. **Basic validation** - Not a full parser
4. **AI dependent** - Quality varies by model
5. **No sync scroll** - Panels scroll independently

### Future Enhancements

1. **Undo/Redo stack** - Multi-level history
2. **Line-by-line diff** - Green/red/yellow highlights
3. **Syntax highlighting** - Color-coded keywords
4. **Scroll sync** - Synchronized scrolling
5. **Search in code** - Find text in panels
6. **Export options** - Save to file, share as image

---

## 📊 Success Metrics

**What Success Looks Like:**

- ✅ 80%+ of fixes are syntactically valid
- ✅ Score improvement: +10 to +50 points average
- ✅ Lines reduced: 10-30% typically
- ✅ Nesting reduced: 1-2 levels
- ✅ User applies fix: 60%+ acceptance rate

**Measured By:**

- Validation pass rate
- Before/after score delta
- Metrics change values
- Apply button click rate

---

## 🎓 Best Practices

### For Users

1. Review fixed code before applying
2. Edit if needed (it's editable!)
3. Test fixed code thoroughly
4. Keep undo option in mind

### For Developers

1. Always validate AI output
2. Provide fallback (manual suggestions)
3. Show clear progress indicators
4. Enable undo functionality
5. Make code editable
6. Give haptic feedback
7. Add clipboard support

---

## 🔗 Integration Points

### With RoastResultsScreen

```kotlin
// "SHOW FIXES" button
Button(onClick = {
    navigateToFixGeneration(
        code = originalCode,
        roasts = roasts,
        language = language,
        score = currentScore
    )
})
```

### With Code Analyzer

```kotlin
// Uses same roasts from analysis
val roasts = analyzer.analyzeCode(...)
generator.generateFix(code, roasts, ...)
```

### With Main Input

```kotlin
// Can replace original code
onApplyFix = { fixedCode ->
    updateOriginalCode(fixedCode)
    showSuccessToast()
}
```

---

## ✅ Feature Checklist

**Core Functionality:**

- [x] AI-powered fix generation
- [x] Progress tracking (0-100%)
- [x] Rotating status messages
- [x] Code validation
- [x] Improvement detection
- [x] Metrics calculation
- [x] Score estimation

**UI Components:**

- [x] Split-screen comparison
- [x] Improvement metrics card
- [x] Editable code panel
- [x] Action buttons with haptics
- [x] Loading screen
- [x] Manual suggestions fallback
- [x] Copy to clipboard
- [x] Undo functionality

**Animations:**

- [x] Score counting
- [x] Progress bar
- [x] Checkmark stagger
- [x] Message fade
- [x] Icon rotation
- [x] Color transitions

**Polish:**

- [x] Responsive layout (phone/tablet)
- [x] Cyberpunk aesthetic
- [x] Haptic feedback
- [x] Toast confirmations
- [x] Error handling
- [x] Preview composables

---

**The Code Fix Feature is now complete with AI-powered refactoring, beautiful UI, and a polished
user experience!** 🎉

Every aspect has been carefully implemented, from the intelligent fix generation to the smooth
animations and responsive design. Users can now automatically improve their code with confidence!
