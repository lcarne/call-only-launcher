package com.incomingcallonly.launcher.ui.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.incomingcallonly.launcher.data.model.CallLog
import com.incomingcallonly.launcher.data.model.CallLogType
import com.incomingcallonly.launcher.ui.components.DepthIcon
import com.incomingcallonly.launcher.ui.theme.SystemBarsColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val DATE_FORMAT_HISTORY = "dd/MM HH:mm"
private const val DURATION_FORMAT_HMS = "%02d:%02d:%02d"
private const val DURATION_FORMAT_MS = "%02d:%02d"
private const val SECONDS_PER_HOUR = 3600L
private const val SECONDS_PER_MINUTE = 60L

private val COLOR_ANSWERED = Color(0xFF4CAF50)
private val COLOR_MISSED = Color(0xFFF44336)
private val COLOR_REJECTED = Color(0xFFE91E63)
private val COLOR_BLOCKED = Color.Gray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallHistoryScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onAddContact: (String) -> Unit
) {
    val callLogs by viewModel.callLogs.collectAsState()
    BackHandler(onBack = onBack)
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // System Bars Configuration - Transparent for edge-to-edge
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    SystemBarsColor(
        darkIcons = !isDarkTheme
    )

    Scaffold(
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = com.incomingcallonly.launcher.R.string.call_history)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        DepthIcon(
                            painter = rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowBack),
                            contentDescription = stringResource(id = com.incomingcallonly.launcher.R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        DepthIcon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(id = com.incomingcallonly.launcher.R.string.clear_history_title),
                            tint = MaterialTheme.colorScheme.error
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
    ) { padding ->
        if (callLogs.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    stringResource(id = com.incomingcallonly.launcher.R.string.no_call_history),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                items(callLogs) { log ->
                    CallLogItem(log, onAddContact)
                }
            }
        }

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text(stringResource(id = com.incomingcallonly.launcher.R.string.clear_history_title)) },
                text = { Text(stringResource(id = com.incomingcallonly.launcher.R.string.clear_history_confirm)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearCallHistory()
                            showDeleteConfirmation = false
                        }
                    ) {
                        Text(
                            stringResource(id = com.incomingcallonly.launcher.R.string.delete),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text(stringResource(id = com.incomingcallonly.launcher.R.string.cancel))
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallLogItem(log: CallLog, onAddContact: (String) -> Unit) {
    val sdf = SimpleDateFormat(DATE_FORMAT_HISTORY, Locale.getDefault())
    val dateStr = sdf.format(Date(log.timestamp))

    val (painter, color, label) = when (log.type) {
        CallLogType.INCOMING_ANSWERED -> Triple(
            rememberVectorPainter(Icons.Default.Call),
            COLOR_ANSWERED,
            stringResource(id = com.incomingcallonly.launcher.R.string.call_received_label)
        )

        CallLogType.INCOMING_MISSED -> Triple(
            rememberVectorPainter(Icons.AutoMirrored.Filled.CallMissed),
            COLOR_MISSED,
            stringResource(id = com.incomingcallonly.launcher.R.string.call_missed_label)
        )

        CallLogType.INCOMING_REJECTED -> Triple(
            rememberVectorPainter(Icons.Default.Block),
            COLOR_REJECTED,
            stringResource(id = com.incomingcallonly.launcher.R.string.call_rejected_label)
        )

        CallLogType.BLOCKED -> Triple(
            rememberVectorPainter(Icons.Default.Block),
            COLOR_BLOCKED,
            stringResource(id = com.incomingcallonly.launcher.R.string.call_rejected_auto_label)
        )
    }

    ListItem(
        leadingContent = {
            DepthIcon(
                painter = painter,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        },
        headlineContent = {
            Text(
                text = log.name ?: log.number,
                style = MaterialTheme.typography.titleMedium
            )
        },
        supportingContent = {
            Column {
                if (log.name != null) {
                    Text(
                        text = log.number,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = color
                    )
                    if (log.durationSeconds > 0) {
                        Text(
                            text = " • ${formatDuration(log.durationSeconds)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (log.name == null) {
                    IconButton(onClick = { onAddContact(log.number) }) {
                        Icon(
                            Icons.Default.PersonAdd,
                            contentDescription = stringResource(id = com.incomingcallonly.launcher.R.string.add_contact),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    )
}

private fun formatDuration(seconds: Long): String {
    val h = seconds / SECONDS_PER_HOUR
    val m = (seconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE
    val s = seconds % SECONDS_PER_MINUTE
    return if (h > 0) {
        String.format(Locale.getDefault(), DURATION_FORMAT_HMS, h, m, s)
    } else {
        String.format(Locale.getDefault(), DURATION_FORMAT_MS, m, s)
    }
}
