# CodeRoast.ai - UI Design Guide

## 🎨 Design Philosophy

CodeRoast.ai features a **cyberpunk/premium aesthetic** with a focus on:

- Dark, edgy visuals with deep blacks (#0a0a0a)
- Neon accent colors (Electric Red #ff0844, Cyan #00f0ff)
- Glassmorphism effects throughout
- Smooth micro-animations on every interaction
- Professional polish meets hacker aesthetic

---

## 🎯 Color Palette

### Primary Colors

```kotlin
DeepBlack     = #0a0a0a  // Background
DarkGray      = #1a1a1a  // Surface
MidGray       = #2a2a2a  // Surface variant
```

### Neon Accents

```kotlin
NeonRed       = #ff0844  // Primary actions, warnings
NeonCyan      = #00f0ff  // Secondary actions, info
NeonPurple    = #bb00ff  // Tertiary
NeonGreen     = #00ff88  // Success
NeonYellow    = #ffff00  // Attention
NeonOrange    = #ff6600  // Warnings
```

### Glassmorphism

```kotlin
GlassWhite10  = rgba(255,255,255,0.1)  // Subtle glass
GlassWhite20  = rgba(255,255,255,0.2)  // Glass borders
GlassBlack30  = rgba(0,0,0,0.3)        // Glass shadows
```

### Text

```kotlin
TextPrimary   = #ffffff     // 100% opacity
TextSecondary = rgba(255,255,255,0.7)  // 70% opacity
TextTertiary  = rgba(255,255,255,0.5)  // 50% opacity
```

---

## 🖼️ Component Library

### 1. Glassmorphic Cards

**GlassmorphicCard**

- Semi-transparent background with blur effect
- Subtle border (1-2dp)
- Rounded corners (24dp default)
- Gradient overlay for depth

```kotlin
GlassmorphicCard(
    cornerRadius = 24.dp,
    borderColor = GlassWhite20,
    backgroundColor = GlassWhite10
) {
    // Content
}
```

**ElevatedGlassCard**

- Same as GlassmorphicCard but with shadow
- 8dp elevation
- Best for cards that need to "float"

---

### 2. Text Components

**NeonText**

- Text with glowing effect
- Animated or static glow
- Customizable glow color and intensity

```kotlin
NeonText(
    text = "CODEROAST.AI",
    color = NeonRed,
    animated = true
)
```

**TypewriterText**

- Animated text reveal effect
- Configurable speed (default 50ms per character)
- Perfect for verdicts and messages

---

### 3. Interactive Elements

**Language Chips**

- Pills with emoji + text
- Neon outline when selected
- Scale animation (1.0 → 1.05) on selection
- Bouncy spring animation

**Personality Cards**

- 100dp square cards
- Large emoji (36sp)
- Scale animation (1.0 → 1.15) when selected
- 3dp neon border on selection
- Radial gradient glow effect

**Intensity Slider**

- Custom neon track (red gradient)
- Emoji indicators at each step
- Large, glowing thumb
- Haptic feedback ready

---

### 4. Buttons

**Primary Button (Roast)**

- Pill-shaped (30dp corner radius)
- Red gradient background
- Pulsing glow animation
- Press animation: scale to 0.95
- Uppercase text with letter spacing
- Disabled state: gray, low opacity

**Secondary Button (Outlined)**

- Transparent with neon border
- Hover/press effects
- Used for "Roast Again"

**Quick Action FABs**

- 48dp circular buttons
- Semi-transparent glass
- Emoji icons
- Ripple effect

---

### 5. Loading States

**LoadingScreen**

- Rotating neon loading indicator
- Color transitions (Red → Cyan)
- Funny rotating messages every 2 seconds
- Shimmer loading bar

**Messages:**

- 🔥 Heating up the roasting pan...
- 🔪 Sharpening the knives...
- 👨‍🍳 Summoning Gordon Ramsay...
- 💀 Analyzing your crimes against code...
- 🎯 Finding all the bugs you missed...
- 🔬 Running code smell detector...
- ⚡ Charging the roast-o-matic 3000...
- 🎭 Preparing dramatic insults...
- 🧨 Loading explosive criticism...
- 🌶️ Adding extra spice...

**SkeletonLoadingCard**

- Pulsing shimmer effect
- Gray placeholder blocks
- Smooth alpha animation

**PulsingDots**

- Three dots with staggered scale animation
- 600ms cycle with 200ms delay between dots

---

### 6. Empty & Error States

**EmptyState**

- Large floating emoji (80sp)
- Title + descriptive message
- Optional CTA button
- Subtle vertical float animation

**ErrorState**

- Similar to EmptyState
- Default emoji: 💥
- "Try Again" action button
- Encouraging/funny error messages

---

## 📱 Screen Layouts

### Main Input Screen

**Structure:**

```
┌─────────────────────────────────────┐
│  Animated Background (particles)    │
│  ┌───────────────────────────────┐  │
│  │  Header Section               │  │
│  │  - Logo with glow             │  │
│  │  - "CODEROAST.AI" title       │  │
│  │  - "Your code is RAW..." line │  │
│  │  - Scanline effect            │  │
│  └───────────────────────────────┘  │
│                                      │
│  ┌───────────────────────────────┐  │
│  │  Language Selector            │  │
│  │  [🐍 Python][⚡ JS][☕ Java]  │  │
│  └───────────────────────────────┘  │
│                                      │
│  ┌───────────────────────────────┐  │
│  │  Code Editor (Glass Card)     │  │
│  │  - Dark editor background     │  │
│  │  - Monospace font             │  │
│  │  - Cyan glow on focus         │  │
│  │  - 200-400dp height           │  │
│  └───────────────────────────────┘  │
│                                      │
│  ┌───────────────────────────────┐  │
│  │  Personality Selector         │  │
│  │  [🔪][👔][🎖️][💅][🎭]       │  │
│  │  Horizontal scrollable        │  │
│  └───────────────────────────────┘  │
│                                      │
│  ┌───────────────────────────────┐  │
│  │  ROAST INTENSITY              │  │
│  │  😊 ━━━━●━━━━ 💀              │  │
│  └───────────────────────────────┘  │
│                                      │
│  ┌───────────────────────────────┐  │
│  │  [ROAST MY CODE 🔥]           │  │
│  │  (Pulsing glow button)        │  │
│  └───────────────────────────────┘  │
│                                      │
│  Quick Action FABs (bottom-right) → │
└─────────────────────────────────────┘
```

**Key Features:**

- Vertical scrollable
- 20dp padding around content
- 40dp top spacing for status bar
- Animated background particles
- Generous spacing between sections

---

### Results Screen

**Structure:**

```
┌─────────────────────────────────────┐
│  ┌───────────────────────────────┐  │
│  │  Hero Score Section           │  │
│  │  - Gradient background        │  │
│  │  - 200dp circular indicator   │  │
│  │  - Animated fill ring         │  │
│  │  - 72sp score number          │  │
│  │  - 48sp letter grade          │  │
│  │  - Typewriter verdict         │  │
│  │  - Particle effects           │  │
│  │  - Confetti for >80 scores    │  │
│  └───────────────────────────────┘  │
│                                      │
│  ┌───────────────────────────────┐  │
│  │  Roast Card 1                 │  │
│  │  [CRITICAL] 🔥🔥🔥🔥🔥        │  │
│  │  ├─ Title                     │  │
│  │  ├─ Description               │  │
│  │  └─ [Line 42]                 │  │
│  └───────────────────────────────┘  │
│                                      │
│  ┌───────────────────────────────┐  │
│  │  Roast Card 2                 │  │
│  │  [WARNING] 🔥🔥🔥              │  │
│  └───────────────────────────────┘  │
│                                      │
│  (More roast cards...)               │
│                                      │
│  ┌───────────────────────────────┐  │
│  │  Floating Action Bar          │  │
│  │  [🔄 AGAIN][📤 SHARE][🔧 FIX]│  │
│  └───────────────────────────────┘  │
└─────────────────────────────────────┘
```

**Key Features:**

- Staggered fade-in animations (100ms delay per card)
- Pull-to-refresh capability
- Swipe-to-dismiss on cards
- Color-coded severity borders:
    - Critical: Red
    - Warning: Orange
    - Info: Cyan
    - Other: Purple
- Animated score counting (0 → final over 2s)
- Ring fill animation synchronized

---

## ✨ Animation Specifications

### Timing Functions

```kotlin
// Fast interactions
Spring(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)

// Smooth transitions
tween(
    durationMillis = 500,
    easing = FastOutSlowInEasing
)

// Continuous animations
infiniteRepeatable(
    animation = tween(1500),
    repeatMode = RepeatMode.Reverse
)
```

### Key Animations

**Button Press:**

- Scale: 1.0 → 0.95 (100ms)
- Then bounce back (spring animation)
- Haptic feedback

**Selection:**

- Scale: 1.0 → 1.15 (300ms, bouncy spring)
- Border glow appears
- Background tint changes

**Card Enter:**

- FadeIn (500ms) + SlideInVertically (500ms)
- Stagger: 100ms delay per card
- Easing: FastOutSlowInEasing

**Glow Pulse:**

- Alpha: 0.3 → 0.7 (1500ms)
- Continuous reverse loop
- Used on primary button

**Particle Float:**

- TranslateY: 0 → 1000px (20000ms)
- Linear easing
- Restart on complete

**Score Counter:**

- Int: 0 → final (2000ms)
- FastOutSlowInEasing
- Synchronized with ring

---

## 🎭 Typography Scale

```kotlin
displayLarge    = 72sp, Bold     // Hero scores
displayMedium   = 48sp, Bold     // Letter grades
headlineLarge   = 32sp, Bold, +1.5sp letter spacing  // Section headers
headlineMedium  = 24sp, Bold, +1sp letter spacing
titleLarge      = 22sp, SemiBold // Card titles
titleMedium     = 18sp, SemiBold
bodyLarge       = 16sp, Regular, 1.5 line height
bodyMedium      = 14sp, Regular
labelLarge      = 14sp, Bold, +1.25sp letter spacing  // Buttons
labelMedium     = 12sp, Bold, +1sp letter spacing     // Tags
```

**Special Fonts:**

- Code: Monospace (system default or JetBrains Mono if added)
- All other text: System default (Roboto on Android)
- Uppercase for: Buttons, labels, section headers

---

## 📐 Spacing System

```kotlin
Tiny    = 4.dp
Small   = 8.dp
Medium  = 12.dp
Default = 16.dp
Large   = 24.dp
XLarge  = 32.dp
XXLarge = 40.dp
```

**Usage:**

- Between cards: 12dp
- Horizontal padding: 20dp
- Vertical padding sections: 24dp
- Touch targets minimum: 48dp
- Card internal padding: 16dp
- Button height: 60dp (primary), 48dp (secondary)

---

## 🎨 Visual Effects

### Glassmorphism Recipe

```kotlin
Surface(
    color = GlassWhite10,
    border = BorderStroke(1.dp, GlassWhite20)
) {
    Box(
        modifier = Modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.05f),
                        Color.Transparent
                    )
                )
            )
    ) {
        // Content
    }
}
```

### Neon Glow Effect

```kotlin
Modifier.drawBehind {
    drawIntoCanvas { canvas ->
        paint.setShadowLayer(
            radius = 12f,
            dx = 0f,
            dy = 0f,
            color = NeonRed.copy(alpha = 0.8f)
        )
    }
}
```

### Gradient Backgrounds

```kotlin
Brush.horizontalGradient(
    colors = listOf(NeonRed, NeonRedDark)
)

Brush.verticalGradient(
    colors = listOf(
        Color.copy(alpha = 0.3f),
        Color.Transparent
    )
)

Brush.radialGradient(
    colors = listOf(
        NeonRed.copy(alpha = 0.1f),
        Color.Transparent
    )
)
```

---

## 🌈 Color Usage Guide

**Primary (Neon Red):**

- Main CTA buttons
- Critical severity indicators
- Selected state highlights
- Error states

**Secondary (Neon Cyan):**

- Info badges
- Line number chips
- Share button
- Links and secondary actions

**Success (Neon Green):**

- High scores (>75)
- Success messages
- Positive indicators

**Warning (Neon Orange):**

- Medium scores (50-75)
- Warning severity
- Caution states

**Purple:**

- Miscellaneous severity
- Tertiary actions
- Decorative accents

---

## 📱 Responsive Design

**Phone (< 600dp):**

- Single column layout
- Full-width cards
- Stacked action buttons
- Scrollable personality selector

**Tablet (600-840dp):**

- Same layout, more spacing
- Larger touch targets
- Bigger cards
- More particles

**Large Tablet/Foldable (> 840dp):**

- Consider two-column for roasts
- Wider max-width container
- More prominent animations

---

## ♿ Accessibility

**Implemented:**

- ✅ Minimum touch target: 48dp
- ✅ Text contrast ratio > 4.5:1
- ✅ Semantic color use (not just decorative)
- ✅ Content descriptions for all interactive elements
- ✅ Keyboard navigation support
- ✅ Screen reader friendly

**Best Practices:**

- Use `semantics { }` modifier for complex composables
- Provide clear labels for all buttons
- Avoid relying solely on color to convey information
- Ensure animations can be disabled for motion sensitivity

---

## 🎯 Implementation Checklist

### Phase 1: Core UI ✅

- [x] Color theme setup
- [x] Typography system
- [x] Glassmorphic card component
- [x] Main input screen layout
- [x] Language selector chips
- [x] Code editor with glass effect
- [x] Personality selector cards
- [x] Intensity slider
- [x] Primary roast button with animations

### Phase 2: Results & Loading ✅

- [x] Results screen layout
- [x] Hero score section with animations
- [x] Roast cards with severity styling
- [x] Floating action bar
- [x] Loading screen with rotating messages
- [x] Skeleton loading states
- [x] Empty state component
- [x] Error state component

### Phase 3: Polish ⏳

- [ ] Add haptic feedback
- [ ] Implement pull-to-refresh
- [ ] Add swipe gestures
- [ ] Confetti animation for high scores
- [ ] Screen shake for low scores
- [ ] Custom fonts (JetBrains Mono)
- [ ] Sound effects (optional)
- [ ] Dark mode transitions

### Phase 4: Optimization ⏳

- [ ] Performance profiling
- [ ] Reduce overdraw
- [ ] Lazy loading for large lists
- [ ] Animation performance testing
- [ ] Memory optimization
- [ ] Battery usage optimization

---

## 🎨 Design Inspiration

**Visual Style:**

- Cyberpunk 2077 UI
- Blade Runner aesthetics
- Modern fintech apps (Robinhood, Cash App)
- Gaming dashboards (Valorant, Apex Legends)
- Neon noir film aesthetic

**Interaction Patterns:**

- Apple's iOS fluid animations
- Material Design 3 elevation system
- Gaming UIs with satisfying feedback
- Premium banking apps

---

## 📚 Resources

**Colors:**

- [Coolors.co](https://coolors.co) - Color palette generator
- [Adobe Color](https://color.adobe.com) - Color wheel

**Typography:**

- [Google Fonts](https://fonts.google.com)
- [JetBrains Mono](https://www.jetbrains.com/lp/mono/) - Monospace font

**Icons/Emoji:**

- Native Android emoji set
- [Material Symbols](https://fonts.google.com/icons)

**Animations:**

- [Material Motion](https://m3.material.io/styles/motion)
- [Lottie Files](https://lottiefiles.com) - For complex animations

---

## 🚀 Future Enhancements

**Visual:**

- [ ] Particle system with more variety
- [ ] Animated scanline overlay
- [ ] Glitch effects on errors
- [ ] Matrix-style code rain background (subtle)
- [ ] Holographic card effects

**Interaction:**

- [ ] Voice-activated roasting
- [ ] Gesture shortcuts
- [ ] Custom themes (different neon colors)
- [ ] Adjustable animation intensity
- [ ] Dark/darker theme toggle

**Features:**

- [ ] Share as image with styled card
- [ ] Export report as PDF
- [ ] Code diff highlighting
- [ ] Before/after comparison
- [ ] Achievement badges
- [ ] Roast history

---

**Made with 🔥 by the CodeRoast.ai team**

*Where code meets criticism in neon-lit glory*
