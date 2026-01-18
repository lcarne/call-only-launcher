package com.incomingcallonly.launcher.ui.admin.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.data.model.Contact
import com.incomingcallonly.launcher.ui.admin.ContactsViewModel
import com.incomingcallonly.launcher.ui.components.DepthIcon
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiContactImportDialog(
    viewModel: ContactsViewModel,
    onDismiss: () -> Unit,
    onConfirm: (List<Contact>) -> Unit
) {
    val deviceContacts by viewModel.deviceContacts.collectAsState()
    val savedContacts by viewModel.contacts.collectAsState()
    val selectedContacts = remember { mutableStateListOf<Contact>() }

    val context = LocalContext.current
    
    fun isContactSaved(phoneNumber: String): Boolean {
        return savedContacts.any { saved ->
            com.incomingcallonly.launcher.util.PhoneNumberUtils.arePhoneNumbersEqual(
                saved.phoneNumber,
                phoneNumber,
                context
            )
        }
    }

    val selectableDeviceContacts = remember(deviceContacts, savedContacts) {
        deviceContacts.filter { !isContactSaved(it.phoneNumber) }
    }

    LaunchedEffect(Unit) {
        viewModel.loadDeviceContacts()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.import_multiple_contacts),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cancel))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    TextButton(onClick = {
                        selectedContacts.clear()
                        selectedContacts.addAll(selectableDeviceContacts)
                    }) {
                        Text(stringResource(R.string.select_all))
                    }
                    TextButton(onClick = {
                        selectedContacts.clear()
                    }) {
                        Text(stringResource(R.string.deselect_all))
                    }
                }

                // List
                Box(modifier = Modifier.weight(1f)) {
                    if (deviceContacts.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                stringResource(R.string.no_device_contacts),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(deviceContacts) { contact ->
                                val alreadySaved = isContactSaved(contact.phoneNumber)
                                val isSelected = selectedContacts.any { it.phoneNumber == contact.phoneNumber }
                                Surface(
                                    onClick = {
                                        if (!alreadySaved) {
                                            if (isSelected) {
                                                selectedContacts.removeAll { it.phoneNumber == contact.phoneNumber }
                                            } else {
                                                selectedContacts.add(contact)
                                            }
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    color = when {
                                        isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                        alreadySaved -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        else -> Color.Transparent
                                    }
                                ) {
                                    ListItem(
                                        headlineContent = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = contact.name,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Medium,
                                                    modifier = Modifier.weight(1f, fill = false)
                                                )
                                                if (alreadySaved) {
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = stringResource(R.string.already_in_directory),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.secondary,
                                                        modifier = Modifier
                                                            .background(
                                                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                                                RoundedCornerShape(4.dp)
                                                            )
                                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        },
                                        supportingContent = {
                                            Text(
                                                text = contact.phoneNumber,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (alreadySaved) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        },
                                        leadingContent = {
                                            val alpha = if (alreadySaved) 0.6f else 1f
                                            Box(modifier = Modifier.alpha(alpha)) {
                                                if (contact.photoUri != null) {
                                                    AsyncImage(
                                                        model = contact.photoUri,
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .size(40.dp)
                                                            .clip(CircleShape)
                                                            .border(
                                                                width = 1.dp,
                                                                color = MaterialTheme.colorScheme.outlineVariant,
                                                                shape = CircleShape
                                                            ),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                } else {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(40.dp)
                                                            .clip(CircleShape)
                                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Person,
                                                            contentDescription = null,
                                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            modifier = Modifier.size(24.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        },
                                        trailingContent = {
                                            if (!alreadySaved) {
                                                Checkbox(
                                                    checked = isSelected,
                                                    onCheckedChange = { checked ->
                                                        if (checked) {
                                                            selectedContacts.add(contact)
                                                        } else {
                                                            selectedContacts.removeAll { it.phoneNumber == contact.phoneNumber }
                                                        }
                                                    }
                                                )
                                            }
                                        },
                                        colors = ListItemDefaults.colors(
                                            containerColor = Color.Transparent
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    Button(
                        onClick = { onConfirm(selectedContacts.toList()) },
                        enabled = selectedContacts.isNotEmpty(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            pluralStringResource(
                                R.plurals.import_selected_contacts,
                                selectedContacts.size,
                                selectedContacts.size
                            )
                        )
                    }
                }
            }
        }
    }
}
