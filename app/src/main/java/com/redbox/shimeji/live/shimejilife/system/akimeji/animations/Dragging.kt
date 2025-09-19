package com.redbox.shimeji.live.shimejilife.system.akimeji.animations

import java.util.concurrent.ThreadLocalRandom

class Dragging(akimejit2Id: Int) : Animation(akimejit2Id) {
    /*private Animations animations = Animations.DRAGGING;

    @Override
    Animations getActualAnimation() {
        return this.animations;
    }*/
    override val direction: Direction =
        if (ThreadLocalRandom.current().nextBoolean()) Direction.LEFT else Direction.RIGHT

    fun drop(): Animation {
        return nextAnimation
    }

    override fun getSprites(): MutableList<SpriteT2> {
        val spritesAlice: MutableList<SpriteT2> = ArrayList(6)
        val spritesJotarin: MutableList<SpriteT2> = ArrayList(6)
        val spritesDarling: MutableList<SpriteT2> = ArrayList(6)
        val spritesLen: MutableList<SpriteT2> = ArrayList(6)
        val spritesTaku: MutableList<SpriteT2> = ArrayList(6)
        val spritesPadoru: MutableList<SpriteT2> = ArrayList(1)
        when (akimejit2Id) {
            0 -> {
                spritesAlice.add(SpriteT2(0, 0f, 0, 100))
                return spritesAlice
            }
            1 -> {
                spritesJotarin.add(SpriteT2(2, 0f, 0, 10))
                spritesJotarin.add(SpriteT2(3, 0f, 0, 10))
                spritesJotarin.add(SpriteT2(4, 0f, 0, 10))
                spritesJotarin.add(SpriteT2(5, 0f, 0, 10))
                spritesJotarin.add(SpriteT2(6, 0f, 0, 10))
                spritesJotarin.add(SpriteT2(7, 0f, 0, 10))
                spritesJotarin.add(SpriteT2(8, 0f, 0, 10))
                spritesJotarin.add(SpriteT2(9, 0f, 0, 10))
                return spritesJotarin
            }
            2 -> {
                spritesDarling.add(SpriteT2(0, 0f, 0, 50))
                spritesDarling.add(SpriteT2(1, 0f, 0, 50))
                return spritesDarling
            }
            3 -> {
                spritesLen.add(SpriteT2(0, 0f, 0, 50))
                return spritesLen
            }
            4 -> {
                spritesTaku.add(SpriteT2(0, 0f, 0, 50))
                spritesTaku.add(SpriteT2(1, 0f, 0, 50))
                spritesTaku.add(SpriteT2(2, 0f, 0, 50))
                spritesTaku.add(SpriteT2(3, 0f, 0, 50))
                spritesTaku.add(SpriteT2(4, 0f, 0, 50))
                return spritesTaku
            }
            5 -> {
                spritesPadoru.add(SpriteT2(0, 0f, 0, 50))
                return spritesPadoru
            }
            else ->  {
                spritesPadoru.add(SpriteT2(0, 0f, 0, 10))
                return spritesPadoru
            }
        }

    }

    override fun getOptionalAnimation(): Animation? {
        return null
    }

    override val nextAnimation: Animation
        get() = WalkRight(akimejit2Id)

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