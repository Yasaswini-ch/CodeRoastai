# 🔥 CodeRoast.AI

**Brutally honest AI code reviews with personality!**

An Android application that analyzes your code and roasts it with hilarious, personality-driven
feedback using on-device AI powered by RunAnywhere SDK.

---





---

## ✨ Features

### 🎯 Core Features

- **AI-Powered Code Analysis** - Analyzes code quality, naming, nesting, performance issues
- **5 Unique Personalities** - Gordon Ramsay, Drill Sergeant, Disappointed Dad, Gen Z, Shakespeare
- **5 Intensity Levels** - From gentle suggestions to nuclear roasts
- **Multi-Language Support** - Python, JavaScript, Java, Kotlin, C++
- **On-Device Processing** - Fast, private, no cloud required (powered by RunAnywhere SDK)

### 🔧 Advanced Features

- **Code Fix Generator** - AI generates improved, refactored code
- **Side-by-Side Comparison** - See original vs fixed code with diff highlighting
- **Roast History** - Saves last 50 roasts with search and filtering
- **Pre-loaded Examples** - 10+ terrible code samples to try
- **Social Sharing** - Generate beautiful share cards with custom templates
- **Export History** - Save roast history as JSON

### 🎨 UI/UX Excellence

- **Modern Material 3 Design** - Sleek dark theme with neon accents
- **Smooth Animations** - Spring animations, fade transitions, progress indicators
- **Responsive Layout** - Optimized for phones and tablets
- **Accessibility** - WCAG AA color contrast, 48dp+ touch targets
- **Bottom Navigation** - Easy navigation between Home, Examples, History, Settings

---

## 🚀 Tech Stack

### Frontend

- **Jetpack Compose** - Modern declarative UI toolkit
- **Material 3** - Latest Material Design components
- **Navigation Compose** - Type-safe navigation
- **Coroutines & Flow** - Asynchronous programming
- **Kotlin** - 100% Kotlin codebase

### Backend/AI

- **RunAnywhere SDK** - On-device AI inference
- **SmolLM2** - Lightweight language model (135M params)
- **Room Database** - Local data persistence
- **DataStore** - Settings and preferences

### Additional Libraries

- **Coil** - Image loading (for share feature)
- **Gson** - JSON serialization
- **AndroidX Security** - Encrypted data storage

---

## 🏗️ Architecture

```
app/
├── data/                        # Data layer
│   ├── RoastHistoryEntity.kt   # Room database entities
│   ├── RoastHistoryDao.kt      # Database access
│   └── RoastHistoryRepository.kt # Repository pattern
│
├── navigation/                  # Navigation setup
│   └── Screen.kt               # Navigation routes
│
├── ui/
│   ├── screens/                # All screen composables
│   │   ├── HomeScreen.kt       # Main input screen
│   │   ├── ResultsScreen.kt    # Roast results display
│   │   ├── ExamplesScreen.kt   # Pre-loaded examples
│   │   ├── HistoryScreen.kt    # Roast history
│   │   ├── SettingsScreen.kt   # App settings
│   │   ├── FixComparisonScreen.kt # Code fix comparison
│   │   └── ShareScreen.kt      # Social sharing
│   │
│   ├── components/             # Reusable UI components
│   └── theme/                  # App theming
│
├── CodeAnalyzer.kt             # Code analysis logic
├── RoastGenerator.kt           # Roast generation with AI
├── CodeFixGenerator.kt         # Code improvement AI
├── ShareGenerator.kt           # Social share image generation
└── MainActivity.kt             # Entry point

```

---

## 📦 Setup Instructions

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or later
- Android SDK 24+ (Minimum API 24, Target API 36)
- 4GB RAM minimum for emulator

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/Yasaswini-ch/CodeRoastai.git
   cd CodeRoastai
   ```

2. **Open in Android Studio**
    - Launch Android Studio
    - Select "Open an Existing Project"
    - Navigate to the cloned directory

3. **Add RunAnywhere SDK**

   Place the following AAR files in `app/libs/`:
    - `RunAnywhereKotlinSDK-release.aar` (4.0MB)
    - `runanywhere-llm-llamacpp-release.aar` (2.1MB)

   Download from: [RunAnywhere SDK](https://github.com/Nerds-Room/RunAnywhere-sdk)

4. **Configure API Key (Optional)**

   If using cloud features, set environment variable:
   ```bash
   export RUNANYWHERE_API_KEY="your_key_here"
   ```

   Or in `local.properties`:
   ```
   RUNANYWHERE_API_KEY=your_key_here
   ```

5. **Sync Gradle**
   ```bash
   ./gradlew sync
   ```

6. **Run the App**
    - Connect Android device or start emulator
    - Click Run (▶️) in Android Studio
    - Or use terminal:
      ```bash
      ./gradlew installDebug
      ```

### Building APK

```bash
# Debug APK
./gradlew assembleDebug

