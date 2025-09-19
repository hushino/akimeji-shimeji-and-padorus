package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

import java.util.concurrent.ThreadLocalRandom

class DescendRight(shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Descend(shimejiId, paidenable, flinging) {

    override val nextAnimation: Animation
        get() = if (classicmode) {
            if (ThreadLocalRandom.current().nextBoolean()) ClimbRight(shimejiId, paidenable = paidenable, flinging = flinging) else WalkLeft(shimejiId, paidenable = paidenable, flinging = flinging)
        } else {
            when (ThreadLocalRandom.current().nextInt(1, 6)) {
                1 -> JumpLeft(shimejiId, paidenable = paidenable, flinging = flinging)
                3 -> ClimbRight(shimejiId, paidenable = paidenable, flinging = flinging)
                4 -> ClimbRight(shimejiId, paidenable = paidenable, flinging = flinging)
                5 -> WalkLeft(shimejiId, paidenable = paidenable, flinging = flinging)
                else -> Falling(direction, shimejiId, paidenable = paidenable, flinging = flinging)
            }
        }

    override fun getOptionalAnimation(): Animation? {
        return null
    }

    override val direction: Direction
        get() = Direction.RIGHT
}