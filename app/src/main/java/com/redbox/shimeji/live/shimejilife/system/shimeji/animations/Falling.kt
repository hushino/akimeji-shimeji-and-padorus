package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

import com.redbox.shimeji.live.shimejilife.system.shimeji.SpriteUtil
import java.util.concurrent.ThreadLocalRandom

class Falling(var direction2: Direction?, shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Animation(shimejiId, paidenable, flinging) {

    override var direction = if (ThreadLocalRandom.current().nextBoolean()) Direction.LEFT else Direction.RIGHT


    private fun randomm(): Int {
        val x = 1
        val xmenor = -1

        return when (ThreadLocalRandom.current().nextInt(0, 3)) {
            0 -> x
            1 -> xmenor
            else -> 0
        }
    }

    //sprites.shuffle()
    override fun getSprites(): MutableList<Sprite> {
        val sprites: MutableList<Sprite> = ArrayList(1)
        val spritesAkari: MutableList<Sprite> = ArrayList(1)
        val spritesAkariP: MutableList<Sprite> = ArrayList(2)

        return when (shimejiId) {
            116 -> {
                spritesAkariP.add(Sprite(SpriteUtil.FALLINGAKARIP.values[0], randomm(), ThreadLocalRandom.current().nextInt(2, 5), 60))
                spritesAkariP.add(Sprite(SpriteUtil.FALLINGAKARIP.values[1], randomm(), ThreadLocalRandom.current().nextInt(2, 5), 60))
                spritesAkariP
            }
            32 -> {
                spritesAkari.add(Sprite(SpriteUtil.FALLINGAKARI.values[0], randomm(), ThreadLocalRandom.current().nextInt(2, 5), 60))
                spritesAkari
            }
            else -> {
                if (classicmode) {
                    sprites.add(Sprite(3, 0, 15, 250))
                } else {
                    sprites.add(Sprite(3, randomm(), ThreadLocalRandom.current().nextInt(2, 5), 60))
                }
                sprites
            }
        }

    }

    override fun getOptionalAnimation(): Animation? {
        return null
    }

    private fun nextanima(): Direction? {
        return if (direction2 != null) {
            direction2
        } else {
            direction
        }
    }

    override val nextAnimation: Animation
        get() = Bounce(nextanima()!!, shimejiId, paidenable = paidEnabled, flinging = flinging)

    override val isOneShot: Boolean
        get() = false

    override fun checkBorders(
        atTop: Boolean,
        atBottom: Boolean,
        atLeft: Boolean,
        atRight: Boolean
    ) {
        nextAnimationRequested = atBottom
    }
}