package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

import com.redbox.shimeji.live.shimejilife.system.shimeji.SpriteUtil

class WalkLeft(shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Walk(shimejiId, paidenable, flinging) {

    override fun getSprites(): MutableList<Sprite> {
        //val random = ThreadLocalRandom.current().nextInt(-2, -1)
        val sprites: MutableList<Sprite> = ArrayList(4)
        val spritesAkari: MutableList<Sprite> = ArrayList(4)
        val spritesAkariP: MutableList<Sprite> = ArrayList(4)

        when (shimejiId) {
            32 -> {
                spritesAkari.add(Sprite(SpriteUtil.WALKAKARI.values[0], -1, 0, 15))
                spritesAkari.add(Sprite(SpriteUtil.WALKAKARI.values[1], -1, 0, 15))
                spritesAkari.add(Sprite(SpriteUtil.WALKAKARI.values[2], -1, 0, 15))
                spritesAkari.add(Sprite(SpriteUtil.WALKAKARI.values[3], -1, 0, 15))
                return spritesAkari
            }
            116 -> {
                spritesAkariP.add(Sprite(SpriteUtil.WALKAKARIP.values[0], -1, 0, 15))
                spritesAkariP.add(Sprite(SpriteUtil.WALKAKARIP.values[1], -1, 0, 15))
                spritesAkariP.add(Sprite(SpriteUtil.WALKAKARIP.values[2], -1, 0, 15))
                spritesAkariP.add(Sprite(SpriteUtil.WALKAKARIP.values[3], -1, 0, 15))
                return spritesAkariP
            }
            else -> {
                if (classicmode){
                    sprites.add(Sprite(0, -2, 0, 6))
                    sprites.add(Sprite(1, -2, 0, 6))
                    sprites.add(Sprite(0, -2, 0, 6))
                    sprites.add(Sprite(2, -2, 0, 6))
                }else{
                    sprites.add(Sprite(0, -1, 0, 15))
                    sprites.add(Sprite(1, -1, 0, 15))
                    sprites.add(Sprite(0, -1, 0, 15))
                    sprites.add(Sprite(2, -1, 0, 15))
                }

                return sprites
            }
        }

    }

    override val nextAnimation: Animation
        get() = /*if (ThreadLocalRandom.current().nextBoolean()) ClimbLeft(shimejiId) else*/ WalkRight(shimejiId, paidenable = paidenable, flinging = flinging)

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