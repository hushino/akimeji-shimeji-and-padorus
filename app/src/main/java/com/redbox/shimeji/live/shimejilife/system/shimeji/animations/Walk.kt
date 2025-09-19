package com.redbox.shimeji.live.shimejilife.system.shimeji.animations

import timber.log.Timber
import java.util.concurrent.ThreadLocalRandom

abstract class Walk(shimejiId: Int, paidenable: Boolean, flinging: Boolean) : Animation(shimejiId, paidenable, flinging) {

    private val possibleAnimations: Int = 2

    override fun getOptionalAnimation(): Animation {
        val possibleAnimations = 9 //2
        /*if (paidEnabled or paidenable) {
            possibleAnimations = 9
        }*/
        //Timber.e("ANIMACIONES POSIBLES: $possibleAnimations")
        //Timber.e("ANIMACIONES paidenable: $paidenable")
        //Timber.e("ANIMACIONES paidEnabled: $paidEnabled")

        //Timber.e("${Animation.classicmode}")
        if (shimejiId == 32 || shimejiId == 116) {
            return when (ThreadLocalRandom.current().nextInt(0, 3)) {
                0 -> WalkLeft(shimejiId, paidenable = paidenable, flinging = flinging)
                7 -> if (direction === Direction.LEFT) TrippingLeft(shimejiId, paidenable = paidenable, flinging = flinging) else TrippingRight(shimejiId, paidenable = paidenable, flinging = flinging)
                8 -> if (direction === Direction.LEFT) CreepLeft(shimejiId, paidenable = paidenable, flinging = flinging) else CreepRight(shimejiId, paidenable = paidenable, flinging = flinging)
                else -> if (direction === Direction.LEFT) JumpLeft(shimejiId, paidenable = paidenable, flinging = flinging) else JumpRight(shimejiId, paidenable = paidenable, flinging = flinging)
            }
        } else if (!Animation.classicmode) {
            return when (ThreadLocalRandom.current().nextInt(0, possibleAnimations)) {
                0 -> Sit(direction, shimejiId, paidenable = paidenable, flinging = flinging)
                1 -> Stand(direction, shimejiId, paidenable = paidenable, flinging = flinging)
                2 -> Sprawl(direction, shimejiId, paidenable = paidenable, flinging = flinging)
                3 -> Wink(direction, shimejiId, paidenable = paidenable, flinging = flinging)
                4 -> SitAndDangleLegs(direction, shimejiId, paidenable = paidenable, flinging = flinging)
                5 -> SitWithLegsDown(direction, shimejiId, paidenable = paidenable, flinging = flinging)
                6 -> SitAndLookUp(direction, shimejiId, paidenable = paidenable, flinging = flinging)
                7 -> if (direction === Direction.LEFT) TrippingLeft(shimejiId, paidenable = paidenable, flinging = flinging) else TrippingRight(shimejiId, paidenable = paidenable, flinging = flinging)
                8 -> if (direction === Direction.LEFT) CreepLeft(shimejiId, paidenable = paidenable, flinging = flinging) else CreepRight(shimejiId, paidenable = paidenable, flinging = flinging)
                else -> if (direction === Direction.LEFT) JumpLeft(shimejiId, paidenable = paidenable, flinging = flinging) else JumpRight(shimejiId, paidenable = paidenable, flinging = flinging)
            }
        } else {
            return when (ThreadLocalRandom.current().nextInt(9)) {
                0 -> Sit(direction, shimejiId, paidenable = true, flinging = true)
                1 -> Stand(direction, shimejiId, paidenable = true, flinging = true)
                2 -> Sprawl(direction, shimejiId, paidenable = true, flinging = true)
                3 -> Wink(direction, shimejiId, paidenable = true, flinging = true)
                4 -> SitAndDangleLegs(direction, shimejiId, paidenable = true, flinging = true)
                5 -> SitWithLegsDown(direction, shimejiId, paidenable = true, flinging = true)
                6 -> SitAndLookUp(direction, shimejiId, paidenable = true, flinging = true)
                7 -> if (direction === Direction.LEFT) TrippingLeft(shimejiId, paidenable = true, flinging = true) else TrippingRight(shimejiId, paidenable = true, flinging = true)
                else -> if (direction === Direction.LEFT) CreepLeft(shimejiId, paidenable = true, flinging = true) else CreepRight(shimejiId, paidenable = true, flinging = true)
            }
        }

    }

    override fun getMaxDuration(): Int {
        return if (classicmode) {
            ThreadLocalRandom.current().nextInt(150) + 10
        } else {
            ThreadLocalRandom.current().nextInt(10, 160)
        }
    }

    override val isOneShot: Boolean
        get() = false
}