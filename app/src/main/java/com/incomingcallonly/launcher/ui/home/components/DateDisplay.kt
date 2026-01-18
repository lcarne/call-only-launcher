package com.incomingcallonly.launcher.ui.home.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.incomingcallonly.launcher.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val CLOCK_DATE_FORMAT = "EEEE d MMMM yyyy"
private const val ADMIN_TAP_THRESHOLD = 15
private const val ADMIN_TAP_TIMEOUT_MS = 1000L

@Composable
fun DateDisplay(
    currentTime: Date,
    onAdminClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var adminTapCount by remember { androidx.compose.runtime.mutableIntStateOf(0) }
    var lastTapTime by remember { androidx.compose.runtime.mutableLongStateOf(0L) }

    val dateFormat = remember { SimpleDateFormat(CLOCK_DATE_FORMAT, Locale.getDefault()) }

    val rawDate = dateFormat.format(currentTime)
    val capitalizedDate = rawDate.split(" ").joinToString(" ") {
        if (it.firstOrNull()?.isLetter() == true)
            it.replaceFirstChar { char -> char.uppercase() }
        else it
    }

    Text(
        text = capitalizedDate,
        style = MaterialTheme.typography.displayMedium.copy(
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        ),
        color = White,
        textAlign = TextAlign.Center,
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    val now = System.currentTimeMillis()
                    if (now - lastTapTime < ADMIN_TAP_TIMEOUT_MS) {
                        adminTapCount++
                    } else {
                        adminTapCount = 1
                    }
                    lastTapTime = now

                    if (adminTapCount >= ADMIN_TAP_THRESHOLD) {
                        adminTapCount = 0
                        onAdminClick()
                    }
                }
            )
        }
    )
}
