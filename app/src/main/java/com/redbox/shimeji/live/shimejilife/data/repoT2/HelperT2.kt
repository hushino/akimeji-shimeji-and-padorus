package com.redbox.shimeji.live.shimejilife.data.repoT2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.redbox.shimeji.live.shimejilife.R
import com.redbox.shimeji.live.shimejilife.common.constants.AkimejiT2Constants
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.AkimejiListing
import com.redbox.shimeji.live.shimejilife.system.akimeji.Sprites
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class HelperT2(val context: Context) {

    fun getReappearDelayMs(context: Context): Int {
        return (context.getSharedPreferences(AkimejiT2Constants.MY_PREFS, Context.MODE_MULTI_PROCESS)
            .getString(AkimejiT2Constants.REAPPEAR_DELAY, AkimejiT2Constants.DEFAULT_REAPPEAR_DELAY_MINUTES)
            ?.toInt() ?: 1) * 1000 * 60
    }

    fun getSpeedMultiplier(context: Context): Double {
        return java.lang.Double.parseDouble(
            context.getSharedPreferences(AkimejiT2Constants.MY_PREFS, Context.MODE_MULTI_PROCESS)
                .getString(AkimejiT2Constants.ANIMATION_SPEED, AkimejiT2Constants.DEFAULT_SIZE_MULTIPLIER)!!
        )
    }

    fun getNotificationVisibility(context: Context): Boolean {
        return context.getSharedPreferences(AkimejiT2Constants.MY_PREFS, Context.MODE_MULTI_PROCESS)
            .getBoolean(AkimejiT2Constants.SHOW_NOTIFICATION, AkimejiT2Constants.DEFAULT_SHOW_NOTIFICATION)
    }

    fun getSizeMultiplier(context: Context): Double {
        return java.lang.Double.parseDouble(
            context.getSharedPreferences
                (AkimejiT2Constants.MY_PREFS, Context.MODE_MULTI_PROCESS)
                .getString(AkimejiT2Constants.SIZE_MULTIPLIER, "0.8")!!
        )
    }


    fun saveActiveTeamMembers(list: List<Int>) {
        val edit = context.getSharedPreferences(AkimejiT2Constants.MY_PREFS, Context.MODE_MULTI_PROCESS).edit()
        val str = StringBuilder()
        for (id in list) {
            str.append(id).append(",")
        }
        edit.putString(AkimejiT2Constants.ACTIVE_SHIMEJI_IDS, str.toString())
        edit.apply()
    }

    fun getActiveTeamMembers(): List<Int> {
        val savedList = ArrayList<Int>(10)
        val ids = context.getSharedPreferences(AkimejiT2Constants.MY_PREFS, Context.MODE_MULTI_PROCESS)
            .getString(AkimejiT2Constants.ACTIVE_SHIMEJI_IDS, "")!!.split(",".toRegex())
            .dropLastWhile { it.isEmpty() }.toTypedArray()
        if (!(ids.size == 1 && ids[0] == "")) {
            for (str in ids) {
                savedList.add(Integer.parseInt(str))
            }
        }
        return savedList
    }

    fun notifyBackgroundChanged() {
        val preferences =
            context.getSharedPreferences(AkimejiT2Constants.MY_PREFS, Context.MODE_PRIVATE)
        val prefEditor = preferences.edit()
        prefEditor.putInt(
            context.getString(R.string.UPDATE_EVENT_TOKEN),
            preferences.getInt(context.getString(R.string.UPDATE_EVENT_TOKEN), 0) + 1
        )
        prefEditor.apply()
    }

    fun wasCustomBackgroundSet(): Boolean {
        return context.getSharedPreferences(
            AkimejiT2Constants.MY_PREFS,
            Context.MODE_PRIVATE
        )
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
            b.compress(Bitmap.CompressFormat.PNG, 90, output)
        } catch (e: Exception) {
            return byteArrayOf()
        }
        return output.toByteArray()
    }

    fun byteArrayToBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeStream(ByteArrayInputStream(bytes))
    }

    @Throws(JSONException::class)
    fun parseJSON(jsonString: String): List<AkimejiListing> {
        val thumbs = ArrayList<AkimejiListing>()
        //Log.e("JSON NNNNNNNNNNNNN ", jsonString.toString())
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val m = jsonArray.getJSONObject(i)
            val thumb =
                AkimejiListing()
            thumb.id = m.getInt("id")
            thumb.name = m.getString("name")
            thumbs.add(thumb)
        }
        return thumbs
    }
}