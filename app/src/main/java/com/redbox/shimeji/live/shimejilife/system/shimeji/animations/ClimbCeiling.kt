package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

import java.util.concurrent.ThreadLocalRandom

abstract class ClimbCeiling(shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Animation(shimejiId, paidenable, flinging) {
    override val isOneShot: Boolean
        get() = false

    /* override val optionalAnimation: Animation
         get() = Falling(direction)  */
    /*override val optionalAnimation: Animation
        get() = if (ThreadLocalRandom.current().nextBoolean()) ClimbCeilingLeft() else Falling(direction)*/
    override fun getOptionalAnimation(): Animation {
        return if (classicmode) {
            Falling(direction, shimejiId, paidenable = paidenable, flinging = flinging)
        } else {
            when (ThreadLocalRandom.current().nextInt(1, 3)) {
                1 -> ClimbCeilingRight(shimejiId, paidenable = paidenable, flinging = flinging)
                2 -> ClimbCeilingLeft(shimejiId, paidenable = paidenable, flinging = flinging)
                else -> Falling(direction, shimejiId, paidenable = paidenable, flinging = flinging)
            }
        }

    }

    override fun getMaxDuration(): Int {
        //random.nextInt(170) + 30
        //return ThreadLocalRandom.current().nextInt(210, 600)
        return if (classicmode) {
            ThreadLocalRandom.current().nextInt(170) + 30
        } else {
            60
        }
    }
}