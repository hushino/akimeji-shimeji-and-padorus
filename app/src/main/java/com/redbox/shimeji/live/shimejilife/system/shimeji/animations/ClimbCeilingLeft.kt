package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

import com.redbox.shimeji.live.shimejilife.system.shimeji.SpriteUtil
import java.util.concurrent.ThreadLocalRandom

class ClimbCeilingLeft(shimejiId: Int, paidenable: Boolean, flinging: Boolean) : ClimbCeiling(shimejiId, paidenable, flinging) {

    override fun getSprites(): MutableList<Sprite> {

        val sprites: MutableList<Sprite> = ArrayList(8)
        val spritesAkari: MutableList<Sprite> = ArrayList(8)
        val spritesAkariP: MutableList<Sprite> = ArrayList(8)

        return when (shimejiId) {
            32 -> {
                spritesAkari.add(Sprite(SpriteUtil.CLIMB_CEILINGAKARI.values[0], 0, 0, 20))
                spritesAkari.add(Sprite(SpriteUtil.CLIMB_CEILINGAKARI.values[1], -1, 0, 15))
                spritesAkari.add(Sprite(SpriteUtil.CLIMB_CEILINGAKARI.values[2], -1, 0, 10))
                spritesAkari
            }
            116 -> {
                spritesAkariP.add(Sprite(SpriteUtil.CLIMB_CEILINGAKARIP.values[0], 0, 0, 20))
                spritesAkariP.add(Sprite(SpriteUtil.CLIMB_CEILINGAKARIP.values[1], -1, 0, 15))
                spritesAkariP
            }
            else -> {
                if (classicmode) {
                    sprites.add(Sprite(24, 0, 0, 16))
                    sprites.add(Sprite(24, -1, 0, 4))
                    sprites.add(Sprite(22, -1, 0, 4))
                    sprites.add(Sprite(23, -1, 0, 4))
                    sprites.add(Sprite(23, 0, 0, 16))
                    sprites.add(Sprite(23, -2, 0, 4))
                    sprites.add(Sprite(22, -2, 0, 4))
                    sprites.add(Sprite(24, -2, 0, 4))
                } else {
                    sprites.add(Sprite(24, 0, 0, 20))
                    sprites.add(Sprite(24, -1, 0, 15))
                    sprites.add(Sprite(22, -1, 0, 10))
                    sprites.add(Sprite(23, -1, 0, 15))
                }
                sprites

            }
        }
    }

    override val nextAnimation: Animation
        get() = if (classicmode) {
            if (ThreadLocalRandom.current().nextBoolean()) DescendLeft(shimejiId, paidenable = paidenable, flinging = flinging) else ClimbCeilingRight(
                shimejiId,
                paidenable = paidenable,
                flinging = flinging
            )
        } else {
            when (ThreadLocalRandom.current().nextInt(1, 5)) {
                1 -> DescendLeft(shimejiId, paidenable = paidenable, flinging = flinging)
                2 -> ClimbCeilingRight(shimejiId, paidenable = paidenable, flinging = flinging)
                3 -> ClimbCeilingRight(shimejiId, paidenable = paidenable, flinging = flinging)
                else -> Falling(direction, shimejiId, paidenable = paidenable, flinging = flinging)
            }
        }

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