package com.redbox.shimeji.live.shimejilife.system.shimeji

import android.content.Context

class Playground {

    var bottom = 0
    var left: Int
    var right: Int
    var top: Int

    constructor(top: Int, bottom: Int, left: Int, right: Int) {
        this.top = top
        this.bottom = bottom
        this.left = left
        this.right = right
    }

    constructor(context: Context, isInsideLiveWallpaper: Boolean) {
        val screen = context.resources.displayMetrics
        if (isInsideLiveWallpaper) {
            bottom = screen.heightPixels - dpToPx(128.0f, screen.density)
        } else {
            var statusBarHeight = 75
            val statusId =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (statusId > 0) {
                statusBarHeight = context.resources.getDimensionPixelSize(statusId)
            }
            bottom = screen.heightPixels - statusBarHeight
            //Toast.makeText(context,""+this.bottom,Toast.LENGTH_LONG).show();//1848, sin statusbar 1920
        }
        right = screen.widthPixels
        top = 0
        left = 0
    }

    private fun dpToPx(dp: Float, screenDensity: Float): Int {
        return (screenDensity * dp + 0.5f).toInt()
    }
}