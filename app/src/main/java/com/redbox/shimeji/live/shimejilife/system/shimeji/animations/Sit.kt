package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

import com.redbox.shimeji.live.shimejilife.system.shimeji.SpriteUtil


class Sit internal constructor(override val direction: Direction, shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Animation(shimejiId, paidenable,
    flinging
) {

    // if paid enable y talk enable traer el index 46
    // el index es el numero en la columna frame
    override fun getSprites(): MutableList<Sprite>? {
        val sprites: MutableList<Sprite> = ArrayList(1)
        return when (shimejiId) {
            32 -> {
               null
            }
            116 -> {
                null
            }
            else -> {
                sprites.add(Sprite(10, 0, 0, 250)) // original duration 250,index 10
                sprites
            }
        }
    }

    override fun getOptionalAnimation(): Animation? {
        return null
    }

    fun drop(): Animation {
        return nextAnimation
    }

    override val nextAnimation: Animation
        get() = if (direction === Direction.LEFT) WalkLeft(shimejiId, paidenable = paidenable, flinging = flinging) else WalkRight(shimejiId, paidenable = paidenable, flinging = flinging)

    override fun checkBorders(
        atTop: Boolean,
        atBottom: Boolean,
        atLeft: Boolean,
        atRight: Boolean
    ) {
    }

    override val isOneShot: Boolean
        get() = true

}