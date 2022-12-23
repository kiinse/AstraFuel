package kinse.android.astrafuel.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

object CanvasUtils {

    fun drawMark(size: Int, color: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val paint = Paint()
        paint.color = color
        paint.style = Paint.Style.FILL
        Canvas(bitmap).drawCircle(size / 2F, size / 2F, size / 2F, paint)
        return bitmap
    }

    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
        var drawable = ContextCompat.getDrawable(context, drawableId) ?: return null
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable).mutate()
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888) ?: return null
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}