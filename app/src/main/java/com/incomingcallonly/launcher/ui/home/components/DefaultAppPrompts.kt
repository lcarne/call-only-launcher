package com.incomingcallonly.launcher.ui.home.components

import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.telecom.TelecomManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.ui.theme.White

// Bleu Material élégant
private val AccentBlue = Color(0xFF4A90D9)

@Composable
fun DefaultAppPrompts(
    isDefaultDialer: Boolean,
    isDefaultLauncher: Boolean
) {
    val context = LocalContext.current

    // Launcher for dialer role request
    val dialerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { /* Result handled by ViewModel refresh */ }

    // Function to request default dialer
    val requestDefaultDialer = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(RoleManager::class.java)
            if (roleManager?.isRoleAvailable(RoleManager.ROLE_DIALER) == true &&
                !roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
                dialerLauncher.launch(intent)
            }
        } else {
            val telecomManager = context.getSystemService(TelecomManager::class.java)
            if (telecomManager?.defaultDialerPackage != context.packageName) {
                @Suppress("DEPRECATION")
                val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                    putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, context.packageName)
                }
                dialerLauncher.launch(intent)
            }
        }
    }

    // Function to request default launcher
    val requestDefaultLauncher = {
        val intent = Intent(android.provider.Settings.ACTION_HOME_SETTINGS)
        context.startActivity(intent)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (!isDefaultDialer || !isDefaultLauncher) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                if (!isDefaultDialer) {
                    SetupButton(
                        icon = Icons.Default.Phone,
                        text = stringResource(R.string.activate_calls),
                        modifier = Modifier.weight(1f),
                        onClick = { requestDefaultDialer() }
                    )
                }

                if (!isDefaultLauncher) {
                    SetupButton(
                        icon = Icons.Default.Home,
                        text = stringResource(R.string.activate_launcher),
                        modifier = Modifier.weight(1f),
                        onClick = { requestDefaultLauncher() }
                    )
                }
            }
        }
    }
}

@Composable
private fun SetupButton(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, AccentBlue),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AccentBlue,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = text,
                color = White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}


