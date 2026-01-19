package com.incomingcallonly.launcher.ui.admin

import android.content.Intent
import android.provider.Settings
import android.telecom.TelecomManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.ui.admin.components.AdminDangerButton
import com.incomingcallonly.launcher.ui.admin.components.AdminDialog
import com.incomingcallonly.launcher.ui.admin.components.AdminDivider
import com.incomingcallonly.launcher.ui.admin.components.AdminIcon
import com.incomingcallonly.launcher.ui.admin.components.AdminNavigationItem
import com.incomingcallonly.launcher.ui.admin.components.AdminSectionHeader
import com.incomingcallonly.launcher.ui.admin.components.AdminSettingsCard
import com.incomingcallonly.launcher.ui.admin.components.SettingsAudioSection
import com.incomingcallonly.launcher.ui.admin.components.SettingsDisplaySection
import com.incomingcallonly.launcher.ui.admin.components.SettingsLocalizationSection
import com.incomingcallonly.launcher.ui.admin.components.SettingsSystemSection
import com.incomingcallonly.launcher.ui.components.AppDialog
import com.incomingcallonly.launcher.ui.components.DepthIcon
import com.incomingcallonly.launcher.ui.theme.Spacing
import com.incomingcallonly.launcher.ui.theme.SystemBarsColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(
    settingsViewModel: SettingsViewModel,
    contactsViewModel: ContactsViewModel,
    authViewModel: AuthViewModel,
    onExit: () -> Unit,
    onLogout: () -> Unit,
    onUnpin: () -> Unit,
    onPin: () -> Unit,
    onManageContacts: () -> Unit,
    onShowHistory: () -> Unit
) {
    val scrollState = rememberScrollState()
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    var showResetDataDialog by remember { androidx.compose.runtime.mutableStateOf(false) }
    var showResetSettingsDialog by remember { androidx.compose.runtime.mutableStateOf(false) }
    var showDialerExplanation by remember { androidx.compose.runtime.mutableStateOf(false) }
    var showLauncherExplanation by remember { androidx.compose.runtime.mutableStateOf(false) }
    var showPinExplanation by remember { androidx.compose.runtime.mutableStateOf(false) }

    // System Bars Configuration
    val isDarkTheme = isSystemInDarkTheme()
    SystemBarsColor(
        darkIcons = !isDarkTheme
    )

    // Modern Dialogs
    if (showResetDataDialog) {
        AdminDialog(
            onDismissRequest = { showResetDataDialog = false },
            title = stringResource(R.string.reset_all_data),
            icon = Icons.Default.Delete,
            iconContainerColor = MaterialTheme.colorScheme.errorContainer,
            iconTint = MaterialTheme.colorScheme.error,
            animated = false,
            content = {
                Text(
                    stringResource(R.string.confirm_reset_all_data),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
            },
            confirmButton = {
                AdminDangerButton(
                    text = stringResource(R.string.confirm),
                    onClick = {
                        settingsViewModel.deleteAllData()
                        showResetDataDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showResetDataDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showResetSettingsDialog) {
        AdminDialog(
            onDismissRequest = { showResetSettingsDialog = false },
            title = stringResource(R.string.reset_settings),
            icon = Icons.Default.Refresh,
            animated = false,
            content = {
                Text(
                    stringResource(R.string.confirm_reset_settings),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                AdminDangerButton( // Changed to DangerButton as requested
                    text = stringResource(R.string.confirm),
                    onClick = {
                        settingsViewModel.resetSettings()
                        showResetSettingsDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showResetSettingsDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    val exportLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.CreateDocument("text/x-vcard"),
        onResult = { uri ->
            uri?.let { contactsViewModel.exportContacts(it) }
        }
    )

    val importLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let { contactsViewModel.importContacts(it) }
        }
    )

    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
    val importExportState by contactsViewModel.importExportState.collectAsState()

    androidx.compose.runtime.LaunchedEffect(importExportState) {
        importExportState?.let { result ->
            when (result) {
                is Result.Success -> {
                    snackbarHostState.showSnackbar(result.data)
                    contactsViewModel.resetImportExportState()
                }

                is Result.Error -> {
                    snackbarHostState.showSnackbar(result.message ?: "Unknown error")
                    contactsViewModel.resetImportExportState()
                }

                is Result.Loading -> {
                    // Optionally show loading indicator
                }
            }
        }
    }

    val isKioskActive by settingsViewModel.isKioskActive.collectAsState()
    val isDefaultDialer by settingsViewModel.isDefaultDialer.collectAsState()
    val isDefaultLauncher by settingsViewModel.isDefaultLauncher.collectAsState()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                settingsViewModel.checkDefaultApps()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val startForResult = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    val dialerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(startForResult) { 
        settingsViewModel.checkDefaultApps()
    }

    val requestDefaultDialer = {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(android.app.role.RoleManager::class.java)
            if (roleManager?.isRoleAvailable(android.app.role.RoleManager.ROLE_DIALER) == true) {
                // Always request the role to show the popup, even if we already hold it
                // This allows the user to re-confirm or potentially see the dialog (depending on OS behavior)
                // as requested by the user to avoid the generic settings screen.
                val intent = roleManager.createRequestRoleIntent(android.app.role.RoleManager.ROLE_DIALER)
                dialerLauncher.launch(intent)
            }
        } else {
            // Pre-Android Q
            try {
                val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, context.packageName)
                dialerLauncher.launch(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.settings),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.25f),
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.shadow(
                    elevation = 4.dp,
                    spotColor = Color.Black.copy(alpha = 0.15f)
                ),
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Configuration Section
            AdminSectionHeader(text = stringResource(id = R.string.settings_section_configuration))

            // Quick Actions - Now more prominent
            AdminSettingsCard {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = {
                        Text(
                            stringResource(id = R.string.change_default_phone_app),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    leadingContent = {
                        AdminIcon(
                            imageVector = Icons.Default.Call,
                            tint = if (!isDefaultDialer) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            containerColor = if (!isDefaultDialer) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                        )
                    },
                    modifier = Modifier.clickable {
                        if (!isDefaultDialer) {
                            showDialerExplanation = true
                        } else {
                            try {
                                val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                requestDefaultDialer()
                            }
                        }
                    }
                )
                AdminDivider()

                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = {
                        Text(
                            stringResource(id = R.string.change_default_launcher),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    leadingContent = {
                        AdminIcon(
                            imageVector = Icons.Default.Home,
                            tint = if (!isDefaultLauncher) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            containerColor = if (!isDefaultLauncher) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                        )
                    },
                    modifier = Modifier.clickable {
                        if (!isDefaultLauncher) {
                            showLauncherExplanation = true
                        } else {
                            try {
                                val intent = Intent(Settings.ACTION_HOME_SETTINGS)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                )
                AdminDivider()

                if (isKioskActive) {
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = {
                            Text(
                                stringResource(id = R.string.unpin),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        leadingContent = {
                            AdminIcon(
                                imageVector = Icons.Default.LockOpen,
                                tint = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        },
                        modifier = Modifier.clickable {
                            onUnpin()
                            onLogout()
                            onExit()
                        }
                    )
                } else {
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = {
                            Text(
                                stringResource(id = R.string.pin),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        leadingContent = {
                            AdminIcon(
                                imageVector = Icons.Default.Lock,
                                tint = MaterialTheme.colorScheme.error,
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        },
                        modifier = Modifier.clickable {
                            showPinExplanation = true
                        }
                    )
                }
                AdminDivider()
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = {
                        Text(
                            stringResource(id = R.string.back_arrow),
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    leadingContent = {
                        AdminIcon(
                            painter = rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowBack)
                        )
                    },
                    modifier = Modifier.clickable {
                        onLogout()
                        onExit()
                    }
                )
            }

            // Modals for Onboarding
            if (showDialerExplanation) {
                AppDialog(
                    onDismissRequest = { showDialerExplanation = false },
                    title = stringResource(id = R.string.onboarding_default_dialer_title),
                    message = stringResource(id = R.string.onboarding_default_dialer_message),
                    buttons = {
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                        ) {
                            androidx.compose.material3.Button(
                                onClick = { showDialerExplanation = false },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(stringResource(id = R.string.not_now))
                            }
                            androidx.compose.material3.Button(
                                onClick = {
                                    showDialerExplanation = false
                                    requestDefaultDialer()
                                },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = com.incomingcallonly.launcher.ui.theme.ConfirmGreen),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(stringResource(id = R.string.understood))
                            }
                        }
                    }
                )
            }

            if (showLauncherExplanation) {
                AppDialog(
                    onDismissRequest = { showLauncherExplanation = false },
                    title = stringResource(id = R.string.onboarding_default_launcher_title),
                    message = stringResource(id = R.string.onboarding_default_launcher_message),
                    buttons = {
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                        ) {
                            androidx.compose.material3.Button(
                                onClick = { showLauncherExplanation = false },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(stringResource(id = R.string.not_now))
                            }
                            androidx.compose.material3.Button(
                                onClick = {
                                    showLauncherExplanation = false
                                    try {
                                        val intent = Intent(Settings.ACTION_HOME_SETTINGS)
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = com.incomingcallonly.launcher.ui.theme.ConfirmGreen),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(stringResource(id = R.string.configure))
                            }
                        }
                    }
                )
            }

            if (showPinExplanation) {
                AppDialog(
                    onDismissRequest = { showPinExplanation = false },
                    title = stringResource(id = R.string.onboarding_pinned_mode_title),
                    message = stringResource(id = R.string.onboarding_pinned_mode_message),
                    buttons = {
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                        ) {
                            androidx.compose.material3.Button(
                                onClick = { showPinExplanation = false },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(stringResource(id = R.string.not_now))
                            }
                            androidx.compose.material3.Button(
                                onClick = {
                                    showPinExplanation = false
                                    onPin()
                                },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = com.incomingcallonly.launcher.ui.theme.ConfirmGreen),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(stringResource(id = R.string.understood))
                            }
                        }
                    }
                )
            }

            // Content Management
            AdminSectionHeader(text = stringResource(id = R.string.settings_section_content))

            // Standalone buttons for Content Management (as requested)
            AdminNavigationItem(
                headlineText = stringResource(id = R.string.manage_contacts),
                leadingIcon = {
                    AdminIcon(
                        painter = rememberVectorPainter(Icons.AutoMirrored.Filled.List),
                        tint = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                },
                onClick = onManageContacts,
                trailingIcon = {
                    DepthIcon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(Spacing.iconSmall),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.padding(horizontal = Spacing.md)
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            AdminNavigationItem(
                headlineText = stringResource(id = R.string.call_history),
                leadingIcon = {
                    AdminIcon(
                        painter = rememberVectorPainter(Icons.Default.Call),
                        tint = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                },
                onClick = onShowHistory,
                trailingIcon = {
                    DepthIcon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(Spacing.iconSmall),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.padding(horizontal = Spacing.md)
            )

            // Settings Section
            SettingsSection(settingsViewModel, authViewModel)

            // Data Management
            AdminSectionHeader(text = stringResource(id = R.string.data_management))

            AdminSettingsCard {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text(stringResource(id = R.string.export_contacts)) },
                    leadingContent = {
                        AdminIcon(
                            imageVector = Icons.Default.Share,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.clickable { exportLauncher.launch("contacts_backup.vcf") }
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text(stringResource(id = R.string.import_contacts)) },
                    leadingContent = {
                        AdminIcon(
                            imageVector = Icons.Default.Add,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.clickable {
                        importLauncher.launch(
                            arrayOf(
                                "text/x-vcard",
                                "text/vcard"
                            )
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Dangerous Zone - Same style as export/import section
            AdminSettingsCard {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text(stringResource(id = R.string.reset_settings)) },
                    leadingContent = {
                        AdminIcon(
                            imageVector = Icons.Default.Refresh,
                            tint = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    },
                    modifier = Modifier.clickable { showResetSettingsDialog = true }
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = {
                        Text(
                            stringResource(id = R.string.reset_all_data),
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    leadingContent = {
                        AdminIcon(
                            imageVector = Icons.Default.Delete,
                            tint = MaterialTheme.colorScheme.error,
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    },
                    modifier = Modifier.clickable { showResetDataDialog = true }
                )
            }

            // Support Section
            AdminSectionHeader(text = stringResource(id = R.string.support))

            AdminSettingsCard {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text(stringResource(id = R.string.buy_me_coffee)) },
                    leadingContent = {
                        AdminIcon(
                            imageVector = Icons.Default.Favorite,
                            tint = Color(0xFFFFDD00), // Gold
                            containerColor = Color(0xFFFFDD00).copy(alpha = 0.1f)
                        )
                    },
                    trailingContent = {
                        DepthIcon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(Spacing.iconLarge),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier.clickable {
                        onUnpin()
                        uriHandler.openUri("https://buymeacoffee.com/leocarne")
                    }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            Text(
                text = stringResource(id = R.string.settings_footer_message),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.xl),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.xxxl))
        }
    }
}

@OptIn(
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun SettingsSection(viewModel: SettingsViewModel, authViewModel: AuthViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SettingsSystemSection(viewModel, authViewModel)
        SettingsAudioSection(viewModel)
        SettingsDisplaySection(viewModel)
        SettingsLocalizationSection(viewModel)
    }
}
