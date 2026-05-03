# Tugas Minggu 9 — Integrasi AI API
### Louis Hutabarat | 123140052
### IF25-22017 Pengembangan Aplikasi Mobile 
### Fitur: AI Chat + Analisis Nutrisi Makanan (Google Gemini 2.5 Flash)

---

## Versi yang Digunakan (sesuai template asli)

| Komponen | Versi |
|---|---|
| Kotlin | **2.3.20** |
| Compose Multiplatform | **1.10.3** |
| AGP (Android Gradle Plugin) | **8.11.2** |
| Ktor Client | **3.1.3** |
| Kotlinx Coroutines | **1.10.2** |
| Kotlinx Serialization | **1.8.1** |
| Navigation Compose (multiplatform) | **2.9.0-alpha13** |
| Android compileSdk / targetSdk | **36** |

---

## Fitur yang Diimplementasikan

| Fitur | Bobot Rubrik | Status |
|---|---|---|
| **AI Chat** (multi-turn conversation) | AI Integration 30% | ✅ |
| **Analisis Nutrisi** (structured JSON output) | Prompt Engineering 25% | ✅ |
| **Error Handling** (sealed class AiError) | Error Handling 20% | ✅ |
| **Loading state + Snackbar + Animasi** | UI/UX 15% | ✅ |
| **Clean Architecture** (Repository pattern) | Code Quality 10% | ✅ |
| **BONUS: Multi-turn conversation** | +5% | ✅ |

---

## 📁 File yang Ditambahkan ke Template

```
composeApp/src/
│
├── commonMain/kotlin/org/example/project/
│   ├── App.kt                                   ← DIUBAH: panggil AppNavigation()
│   ├── config/
│   │   └── ApiConfig.kt                         ← BARU: expect (API key config)
│   ├── data/
│   │   ├── model/
│   │   │   ├── AiModels.kt                      ← BARU: DTO Gemini request/response
│   │   │   ├── AiError.kt                       ← BARU: sealed class error handling
│   │   │   └── UiModels.kt                      ← BARU: UI state + expect utils
│   │   └── remote/
│   │       ├── HttpClientFactory.kt             ← BARU: konfigurasi Ktor client
│   │       ├── SystemPrompts.kt                 ← BARU: prompt engineering
│   │       └── GeminiService.kt                 ← BARU: service Gemini API
│   ├── domain/
│   │   └── repository/
│   │       └── AiRepository.kt                  ← BARU: repository pattern
│   └── presentation/
│       ├── chat/
│       │   ├── ChatViewModel.kt                 ← BARU: state management
│       │   ├── ChatScreen.kt                    ← BARU: UI percakapan
│       │   └── NutritionScreen.kt               ← BARU: UI analisis nutrisi
│       ├── components/
│       │   └── TypingIndicator.kt               ← BARU: animasi 3 titik
│       └── navigation/
│           └── AppNavigation.kt                 ← BARU: bottom navigation
│
├── androidMain/kotlin/org/example/project/
│   ├── config/
│   │   └── ApiConfig.android.kt                 ← BARU: actual (baca BuildConfig)
│   └── data/model/
│       └── Utils.android.kt                     ← BARU: actual UUID & timestamp
│
└── iosMain/kotlin/org/example/project/
    ├── config/
    │   └── ApiConfig.ios.kt                     ← BARU: actual (baca Info.plist)
    └── data/model/
        └── Utils.ios.kt                         ← BARU: actual NSUUID & NSDate

gradle/
└── libs.versions.toml                           ← DIUBAH: tambah Ktor, Serialization, dll

composeApp/
└── build.gradle.kts                             ← DIUBAH: tambah plugin + dependencies

composeApp/src/androidMain/
└── AndroidManifest.xml                          ← DIUBAH: tambah INTERNET permission

local.properties                                 ← DIUBAH: tambah placeholder GEMINI_API_KEY
```

---

## Cara Menjalankan (Step by Step)

### LANGKAH 1 — Dapatkan API Key Gemini (GRATIS)

1. Buka **https://aistudio.google.com/app/apikey**
2. Login dengan akun Google
3. Klik **"Create API key"** → pilih project atau buat baru
4. Copy API key (format: `AIzaSy...`)

> **Free tier:** 15 request/menit, 1.500 request/hari — cukup untuk development!

---

