# 🚀 Quick Start Guide - RunAnywhere SDK

Get the RunAnywhere SDK running in your Android project in under 5 minutes!

## ⚡ 3-Step Setup

### Step 1: Sync Gradle

```
File → Sync Project with Gradle Files
```

⏱️ **First sync takes 2-3 minutes** (JitPack builds the SDK)

### Step 2: Run the App

Press `Shift + F10` or click the Run button

### Step 3: Verify

Look for this in your UI:

- ✅ Green card saying "✓ SDK initialized successfully"
- ✅ API Key: "dev"
- ✅ Button: "Load Available Models" (enabled)

**That's it! The SDK is ready.** 🎉

---

## 📱 What You'll See

### On App Launch

The app displays:

1. **SDK Initialization Status** (should be green ✓)
2. **API Key Configuration** (shows "dev" by default)
3. **Load Available Models Button**
4. **Getting Started Instructions**

### After Clicking "Load Available Models"

You'll see 5 pre-registered AI models:

- SmolLM2 360M (119 MB) - Fastest
- LiquidAI LFM2 350M (210 MB)
- Qwen 2.5 0.5B (374 MB)
- Llama 3.2 1B (815 MB)
- Qwen 2.5 1.5B (1.2 GB) - Best quality

---

## 🔧 Optional: Set API Key

If you have a RunAnywhere API key:

**Windows PowerShell:**

```powershell
$env:RUNANYWHERE_API_KEY = "your-api-key"
```

**macOS/Linux:**

```bash
export RUNANYWHERE_API_KEY="your-api-key"
```

Then restart Android Studio and rebuild.

**Note:** For development and testing, the default `"dev"` key works perfectly!

---

## 📝 Check Logcat (Optional)

Filter by `CodeRoastApp` to see initialization logs:

```
D/CodeRoastApp: Application starting...
D/CodeRoastApp: ✓ SDK core initialized
D/CodeRoastApp: ✓ LlamaCpp Service Provider registered
D/CodeRoastApp: ✓ Models registered
I/CodeRoastApp: ✓✓✓ RunAnywhere SDK initialized successfully ✓✓✓
```

---

## 🎯 Try It Out: Basic AI Text Generation

Add this to your code to test the SDK:

```kotlin
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.extensions.listAvailableModels
import kotlinx.coroutines.launch

// 1. List available models
viewModelScope.launch {
    val models = listAvailableModels()
    models.forEach { model ->
        println("Model: ${model.name}")
    }
}

// 2. Download a model (smallest one)
viewModelScope.launch {
    val modelId = "SmolLM2-360M-Q8_0.gguf" // Adjust based on actual ID
    
    RunAnywhere.downloadModel(modelId).collect { progress ->
        println("Download: ${(progress * 100).toInt()}%")
    }
}

// 3. Load the model
viewModelScope.launch {
    val success = RunAnywhere.loadModel(modelId)
    if (success) {
        println("Model loaded!")
    }
}

// 4. Generate text
viewModelScope.launch {
    val response = RunAnywhere.generate("What is Android?")
    println("AI Response: $response")
}

// 5. Stream text (real-time)
viewModelScope.launch {
    var fullResponse = ""
    RunAnywhere.generateStream("Tell me a joke").collect { token ->
        fullResponse += token
        println(fullResponse) // Updates in real-time
    }
}
```

---

## 🐛 Troubleshooting

### Problem: SDK not initialized

**Solution:** Wait a few seconds - initialization is async. Check Logcat for errors.

### Problem: Gradle sync takes forever

**Solution:** First sync takes 2-3 minutes while JitPack builds the SDK. Be patient!

### Problem: "Unresolved reference" errors

**Solution:** Gradle sync not complete. Try: `File → Invalidate Caches → Restart`

### Problem: App crashes with OutOfMemoryError

**Solution:**

1. Verify `android:largeHeap="true"` in AndroidManifest.xml ✅ (Already set!)
2. Use smaller model (SmolLM2 360M)
3. Close other apps

---

## 📚 Next Steps

1. ✅ **Explore the UI** - Click around, check the status screen
2. 📥 **Download a Model** - Implement download functionality
3. 🤖 **Generate Text** - Test AI inference
4. 💬 **Build a Chat** - Create a conversational interface
5. 🎨 **Customize** - Make it your own!

---

## 📖 Full Documentation

For detailed information, see:

- **README.md** - Complete setup and usage guide
- **SETUP_CHECKLIST.md** - Detailed checklist of all changes
- **INTEGRATION_SUMMARY.md** - Technical summary of integration
- **Hackss/RUNANYWHERE_SDK_COMPLETE_GUIDE.md** - Official SDK documentation

---

## 🆘 Need Help?

**SDK Issues:**

-
GitHub: [RunanywhereAI/runanywhere-sdks/issues](https://github.com/RunanywhereAI/runanywhere-sdks/issues)

**Project Issues:**

- Check Logcat (filter: `CodeRoastApp`)
- Review README.md troubleshooting section
- Check SETUP_CHECKLIST.md verification steps

---

## 🎉 You're Ready!

The RunAnywhere SDK is fully integrated and ready to use.

**Start building amazing on-device AI features!** 🚀

---

*Made with ❤️ using RunAnywhere SDK v0.1.2-alpha*
