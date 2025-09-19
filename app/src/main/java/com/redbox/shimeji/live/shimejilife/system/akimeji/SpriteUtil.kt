package com.redbox.shimeji.live.shimejilife.system.akimeji

import java.util.*

enum class SpriteUtil(var values: IntArray) {
    //columna frame.

    //alice
    WALKALICE(intArrayOf(0, 1, 2, 3, 4, 5, 6, 7)),
    DRAGGINGALICE(intArrayOf(0)),

    //jotaro
    WALKJOTARIN(intArrayOf(0, 1)), DRAGGINGJOTARIN(intArrayOf(2, 3, 4, 5, 6, 7, 8, 9)),

    WALKDARLING(intArrayOf(0, 1)), DRAGGINGDARLING(intArrayOf(0, 1)),
    WALKLEN(intArrayOf(0, 1)), DRAGGINGLEN(intArrayOf(0, 1)),
    WALKTAKU(intArrayOf(0, 1, 2, 3, 4)), DRAGGINTAKU(intArrayOf(0, 1, 2, 3, 4)),

    //Padoru
    WALKPADORU(intArrayOf(0)), DRAGGINGPADORU(intArrayOf(0))

    ;

    companion object {
        fun usedSprites(id: Int): HashSet<Int> {
            val animationsJotarin: HashSet<Int> = HashSet(10)
            for (anim in arrayOf(
                WALKJOTARIN,
                DRAGGINGJOTARIN
            )) {
                for (valueOf in anim.values) {
                    animationsJotarin.add(Integer.valueOf(valueOf))
                }
            }

            val animationsAlice: HashSet<Int> = HashSet(10)
            for (anim in arrayOf(
                WALKALICE,
                DRAGGINGALICE
            )) {
                for (valueOf in anim.values) {
                    animationsAlice.add(Integer.valueOf(valueOf))
                }
            }

            val animationsDarling: HashSet<Int> = HashSet(10)
            for (anim in arrayOf(
                WALKDARLING,
                DRAGGINGDARLING
            )) {
                for (valueOf in anim.values) {
                    animationsDarling.add(Integer.valueOf(valueOf))
                }
            }
            val animationsLen: HashSet<Int> = HashSet(10)
            for (anim in arrayOf(
                WALKLEN,
                DRAGGINGLEN
            )) {
                for (valueOf in anim.values) {
                    animationsLen.add(Integer.valueOf(valueOf))
                }
            }
            val animationsTaku: HashSet<Int> = HashSet(10)
            for (anim in arrayOf(
                WALKTAKU,
                DRAGGINTAKU
            )) {
                for (valueOf in anim.values) {
                    animationsTaku.add(Integer.valueOf(valueOf))
                }
            }

            val animationsPadoru: HashSet<Int> = HashSet(1)
            for (anim in arrayOf(
                WALKPADORU,
                DRAGGINGPADORU
            )) {
                for (valueOf in anim.values) {
                    animationsPadoru.add(Integer.valueOf(valueOf))
                }
            }

            return when (id) {
                0 -> animationsAlice
                1 -> animationsJotarin
                2 -> animationsDarling
                3 -> animationsLen
                4 -> animationsTaku
                5 -> animationsPadoru
                else -> animationsPadoru
            }

        }
    }

}

/*estas son las animaciones con el numero q corresponde a el nombre d los dibujos en la carpeta
    WALK(0, 1, 0, 2),
    DRAGGING(4, 5, 6, 7),
    JUMP(21),
    FALLING(3),
    CLIMB_WALL(11, 12, 13),
    CLIMB_CEILING(22, 23, 24),
    BOUNCE(17, 18),
    WINK(14, 16),
    SIT(10),
    SIT_DANGLE_LEGS(30, 31),
    SPRAWL(20),
    CREEP(19, 20),
    SIT_LOOK_UP(25),
    TRIP(17, 18, 19);*/