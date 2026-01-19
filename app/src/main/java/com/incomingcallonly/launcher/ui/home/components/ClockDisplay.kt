package com.incomingcallonly.launcher.ui.home.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClockDisplay(
    currentTime: Date,
    timeFormat: String,
    clockColor: Color,
    modifier: Modifier = Modifier
) {
    val is24Hour = timeFormat == "24"
    val timePattern = if (is24Hour) "HH:mm" else "hh:mm"
    val timeFormatObj = SimpleDateFormat(timePattern, Locale.getDefault())
    val formattedTime = timeFormatObj.format(currentTime)

    var fontSize by remember { mutableStateOf(120.sp) }
    var readyToDraw by remember(formattedTime, timeFormat) { mutableStateOf(false) }

    // Reset font size when time or format changes to try to fit at max size again
    remember(formattedTime, timeFormat) {
        fontSize = 120.sp
        readyToDraw = false
        true
    }

    val annotatedTime = buildAnnotatedString {
        append(formattedTime)
        if (!is24Hour) {
            val periodFormatObj = SimpleDateFormat("a", Locale.getDefault())
            val period = periodFormatObj.format(currentTime)
            withStyle(style = SpanStyle(fontSize = (fontSize.value * 0.33f).sp)) {
                append(" $period")
            }
        }
    }

    Text(
        text = annotatedTime,
        modifier = modifier
            .fillMaxWidth()
            .drawWithContent {
                if (readyToDraw) {
                    drawContent()
                }
            },
        style = MaterialTheme.typography.displayLarge.copy(
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp
        ),
        color = clockColor,
        maxLines = 1,
        softWrap = false,
        textAlign = TextAlign.Center,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                fontSize = (fontSize.value * 0.9f).sp
            } else {
                readyToDraw = true
            }
        }
    )
}
