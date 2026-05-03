package org.example.project.config

import platform.Foundation.NSBundle

/**
 * iOS actual: membaca API key dari Info.plist.
 * Tambahkan GEMINI_API_KEY ke Info.plist di Xcode.
 */
actual object ApiConfig {
    actual val geminiApiKey: String
        get() = NSBundle.mainBundle.infoDictionary
            ?.get("GEMINI_API_KEY") as? String ?: ""

    actual val openAiApiKey: String
        get() = NSBundle.mainBundle.infoDictionary
            ?.get("OPENAI_API_KEY") as? String ?: ""
}
