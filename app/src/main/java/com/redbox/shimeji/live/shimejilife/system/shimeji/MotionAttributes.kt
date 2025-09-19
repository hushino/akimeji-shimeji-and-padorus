package com.redbox.shimeji.live.shimejilife.system.shimeji

class MotionAttributes {
    private val INVALID_POINTER_ID = -1
    var mActivePointerId = -1
    var mLastTouchX = 0f
    var mLastTouchY = 0f

    //test
    var isOk = false
    fun invalidate() {
        mActivePointerId = -1
    }
}