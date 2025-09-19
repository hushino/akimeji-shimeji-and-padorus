package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

import com.redbox.shimeji.live.shimejilife.system.shimeji.SpriteUtil
import java.util.concurrent.ThreadLocalRandom

class Dragging(shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Animation(shimejiId, paidenable, flinging) {

    override val direction: Direction =
        if (ThreadLocalRandom.current().nextBoolean()) Direction.LEFT else Direction.RIGHT

    fun drop(): Animation {
        return nextAnimation
    }

    //DRAGGING(intArrayOf(4, 5, 6, 7, 8, 9))
    override fun getSprites(): MutableList<Sprite> {

        val sprites: MutableList<Sprite> = ArrayList(6)
        val spritesAkari: MutableList<Sprite> = ArrayList(6)
        val spritesAkariP: MutableList<Sprite> = ArrayList(6)

        return when (shimejiId) {
            32 -> {
                spritesAkari.add(Sprite(SpriteUtil.DRAGGINGAKARI.values[0], 0, 0, 10))
                spritesAkari.add(Sprite(SpriteUtil.DRAGGINGAKARI.values[1], 0, 0, 10))
                spritesAkari.add(Sprite(SpriteUtil.DRAGGINGAKARI.values[2], 0, 0, 10))
                spritesAkari.add(Sprite(SpriteUtil.DRAGGINGAKARI.values[3], 0, 0, 10))
                spritesAkari
            }
            116 -> {
                spritesAkariP.add(Sprite(SpriteUtil.DRAGGINGAKARIP.values[0], 0, 0, 10))
                spritesAkariP.add(Sprite(SpriteUtil.DRAGGINGAKARIP.values[1], 0, 0, 10))
                spritesAkariP
            }
            else -> {
                if (classicmode) {
                    sprites.add(Sprite(6, 0, 0, 8))
                    sprites.add(Sprite(4, 0, 0, 8))
                    sprites.add(Sprite(5, 0, 0, 8))
                    sprites.add(Sprite(7, 0, 0, 8))
                    sprites.add(Sprite(5, 0, 0, 8))
                    sprites.add(Sprite(4, 0, 0, 8))
                } else {
                    sprites.add(Sprite(4, 0, 0, 10))
                    sprites.add(Sprite(6, 0, 0, 10))
                    sprites.add(Sprite(8, 0, 0, 10))
                    sprites.add(Sprite(6, 0, 0, 10))
                    sprites.add(Sprite(7, 0, 0, 10))
                    sprites.add(Sprite(9, 0, 0, 10))
                }
                sprites
            }
        }
    }

    override fun getOptionalAnimation(): Animation? {
        return null
    }

    override val nextAnimation: Animation
        get() = Falling(direction, shimejiId, paidenable = paidenable, flinging = flinging)

    override val isOneShot: Boolean
        get() = false

    override fun checkBorders(
        atTop: Boolean,
        atBottom: Boolean,
        atLeft: Boolean,
        atRight: Boolean
    ) {
    }

}