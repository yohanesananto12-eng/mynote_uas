package com.example.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.Note
import com.example.data.model.NoteColor
import org.json.JSONArray
import org.json.JSONObject

/**
 * NotePreferences handles persistent storage of notes using Android's SharedPreferences.
 * Notes are serialized to and deserialized from JSON strings.
 */
class NotePreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "mynote_shared_preferences"
        private const val KEY_NOTES_JSON = "key_notes_list_json"
        private const val KEY_FIRST_RUN = "key_is_first_run"
    }

    init {
        if (isFirstRun()) {
            saveInitialNotes()
            setFirstRunCompleted()
        }
    }

    private fun isFirstRun(): Boolean {
        return prefs.getBoolean(KEY_FIRST_RUN, true)
    }

    private fun setFirstRunCompleted() {
        prefs.edit().putBoolean(KEY_FIRST_RUN, false).apply()
    }

    private fun saveInitialNotes() {
        val sampleNotes = listOf(
            Note(
                title = "Catatan Kuliah Sistem Informasi",
                content = "Topik Hari Ini: Pemrograman Mobile & Penyimpanan Data Android.\n- SharedPreferences untuk key-value ringan\n- Sticky Note Color Customization\n- Material Design 3 Jetpack Compose",
                colorHex = NoteColor.YELLOW.hex,
                isPinned = true,
                timestamp = System.currentTimeMillis() - 3600000
            ),
            Note(
                title = "Rencana Tugas Akhir MyNote",
                content = "1. Fitur Penyimpanan SharedPreferences ✓\n2. Pilihan Warna Sticky Note ✓\n3. Halaman About (Informasi Mahasiswa) ✓\n4. Export APK MyNote ke GitHub ✓",
                colorHex = NoteColor.BLUE.hex,
                isPinned = true,
                timestamp = System.currentTimeMillis() - 7200000
            ),
            Note(
                title = "Reminder Bimbingan Skripsi",
                content = "Jangan lupa temui Dosen Pembimbing minggu depan untuk konsultasi modul MyNote dan laporan proyek Semester 6.",
                colorHex = NoteColor.PINK.hex,
                isPinned = false,
                timestamp = System.currentTimeMillis() - 10800000
            ),
            Note(
                title = "Daftar Belanja & Kegiatan Juli 2026",
                content = "• Buku referensi Kotlin Jetpack Compose\n• Persiapan presentasi proyek MyNote\n• Backup repositori ke GitHub",
                colorHex = NoteColor.GREEN.hex,
                isPinned = false,
                timestamp = System.currentTimeMillis() - 14400000
            )
        )
        saveNotes(sampleNotes)
    }

    /**
     * Retrieves all notes stored in SharedPreferences.
     */
    fun getNotes(): List<Note> {
        val jsonString = prefs.getString(KEY_NOTES_JSON, null) ?: return emptyList()
        val notesList = mutableListOf<Note>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val note = Note(
                    id = jsonObject.optString("id"),
                    title = jsonObject.optString("title"),
                    content = jsonObject.optString("content"),
                    colorHex = jsonObject.optString("colorHex", NoteColor.YELLOW.hex),
                    isPinned = jsonObject.optBoolean("isPinned", false),
                    timestamp = jsonObject.optLong("timestamp", System.currentTimeMillis())
                )
                notesList.add(note)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return notesList.sortedWith(compareByDescending<Note> { it.isPinned }.thenByDescending { it.timestamp })
    }

    /**
     * Saves the full list of notes to SharedPreferences.
     */
    fun saveNotes(notes: List<Note>) {
        try {
            val jsonArray = JSONArray()
            notes.forEach { note ->
                val jsonObject = JSONObject().apply {
                    put("id", note.id)
                    put("title", note.title)
                    put("content", note.content)
                    put("colorHex", note.colorHex)
                    put("isPinned", note.isPinned)
                    put("timestamp", note.timestamp)
                }
                jsonArray.put(jsonObject)
            }
            prefs.edit().putString(KEY_NOTES_JSON, jsonArray.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Adds or updates a single note and syncs with SharedPreferences.
     */
    fun addOrUpdateNote(note: Note) {
        val currentNotes = getNotes().toMutableList()
        val index = currentNotes.indexOfFirst { it.id == note.id }
        if (index != -1) {
            currentNotes[index] = note
        } else {
            currentNotes.add(0, note)
        }
        saveNotes(currentNotes)
    }

    /**
     * Deletes a note by ID from SharedPreferences.
     */
    fun deleteNote(noteId: String) {
        val updatedNotes = getNotes().filterNot { it.id == noteId }
        saveNotes(updatedNotes)
    }

    /**
     * Updates only the color of a note in SharedPreferences.
     */
    fun updateNoteColor(noteId: String, newColorHex: String) {
        val currentNotes = getNotes().map { note ->
            if (note.id == noteId) note.copy(colorHex = newColorHex) else note
        }
        saveNotes(currentNotes)
    }

    /**
     * Toggles the pin status of a note.
     */
    fun togglePin(noteId: String) {
        val currentNotes = getNotes().map { note ->
            if (note.id == noteId) note.copy(isPinned = !note.isPinned) else note
        }
        saveNotes(currentNotes)
    }
}
