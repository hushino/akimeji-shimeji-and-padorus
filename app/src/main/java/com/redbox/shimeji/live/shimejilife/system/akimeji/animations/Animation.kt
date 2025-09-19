package com.redbox.shimeji.live.shimejilife.system.akimeji.animations



abstract class Animation public constructor(var akimejit2Id:Int) {
    private var frameNumber = 0
    private var lastSpriteFrame = 0
    private val maxDuration = this.getMaxDuration()

    @JvmField
    var nextAnimationRequested = false

    //@JvmField
    //var random = Random()
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
    abstract fun getSprites(): MutableList<SpriteT2>
    abstract fun getOptionalAnimation(): Animation?
    /*  open val optionalAnimation: Animation?
        get() = null*/

    open fun getMaxDuration(): Int {
        return 0
        //return ThreadLocalRandom.current().nextInt(0, 50)
    }

    fun frameTick(): Animation? {
        frameNumber++
        if (nextAnimationRequested) {
            return nextAnimation
        }
        return if (maxDuration <= 0 || frameNumber < maxDuration || getOptionalAnimation() == null) {
            if (frameNumber <= lastSpriteFrame + sprites[spriteIndex].duration || !updateSprite()) this else nextAnimation
        } else getOptionalAnimation()
    }

    private fun updateSprite(): Boolean {
        lastSpriteFrame = frameNumber
        if (spriteIndex + 1 >= sprites.size) {
            val isOneShot = isOneShot
            spriteIndex = 0
            return isOneShot
        }
        spriteIndex++
        return false
    }

    val xVelocity: Float
        get() = sprites[spriteIndex].xVelocity

    val yVelocity: Int
        get() = sprites[spriteIndex].yVelocity

    val spriteIdentifier: Int
        get() = sprites[spriteIndex].index

    // true or false
    val isFacingLeft: Boolean
        get() = direction == Direction.LEFT // true or false

    companion object {
        var flingEnabled = false
        var paidEnabled = false
    }
}