# Quick UI Reference - CodeRoast.ai

## 🎨 Using the New UI Components

### Import the Theme

```kotlin
import com.example.coderoastai.ui.theme.*
```

### Color Palette Quick Reference

```kotlin
// Backgrounds
DeepBlack        // #0a0a0a - Main background
DarkGray         // #1a1a1a - Surface
MidGray          // #2a2a2a - Surface variant

// Neon Accents
NeonRed          // #ff0844 - Primary actions
NeonCyan         // #00f0ff - Secondary actions
NeonGreen        // #00ff88 - Success
NeonOrange       // #ff6600 - Warnings

// Glass Effects
GlassWhite10     // 10% white - Subtle glass
GlassWhite20     // 20% white - Glass borders

// Text
TextPrimary      // White 100%
TextSecondary    // White 70%
TextTertiary     // White 50%
```

---

## 🎭 Component Usage

### Glassmorphic Card

```kotlin
GlassmorphicCard(
    cornerRadius = 24.dp,
    borderColor = GlassWhite20,
    backgroundColor = GlassWhite10
) {
    // Your content here
    Text("Hello World")
}
```

### Elevated Glass Card

```kotlin
ElevatedGlassCard(
    cornerRadius = 20.dp
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Card with shadow")
    }
}
```

### Neon Text (with glow)

```kotlin
NeonText(
    text = "CODEROAST",
    color = NeonRed,
    animated = true  // Pulsing glow
)
```

### Typewriter Effect

```kotlin
TypewriterText(
    text = "Your code is amazing! Just kidding.",
    delayMs = 50L  // Speed per character
)
```

### Empty State

```kotlin
EmptyState(
    emoji = "📝",
    title = "No code yet",
    message = "Paste your code to get started",
    actionText = "Load Example",
    onAction = { /* Do something */ }
)
```

### Error State

```kotlin
ErrorState(
    emoji = "💥",
    title = "Oops!",
    message = "Something went wrong",
    actionText = "Try Again",
    onAction = { /* Retry */ }
)
```

---

## ✨ Animation Examples

### Button with Scale Animation

```kotlin
var isPressed by remember { mutableStateOf(false) }

val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.95f else 1f,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy
    )
)

Button(
    onClick = { isPressed = true },
    modifier = Modifier.scale(scale)
) {
    Text("Press Me")
}
```

### Fade In with Delay

```kotlin
var visible by remember { mutableStateOf(false) }

LaunchedEffect(Unit) {
    delay(300)
    visible = true
}

AnimatedVisibility(
    visible = visible,
    enter = fadeIn(tween(500)) + slideInVertically(tween(500))
) {
    // Your content
}
```

### Pulsing Glow Effect

```kotlin
val infiniteTransition = rememberInfiniteTransition()

val glowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 0.7f,
    animationSpec = infiniteRepeatable(
        animation = tween(1500),
        repeatMode = RepeatMode.Reverse
    )
)

Box(
    modifier = Modifier
        .border(
            2.dp,
            NeonRed.copy(alpha = glowAlpha),
            RoundedCornerShape(30.dp)
        )
)
```

---

## 🎨 Common Gradients

### Horizontal Gradient Button

```kotlin
Box(
    modifier = Modifier
        .background(
            Brush.horizontalGradient(
                colors = listOf(NeonRed, NeonRedDark)
            )
        )
)
```

### Vertical Gradient Card

```kotlin
Box(
    modifier = Modifier
        .background(
            Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.05f),
                    Color.Transparent
                )
            )
        )
)
```

### Radial Gradient Glow

```kotlin
Box(
    modifier = Modifier
        .background(
            Brush.radialGradient(
                colors = listOf(
                    NeonRed.copy(alpha = 0.1f),
                    Color.Transparent
                )
            )
        )
)
```

---

## 📏 Spacing Guide

```kotlin
// Use consistent spacing
Modifier.padding(
    horizontal = 20.dp,  // Screen edges
    vertical = 24.dp     // Between sections
)

// Card spacing
Arrangement.spacedBy(12.dp)  // Between cards

// Button height
Modifier.height(60.dp)  // Primary buttons

// Touch targets
Modifier.size(48.dp)    // Minimum for FABs
```

---

## 🎯 Typography Quick Reference

```kotlin
// Hero text
MaterialTheme.typography.displayLarge  // 72sp, Bold

// Section headers
MaterialTheme.typography.headlineLarge  // 32sp, Bold, +1.5sp spacing

// Card titles
MaterialTheme.typography.titleMedium   // 18sp, SemiBold

// Body text
MaterialTheme.typography.bodyLarge     // 16sp, Regular

// Button labels
MaterialTheme.typography.labelLarge    // 14sp, Bold, +1.25sp spacing

// Small tags
MaterialTheme.typography.labelSmall    // 11sp, Medium
```

---

## 🎬 Pre-built Screens

### Show Loading Screen

```kotlin
LoadingScreen()
// Automatically shows rotating funny messages
```

### Show Results

