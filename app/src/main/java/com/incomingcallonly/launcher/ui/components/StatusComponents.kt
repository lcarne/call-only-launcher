package com.incomingcallonly.launcher.ui.components


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.telephony.SignalStrength
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.incomingcallonly.launcher.R

private const val BATTERY_LOW_THRESHOLD = 20
private const val BATTERY_MEDIUM_THRESHOLD = 50

// Gradient Colors
private val COLOR_LOW_START = Color(0xFFEF5350) // Red 400
private val COLOR_LOW_END = Color(0xFFC62828)   // Red 800

private val COLOR_MEDIUM_START = Color(0xFFFFEE58) // Yellow 400
private val COLOR_MEDIUM_END = Color(0xFFF9A825)   // Yellow 800

private val COLOR_HIGH_START = Color(0xFF66BB6A)   // Green 400
private val COLOR_HIGH_END = Color(0xFF2E7D32)     // Green 800

private val COLOR_BODY_START = Color(0xFF3E3E3E)   // Dark Grey
private val COLOR_BODY_END = Color(0xFF1F1F1F)     // Darker Grey - Volume

private const val BODY_WIDTH_RATIO = 0.65f
private const val BODY_HEIGHT_RATIO = 0.8f
private const val CAP_WIDTH_RATIO_OF_BODY = 0.4f
private const val CAP_HEIGHT_RATIO = 0.08f

