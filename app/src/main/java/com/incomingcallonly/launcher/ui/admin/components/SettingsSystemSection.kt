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
    }
}
