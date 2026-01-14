package com.incomingcallonly.launcher.ui.admin.dialogs

import androidx.compose.foundation.layout.Box
import com.incomingcallonly.launcher.ui.admin.components.AdminDialog
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.incomingcallonly.launcher.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogWrapper(
    initialHour: Int,
    initialMinute: Int,
    is24HourFormat: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24HourFormat
    )

    AdminDialog(
        onDismissRequest = onDismiss,
        title = stringResource(id = R.string.select_hour),
        content = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TimePicker(
                    state = timePickerState,
                    colors = androidx.compose.material3.TimePickerDefaults.colors(
                        periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                        clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
                        selectorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(timePickerState.hour, timePickerState.minute)
            }) {
                Text(stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}
