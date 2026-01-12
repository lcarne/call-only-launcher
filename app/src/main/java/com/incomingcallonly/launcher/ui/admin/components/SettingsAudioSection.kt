package com.incomingcallonly.launcher.ui.admin.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.ui.admin.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAudioSection(viewModel: AdminViewModel) {
    Column {
        // Audio Output
        val isDefaultSpeakerEnabled by viewModel.isDefaultSpeakerEnabled.collectAsState()
        ListItem(
            headlineContent = { Text(stringResource(id = R.string.default_audio_output)) },
            supportingContent = {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    SegmentedButton(
                        selected = !isDefaultSpeakerEnabled,
                        onClick = { viewModel.setDefaultSpeakerEnabled(false) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text(
                            text = stringResource(id = R.string.earpiece),
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                    SegmentedButton(
                        selected = isDefaultSpeakerEnabled,
                        onClick = { viewModel.setDefaultSpeakerEnabled(true) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text(
                            text = stringResource(id = R.string.speaker),
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        )

        // Ringer Active
        val isRingerEnabled by viewModel.isRingerEnabled.collectAsState()
        ListItem(
            headlineContent = { Text(stringResource(id = R.string.ringer_active)) },
            trailingContent = {
                Switch(
                    checked = isRingerEnabled,
                    onCheckedChange = { viewModel.setRingerEnabled(it) }
                )
            }
        )

        // Ringer Volume
        val ringerVolume by viewModel.ringerVolume.collectAsState()
        ListItem(
            headlineContent = { Text(stringResource(id = R.string.ringer_volume)) },
            supportingContent = {
                Column {
                    Slider(
                        value = ringerVolume.toFloat(),
                        onValueChange = { viewModel.setRingerVolume(it.toInt()) },
                        valueRange = 0f..100f
                    )
                }
            },
            trailingContent = {
                IconButton(onClick = { viewModel.testRingtone() }) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = stringResource(id = R.string.test_ringer),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }
}
