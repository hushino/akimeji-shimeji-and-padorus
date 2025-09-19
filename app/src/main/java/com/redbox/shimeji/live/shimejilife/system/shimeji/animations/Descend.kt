package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

import com.redbox.shimeji.live.shimejilife.system.shimeji.SpriteUtil
import java.util.concurrent.ThreadLocalRandom


abstract class Descend(shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Animation(shimejiId, paidenable, flinging) {

    override fun getSprites(): MutableList<Sprite> {

        val random = ThreadLocalRandom.current().nextInt(1, 2)
        val sprites: MutableList<Sprite> = ArrayList(8)
        val spritesAkimeji: MutableList<Sprite> = ArrayList(8)
        val spritesAkimejiP: MutableList<Sprite> = ArrayList(8)

        return when (shimejiId) {
            32 -> {
                spritesAkimeji.add(Sprite(SpriteUtil.CLIMB_WALLAKARI.values[0], 0, 0, 9))
                spritesAkimeji.add(Sprite(SpriteUtil.CLIMB_WALLAKARI.values[1], 0, random, 7))
                spritesAkimeji.add(Sprite(SpriteUtil.CLIMB_WALLAKARI.values[2], 0, random, 7))
                spritesAkimeji.add(Sprite(SpriteUtil.CLIMB_WALLAKARI.values[3], 0, random, 7))
                spritesAkimeji.add(Sprite(SpriteUtil.CLIMB_WALLAKARI.values[3], 0, 0, 9))
                spritesAkimeji.add(Sprite(SpriteUtil.CLIMB_WALLAKARI.values[0], 0, random, 7))
                spritesAkimeji.add(Sprite(SpriteUtil.CLIMB_WALLAKARI.values[1], 0, random, 7))
                spritesAkimeji.add(Sprite(SpriteUtil.CLIMB_WALLAKARI.values[2], 0, random, 7))
                spritesAkimeji.add(Sprite(SpriteUtil.CLIMB_WALLAKARI.values[3], 0, random, 7))
                spritesAkimeji
            }
            116 -> {
                spritesAkimejiP.add(Sprite(SpriteUtil.CLIMB_WALLAKARIP.values[0], 0, 0, 9))
                spritesAkimejiP.add(Sprite(SpriteUtil.CLIMB_WALLAKARIP.values[1], 0, random, 7))
                spritesAkimejiP.add(Sprite(SpriteUtil.CLIMB_WALLAKARIP.values[1], 0, 0, 9))
                spritesAkimejiP.add(Sprite(SpriteUtil.CLIMB_WALLAKARIP.values[0], 0, random, 7))
                spritesAkimejiP.add(Sprite(SpriteUtil.CLIMB_WALLAKARIP.values[1], 0, random, 7))
                spritesAkimejiP
            }
            else -> {
                if (classicmode) {
                    sprites.add(Sprite(13, 0, 0, 16))
                    sprites.add(Sprite(13, 0, 2, 4))
                    sprites.add(Sprite(11, 0, 2, 4))
                    sprites.add(Sprite(12, 0, 2, 4))
                    sprites.add(Sprite(12, 0, 0, 16))
                    sprites.add(Sprite(12, 0, 1, 4))
                    sprites.add(Sprite(11, 0, 1, 4))
                    sprites.add(Sprite(13, 0, 1, 4))
                } else {
                    sprites.add(Sprite(13, 0, 0, 9))
                    sprites.add(Sprite(13, 0, random, 7))
                    sprites.add(Sprite(11, 0, random, 7))
                    sprites.add(Sprite(12, 0, random, 7))
                    sprites.add(Sprite(12, 0, 0, 9))
                    sprites.add(Sprite(12, 0, random, 7))
                    sprites.add(Sprite(11, 0, random, 7))
                    sprites.add(Sprite(13, 0, random, 7))
                }

                sprites
            }
        }
    }

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