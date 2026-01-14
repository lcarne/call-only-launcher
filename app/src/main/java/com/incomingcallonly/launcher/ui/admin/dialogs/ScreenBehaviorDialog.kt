package com.incomingcallonly.launcher.ui.admin.dialogs

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.ui.admin.components.AdminSelectionDialog
import com.incomingcallonly.launcher.ui.theme.Spacing

@Composable
fun ScreenBehaviorDialog(
    title: String,
    icon: Any? = null,
    currentValue: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(0, 1, 2)
    
    AdminSelectionDialog(
        title = title,
        options = options,
        selectedOption = currentValue,
        onOptionSelected = {
            onConfirm(it)
        },
        onDismissRequest = onDismiss,
        headerIcon = icon,
        labelProvider = { mode ->
            when (mode) {
                0 -> stringResource(id = R.string.mode_off)
                1 -> stringResource(id = R.string.mode_dim)
                2 -> stringResource(id = R.string.mode_awake)
                else -> ""
            }
        }
    )
}
