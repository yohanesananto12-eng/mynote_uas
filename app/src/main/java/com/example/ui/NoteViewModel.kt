package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Note
import com.example.data.storage.NotePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = NotePreferences(application)

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedColorFilter = MutableStateFlow<String?>(null)
    val selectedColorFilter: StateFlow<String?> = _selectedColorFilter.asStateFlow()

    private val _isGridView = MutableStateFlow(true)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            _notes.value = preferences.getNotes()
        }
    }

    fun saveNote(
        title: String,
        content: String,
        colorHex: String,
        isPinned: Boolean = false,
        existingId: String? = null
    ) {
        viewModelScope.launch {
            val noteToSave = Note(
                id = existingId ?: java.util.UUID.randomUUID().toString(),
                title = title.ifBlank { "Tanpa Judul" },
                content = content,
                colorHex = colorHex,
                isPinned = isPinned,
                timestamp = System.currentTimeMillis()
            )
            preferences.addOrUpdateNote(noteToSave)
            loadNotes()
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            preferences.deleteNote(noteId)
            loadNotes()
        }
    }

    fun updateNoteColor(noteId: String, newColorHex: String) {
        viewModelScope.launch {
            preferences.updateNoteColor(noteId, newColorHex)
            loadNotes()
        }
    }

    fun togglePin(noteId: String) {
        viewModelScope.launch {
            preferences.togglePin(noteId)
            loadNotes()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setColorFilter(colorHex: String?) {
        _selectedColorFilter.value = if (_selectedColorFilter.value == colorHex) null else colorHex
    }

    fun toggleViewMode() {
        _isGridView.value = !_isGridView.value
    }
}
