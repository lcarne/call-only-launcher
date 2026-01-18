package com.incomingcallonly.launcher.ui.admin


import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ImportContacts
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.data.model.Contact
import com.incomingcallonly.launcher.ui.admin.components.AdminDangerButton
import com.incomingcallonly.launcher.ui.admin.components.AdminDialog
import com.incomingcallonly.launcher.ui.admin.components.AdminLargeButton
import com.incomingcallonly.launcher.ui.admin.components.ContactListItem
import com.incomingcallonly.launcher.ui.admin.dialogs.ContactDialog
import com.incomingcallonly.launcher.ui.admin.dialogs.MultiContactImportDialog
import com.incomingcallonly.launcher.ui.components.AppDialog
import com.incomingcallonly.launcher.ui.components.DepthIcon
import com.incomingcallonly.launcher.ui.theme.ConfirmGreen
import com.incomingcallonly.launcher.ui.theme.Spacing
import com.incomingcallonly.launcher.ui.theme.SystemBarsColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactManagementScreen(
    viewModel: ContactsViewModel,
    onBack: () -> Unit,
    onOpenCamera: ((android.net.Uri) -> Unit) -> Unit,
    initialNumber: String? = null
) {
    val contacts by viewModel.contacts.collectAsState()
    val lastCountryCode by viewModel.lastSelectedCountryCode.collectAsState()

    BackHandler(onBack = onBack)

    var showAddDialog by remember { mutableStateOf(false) }
    var effectiveInitialNumber by remember(initialNumber) { mutableStateOf(initialNumber) }

    LaunchedEffect(initialNumber) {
        if (initialNumber != null) {
            showAddDialog = true
            effectiveInitialNumber = initialNumber
        }
    }
    var showMultiImportDialog by remember { mutableStateOf(false) }
    var showContactPermissionRationale by remember { mutableStateOf(false) }
    var contactToEdit by remember { mutableStateOf<Contact?>(null) }
    var contactToDelete by remember { mutableStateOf<Contact?>(null) }

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                showMultiImportDialog = true
            }
        }
    )

    // System Bars Configuration - Transparent for edge-to-edge
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    SystemBarsColor(
        darkIcons = !isDarkTheme
    )

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.contacts)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        DepthIcon(
                            painter = rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowBack),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.shadow(
                    elevation = 4.dp,
                    spotColor = Color.Black.copy(alpha = 0.15f)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            AdminLargeButton(
                text = stringResource(R.string.import_from_directory),
                icon = Icons.Default.ImportContacts,
                onClick = {
                    val permission = Manifest.permission.READ_CONTACTS
                    if (ContextCompat.checkSelfPermission(
                            context,
                            permission
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        showMultiImportDialog = true
                    } else {
                        showContactPermissionRationale = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(top = Spacing.md)
            )
            AdminLargeButton(
                text = stringResource(R.string.add_contact),
                icon = Icons.Default.Add,
                onClick = {
                    effectiveInitialNumber = null
                    showAddDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(bottom = Spacing.xs)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(contacts) { contact ->
                    ContactListItem(
                        contact = contact,
                        onClick = { contactToEdit = contact },
                        onDelete = { contactToDelete = contact }
                    )
                }
            }
        }

        contactToDelete?.let { contact ->
            AdminDialog(
                onDismissRequest = { contactToDelete = null },
                title = stringResource(R.string.delete_contact),
                icon = Icons.Default.Delete,
                iconContainerColor = MaterialTheme.colorScheme.errorContainer,
                iconTint = MaterialTheme.colorScheme.error,
                animated = false,
                content = {
                    Text(
                        text = stringResource(
                            R.string.confirm_delete_contact,
                            contact.name
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    AdminDangerButton(
                        text = stringResource(R.string.delete),
                        onClick = {
                            viewModel.deleteContact(contact)
                            contactToDelete = null
                        }
                    )
                },
                dismissButton = {
                    TextButton(onClick = { contactToDelete = null }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }

        if (showAddDialog) {
            ContactDialog(
                contactToEdit = null,
                allContacts = contacts,
                defaultCountryCode = lastCountryCode,
                onDismiss = { showAddDialog = false },
                onOpenCamera = onOpenCamera,
                initialNumber = effectiveInitialNumber,
                onConfirm = { name, number, photoUri, countryCode ->
                    viewModel.addContact(name, number, photoUri)
                    viewModel.setLastSelectedCountryCode(countryCode)
                    showAddDialog = false
                }
            )
        }

        if (showMultiImportDialog) {
            MultiContactImportDialog(
                viewModel = viewModel,
                onDismiss = { showMultiImportDialog = false },
                onConfirm = { selected ->
                    viewModel.addContacts(selected)
                    showMultiImportDialog = false
                }
            )
        }

        contactToEdit?.let { contact ->
            ContactDialog(
                contactToEdit = contact,
                allContacts = contacts,
                defaultCountryCode = lastCountryCode,
                onDismiss = { contactToEdit = null },
                onOpenCamera = onOpenCamera,
                onConfirm = { name, number, photoUri, countryCode ->
                    viewModel.updateContact(
                        contact.copy(
                            name = name,
                            phoneNumber = number,
                            photoUri = photoUri
                        )
                    )
                    viewModel.setLastSelectedCountryCode(countryCode)
                    contactToEdit = null
                }
            )
        }

        if (showContactPermissionRationale) {
            AppDialog(
                onDismissRequest = { showContactPermissionRationale = false },
                title = stringResource(id = R.string.onboarding_auth_contacts_title),
                message = stringResource(id = R.string.onboarding_auth_contacts_message),
                buttons = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { showContactPermissionRationale = false },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(stringResource(id = R.string.not_now))
                        }
                        Button(
                            onClick = {
                                showContactPermissionRationale = false
                                permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ConfirmGreen),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(stringResource(id = R.string.understood))
                        }
                    }
                }
            )
        }
    }
}
