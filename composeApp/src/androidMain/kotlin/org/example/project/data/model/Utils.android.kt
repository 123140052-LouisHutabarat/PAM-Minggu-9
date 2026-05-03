package org.example.project.data.model

import java.util.UUID

actual fun generateId(): String = UUID.randomUUID().toString()
actual fun currentTimeMillis(): Long = System.currentTimeMillis()
