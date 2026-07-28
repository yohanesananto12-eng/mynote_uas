package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Note
import com.example.data.model.NoteColor

@Composable
fun NoteEditDialog(
    note: Note?,
    onDismiss: () -> Unit,
    onSave: (title: String, content: String, colorHex: String, isPinned: Boolean) -> Unit
) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    var colorHex by remember { mutableStateOf(note?.colorHex ?: NoteColor.YELLOW.hex) }
    var isPinned by remember { mutableStateOf(note?.isPinned ?: false) }

    val activeNoteColor = NoteColor.fromHex(colorHex)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("note_edit_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Dialog Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (note == null) "Tambah Note Baru" else "Edit Sticky Note",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_dialog_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Live Sticky Note Preview Card
                Text(
                    text = "Pratinjau Sticky Note:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = activeNoteColor.containerColor
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, activeNoteColor.borderColor, RoundedCornerShape(12.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .background(activeNoteColor.borderColor)
                        )
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = title.ifBlank { "Judul Catatan..." },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = activeNoteColor.textColor
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = content.ifBlank { "Isi catatan akan tampil di sini..." },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = activeNoteColor.textColor.copy(alpha = 0.8f)
                                ),
                                maxLines = 3
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Note") },
                    placeholder = { Text("Contoh: Belajar SharedPreferences") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_title_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = activeNoteColor.borderColor,
                        focusedLabelColor = activeNoteColor.borderColor
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Content Input
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Isi Note") },
                    placeholder = { Text("Tuliskan detail catatan di sini...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .testTag("note_content_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = activeNoteColor.borderColor,
                        focusedLabelColor = activeNoteColor.borderColor
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Color Picker Row
                ColorPickerRow(
                    selectedColorHex = colorHex,
                    onColorSelected = { selected -> colorHex = selected.hex }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Pin Switch Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = null,
                            tint = activeNoteColor.borderColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sematkan ke Atas (Pin)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Switch(
                        checked = isPinned,
                        onCheckedChange = { isPinned = it },
                        modifier = Modifier.testTag("pin_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = activeNoteColor.borderColor,
                            checkedTrackColor = activeNoteColor.containerColor
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("cancel_save_btn")
                    ) {
                        Text("Batal")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank() || content.isNotBlank()) {
                                onSave(title, content, colorHex, isPinned)
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank() || content.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = activeNoteColor.borderColor
                        ),
                        modifier = Modifier.testTag("save_note_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan Note")
                    }
                }
            }
        }
    }
}
