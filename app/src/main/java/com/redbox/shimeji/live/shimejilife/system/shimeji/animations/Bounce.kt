package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

import com.redbox.shimeji.live.shimejilife.system.shimeji.SpriteUtil
import java.util.concurrent.ThreadLocalRandom

internal class Bounce(direction: Direction, shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Animation(shimejiId, paidenable, flinging) {

    override var direction = Direction.RIGHT

    override fun getSprites(): MutableList<Sprite> {
        val sprites: MutableList<Sprite> = ArrayList(2)
        val spritesAkari: MutableList<Sprite> = ArrayList(2)
        val spritesAkariP: MutableList<Sprite> = ArrayList(2)
        return when (shimejiId){
            32-> {
                spritesAkari.add(Sprite(SpriteUtil.BOUNCEAKARI.values[0], 0, 0, 30))
                spritesAkari
            }
            116-> {
                spritesAkariP.add(Sprite(SpriteUtil.BOUNCEAKARIP.values[0], 0, 0, 30))
                spritesAkariP
            }
            else->{
                if (Animation.classicmode){
                    sprites.add(Sprite(17, 0, 0, 8))
                    sprites.add(Sprite(18, 0, 0, 8))
                }else{
                    sprites.add(Sprite(17, 0, 0, 30))
                    sprites.add(Sprite(18, 0, 0, 30))
                }
                sprites
            }
        }

    }

    override fun getOptionalAnimation(): Animation? {
        return null
    }

    override val nextAnimation: Animation
        get() = if (ThreadLocalRandom.current().nextBoolean()) WalkLeft(shimejiId, paidenable = paidenable, flinging = flinging) else WalkRight(shimejiId, paidenable = paidenable, flinging = flinging)

    override val isOneShot: Boolean
        get() = true

    override fun checkBorders(
        atTop: Boolean,
        atBottom: Boolean,
        atLeft: Boolean,
        atRight: Boolean
    ) {
    }

    init {
        this.direction = direction
    }
}