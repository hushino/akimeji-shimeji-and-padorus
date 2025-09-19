package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

abstract class Animation constructor(var shimejiId: Int, var paidenable: Boolean, var flinging: Boolean) {

    private var frameNumber = 0
    private var lastSpriteFrame = 0
    private val maxDuration = this.getMaxDuration()


    @JvmField
    var nextAnimationRequested = false

    private var spriteIndex = 0
    private val sprites = this.getSprites()

    enum class Direction {
        LEFT, RIGHT
    }

    abstract fun checkBorders(
        atTop: Boolean,
        atBottom: Boolean,
        atLeft: Boolean,
        atRight: Boolean
    )

    abstract val direction: Direction
    abstract val isOneShot: Boolean
    abstract val nextAnimation: Animation?
    abstract fun getSprites(): MutableList<Sprite>?
    abstract fun getOptionalAnimation(): Animation?

    open fun getMaxDuration(): Int {
        return 0
    }

    fun frameTick(): Animation? {
        frameNumber++
        if (nextAnimationRequested) {
            return nextAnimation
        }
        return if (maxDuration <= 0 || frameNumber < maxDuration || getOptionalAnimation() == null) {
            if (frameNumber <= lastSpriteFrame + (sprites?.get(spriteIndex)?.duration
                    ?: return nextAnimation) || !updateSprite()
            ) this else nextAnimation
        } else getOptionalAnimation()
    }

    private fun updateSprite(): Boolean {
        lastSpriteFrame = frameNumber
        if (sprites != null) {
            if (spriteIndex + 1 >= sprites.size) {
                val isOneShot = isOneShot
                spriteIndex = 0
                return isOneShot
            }
        }
        spriteIndex++
        return false
    }

    val xVelocity: Int?
        get() = sprites?.get(spriteIndex)?.xVelocity

    val yVelocity: Int?
        get() = sprites?.get(spriteIndex)?.yVelocity

    val spriteIdentifier: Int?
        get() = sprites?.get(spriteIndex)?.index

    // true or false
    val isFacingLeft: Boolean
        get() = direction == Direction.LEFT // true or false

    companion object {
        //var flingEnabled = true
        var paidEnabled = true
        var paidEnabledApp = false
        var paidEnabledAppShimeji101 = false
        var classicmode = false
    }
}