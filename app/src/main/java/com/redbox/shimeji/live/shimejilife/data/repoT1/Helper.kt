package com.redbox.shimeji.live.shimejilife.data.repoT1

import android.content.Context
import android.content.Context.MODE_MULTI_PROCESS
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.redbox.shimeji.live.shimejilife.R
import com.redbox.shimeji.live.shimejilife.common.constants.AppConstants
import com.redbox.shimeji.live.shimejilife.system.shimeji.Sprites
import org.json.JSONArray
import org.json.JSONException
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.collections.*

class Helper(val context: Context) {

    fun getReappearDelayMs(context: Context): Int {
        return (context.getSharedPreferences(AppConstants.MY_PREFS, MODE_MULTI_PROCESS)
            .getString(AppConstants.REAPPEAR_DELAY, AppConstants.DEFAULT_REAPPEAR_DELAY_MINUTES)
            ?.toInt() ?: 1) * 1000 * 60
    }

    fun getSpeedMultiplier(context: Context): Double {
        return java.lang.Double.parseDouble(
            context.getSharedPreferences(AppConstants.MY_PREFS, MODE_MULTI_PROCESS)
                .getString(AppConstants.ANIMATION_SPEED, AppConstants.DEFAULT_SIZE_MULTIPLIER)!!
        )
    }

    fun getNotificationVisibility(context: Context): Boolean {
        return context.getSharedPreferences(AppConstants.MY_PREFS, MODE_MULTI_PROCESS)
            .getBoolean(AppConstants.SHOW_NOTIFICATION, AppConstants.DEFAULT_SHOW_NOTIFICATION)
    }

    fun getSizeMultiplier(context: Context): Double {
        return java.lang.Double.parseDouble(
            context.getSharedPreferences
                (AppConstants.MY_PREFS, MODE_MULTI_PROCESS)
                .getString(AppConstants.SIZE_MULTIPLIER, "2.0")!!
        )
    }


    fun saveActiveTeamMembers(list: List<Int>) {
        val prefs = context.getSharedPreferences(AppConstants.MY_PREFS, MODE_MULTI_PROCESS)
        val edit = prefs.edit()
        val str = StringBuilder()
        for (id in list) {
            str.append(id).append(",")
        }
        val value = str.toString()
        edit.putString(AppConstants.ACTIVE_SHIMEJI_IDS, value)
        val committed = edit.commit()
        Timber.e("saveActiveTeamMembers committed=%s value='%s'", committed, value)
    }

    fun getActiveTeamMembers(): List<Int> {
        val savedList = ArrayList<Int>(10)
        val raw = context.getSharedPreferences(AppConstants.MY_PREFS, MODE_MULTI_PROCESS)
            .getString(AppConstants.ACTIVE_SHIMEJI_IDS, "") ?: ""
        val tokens = raw.split(",").filter { it.isNotEmpty() }
        for (token in tokens) {
            try {
                savedList.add(token.trim().toInt())
            } catch (_: Exception) {
            }
        }
        Timber.e("getActiveTeamMembers raw='%s' parsed=%s", raw, savedList)
        return savedList
    }

    fun notifyBackgroundChanged() {
        val preferences = context.getSharedPreferences(AppConstants.MY_PREFS, MODE_MULTI_PROCESS)
        val prefEditor = preferences.edit()
        prefEditor.putInt(
            context.getString(R.string.UPDATE_EVENT_TOKEN),
            preferences.getInt(context.getString(R.string.UPDATE_EVENT_TOKEN), 0) + 1
        )
        prefEditor.apply()
    }

    fun wasCustomBackgroundSet(): Boolean {
        return context.getSharedPreferences(AppConstants.MY_PREFS, MODE_PRIVATE)
            .getInt(context.getString(R.string.UPDATE_EVENT_TOKEN), 0) != 0
    }

    private fun getResizedBitmap(bm: Bitmap, sizeMultiplier: Float): Bitmap {
        val width = bm.width
        val height = bm.height
        val matrix = Matrix()
        matrix.postScale(sizeMultiplier, sizeMultiplier)
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width.toFloat()
        val scaleHeight = newHeight.toFloat() / height.toFloat()
        val matrix = Matrix()
        if (scaleWidth > scaleHeight) {
            matrix.postScale(scaleWidth, scaleWidth)
        } else {
            matrix.postScale(scaleHeight, scaleHeight)
        }
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
    }

    internal fun resizeSprites(sprites: Sprites, multiplier: Double): Sprites {
        for (key in sprites.keys) {
            sprites[key] = getResizedBitmap(sprites[key]!!, multiplier.toFloat())
        }
        return sprites
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }


    internal fun bitmapToByteArray(b: Bitmap): ByteArray? {

        val output: ByteArrayOutputStream
        try {
            output = ByteArrayOutputStream()
            //quality no work for .png
            b.compress(CompressFormat.PNG, 90, output)
        } catch (e: Exception) {
            return byteArrayOf()
        }
        return output.toByteArray()
    }

    fun byteArrayToBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeStream(ByteArrayInputStream(bytes))
    }
}
