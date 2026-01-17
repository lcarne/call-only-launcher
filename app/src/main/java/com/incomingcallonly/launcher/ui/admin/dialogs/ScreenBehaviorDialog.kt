package com.incomingcallonly.launcher.ui.admin.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.ui.admin.components.AdminSelectionDialog

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
