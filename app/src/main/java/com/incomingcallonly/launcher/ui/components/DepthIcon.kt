package com.incomingcallonly.launcher.ui.components

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun DepthIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    shadowColor: Color = Color.Black.copy(alpha = 0.15f),
    shadowOffset: Offset = Offset(4f, 4f)
) {
    DepthIcon(
        painter = rememberVectorPainter(imageVector),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
        shadowColor = shadowColor,
        shadowOffset = shadowOffset
    )
}

@Composable
fun DepthIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    shadowColor: Color = Color.Black.copy(alpha = 0.15f),
    shadowOffset: Offset = Offset(4f, 4f)
) {
    // Default size is 24.dp if not specified in modifier
    Box(
        modifier = Modifier
            .defaultMinSize(24.dp, 24.dp)
            .then(modifier)
            .semantics(
                properties = {
                    if (contentDescription != null) {
                        this.contentDescription = contentDescription
                    }
                }
            )
            .drawWithCache {
                val width = size.width.toInt().coerceAtLeast(1)
                val height = size.height.toInt().coerceAtLeast(1)
                val bitmap = ImageBitmap(width, height, ImageBitmapConfig.Argb8888)
                val canvas = Canvas(bitmap)
                val canvasDrawScope = CanvasDrawScope()

                // Draw the icon into the bitmap, tinted with shadow color
                canvasDrawScope.draw(this, layoutDirection, canvas, size) {
                    with(painter) {
                        draw(size, colorFilter = ColorFilter.tint(shadowColor))
                    }
                }

                val blurRadius = 8.dp.toPx()
                val paint = Paint().asFrameworkPaint().apply {
                    maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
                }

                onDrawBehind {
                    // Draw Shadow
                    drawIntoCanvas { canvas ->
                        canvas.save()
                        canvas.translate(shadowOffset.x, shadowOffset.y)
                        canvas.nativeCanvas.drawBitmap(bitmap.asAndroidBitmap(), 0f, 0f, paint)
                        canvas.restore()
                    }

                    // Draw Foreground
                    with(painter) {
                        draw(size, colorFilter = ColorFilter.tint(tint))
                    }
                }
            }
    )
}
