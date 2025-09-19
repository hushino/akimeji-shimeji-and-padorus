package com.redbox.shimeji.live.shimejilife.system.akimeji.animations

import java.util.concurrent.ThreadLocalRandom

class WalkRight(akimejit2Id: Int) : Walk(akimejit2Id) {

    override fun getSprites(): MutableList<SpriteT2> {
        val spritesAlice: MutableList<SpriteT2> = ArrayList(10)
        val spritesJotarin: MutableList<SpriteT2> = ArrayList(10)
        val spritesDarling: MutableList<SpriteT2> = ArrayList(10)
        val spritesLen: MutableList<SpriteT2> = ArrayList(10)
        val spritesTaku: MutableList<SpriteT2> = ArrayList(10)
        val spritesPadoru: MutableList<SpriteT2> = ArrayList(1)
        when (akimejit2Id) {
            0 -> {
                spritesAlice.add(SpriteT2(0, 1f, 0, 5))
                spritesAlice.add(SpriteT2(1, 1f, 0, 5))
                return spritesAlice
            }
            1 -> {
                spritesJotarin.add(SpriteT2(0, 1f, 0, 5))
                spritesJotarin.add(SpriteT2(1, 1f, 0, 5))
                return spritesJotarin
            }
            2 -> {
                spritesDarling.add(SpriteT2(0, 0.5f, 0, 50))
                spritesDarling.add(SpriteT2(1, 0.5f, 0, 50))
                return spritesDarling
            }
            3 -> {
                spritesLen.add(SpriteT2(0, 0.8f, 0, 40))
                spritesLen.add(SpriteT2(1, 0.8f, 0, 40))
                return spritesLen
            }
            4 -> {
                spritesTaku.add(SpriteT2(0, 0.8f, 0, 40))
                spritesTaku.add(SpriteT2(1, 0.8f, 0, 40))
                spritesTaku.add(SpriteT2(2, 0.8f, 0, 40))
                spritesTaku.add(SpriteT2(3, 0.8f, 0, 40))
                spritesTaku.add(SpriteT2(4, 0.8f, 0, 40))
                return spritesTaku
            }
            5 -> {
                spritesPadoru.add(SpriteT2(0, 0.8f, 0, 90))
                spritesPadoru.add(SpriteT2(0, 0.8f, -2, 10))
                spritesPadoru.add(SpriteT2(0, 0.8f, 2, 10))
                spritesPadoru.add(SpriteT2(0, 0.8f, 0, 90))
                return spritesPadoru
            }
            else -> {
                spritesPadoru.add(SpriteT2(0, 0.8f, 0, 90))
                spritesPadoru.add(SpriteT2(0, 0.8f, -2, 5))
                spritesPadoru.add(SpriteT2(0, 0.8f, 2, 5))
                spritesPadoru.add(SpriteT2(0, 0.8f, 0, 90))
                return spritesPadoru
            }
        }
    }

    override val nextAnimation: Animation
        get() = WalkLeft(akimejit2Id)

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