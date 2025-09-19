package com.redbox.shimeji.live.shimejilife.system.akimeji.animations

import java.util.concurrent.ThreadLocalRandom

abstract class Walk(akimejit2Id: Int) : Animation(akimejit2Id) {

    private val possibleAnimations: Int = 2

    override fun getOptionalAnimation(): Animation {
        var possibleAnimations = 2 // original 2
        if (paidEnabled) {
            possibleAnimations = 2 //9
        }
        //Timber.e("possibleAnimations: $possibleAnimations")
        return when (ThreadLocalRandom.current().nextInt(0, possibleAnimations)) {
            0 -> WalkRight(akimejit2Id)
            1 -> WalkLeft(akimejit2Id)
            else -> WalkLeft(akimejit2Id)
        }
    }



    override fun getMaxDuration(): Int {
        if (paidEnabled) {
            return ThreadLocalRandom.current().nextInt(10, 60)
        }
        return ThreadLocalRandom.current().nextInt(10, 160)

    }

    override val isOneShot: Boolean
        get() = false
}