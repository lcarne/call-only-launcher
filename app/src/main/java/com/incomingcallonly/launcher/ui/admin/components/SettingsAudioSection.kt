package com.incomingcallonly.launcher.ui.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.ui.admin.AdminViewModel
import com.incomingcallonly.launcher.ui.theme.Spacing
import androidx.compose.foundation.shape.RoundedCornerShape

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

        // Ringer Volume with Enhanced Display
        val ringerVolume by viewModel.ringerVolume.collectAsState()
        ListItem(
            headlineContent = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(id = R.string.ringer_volume))
                    // Inline percentage display
                    Text(
                        text = "$ringerVolume%",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            },
            supportingContent = {
                Column {
                    Slider(
                        value = ringerVolume.toFloat(),
                        onValueChange = { viewModel.setRingerVolume(it.toInt()) },
                        valueRange = 0f..100f,
                        steps = 9,  // 10% increments for easier control
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    // Min/max labels for context
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "0%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "100%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            trailingContent = {
                IconButton(
                    onClick = { viewModel.testRingtone() },
                    modifier = Modifier.size(Spacing.iconButtonSize)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = stringResource(id = R.string.test_ringer),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Spacing.iconLarge)
                    )
                }
            }
        )
    }
}
