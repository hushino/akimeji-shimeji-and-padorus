package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

import java.util.concurrent.ThreadLocalRandom

class ClimbLeft(shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Climb(shimejiId, paidenable, flinging) {

    override val nextAnimation: Animation
        get() = ClimbCeilingRight(shimejiId, paidenable = paidenable, flinging = flinging)

    override fun getOptionalAnimation(): Animation {
        return if (classicmode){
            if (ThreadLocalRandom.current().nextInt(100) < 70) JumpRight(shimejiId, paidenable = paidenable, flinging = flinging) else Falling(direction,shimejiId, paidenable = paidenable, flinging = flinging)
        }else{
            when (ThreadLocalRandom.current().nextInt(1, 4)) {
                1 -> JumpRight(shimejiId, paidenable = paidenable, flinging = flinging)
                2 -> DescendLeft(shimejiId, paidenable = paidenable, flinging = flinging)
                3 -> DescendLeft(shimejiId, paidenable = paidenable, flinging = flinging)
                else -> Falling(direction,shimejiId, paidenable = paidenable, flinging = flinging)
            }
        }
    }
    override val direction: Direction
        get() = Direction.LEFT
}