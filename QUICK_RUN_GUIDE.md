# 🚀 Quick Run Guide - Get Your App Running in 5 Minutes

## Current Status

✅ **BUILD SUCCESSFUL** - Your app is ready to run!

---

## Step-by-Step: Run Your App Now

### 1. Open in Android Studio

```
File → Open → Select "CodeRoastai" folder
```

### 2. Wait for Gradle Sync (if needed)

- Android Studio will sync automatically
- Wait for "Build: Build Successful" in the bottom right
- Should take 30-60 seconds

### 3. Connect Device or Start Emulator

**Option A: Real Android Device**

```
1. Enable Developer Mode on your phone
2. Enable USB Debugging
3. Connect via USB cable
4. Device will appear in Android Studio toolbar
```

**Option B: Android Emulator**

```
1. Click "Device Manager" in Android Studio toolbar
2. Create new device (or use existing)
3. Recommended: Pixel 5 API 34 (Android 14)
4. Start the emulator
```

### 4. Click Run!

```
Click the green ▶ "Run" button in toolbar
OR
Press Shift + F10
```

### 5. Wait for App to Launch

- App installs on device (~20 seconds)
- App opens automatically
- You'll see "CodeRoast.ai" screen

---

## What You'll See

### On First Launch:

```
┌─────────────────────────────────┐
│   RunAnywhere SDK Status        │
├─────────────────────────────────┤
│                                 │
│  SDK Initialization             │
│  ⏳ Initializing SDK...         │
│                                 │
│  Configuration                  │
│  API Key: dev                   │
│  Environment: DEVELOPMENT       │
│                                 │
│  [Load Available Models]        │
│                                 │
└─────────────────────────────────┘
```

### After 2-5 Seconds:

```
┌─────────────────────────────────┐
│   RunAnywhere SDK Status        │
├─────────────────────────────────┤
│                                 │
│  SDK Initialization             │
│  ✓ SDK initialized successfully │
│                                 │
│  Configuration                  │
│  API Key: dev                   │
│  Environment: DEVELOPMENT       │
│                                 │
│  [Load Available Models] ← CLICK│
│                                 │
└─────────────────────────────────┘
```

### After Clicking "Load Available Models":

```
┌─────────────────────────────────┐
│   RunAnywhere SDK Status        │
├─────────────────────────────────┤
│                                 │
│  Available Models (5)           │
│                                 │
│  📦 SmolLM2 360M Q8_0          │
│     ID: model-xxx               │
│     Downloaded: ✗ No            │
│                                 │
│  📦 LiquidAI LFM2 350M         │
│     ID: model-xxx               │
│     Downloaded: ✗ No            │
│                                 │
│  [4 more models...]             │
│                                 │
└─────────────────────────────────┘
```

---

## Quick Command Reference

### Run from Terminal

```bash
# Navigate to project
cd C:/Users/chebo/AndroidStudioProjects/CodeRoastai

# Build and install debug APK
./gradlew installDebug

# Or just build APK
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Clean Build (if needed)

```bash
./gradlew clean assembleDebug
```

---

## Common Issues & Solutions

### Issue: IDE shows "Unresolved reference" errors

**Solution**: Ignore them! The app builds successfully.

```
File → Sync Project with Gradle Files
```

### Issue: "SDK not initialized yet"

**Solution**: Wait 2-5 seconds and try again. This is normal on first launch.

### Issue: Gradle sync takes forever

**Solution**:

- First sync can take 2-3 minutes (downloads dependencies)
- Subsequent syncs are fast (30 seconds)
- Make sure you have internet connection

### Issue: App crashes on launch

**Solution**:

- Check Logcat for errors
- Look for "CodeRoastApp" tag
- Most likely cause: Missing permissions (already added ✓)

---

## Verify Everything Works

### Check Build Output

```
> Task :app:assembleDebug

BUILD SUCCESSFUL in 2m 24s
```

✅ If you see "BUILD SUCCESSFUL" - you're good!

### Check Logcat After Launch

Look for these logs:

```
D/CodeRoastApp: Application starting...
D/CodeRoastApp: Starting RunAnywhere SDK initialization...
D/CodeRoastApp: Using API key: dev (development mode)
D/CodeRoastApp: Environment: DEVELOPMENT
D/CodeRoastApp: ✓ SDK core initialized
D/CodeRoastApp: ✓ LlamaCpp Service Provider registered
D/CodeRoastApp: ✓ Models registered
I/CodeRoastApp: ✓✓✓ RunAnywhere SDK initialized successfully ✓✓✓
```

---

## Next Steps After App Runs

### 1. Explore the Sample App

```
Open: Hackss/ folder
File: Hackss/app/src/main/java/com/runanywhere/startup_hackathon20/MainActivity.kt
```

This has a complete working chat interface!

### 2. Add Download Functionality

Your current app can **list** models but can't **download** them yet.

Copy code from `Hackss/app/.../ChatViewModel.kt` to add:

- Model download with progress
- Model loading
- Text generation

### 3. Test on Real Device

For best performance:

- Real device > Emulator
- Device with 4GB+ RAM recommended
- ARM64 architecture required

---

## Device Requirements

### Minimum:

- Android 7.0 (API 24)
- 2 GB RAM
- 200 MB free storage
- ARM64 architecture

### Recommended:

- Android 10+ (API 29+)
- 4 GB+ RAM
- 2 GB free storage
- Modern ARM64 CPU (Snapdragon 7-series or better)

---

## Performance Tips

### Model Selection by Device:

- **Low-end** (2 GB RAM): SmolLM2 360M (119 MB)
- **Mid-range** (3 GB RAM): Qwen 0.5B (374 MB)
- **High-end** (4+ GB RAM): Llama 3.2 1B (815 MB) or Qwen 1.5B (1.2 GB)

### Speed Optimization:

1. Start with smallest model (SmolLM2 360M)
2. Close other apps before running
3. Keep app in foreground during generation
4. Test on real device for accurate performance

---

## File Locations

### APK Output:

```
app/build/outputs/apk/debug/app-debug.apk
```

You can copy this APK to any Android device and install it!

### SDK AARs:

```
app/libs/RunAnywhereKotlinSDK-release.aar (4.0 MB)
app/libs/runanywhere-llm-llamacpp-release.aar (2.1 MB)
```

### Main Code:

```
app/src/main/java/com/example/coderoastai/
├── CodeRoastApplication.kt  ← SDK initialization
└── MainActivity.kt           ← UI
```

---

## That's It! 🎉

Your app is **ready to run**. Just click the green ▶ Run button in Android Studio!

**Current Capabilities:**

- ✅ Show SDK initialization status
- ✅ Display configuration (API key, environment)
- ✅ List available models
- ⏳ Download models (needs implementation)
- ⏳ Generate AI responses (needs implementation)

**Recommended Next Action:**

1. **Run the app** to verify it works
2. **Check Logcat** to see SDK initialization
3. **Click "Load Available Models"** to see the 5 registered models
4. **Copy code from `Hackss` sample** to add download + chat features

---

Made with ❤️ using RunAnywhere SDK
