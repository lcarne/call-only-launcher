package com.incomingcallonly.launcher.ui.onboarding

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.ui.theme.ConfirmGreen
import com.incomingcallonly.launcher.ui.theme.ErrorRed
import kotlinx.coroutines.delay

@Composable
fun OnboardingFlow(onDismiss: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }

    // 0: Presentation
    // 1: Authorization Requests Explanation
    // 2: Request Contacts
    // 3: Request Phone State
    // 4: Location Explanation
    // 5: Request Location
    // 6: Admin Explanation

    val contactLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { _ -> step++ }
    )
    
    val phoneLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { _ -> step++ }
    )

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { _ -> step = 6 }
    )

    AlertDialog(
        onDismissRequest = { /* Prevent dismissal */ },
        icon = {
            androidx.compose.material3.Icon(
                imageVector = when (step) {
                    0 -> Icons.Default.Info
                    1, 6 -> Icons.Default.Lock
                    2 -> Icons.Default.Person
                    3 -> Icons.Default.Phone
                    4 -> Icons.Default.LocationOn
                    else -> Icons.Default.Info
                },
                contentDescription = null
            )
        },
        title = {
            Text(
                text = stringResource(id = when (step) {
                    0 -> R.string.onboarding_presentation_title
                    1 -> R.string.onboarding_auth_intro_title
                    2 -> R.string.onboarding_auth_contacts_title
                    3 -> R.string.onboarding_auth_calls_title
                    4 -> R.string.onboarding_auth_location_intro_title
                    else -> R.string.onboarding_admin_intro_title
                }),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column {
                Text(
                    text = parseBoldString(stringResource(id = when (step) {
                        0 -> R.string.onboarding_presentation_message
                        1 -> R.string.onboarding_auth_intro_message
                        2 -> R.string.onboarding_auth_contacts_message
                        3 -> R.string.onboarding_auth_calls_message
                        4 -> R.string.onboarding_auth_location_intro_message
                        else -> R.string.onboarding_admin_intro_message
                    })),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (step == 6) {
                    Text(
                        text = stringResource(id = R.string.onboarding_important),
                        style = MaterialTheme.typography.titleMedium,
                        color = ErrorRed,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {
            if (step == 6) {
                // Admin Step Logic (Double Tap)
                var tapCount by remember { mutableIntStateOf(0) }

                LaunchedEffect(tapCount) {
                    if (tapCount > 0) {
                        delay(2000)
                        if (tapCount == 1) {
                            tapCount = 0
                        }
                    }
                }

                Button(
                    onClick = {
                        if (tapCount == 0) {
                            tapCount++
                        } else {
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (tapCount == 0) MaterialTheme.colorScheme.primary else ConfirmGreen
                    )
                ) {
                    Text(
                        text = if (tapCount == 0) stringResource(id = R.string.understood) else stringResource(
                            id = R.string.press_again_to_confirm
                        ),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            } else {
                Button(
                    onClick = {
                        when (step) {
                            0 -> step++
                            1 -> step = 3

                            2 -> contactLauncher.launch(android.Manifest.permission.READ_CONTACTS)
                            3 -> {
                                val permissions = mutableListOf(
                                    android.Manifest.permission.READ_PHONE_STATE,
                                    android.Manifest.permission.ANSWER_PHONE_CALLS
                                )
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                    permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
                                }
                                phoneLauncher.launch(permissions.toTypedArray())
                            }
                            4 -> locationLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                ) {
                    val currentStep = when(step) {
                        0 -> 1
                        1 -> 2
                        3 -> 3
                        4 -> 4
                        else -> 0
                    }
                    val buttonText = stringResource(id = if (step in listOf(2, 3, 4)) R.string.validate else R.string.next)
                    Text(
                        text = if (currentStep > 0) "$buttonText ($currentStep/5)" else buttonText,
                        fontSize = 18.sp
                    )
                }
            }
        },
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth(0.90f)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                ambientColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            ),
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

@Composable
fun parseBoldString(text: String): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        val parts = text.split("*")
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) { // Odd indices are inside * *
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(part)
                }
            } else {
                append(part)
            }
        }
    }
}
