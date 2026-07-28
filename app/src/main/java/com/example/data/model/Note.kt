package com.example.data.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

enum class NoteColor(
    val hex: String,
    val displayName: String,
    val containerColor: Color,
    val borderColor: Color,
    val textColor: Color
) {
    YELLOW(
        hex = "#FFF176",
        displayName = "Kuning",
        containerColor = Color(0xFFFFF9C4),
        borderColor = Color(0xFFFBC02D),
        textColor = Color(0xFF332A00)
    ),
    GREEN(
        hex = "#A7F3D0",
        displayName = "Hijau Mint",
        containerColor = Color(0xFFE8F5E9),
        borderColor = Color(0xFF4CAF50),
        textColor = Color(0xFF003311)
    ),
    BLUE(
        hex = "#BAE6FD",
        displayName = "Biru Langit",
        containerColor = Color(0xFFE1F5FE),
        borderColor = Color(0xFF03A9F4),
        textColor = Color(0xFF002B49)
    ),
    PINK(
        hex = "#FBCFE8",
        displayName = "Merah Muda",
        containerColor = Color(0xFFFCE4EC),
        borderColor = Color(0xFFE91E63),
        textColor = Color(0xFF4A001F)
    ),
    PURPLE(
        hex = "#E9D5FF",
        displayName = "Ungu",
        containerColor = Color(0xFFF3E5F5),
        borderColor = Color(0xFFAB47BC),
        textColor = Color(0xFF300038)
    ),
    ORANGE(
        hex = "#FFEDD5",
        displayName = "Jingga",
        containerColor = Color(0xFFFFF3E0),
        borderColor = Color(0xFFFF9800),
        textColor = Color(0xFF4A2000)
    );

    companion object {
        fun fromHex(hex: String): NoteColor {
            return entries.find { it.hex.equals(hex, ignoreCase = true) } ?: YELLOW
        }
    }
}

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val colorHex: String = NoteColor.YELLOW.hex,
    val isPinned: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
