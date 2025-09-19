package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

import java.util.concurrent.ThreadLocalRandom


internal class TrippingRight(shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Tripping(shimejiId, paidenable, flinging) {

    override fun getSprites(): MutableList<Sprite>? {
        val sprites: MutableList<Sprite> = ArrayList(5)
        val random2 = ThreadLocalRandom.current().nextInt(2, 4)

        return when (shimejiId) {
            32 -> {
                null
            }
            116 -> {
                null
            }
            else -> {
                if (classicmode) {
                    sprites.add(Sprite(18, 8, 0, 8))
                    sprites.add(Sprite(17, 4, 0, 4))
                    sprites.add(Sprite(19, 2, 0, 4))
                    sprites.add(Sprite(19, 0, 0, 10))
                    sprites.add(Sprite(18, 4, 0, 4))
                } else {
                    sprites.add(Sprite(18, random2, 0, 8))
                    sprites.add(Sprite(17, random2, 0, 4))
                    sprites.add(Sprite(19, random2, 0, 4))
                    sprites.add(Sprite(19, 0, 0, 10))
                    sprites.add(Sprite(18, random2, 0, 4))
                }

                sprites
            }
        }
    }

    override fun getOptionalAnimation(): Animation? {
        return null
    }

    override val nextAnimation: Animation
        get() = WalkRight(shimejiId, paidenable = paidenable, flinging = flinging)

    override val direction: Direction
        get() = Direction.RIGHT
}