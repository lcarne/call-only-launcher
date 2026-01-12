package com.incomingcallonly.launcher.ui.admin.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.ui.admin.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsLocalizationSection(viewModel: AdminViewModel) {
    Column {
        Text(
            text = stringResource(id = R.string.settings_section_localization),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )

        var showLangDialog by remember { mutableStateOf(false) }
        ListItem(
            headlineContent = { Text(stringResource(id = R.string.language)) },
            supportingContent = {
                val lang by viewModel.language.collectAsState()
                Text(if (lang == "fr") "Français" else "English")
            },
            modifier = Modifier.clickable { showLangDialog = true }
        )

        val currentFormat by viewModel.timeFormat.collectAsState()
        ListItem(
            headlineContent = { Text(stringResource(id = R.string.time_format_title)) },
            supportingContent = {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    SegmentedButton(
                        selected = currentFormat == "12",
                        onClick = { viewModel.setTimeFormat("12") },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text(
                            text = stringResource(id = R.string.time_format_12),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    SegmentedButton(
                        selected = currentFormat == "24",
                        onClick = { viewModel.setTimeFormat("24") },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text(
                            text = stringResource(id = R.string.time_format_24),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        )

        if (showLangDialog) {
            val lang by viewModel.language.collectAsState()
            val context = LocalContext.current
            AlertDialog(
                onDismissRequest = { showLangDialog = false },
                title = {
                    Text(
                        stringResource(id = R.string.language),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("fr", stringResource(id = R.string.language_french), "🇫🇷"),
                            Triple("en", stringResource(id = R.string.language_english), "🇬🇧")
                        ).forEach { (code, label, flag) ->
                            val isSelected = lang == code
                            Surface(
                                onClick = {
                                    viewModel.setLanguage(code)
                                    showLangDialog = false
                                    (context as? android.app.Activity)?.recreate()
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = flag,
                                        fontSize = 28.sp,
                                        modifier = Modifier.size(32.dp),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = null
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showLangDialog = false }) {
                        Text(stringResource(id = R.string.cancel))
                    }
                }
            )
        }
    }
}
