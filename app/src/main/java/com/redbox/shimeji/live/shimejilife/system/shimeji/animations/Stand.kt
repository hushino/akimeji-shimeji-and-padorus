package com.redbox.shimeji.live.shimejilife.system.shimeji.animations


internal class Stand(override val direction: Direction, shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Animation(shimejiId, paidenable,
    flinging
) {

    override fun getSprites(): MutableList<Sprite>? {
        val sprites: MutableList<Sprite> = ArrayList(1)
        return when (shimejiId) {
            32 -> {
                null
            }
            116 -> {
                null
            }
            else -> {
                if (classicmode){
                    sprites.add(Sprite(0, 0, 0, 150))
                }else{
                    sprites.add(Sprite(0, 0, 0, 60))
                }
                sprites
            }
        }
    }

    override fun getOptionalAnimation(): Animation? {
        return null
    }

    override val nextAnimation: Animation
        get() = if (direction === Direction.LEFT) WalkLeft(shimejiId, paidenable = paidenable, flinging = flinging) else WalkRight(shimejiId, paidenable = paidenable, flinging = flinging)

    override val isOneShot: Boolean
        get() = true

    override fun checkBorders(
        atTop: Boolean,
        atBottom: Boolean,
        atLeft: Boolean,
        atRight: Boolean
    ) {
    }

}