```kotlin
val result = RoastResult(
    score = 65,
    grade = "C",
    verdict = "Your code works, but...",
    roasts = listOf(
        RoastItem(
            severity = "critical",
            title = "Variable Naming",
            description = "What is 'x'?",
            lineNumber = 42,
            flames = 5
        )
    )
)

ResultsScreen(
    result = result,
    onRoastAgain = { /* Go back */ },
    onShare = { /* Share results */ },
    onFixCode = { /* Future feature */ }
)
```

---

## 🔥 Quick Snippets

### Selection Chip

```kotlin
Surface(
    onClick = { /* Select */ },
    shape = RoundedCornerShape(12.dp),
    color = if (selected) NeonRed.copy(alpha = 0.2f) else GlassWhite10,
    border = if (selected) 
        BorderStroke(2.dp, NeonRed) 
    else 
        BorderStroke(1.dp, GlassWhite20)
) {
    Text("Option", modifier = Modifier.padding(12.dp))
}
```

### Floating Action Button

```kotlin
FloatingActionButton(
    onClick = { /* Action */ },
    modifier = Modifier.size(48.dp),
    shape = CircleShape,
    containerColor = GlassWhite10
) {
    Text("⚙️", fontSize = 20.sp)
}
```

### Severity Badge

```kotlin
Surface(
    shape = RoundedCornerShape(8.dp),
    color = NeonRed.copy(alpha = 0.2f),
    border = BorderStroke(1.dp, NeonRed)
) {
    Text(
        "CRITICAL",
        style = MaterialTheme.typography.labelSmall,
        color = NeonRed,
        modifier = Modifier.padding(
            horizontal = 8.dp, 
            vertical = 4.dp
        )
    )
}
```

---

## 🎨 Color-Coded Severity

```kotlin
val severityColor = when (severity) {
    "critical" -> NeonRed
    "warning" -> NeonOrange
    "info" -> NeonCyan
    else -> NeonPurple
}
```

---

## ⚡ Performance Tips

1. **Use `remember` for expensive operations**

```kotlin
val expensiveValue = remember(key) { 
    computeExpensiveValue() 
}
```

2. **Derive state efficiently**

```kotlin
val derivedValue = remember { 
    derivedStateOf { compute(state) } 
}.value
```

3. **Lazy load lists**

```kotlin
LazyColumn {
    items(list) { item ->
        // Item composable
    }
}
```

4. **Avoid unnecessary recomposition**

```kotlin
@Composable
fun MyComponent(
    data: Data,  // Stable
    onClick: () -> Unit  // Stable lambda
)
```

---

## 🎯 Common Patterns

### State Hoisting

```kotlin
@Composable
fun ParentScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    
    ChildComponent(
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it }
    )
}
```

### Side Effects

```kotlin
LaunchedEffect(key) {
    // Runs when key changes
    doSomething()
}

DisposableEffect(key) {
    // Setup
    onDispose {
        // Cleanup
    }
}
```

---

## 📱 Responsive Layouts

```kotlin
val configuration = LocalConfiguration.current
val isTablet = configuration.screenWidthDp >= 600

if (isTablet) {
    // Tablet layout
    Row { /* Two columns */ }
} else {
    // Phone layout
    Column { /* Single column */ }
}
```

---

## 🎨 Custom Modifiers

### Add neon glow

```kotlin
fun Modifier.neonGlow(
    color: Color,
    radius: Dp = 12.dp
) = this.drawBehind {
    // Add glow effect
}
```

### Glassmorphic effect

```kotlin
fun Modifier.glassmorphic() = this
    .background(GlassWhite10)
    .border(1.dp, GlassWhite20, RoundedCornerShape(24.dp))
    .background(
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.05f),
                Color.Transparent
            )
        )
    )
```

---

## 🚀 Getting Started Checklist

- [x] Import theme colors
- [x] Use Typography scale
- [x] Apply glassmorphic cards
- [x] Add animations to interactions
- [x] Use consistent spacing
- [x] Test on different screen sizes
- [ ] Add haptic feedback
- [ ] Implement gestures
- [ ] Add custom fonts

---

## 💡 Pro Tips

1. **Always use theme colors** - Don't hardcode
2. **Consistent animations** - Use same timing/easing
3. **Spacing system** - Use 4dp increments
4. **Accessibility** - Min 48dp touch targets
5. **Performance** - Profile animations
6. **Feedback** - Visual response to every action

---

## 🎯 Common Issues & Solutions

**Issue:** Animation not smooth

```kotlin
// ✅ Use animate*AsState
val scale by animateFloatAsState(targetValue)

// ❌ Don't manually update
var scale by remember { mutableStateOf(1f) }
```

**Issue:** Too many recompositions

```kotlin
// ✅ Stable parameters
onClick: () -> Unit

// ❌ Unstable lambdas
onClick: (data: Data) -> Unit
```

**Issue:** Colors don't match

```kotlin
// ✅ Use theme colors
color = MaterialTheme.colorScheme.primary

// ❌ Hardcode colors
color = Color(0xFFff0000)
```

---

**Ready to build amazing UIs!** 🚀

For detailed information, see:

- `UI_DESIGN_GUIDE.md` - Complete design system
- `UI_REDESIGN_SUMMARY.md` - Full feature list
