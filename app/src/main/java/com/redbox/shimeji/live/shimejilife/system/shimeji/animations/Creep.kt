package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

internal abstract class Creep(shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Animation(shimejiId, paidenable, flinging) {
    override val isOneShot: Boolean
        get() = false
}