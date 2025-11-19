package com.example.coderoastai

import android.app.Application
import android.util.Log
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.data.models.SDKEnvironment
import com.runanywhere.sdk.public.extensions.addModelFromURL
import com.runanywhere.sdk.llm.llamacpp.LlamaCppServiceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Custom Application class for initializing RunAnywhere SDK
 *
 * This class handles:
 * - SDK initialization with proper error handling
 * - LLM Service Provider registration
 * - Model registration (multiple AI models for different use cases)
 * - Scanning for previously downloaded models
 */
class CodeRoastApplication : Application() {

    companion object {
        private const val TAG = "CodeRoastApp"

        // Initialization state
        @Volatile
        var isSDKInitialized = false
            private set

        @Volatile
        var initializationError: String? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "Application starting...")

        // Initialize SDK asynchronously to avoid blocking the main thread
        GlobalScope.launch(Dispatchers.IO) {
            initializeRunAnywhereSDK()
        }
    }

    /**
     * Initializes the RunAnywhere SDK with comprehensive error handling
     */
    private suspend fun initializeRunAnywhereSDK() {
        try {
            Log.d(TAG, "Starting RunAnywhere SDK initialization...")

            // Step 1: Get API key from environment variable or use 'dev' for development
            val apiKey = BuildConfig.RUNANYWHERE_API_KEY
            val environment = if (apiKey == "dev") {
                SDKEnvironment.DEVELOPMENT
            } else {
                SDKEnvironment.PRODUCTION
            }

            Log.d(TAG, "Using API key: ${if (apiKey == "dev") "dev (development mode)" else "***"}")
            Log.d(TAG, "Environment: $environment")

            // Step 2: Initialize SDK core
            RunAnywhere.initialize(
                context = this@CodeRoastApplication,
                apiKey = apiKey,
                environment = environment
            )
            Log.d(TAG, "✓ SDK core initialized")

            // Step 3: Register LLM Service Provider (enables text generation)
            LlamaCppServiceProvider.register()
            Log.d(TAG, "✓ LlamaCpp Service Provider registered")

            // Step 4: Register AI models
            registerModels()
            Log.d(TAG, "✓ Models registered")

            // Step 5: Scan for previously downloaded models
            RunAnywhere.scanForDownloadedModels()
            Log.d(TAG, "✓ Scanned for downloaded models")

            // Mark initialization as successful
            isSDKInitialized = true
            initializationError = null

            Log.i(TAG, "✓✓✓ RunAnywhere SDK initialized successfully ✓✓✓")

        } catch (e: Exception) {
            // Handle initialization failure
            val errorMessage = "SDK initialization failed: ${e.message}"
            Log.e(TAG, errorMessage, e)

            isSDKInitialized = false
            initializationError = errorMessage

            // Print full stack trace for debugging
            e.printStackTrace()
        }
    }

    /**
     * Registers AI models with the SDK
     *
     * Models are categorized by size and use case:
     * - Ultra-Light: For testing and quick responses (119-210 MB)
     * - Light: For general chat (374 MB)
     * - Medium: Better quality (815 MB)
     * - Large: Best quality (1.2 GB)
     */
    private suspend fun registerModels() {
        try {
            // Ultra-Light Models - Fast and small

            // SmolLM2 360M - Fastest, smallest (119 MB)
            addModelFromURL(
                url = "https://huggingface.co/prithivMLmods/SmolLM2-360M-GGUF/resolve/main/SmolLM2-360M.Q8_0.gguf",
                name = "SmolLM2 360M Q8_0",
                type = "LLM"
            )
            Log.d(TAG, "  ✓ Registered: SmolLM2 360M (119 MB) - Ultra-fast")

            // LiquidAI LFM2 350M (210 MB)
            addModelFromURL(
                url = "https://huggingface.co/Triangle104/LiquidAI-LFM-2-350M-1T-Instruct-Q4_K_M-GGUF/resolve/main/liquidai-lfm-2-350m-1t-instruct-q4_k_m.gguf",
                name = "LiquidAI LFM2 350M Q4_K_M",
                type = "LLM"
            )
            Log.d(TAG, "  ✓ Registered: LiquidAI LFM2 350M (210 MB) - Quick responses")

            // Light Models - General chat

            // Qwen 2.5 0.5B - Good balance (374 MB)
            addModelFromURL(
                url = "https://huggingface.co/Triangle104/Qwen2.5-0.5B-Instruct-Q6_K-GGUF/resolve/main/qwen2.5-0.5b-instruct-q6_k.gguf",
                name = "Qwen 2.5 0.5B Instruct Q6_K",
                type = "LLM"
            )
            Log.d(TAG, "  ✓ Registered: Qwen 2.5 0.5B (374 MB) - Balanced")

            // Medium Models - Better quality

            // Llama 3.2 1B - Better quality (815 MB)
            addModelFromURL(
                url = "https://huggingface.co/bartowski/Llama-3.2-1B-Instruct-GGUF/resolve/main/Llama-3.2-1B-Instruct-Q6_K_L.gguf",
                name = "Llama 3.2 1B Instruct Q6_K",
                type = "LLM"
            )
            Log.d(TAG, "  ✓ Registered: Llama 3.2 1B (815 MB) - High quality")

            // Large Models - Best quality

            // Qwen 2.5 1.5B - Best quality (1.2 GB)
            addModelFromURL(
                url = "https://huggingface.co/Qwen/Qwen2.5-1.5B-Instruct-GGUF/resolve/main/qwen2.5-1.5b-instruct-q6_k.gguf",
                name = "Qwen 2.5 1.5B Instruct Q6_K",
                type = "LLM"
            )
            Log.d(TAG, "  ✓ Registered: Qwen 2.5 1.5B (1.2 GB) - Best quality")

            Log.d(TAG, "Successfully registered 5 AI models")

        } catch (e: Exception) {
            Log.e(TAG, "Error registering models: ${e.message}", e)
            throw e
        }
    }
}
