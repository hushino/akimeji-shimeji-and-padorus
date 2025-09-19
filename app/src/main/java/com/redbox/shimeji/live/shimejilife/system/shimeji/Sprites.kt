package com.redbox.shimeji.live.shimejilife.system.shimeji

import android.graphics.Bitmap


class Sprites @Throws(IllegalArgumentException::class)
constructor(bitmaps: HashMap<Int, Bitmap>) : HashMap<Int,Bitmap?>() {


    init {
        if (bitmaps == null || bitmaps.isEmpty()) {
            throw IllegalArgumentException()
        }
        putAll(bitmaps)
    }

    fun recycle() {
        for (key in keys) {
            (get(key) as Bitmap).recycle()
            put(key, null)
        }
    }

    val width: Int
        get() {
            return try {
                if (size > 0) {
                    (get(0) as Bitmap).width
                } else {
                    500
                }
            } catch (e: NullPointerException) {
                500
            }

        }

    val height: Int
        get() {
            return try {
                if (size > 0) {
                    (get(0) as Bitmap).height
                } else {
                    500
                }

            } catch (e: NullPointerException) {
                500
            }

        }

    val xOffset: Int
        get() = width / 3


}