### LANGKAH 2 — Isi API Key di `local.properties`

Buka file `local.properties` di root project, lalu ganti placeholder:

```properties
# Baris yang sudah ada (jangan diubah):
sdk.dir=C:\Users\lenovo\AppData\Local\Android\Sdk

# Ganti bagian ini:
GEMINI_API_KEY=GANTI_DENGAN_API_KEY_ANDA
```

Menjadi:
```properties
GEMINI_API_KEY=AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
```

> ⚠️ **PENTING:** `local.properties` sudah ada di `.gitignore`. JANGAN commit file ini!

---

### LANGKAH 3 — Sync Gradle

Di Android Studio:
- Klik **"Sync Now"** di banner kuning yang muncul, **ATAU**
- Menu **File → Sync Project with Gradle Files**
- Tunggu 2-5 menit (download dependency Ktor, dll)

Jika muncul error versi, pastikan **Android Studio** Anda versi **Ladybug (2024.2.1)** atau lebih baru.

---

### LANGKAH 4 — Build Project

```
Menu Build → Make Project   (Ctrl+F9 / Cmd+F9)
```

Ini men-generate `BuildConfig` yang berisi `GEMINI_API_KEY`.

---

### LANGKAH 5 — Run di Emulator/Device

```
Klik tombol ▶ Run   (Shift+F10 / Ctrl+R)
```

Pilih emulator Android atau device fisik yang terhubung.

---

## 🔑 Perubahan di `gradle/libs.versions.toml`

Berikut baris yang **ditambahkan** ke versi catalog:

```toml
[versions]
# ...versi lama...
ktor = "3.1.3"
kotlinx-coroutines = "1.10.2"
kotlinx-serialization = "1.8.1"
androidx-navigation = "2.9.0-alpha13"   # versi multiplatform

[libraries]
# ...library lama...
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
ktor-client-okhttp = { module = "io.ktor:ktor-client-okhttp", version.ref = "ktor" }
ktor-client-darwin = { module = "io.ktor:ktor-client-darwin", version.ref = "ktor" }
ktor-client-content-negotiation = { module = "io.ktor:ktor-client-content-negotiation", version.ref = "ktor" }
ktor-client-logging = { module = "io.ktor:ktor-client-logging", version.ref = "ktor" }
ktor-serialization-kotlinx-json = { module = "io.ktor:ktor-serialization-kotlinx-json", version.ref = "ktor" }
kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "kotlinx-coroutines" }
kotlinx-coroutines-android = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-android", version.ref = "kotlinx-coroutines" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinx-serialization" }
androidx-navigation-compose = { module = "org.jetbrains.androidx.navigation:navigation-compose", version.ref = "androidx-navigation" }

[plugins]
# ...plugin lama...
kotlinSerialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

---

## 🔑 Perubahan di `composeApp/build.gradle.kts`

```kotlin
plugins {
    // ...plugin lama...
    alias(libs.plugins.kotlinSerialization)   // ← DITAMBAHKAN
}

sourceSets {
    androidMain.dependencies {
        // ...yang sudah ada...
        implementation(libs.ktor.client.okhttp)           // ← DITAMBAHKAN
        implementation(libs.kotlinx.coroutines.android)   // ← DITAMBAHKAN
    }
    iosMain.dependencies {
        implementation(libs.ktor.client.darwin)           // ← DITAMBAHKAN
    }
    commonMain.dependencies {
        // ...yang sudah ada...
        implementation(libs.ktor.client.core)                    // ← DITAMBAHKAN
        implementation(libs.ktor.client.content.negotiation)     // ← DITAMBAHKAN
        implementation(libs.ktor.client.logging)                 // ← DITAMBAHKAN
        implementation(libs.ktor.serialization.kotlinx.json)     // ← DITAMBAHKAN
        implementation(libs.kotlinx.coroutines.core)             // ← DITAMBAHKAN
        implementation(libs.kotlinx.serialization.json)          // ← DITAMBAHKAN
        implementation(libs.androidx.navigation.compose)         // ← DITAMBAHKAN
    }
}

