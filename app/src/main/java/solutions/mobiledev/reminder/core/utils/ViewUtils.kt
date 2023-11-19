package solutions.mobiledev.reminder.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import solutions.mobiledev.reminder.R

object ViewUtils {
    fun createIcon(context: Context, @DrawableRes res: Int, @ColorInt color: Int): Bitmap? {
        var icon = ContextCompat.getDrawable(context, res)
        if (icon != null) {
            icon = (DrawableCompat.wrap(icon)).mutate()
            DrawableCompat.setTint(icon, color)
            DrawableCompat.setTintMode(icon, PorterDuff.Mode.SRC_IN)
            val bitmap = Bitmap.createBitmap(icon.intrinsicWidth, icon.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            icon.setBounds(0, 0, canvas.width, canvas.height)
            icon.draw(canvas)
            return bitmap
        }
        return null
    }

    fun tintIcon(context: Context, @DrawableRes resource: Int, isDark: Boolean): Drawable? {
        var icon = ContextCompat.getDrawable(context, resource)
        if (icon != null) {
            icon = (DrawableCompat.wrap(icon)).mutate()
            val color = if (isDark) {
                ContextCompat.getColor(context, R.color.pureWhite)
            } else {
                ContextCompat.getColor(context, R.color.pureBlack)
            }
            DrawableCompat.setTint(icon, color)
            DrawableCompat.setTintMode(icon, PorterDuff.Mode.SRC_IN)
            return icon
        }
        return null
    }

}