@Composable
fun BatteryLevelDisplay(
    modifier: Modifier = Modifier,
    iconSize: androidx.compose.ui.unit.Dp = 48.dp,
    fontSize: androidx.compose.ui.unit.TextUnit = 32.sp
) {
    val context = LocalContext.current
    var batteryLevel by remember { mutableStateOf<Pair<Int, Boolean>?>(null) }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL

                if (level >= 0 && scale > 0) {
                    val pct = (level * 100) / scale
                    batteryLevel = Pair(pct, isCharging)
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(receiver, filter)
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    val currentLevel = batteryLevel
    if (currentLevel != null) {
        val (level, charging) = currentLevel

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            BatteryIcon(
                level = level,
                isCharging = charging,
                modifier = Modifier
                    .size(iconSize)
                    .padding(end = 8.dp)
            )
            Text(
                text = "$level%",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = fontSize,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun BatteryIcon(
    level: Int,
    isCharging: Boolean,
    modifier: Modifier = Modifier
) {
    val boltPainter = rememberVectorPainter(Icons.Default.Bolt)

    androidx.compose.foundation.Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Dimensions
        val bodyWidth = width * BODY_WIDTH_RATIO
        val bodyHeight = height * BODY_HEIGHT_RATIO
        val capWidth = bodyWidth * CAP_WIDTH_RATIO_OF_BODY
        val capHeight = height * CAP_HEIGHT_RATIO

        val bodyLeft = (width - bodyWidth) / 2
        val totalHeight = bodyHeight + capHeight
        val startY = (height - totalHeight) / 2
        val bodyTopFinal = startY + capHeight

        val cornerRadius = 6.dp.toPx()

        // 1. Draw Body Background (Container) - suggesting volume
        drawRoundRect(
            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                colors = listOf(COLOR_BODY_START, COLOR_BODY_END),
                start = androidx.compose.ui.geometry.Offset(bodyLeft, bodyTopFinal),
                end = androidx.compose.ui.geometry.Offset(
                    bodyLeft + bodyWidth,
                    bodyTopFinal + bodyHeight
                )
            ),
            topLeft = androidx.compose.ui.geometry.Offset(bodyLeft, bodyTopFinal),
            size = androidx.compose.ui.geometry.Size(bodyWidth, bodyHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
        )

        // 2. Draw Cap (Integrated look)
        val capCornerRadius = 2.dp.toPx()
        drawRoundRect(
            color = COLOR_BODY_START, // Match top of the body gradient
            topLeft = androidx.compose.ui.geometry.Offset(
                x = (width - capWidth) / 2,
                y = startY
            ),
            size = androidx.compose.ui.geometry.Size(capWidth, capHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                capCornerRadius,
                capCornerRadius
            )
        )

        // 3. Subtle Inner Glow / Stroke to define edge without white contour
        // Using a stroke with a dark color slightly lighter than black background to define shape
        drawRoundRect(
            color = Color(0xFF2A2A2A),
            topLeft = androidx.compose.ui.geometry.Offset(bodyLeft, bodyTopFinal),
            size = androidx.compose.ui.geometry.Size(bodyWidth, bodyHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
        )

        // 4. Draw Fill Level (Dynamic Internal Fill)
        val padding = 3.dp.toPx()
        val fillMaxWidth = bodyWidth - padding * 2
        val fillMaxHeight = bodyHeight - padding * 2

        val currentFillHeight = (fillMaxHeight * (level / 100f)).coerceIn(0f, fillMaxHeight)

        if (currentFillHeight > 0) {
            val fillGradient = when {
                level <= BATTERY_LOW_THRESHOLD -> androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(COLOR_LOW_START, COLOR_LOW_END)
                )

                level <= BATTERY_MEDIUM_THRESHOLD -> androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(COLOR_MEDIUM_START, COLOR_MEDIUM_END)
                )

                else -> androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(COLOR_HIGH_START, COLOR_HIGH_END)
                )
            }

            // Calculate top position for fill (it fills from bottom)
            val fillTop = bodyTopFinal + padding + (fillMaxHeight - currentFillHeight)

            drawRoundRect(
                brush = fillGradient,
                topLeft = androidx.compose.ui.geometry.Offset(
                    x = bodyLeft + padding,
                    y = fillTop
                ),
                size = androidx.compose.ui.geometry.Size(fillMaxWidth, currentFillHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                    cornerRadius / 2,
                    cornerRadius / 2
                ) // Softer corners inside
            )
        }


        // 5. Charging Indicator (Vector Icon)
        if (isCharging) {
            val boltWidth = bodyWidth * 0.6f
            val boltHeight = bodyHeight * 0.6f

            val boltLeft = bodyLeft + (bodyWidth - boltWidth) / 2
            val boltTop = bodyTopFinal + (bodyHeight - boltHeight) / 2

            translate(left = boltLeft, top = boltTop) {
                with(boltPainter) {
                    draw(
                        size = androidx.compose.ui.geometry.Size(boltWidth, boltHeight),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                            Color.Black.copy(alpha = 0.75f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun NetworkSignalDisplay(
    modifier: Modifier = Modifier,
    iconSize: androidx.compose.ui.unit.Dp = 32.dp
) {
    val context = LocalContext.current
    var signalLevel by remember { mutableIntStateOf(0) }
    var hasPermission by remember { mutableStateOf(false) }

    // Check permissions
    LaunchedEffect(Unit) {
        hasPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    DisposableEffect(hasPermission) {
        if (hasPermission) {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                val callback =
                    object : TelephonyCallback(), TelephonyCallback.SignalStrengthsListener {
                        override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                            signalLevel = signalStrength.level // 0-4
                        }
                    }
                telephonyManager.registerTelephonyCallback(context.mainExecutor, callback)
                onDispose {
                    telephonyManager.unregisterTelephonyCallback(callback)
                }
            } else {
                @Suppress("DEPRECATION")
                val listener = object : android.telephony.PhoneStateListener() {
                    @Deprecated("Deprecated in Java")
                    override fun onSignalStrengthsChanged(signalStrength: SignalStrength?) {
                        super.onSignalStrengthsChanged(signalStrength)
                        signalLevel = signalStrength?.level ?: 0
                    }
                }
                @Suppress("DEPRECATION")
                telephonyManager.listen(
                    listener,
                    android.telephony.PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                )
                onDispose {
                    @Suppress("DEPRECATION")
                    telephonyManager.listen(
                        listener,
                        android.telephony.PhoneStateListener.LISTEN_NONE
                    )
                }
            }
        } else {
            onDispose { }
        }
    }

    SignalIcon(
        level = signalLevel,
        modifier = modifier.size(iconSize)
    )
}

@Composable
fun SignalIcon(
    level: Int,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Configuration
        val barCount = 4
        val spacingRatio = 0.15f // Space between bars relative to bar width

        // Calculate dimensions
        // width = 4 * barWidth + 3 * spacing
        // spacing = barWidth * spacingRatio
        // width = 4 * barWidth + 3 * barWidth * spacingRatio
        // width = barWidth * (4 + 3 * spacingRatio)

        val totalSpacingRatio = (barCount - 1) * spacingRatio
        val barWidth = width / (barCount + totalSpacingRatio)
        val spacing = barWidth * spacingRatio

        val cornerRadius = 4.dp.toPx()

        for (i in 0 until barCount) {
            // Calculate bar height: increasing steps
            // Let's make the first bar 40% height, last bar 100% height
            val heightRatio = 0.4f + (0.6f * i / (barCount - 1))
            val barHeight = height * heightRatio

            val barLeft = i * (barWidth + spacing)
            val barTop = height - barHeight

            // Determine if this bar is active
            val isActive = i < level

            // Unlit bars look like the battery body (dark grey gradient)
            // Lit bars look like the battery fill (colored gradient)
            val barBrush = if (isActive) {
                when {
                    level <= 1 -> androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(COLOR_LOW_START, COLOR_LOW_END),
                        startY = barTop,
                        endY = height
                    )

                    level == 2 -> androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(COLOR_MEDIUM_START, COLOR_MEDIUM_END),
                        startY = barTop,
                        endY = height
                    )

                    else -> androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(COLOR_HIGH_START, COLOR_HIGH_END),
                        startY = barTop,
                        endY = height
                    )
                }
            } else {
                androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(COLOR_BODY_START, COLOR_BODY_END),
                    start = androidx.compose.ui.geometry.Offset(barLeft, barTop),
                    end = androidx.compose.ui.geometry.Offset(barLeft + barWidth, height)
                )
            }

            // Draw Bar
            drawRoundRect(
                brush = barBrush,
                topLeft = androidx.compose.ui.geometry.Offset(barLeft, barTop),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
            )

            // Optional: Add subtle stroke for definition (like battery)
            drawRoundRect(
                color = Color(0xFF2A2A2A),
                topLeft = androidx.compose.ui.geometry.Offset(barLeft, barTop),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                    cornerRadius,
                    cornerRadius
                ),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
            )
        }
    }
}
