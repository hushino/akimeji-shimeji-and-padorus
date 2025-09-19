package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

abstract class Jump(shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Animation(shimejiId, paidenable, flinging) {
    override val isOneShot: Boolean
        get() = false
}