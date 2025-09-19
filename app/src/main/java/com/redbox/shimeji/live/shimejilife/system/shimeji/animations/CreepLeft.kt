package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

import java.util.concurrent.ThreadLocalRandom


internal class CreepLeft(shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Creep(shimejiId, paidenable, flinging) {

    override fun getSprites(): MutableList<Sprite>? {

        val sprites: MutableList<Sprite> = ArrayList(5)
        val random = ThreadLocalRandom.current().nextInt(-3, -2)

        return when (shimejiId) {
            32 -> {
                null
            }
            116 -> {
                null
            }
            else -> {
                if (classicmode){
                    sprites.add(Sprite(19, 0, 0, 28))
                    sprites.add(Sprite(19, -2, 0, 4))
                    sprites.add(Sprite(20, -2, 0, 4))
                    sprites.add(Sprite(20, -1, 0, 4))
                    sprites.add(Sprite(20, 0, 0, 24))
                }else{
                    sprites.add(Sprite(19, 0, 0, 12))
                    sprites.add(Sprite(19, random, 0, 12))
                    sprites.add(Sprite(20, random, 0, 12))
                    sprites.add(Sprite(20, random, 0, 12))
                    sprites.add(Sprite(20, 0, 0, 12))
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