package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

import java.util.concurrent.ThreadLocalRandom

class ClimbRight(shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Climb(shimejiId, paidenable, flinging) {

    /* bridge */ /* synthetic */ override fun checkBorders(
        atTop: Boolean,
        atBottom: Boolean,
        atLeft: Boolean,
        atRight: Boolean
    ) {
        super.checkBorders(atTop, atBottom, atLeft, atRight)
    }

    override val direction: Direction
        get() = Direction.RIGHT

    override val nextAnimation: Animation
        get() = ClimbCeilingLeft(shimejiId, paidenable = paidenable, flinging = flinging)

    override fun getOptionalAnimation(): Animation {
        return if (classicmode) {
            if (ThreadLocalRandom.current().nextInt(100) < 70) JumpLeft(shimejiId, paidenable = paidenable, flinging = flinging) else Falling(
                direction,
                shimejiId,
                paidenable = paidenable,
                flinging = flinging
            )
        } else {
            when (ThreadLocalRandom.current().nextInt(1, 4)) {
                1 -> JumpLeft(shimejiId, paidenable = paidenable, flinging = flinging)
                2 -> DescendRight(shimejiId, paidenable = paidenable, flinging = flinging)
                3 -> DescendRight(shimejiId, paidenable = paidenable, flinging = flinging)
                else -> Falling(direction, shimejiId, paidenable = paidenable, flinging = flinging)
            }
        }
    }

}