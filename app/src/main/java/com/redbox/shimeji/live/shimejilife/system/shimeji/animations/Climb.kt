package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

import com.redbox.shimeji.live.shimejilife.system.shimeji.SpriteUtil

abstract class Climb(shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Animation(shimejiId, paidenable, flinging) {

    override fun getSprites(): MutableList<Sprite> {

        val sprites: MutableList<Sprite> = ArrayList(8)
        //val sprites2: HashSet<Sprite> = hashSetOf()
        val spritesAkari: MutableList<Sprite> = ArrayList(8)
        val spritesAkariP: MutableList<Sprite> = ArrayList(8)

        return when (shimejiId) {
            32 -> {
                //sprites2.add(Sprite(SpriteUtil.CLIMB_WALLAKARI.values[0], 0, 0, 20))
                spritesAkari.add(Sprite(SpriteUtil.CLIMB_WALLAKARI.values[0], 0, 0, 20))
                spritesAkari.add(Sprite(SpriteUtil.CLIMB_WALLAKARI.values[1], 0, -1, 15))
                spritesAkari.add(Sprite(SpriteUtil.CLIMB_WALLAKARI.values[2], 0, -1, 10))
                //spritesAkari.add(Sprite(SpriteUtil.CLIMB_WALLAKARI.values[3], 0, -1, 15))
                spritesAkari
            }
            116 -> {
                spritesAkariP.add(Sprite(SpriteUtil.CLIMB_WALLAKARIP.values[0], 0, 0, 20))
                spritesAkariP.add(Sprite(SpriteUtil.CLIMB_WALLAKARIP.values[1], 0, -1, 15))
                spritesAkariP
            }
            else -> {
                if (classicmode) {
                    sprites.add(Sprite(13, 0, 0, 16))
                    sprites.add(Sprite(13, 0, -1, 4))
                    sprites.add(Sprite(11, 0, -1, 4))
                    sprites.add(Sprite(12, 0, -1, 4))
                    sprites.add(Sprite(12, 0, 0, 16))
                    sprites.add(Sprite(12, 0, -2, 4))
                    sprites.add(Sprite(11, 0, -2, 4))
                    sprites.add(Sprite(13, 0, -2, 4))
                } else {
                    sprites.add(Sprite(13, 0, 0, 20))
                    sprites.add(Sprite(13, 0, -1, 15))
                    sprites.add(Sprite(11, 0, -1, 10))
                    sprites.add(Sprite(12, 0, -1, 15))
                }

                sprites
            }
        }
    }


    override fun getMaxDuration(): Int {
        return 60
    }

    override val isOneShot: Boolean
        get() = false

    override fun checkBorders(
        atTop: Boolean,
        atBottom: Boolean,
        atLeft: Boolean,
        atRight: Boolean
    ) {
        nextAnimationRequested = atTop
    }
}