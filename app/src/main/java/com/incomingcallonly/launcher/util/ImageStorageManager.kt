package com.incomingcallonly.launcher.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.graphics.scale
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

@Singleton
class ImageStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val photosDir = File(context.filesDir, "contact_photos")

    init {
        if (!photosDir.exists()) {
            photosDir.mkdirs()
        }
    }

    fun saveImageLocally(uri: Uri): String? {
        return try {
            val fileName = "photo_${System.currentTimeMillis()}.jpg"
            val destFile = File(photosDir, fileName)

            // 1. Get orientation
            val orientation = context.contentResolver.openInputStream(uri)?.use { input ->
                val exif = ExifInterface(input)
                exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL

            context.contentResolver.openInputStream(uri)?.use { input ->
                val loadedBitmap = BitmapFactory.decodeStream(input) ?: return null

                // 2. Rotate if needed
                val rotatedBitmap = rotateBitmap(loadedBitmap, orientation)

                // 3. Resize
                val maxDimension = 512
                val ratio = min(
                    maxDimension.toFloat() / rotatedBitmap.width,
                    maxDimension.toFloat() / rotatedBitmap.height
                )
                val width = (rotatedBitmap.width * ratio).toInt()
                val height = (rotatedBitmap.height * ratio).toInt()

                val finalBitmap = rotatedBitmap.scale(width, height, true)

                // 4. Save
                FileOutputStream(destFile).use { output ->
                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
                }

                // Cleanup
                if (loadedBitmap != rotatedBitmap) loadedBitmap.recycle()
                if (rotatedBitmap != finalBitmap) rotatedBitmap.recycle()
                finalBitmap.recycle()
            }
            Uri.fromFile(destFile).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.postRotate(90f)
                matrix.postScale(-1f, 1f)
            }

            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.postRotate(270f)
                matrix.postScale(-1f, 1f)
            }

            else -> return bitmap
        }
        return try {
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            bitmap
        }
    }

    fun deleteImage(uriString: String?) {
        if (uriString == null) return
        try {
            val uri = uriString.toUri()
            if (uri.scheme == "file") {
                val file = File(uri.path ?: return)
                if (file.exists() && file.parentFile?.name == "contact_photos") {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