# Release APK (requires keystore)
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/`

---

## 🎮 Usage Guide

### 1. **Enter Your Code**

- Paste or type code in the editor
- Supports multiple languages
- Or load an example from the Examples tab

### 2. **Choose Settings**

- Select language (Python, JS, Java, etc.)
- Pick personality (Ramsay, Sarge, Dad, GenZ, Shakespeare)
- Adjust roast intensity (1-5)

### 3. **Get Roasted!**

- Tap "ROAST MY CODE 🔥"
- Wait 5-15 seconds for AI analysis
- View your score (0-100) and roasts

### 4. **Fix Your Code (Optional)**

- Tap "FIX CODE" button
- View side-by-side comparison
- Apply fixes or copy improved code

### 5. **Share Your Roast**

- Tap "Share" button
- Choose template (Dark, Light, Minimal, Dramatic)
- Customize and share on social media

---

## 🧪 Testing

### Run Automated Tests

The app includes a comprehensive testing screen:

1. Open app in debug mode
2. Navigate to Settings → Testing (hidden in production)
3. Tap "RUN ALL TESTS"
4. View detailed test results

### Manual Testing Checklist

See [TESTING_CHECKLIST.md](TESTING_CHECKLIST.md) for comprehensive testing guide.

### Key Tests

- ✅ SDK initialization
- ✅ All 5 languages
- ✅ All 5 personalities
- ✅ 5 intensity levels
- ✅ Empty code handling
- ✅ Long code (500+ lines)
- ✅ Special characters
- ✅ UI responsiveness
- ✅ Memory usage (<200MB)
- ✅ Smooth 60fps animations

---

## 🏆 RunAnywhere SDK Integration

### Why On-Device AI?

- **🔒 Privacy** - Code never leaves your device
- **⚡ Speed** - No network latency
- **💰 Cost** - No API fees
- **📴 Offline** - Works without internet
- **🔐 Security** - Enterprise-grade data protection

### SDK Features Used

1. **Model Loading** - SmolLM2 (135M params, 1.8GB)
2. **Streaming Generation** - Real-time roast generation
3. **Prompt Engineering** - Optimized prompts for code analysis
4. **Temperature Control** - Intensity affects AI creativity
5. **Context Management** - Efficient token handling

### Performance Metrics

- **Model Load Time**: 3-5 seconds (first launch)
- **Analysis Time**: 5-15 seconds per code
- **Memory Usage**: ~180MB with model loaded
- **Battery Impact**: Minimal (optimized inference)

---

## 📸 Feature Highlights

### Roast Personalities

| Personality | Style | Example |
|-------------|-------|---------|
| 🔪 Gordon Ramsay | Aggressive chef | "This code is RAW! IT'S F***ING RAW!" |
| 🎖️ Drill Sergeant | Military drill | "MAGGOT! Drop and give me 20 refactors!" |
| 👔 Disappointed Dad | Gentle sadness | "Son, I'm not angry... just disappointed" |
| 💅 Gen Z | Internet slang | "Bestie, this code is giving ick vibes 💀" |
| 🎭 Shakespeare | Old English | "What fresh code hell doth mine eyes behold?!" |

### Intensity Levels

- **Level 1 (😊)** - Gentle suggestions
- **Level 2 (😐)** - Constructive criticism
- **Level 3 (😠)** - Firm feedback
- **Level 4 (😡)** - Harsh roasting
- **Level 5 (💀)** - Nuclear destruction

---

## 🐛 Known Issues & Limitations

- ⚠️ First launch requires model download (~1.8GB)
- ⚠️ Very large files (5000+ lines) may timeout
- ⚠️ Some edge-case syntax errors in generated fixes
- ⚠️ Share feature requires storage permission
- ⚠️ History limited to 50 most recent entries

---

## 📄 License

```
Copyright (c) 2024 Yasaswini Chebolu

Educational project - Not for commercial use
RunAnywhere SDK usage subject to their license terms
```

---

## 🙏 Acknowledgments

- **NERDS Room** - For organizing the Vibeathon
- **RunAnywhere Team** - For the amazing on-device AI SDK
- **Firebender** - For the inspiration and community
- **Material Design Team** - For the beautiful design system
- **Jetpack Compose Team** - For the modern UI toolkit

---

## 👥 Author

**Yasaswini Chebolu**

- GitHub: [@Yasaswini-ch](https://github.com/Yasaswini-ch)
- LinkedIn: [Yasaswini Chebolu](https://linkedin.com/in/yasaswini-chebolu)

---

## 📞 Support

### Issues & Bugs

Report issues on [GitHub Issues](https://github.com/Yasaswini-ch/CodeRoastai/issues)

---

**Made with ❤️ and AI**

---
## 📊 Project Statistics

- **Lines of Code**: ~15,000
- **Files**: 45+
- **Development Time**: [X hours]
- **Features**: 25+
- **Test Cases**: 50+
- **Supported Languages**: 5
- **Personalities**: 5
- **Screens**: 8

---

**⭐ If you like this project, please give it a star!**
