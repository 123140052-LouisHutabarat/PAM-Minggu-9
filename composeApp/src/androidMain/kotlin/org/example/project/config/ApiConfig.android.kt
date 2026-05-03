package org.example.project.config

import org.example.project.BuildConfig

/**
 * Android actual: membaca API key dari BuildConfig.
 * BuildConfig di-generate oleh Gradle dari local.properties saat build.
 */
actual object ApiConfig {
    actual val geminiApiKey: String = BuildConfig.GEMINI_API_KEY
    actual val openAiApiKey: String = BuildConfig.OPENAI_API_KEY
}
