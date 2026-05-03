package org.example.project.config

/**
 * Platform-agnostic API configuration.
 * Actual implementations membaca dari BuildConfig (Android) atau Info.plist (iOS).
 *
 * Teknik expect/actual adalah cara KMP memisahkan implementasi platform-specific
 * sambil menjaga interface yang konsisten di commonMain.
 */
expect object ApiConfig {
    val geminiApiKey: String
    val openAiApiKey: String
}
