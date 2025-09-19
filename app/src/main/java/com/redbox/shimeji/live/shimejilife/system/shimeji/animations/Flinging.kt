package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

class Flinging(velocityX: Int, shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Animation(shimejiId, paidenable, flinging) {
    /*    private Animations animations = Animations.FLINGING;

    @Override
    Animations getActualAnimation() {
        return this.animations;
    }*/
    override val direction: Direction
    private var grabBottom = false
    private var grabLeft = false
    private var grabRight = false
    private var grabTop = false
    fun drop(): Animation {
        return nextAnimation
    }

    override fun getSprites(): MutableList<Sprite> {
        val sprites: MutableList<Sprite> = ArrayList(1)
        sprites.add(Sprite(7, 0, 0, 60))
        return sprites
    }

    override fun getOptionalAnimation(): Animation? {
        return null
    }

    override val nextAnimation: Animation
        get() {
            if (grabLeft) {
                return ClimbLeft(shimejiId, paidenable = paidenable, flinging = flinging)
            }
            if (grabRight) {
                return ClimbRight(shimejiId, paidenable = paidenable, flinging = flinging)
            }
            return if (grabTop) {
                if (direction === Direction.LEFT) ClimbCeilingLeft(shimejiId, paidenable = paidenable, flinging = flinging) else ClimbCeilingRight(shimejiId, paidenable = paidenable, flinging = flinging)
            } else {
                if (grabBottom) {
                    Bounce(direction, shimejiId, paidenable = paidenable, flinging = flinging)
                } else Falling(direction, shimejiId, paidenable = paidenable, flinging = flinging)
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
        grabTop = atTop
        grabBottom = atBottom
        grabLeft = atLeft
        grabRight = atRight
        val z = atTop || atBottom || atLeft || atRight
        nextAnimationRequested = z
    }

    init {
        direction =
            if (velocityX < 0) Direction.LEFT else Direction.RIGHT
    }
}