android {
    defaultConfig {
        // ← DITAMBAHKAN: baca API key dari local.properties
        val localProps = rootProject.file("local.properties")
        val properties = java.util.Properties()
        if (localProps.exists()) { properties.load(localProps.inputStream()) }
        buildConfigField("String", "GEMINI_API_KEY",
            "\"${properties.getProperty("GEMINI_API_KEY", "")}\"")
    }
    buildFeatures {
        buildConfig = true   // ← DITAMBAHKAN
    }
}
```

---

## Arsitektur Aplikasi

```
User Input (ChatScreen / NutritionScreen)
        ↓
   ChatViewModel               ← StateFlow → UI reaktif
        ↓
  AiRepository (interface)     ← abstraksi, mudah ganti provider
        ↓
  AiRepositoryImpl
        ↓
   GeminiService               ← chat(), generateContent(), analyzeFood()
        ↓
  Ktor HttpClient (POST)
        ↓
  https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash
```

**Layer:**
- **Presentation** — Composable UI + ViewModel + StateFlow
- **Domain** — AiRepository interface (business logic)
- **Data** — GeminiService + Ktor + DTO models

---

## Prompt Engineering yang Diterapkan

### 1. Role + Task + Rules + Format (SystemPrompts.kt)
```kotlin
val NUTRITIONIST = """
    Kamu adalah nutritionist profesional dengan pengalaman 10 tahun.
    TUGAS: Analisis kandungan gizi makanan...
    ATURAN KETAT:
    1. Respond HANYA dengan JSON valid
    2. Jangan gunakan code block markdown
    FORMAT OUTPUT:
    { "name": "...", "calories": 0, ... }
""".trimIndent()
```

### 2. System Instruction terpisah dari pesan user
Gemini mendukung `systemInstruction` di level request — terpisah dari `contents`. Ini membuat AI lebih konsisten mengikuti instruksi daripada memasukkan instruksi ke dalam pesan user.

### 3. Structured Output + Parsing (GeminiService.kt)
```kotlin
// Bersihkan markdown jika AI tetap menambahkannya
val clean = raw
    .removePrefix("```json").removePrefix("```")
    .removeSuffix("```").trim()

Json { ignoreUnknownKeys = true }.decodeFromString<NutritionInfo>(clean)
```

### 4. Multi-turn conversation (context window)
```kotlin
// Riwayat disimpan di GeminiService.conversationHistory
// Setiap request dikirim dengan SELURUH riwayat sebelumnya
// AI "ingat" konteks percakapan
conversationHistory.add(userContent)
// ... POST request dengan seluruh conversationHistory ...
conversationHistory.add(assistantContent)
```

---

## Troubleshooting

| Error | Penyebab | Solusi |
|---|---|---|
| `API key tidak valid` | Key salah atau kosong | Cek `local.properties`, pastikan tidak ada spasi/kutip |
| `Unresolved reference: BuildConfig` | buildConfig belum aktif | Pastikan `buildFeatures { buildConfig = true }` ada, lalu **Build → Make Project** |
| `Serializer not found` | Plugin serialization tidak aktif | Pastikan `alias(libs.plugins.kotlinSerialization)` ada di `plugins {}` |
| Network error di emulator | Tidak ada INTERNET permission | Cek `<uses-permission android:name="android.permission.INTERNET" />` di `AndroidManifest.xml` |
| Rate limit (429) | Free tier: 15 req/menit | Tunggu 1 menit, jangan klik berulang |
| Navigation tidak compile | Versi navigation salah | Pastikan pakai `org.jetbrains.androidx.navigation` (bukan `androidx.navigation`) |

---

## Dokumentasi

| Chat AI | Nutrisi |
|---|---|
| ![Gambar 1](https://github.com/user-attachments/assets/271842cb-28f7-4109-bf41-46aca6e13673) | ![Gambar 2](https://github.com/user-attachments/assets/a44912af-5c85-4361-bd67-81da5cea902b) |
| ![Gambar 3](https://github.com/user-attachments/assets/988ec667-1a14-4dd6-8ee5-fa38c1c64ccc) | ![Gambar 4](https://github.com/user-attachments/assets/ab269a6f-3f2b-4c0a-be17-1772580a032e) |


---

## Referensi

- [Google AI Studio (dapatkan API key)](https://aistudio.google.com/app/apikey)
- [Gemini API Docs](https://ai.google.dev/docs)
- [Ktor Client KMP](https://ktor.io/docs/client-create-multiplatform-application.html)
- [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
- [Navigation Compose Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-navigation-routing.html)

---
