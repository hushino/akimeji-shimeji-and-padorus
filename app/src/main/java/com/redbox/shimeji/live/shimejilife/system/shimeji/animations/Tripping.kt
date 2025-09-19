package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

internal abstract class Tripping(shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Animation(shimejiId, paidenable, flinging) {
    override val isOneShot: Boolean
        get() = true

    override fun checkBorders(
        atTop: Boolean,
        atBottom: Boolean,
        atLeft: Boolean,
        atRight: Boolean
    ) {
    }
}