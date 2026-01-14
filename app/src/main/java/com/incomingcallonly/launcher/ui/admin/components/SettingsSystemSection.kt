package com.incomingcallonly.launcher.ui.admin.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.ui.admin.AdminViewModel
import androidx.compose.foundation.clickable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun SettingsSystemSection(viewModel: AdminViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.settings_section_system),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
        )

        // Call Security
        val allowAllCalls by viewModel.allowAllCalls.collectAsState()
        ListItem(
            headlineContent = { Text(stringResource(id = R.string.accept_all_calls)) },
            supportingContent = { Text(stringResource(id = R.string.accept_all_calls_desc)) },
            trailingContent = {
                Switch(
                    checked = allowAllCalls,
                    onCheckedChange = { viewModel.setAllowAllCalls(it) }
                )
            }
        )

        // PIN Management
        var showChangePinDialog by remember { mutableStateOf(false) }
        
        ListItem(
            headlineContent = { Text(stringResource(id = R.string.change_pin)) },
            supportingContent = { Text(stringResource(id = R.string.change_pin_desc)) },
            modifier = Modifier.clickable { showChangePinDialog = true }
        )

        if (showChangePinDialog) {
            var newPin by remember { mutableStateOf("") }
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showChangePinDialog = false },
                title = { Text(stringResource(id = R.string.change_pin)) },
                text = {
                    Column {
                        androidx.compose.material3.OutlinedTextField(
                            value = newPin,
                            onValueChange = { 
                                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                    newPin = it 
                                }
                            },
                            label = { Text(stringResource(id = R.string.new_pin)) },
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword
                            ),
                            supportingText = { Text("4 digits") }
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newPin.length == 4) {
                                viewModel.changePin(newPin)
                                showChangePinDialog = false
                            }
                        },
                        enabled = newPin.length == 4
                    ) {
                        Text(stringResource(id = R.string.validate))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showChangePinDialog = false }) {
                        Text(stringResource(id = R.string.cancel))
                    }
                }
            )
        }
    }
}
