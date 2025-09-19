package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

import java.util.concurrent.ThreadLocalRandom

class DescendLeft(shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Descend(shimejiId, paidenable, flinging) {

    override val nextAnimation: Animation
        get() =if (classicmode){
            if (ThreadLocalRandom.current().nextBoolean()) ClimbLeft(shimejiId, paidenable = paidenable, flinging = flinging) else WalkRight(shimejiId, paidenable = paidenable, flinging = flinging)
        }else{
            when (ThreadLocalRandom.current().nextInt(1, 4)) {
                1 -> JumpRight(shimejiId, paidenable = paidenable, flinging = flinging)
                2 -> ClimbLeft(shimejiId, paidenable = paidenable, flinging = flinging)
                3 -> WalkRight(shimejiId, paidenable = paidenable, flinging = flinging)
                else -> Falling(direction, shimejiId, paidenable = paidenable, flinging = flinging)
            }
        }

    override fun getOptionalAnimation(): Animation? {
        return null
    }

    override val direction: Direction
        get() = Direction.LEFT
}