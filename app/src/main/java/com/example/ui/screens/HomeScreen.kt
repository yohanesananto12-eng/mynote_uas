package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Note
import com.example.data.model.NoteColor
import com.example.ui.NoteViewModel
import com.example.ui.components.NoteCard
import com.example.ui.components.NoteEditDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: NoteViewModel,
    onNavigateToAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val notes by viewModel.notes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedColorFilter by viewModel.selectedColorFilter.collectAsState()
    val isGridView by viewModel.isGridView.collectAsState()

    var showSearchField by remember { mutableStateOf(false) }
    var noteToEdit by remember { mutableStateOf<Note?>(null) }
    var isCreatingNewNote by remember { mutableStateOf(false) }

    // Filter notes based on search query and color filter
    val filteredNotes = remember(notes, searchQuery, selectedColorFilter) {
        notes.filter { note ->
            val matchesQuery = searchQuery.isBlank() ||
                    note.title.contains(searchQuery, ignoreCase = true) ||
                    note.content.contains(searchQuery, ignoreCase = true)

            val matchesColor = selectedColorFilter == null ||
                    note.colorHex.equals(selectedColorFilter, ignoreCase = true)

            matchesQuery && matchesColor
        }
    }

    val pinnedNotes = remember(filteredNotes) { filteredNotes.filter { it.isPinned } }
    val unpinnedNotes = remember(filteredNotes) { filteredNotes.filter { !it.isPinned } }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFF176))
                                    .border(1.5.dp, Color(0xFFFBC02D), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "📝",
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "MyNote",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    },
                    actions = {
                        // Toggle search bar
                        IconButton(
                            onClick = {
                                showSearchField = !showSearchField
                                if (!showSearchField) viewModel.setSearchQuery("")
                            },
                            modifier = Modifier.testTag("toggle_search_btn")
                        ) {
                            Icon(
                                imageVector = if (showSearchField) Icons.Default.Clear else Icons.Default.Search,
                                contentDescription = "Cari Note"
                            )
                        }

                        // Toggle View Mode (Grid / List)
                        IconButton(
                            onClick = { viewModel.toggleViewMode() },
                            modifier = Modifier.testTag("toggle_view_btn")
                        ) {
                            Icon(
                                imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                                contentDescription = "Ubah Tampilan"
                            )
                        }

                        // Open About Screen
                        IconButton(
                            onClick = onNavigateToAbout,
                            modifier = Modifier.testTag("about_screen_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Tentang Aplikasi",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                )

                // Search Bar Field (Expandable)
                if (showSearchField) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Cari judul atau isi catatan...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Hapus Pencarian"
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }

                // Sticky Note Color Filter Bar
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f))
                        .padding(vertical = 6.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedColorFilter == null,
                            onClick = { viewModel.setColorFilter(null) },
                            label = { Text("Semua Warna (${notes.size})") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("filter_all")
                        )
                    }

                    items(NoteColor.entries) { color ->
                        val isSelected = color.hex.equals(selectedColorFilter, ignoreCase = true)
                        val count = notes.count { it.colorHex.equals(color.hex, ignoreCase = true) }

                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setColorFilter(color.hex) },
                            label = { Text("${color.displayName} ($count)") },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(color.containerColor)
                                        .border(1.dp, color.borderColor, CircleShape)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color.containerColor,
                                selectedLabelColor = color.textColor
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = color.borderColor
                            ),
                            modifier = Modifier.testTag("filter_${color.name.lowercase()}")
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isCreatingNewNote = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.testTag("add_note_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Note"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Note Baru",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            if (filteredNotes.isEmpty()) {
                // Empty State View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFFFFF9C4))
                            .border(2.dp, Color(0xFFFBC02D), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📌",
                            fontSize = 48.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (searchQuery.isNotEmpty() || selectedColorFilter != null) {
                            "Tidak ada sticky note yang cocok"
                        } else {
                            "Belum ada sticky note"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (searchQuery.isNotEmpty() || selectedColorFilter != null) {
                            "Coba ubah kata kunci pencarian atau reset filter warna."
                        } else {
                            "Tekan tombol '+ Note Baru' untuk membuat catatan sticky note pertama Anda."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    if (searchQuery.isNotEmpty() || selectedColorFilter != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.clickable {
                                viewModel.setSearchQuery("")
                                viewModel.setColorFilter(null)
                            }
                        ) {
                            Text(
                                text = "Reset Filter & Pencarian",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            } else {
                // Notes List / Grid
                val gridColumns = if (isGridView) GridCells.Fixed(2) else GridCells.Fixed(1)

                LazyVerticalGrid(
                    columns = gridColumns,
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Pinned Notes Section Header
                    if (pinnedNotes.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Catatan Disematkan (${pinnedNotes.size})",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }

                        items(
                            items = pinnedNotes,
                            key = { "pinned_${it.id}" }
                        ) { note ->
                            NoteCard(
                                note = note,
                                onClick = { noteToEdit = note },
                                onDeleteClick = { viewModel.deleteNote(note.id) },
                                onPinToggle = { viewModel.togglePin(note.id) },
                                onColorChange = { newColor -> viewModel.updateNoteColor(note.id, newColor) }
                            )
                        }

                        if (unpinnedNotes.isNotEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Lainnya (${unpinnedNotes.size})",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Unpinned Notes Items
                    items(
                        items = unpinnedNotes,
                        key = { "note_${it.id}" }
                    ) { note ->
                        NoteCard(
                            note = note,
                            onClick = { noteToEdit = note },
                            onDeleteClick = { viewModel.deleteNote(note.id) },
                            onPinToggle = { viewModel.togglePin(note.id) },
                            onColorChange = { newColor -> viewModel.updateNoteColor(note.id, newColor) }
                        )
                    }
                }
            }
        }
    }

    // Dialog for creating new note or editing existing note
    if (isCreatingNewNote || noteToEdit != null) {
        NoteEditDialog(
            note = noteToEdit,
            onDismiss = {
                isCreatingNewNote = false
                noteToEdit = null
            },
            onSave = { title, content, colorHex, isPinned ->
                viewModel.saveNote(
                    title = title,
                    content = content,
                    colorHex = colorHex,
                    isPinned = isPinned,
                    existingId = noteToEdit?.id
                )
                isCreatingNewNote = false
                noteToEdit = null
            }
        )
    }
}
