package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

import com.redbox.shimeji.live.shimejilife.system.shimeji.SpriteUtil
import java.util.concurrent.ThreadLocalRandom

class WalkRight(shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Walk(shimejiId, paidenable, flinging) {

    override fun getSprites(): MutableList<Sprite> {
        //val random = ThreadLocalRandom.current().nextInt(1, 2)
        val sprites: MutableList<Sprite> = ArrayList(4)
        val spritesAkari: MutableList<Sprite> = ArrayList(4)
        val spritesAkariP: MutableList<Sprite> = ArrayList(4)

        return when(shimejiId){
            32->{
                spritesAkari.add(Sprite(SpriteUtil.WALKAKARI.values[0], 1, 0, 15))
                spritesAkari.add(Sprite(SpriteUtil.WALKAKARI.values[1], 1, 0, 15))
                spritesAkari.add(Sprite(SpriteUtil.WALKAKARI.values[2], 1, 0, 15))
                spritesAkari.add(Sprite(SpriteUtil.WALKAKARI.values[3], 1, 0, 15))
                spritesAkari
            }
            116->{
                spritesAkariP.add(Sprite(SpriteUtil.WALKAKARI.values[0], 1, 0, 15))
                spritesAkariP.add(Sprite(SpriteUtil.WALKAKARI.values[1], 1, 0, 15))
                spritesAkariP.add(Sprite(SpriteUtil.WALKAKARI.values[2], 1, 0, 15))
                spritesAkariP.add(Sprite(SpriteUtil.WALKAKARI.values[3], 1, 0, 15))
                spritesAkariP
            }
            else ->{
                if (classicmode){
                    sprites.add(Sprite(0, 2, 0, 6))
                    sprites.add(Sprite(1, 2, 0, 6))
                    sprites.add(Sprite(0, 2, 0, 6))
                    sprites.add(Sprite(2, 2, 0, 6))
                }else{
                    sprites.add(Sprite(0, 1, 0, 15))
                    sprites.add(Sprite(1, 1, 0, 15))
                    sprites.add(Sprite(0, 1, 0, 15))
                    sprites.add(Sprite(2, 1, 0, 15))
                }

                sprites
            }
        }
    }

    override val nextAnimation: Animation
        get() = if (ThreadLocalRandom.current().nextBoolean()) ClimbRight(shimejiId, paidenable = paidenable, flinging = flinging) else WalkLeft(shimejiId, paidenable = paidenable, flinging = flinging)

    override val direction: Direction
        get() = Direction.RIGHT

    override fun checkBorders(
        atTop: Boolean,
        atBottom: Boolean,
        atLeft: Boolean,
        atRight: Boolean
    ) {
        nextAnimationRequested = atRight
    }
}