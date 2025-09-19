package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

import com.redbox.shimeji.live.shimejilife.system.shimeji.SpriteUtil
import java.util.concurrent.ThreadLocalRandom


class JumpLeft(shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Jump(shimejiId, paidenable, flinging) {

    override fun getSprites(): MutableList<Sprite> {
        val random = ThreadLocalRandom.current().nextInt(-6, -3)

        val sprites: MutableList<Sprite> = ArrayList(1)
        val spritesAkari: MutableList<Sprite> = ArrayList(1)
        val spritesAkariP: MutableList<Sprite> = ArrayList(1)

        return when (shimejiId) {
            32 -> {
                spritesAkari.add(Sprite(SpriteUtil.JUMPAKARI.values[0], random, -2, 60))
                spritesAkari
            }
            116 -> {
                spritesAkariP.add(Sprite(SpriteUtil.JUMPAKARIP.values[0], random, -2, 60))
                spritesAkariP
            }
            else -> {
                if (classicmode) {
                    sprites.add(Sprite(21, -10, -1, 2))
                } else {
                    sprites.add(Sprite(21, random, -2, 60))
                }
                sprites
            }
        }
    }

    override fun getOptionalAnimation(): Animation? {
        return null
    }

    override val nextAnimation: Animation
        get() = ClimbLeft(shimejiId, paidenable = paidenable, flinging = flinging)

    override val direction: Direction
        get() = Direction.LEFT

    override fun checkBorders(
        atTop: Boolean,
        atBottom: Boolean,
        atLeft: Boolean,
        atRight: Boolean
    ) {
        nextAnimationRequested = atLeft
    }
}