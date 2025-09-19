package com.redbox.shimeji.live.shimejilife.system.shimeji

enum class SpriteUtil(var values: IntArray) {
    // columna frame.
    WALK(intArrayOf(0, 1, 0, 2)), DRAGGING(intArrayOf(4, 5, 6, 7, 8, 9)),
    JUMP(intArrayOf(21)), FALLING(intArrayOf(3)),
    CLIMB_WALL(intArrayOf(11, 12, 13)), CLIMB_CEILING(intArrayOf(22, 23, 24)), BOUNCE(intArrayOf(17, 18)),
    WINK(intArrayOf(14, 16)), SIT(intArrayOf(10)), SIT_DANGLE_LEGS(intArrayOf(30, 31)), SPRAWL(intArrayOf(20)),
    CREEP(intArrayOf(19, 20)), SIT_LOOK_UP(intArrayOf(25)), TRIP(intArrayOf(17, 18, 19)),

    WALKAKARI(intArrayOf(0, 1, 2, 3)),
    DRAGGINGAKARI(intArrayOf(16, 17, 18, 19)),
    JUMPAKARI(intArrayOf(7)),
    FALLINGAKARI(intArrayOf(6)),
    CLIMB_WALLAKARI(intArrayOf(20, 21, 22, 23)),
    CLIMB_CEILINGAKARI(intArrayOf(12, 13, 14, 15)),
    BOUNCEAKARI(intArrayOf(5)),
    CREEPAKARI(intArrayOf(8, 9, 10, 11)),

    WALKAKARIP(intArrayOf(0, 1, 2, 3)),
    DRAGGINGAKARIP(intArrayOf(17, 18)),
    JUMPAKARIP(intArrayOf(8)),
    FALLINGAKARIP(intArrayOf(11, 12)),
    CLIMB_WALLAKARIP(intArrayOf(13, 14)),
    CLIMB_CEILINGAKARIP(intArrayOf(15, 16)),
    BOUNCEAKARIP(intArrayOf(5, 6, 7, 19)),
    CREEPAKARIP(intArrayOf(9, 10));

    companion object {
        fun usedSprites(id: Int): HashSet<Int> {
            val animations: HashSet<Int> = HashSet(20)
            for (anim in arrayOf(
                WALK, DRAGGING, JUMP, FALLING, CLIMB_WALL, CLIMB_CEILING, BOUNCE, WINK, SIT, SIT_LOOK_UP, SIT_DANGLE_LEGS, SPRAWL, CREEP, TRIP
            )) {
                for (valueOf in anim.values) {
                    animations.add(Integer.valueOf(valueOf))
                }
            }

            val animationsAkarin: HashSet<Int> = HashSet(10)
            for (anim in arrayOf(WALKAKARI, DRAGGINGAKARI, JUMPAKARI, FALLINGAKARI, CLIMB_WALLAKARI, CLIMB_CEILINGAKARI, BOUNCEAKARI, CREEPAKARI)) {
                for (valueOf in anim.values) {
                    animationsAkarin.add(Integer.valueOf(valueOf))
                }
            }

            val animationsAkarinP: HashSet<Int> = HashSet(10)
            for (anim in arrayOf(WALKAKARIP, DRAGGINGAKARIP, JUMPAKARIP, FALLINGAKARIP, CLIMB_WALLAKARIP, CLIMB_CEILINGAKARIP, BOUNCEAKARIP, CREEPAKARIP)) {
                for (valueOf in anim.values) {
                    animationsAkarinP.add(Integer.valueOf(valueOf))
                }
            }

            return when (id) {
                32 -> animationsAkarin
                116 -> animationsAkarinP
                else -> animations
            }


        }


    }

}

/*estas son las animaciones con el numero q corresponde a el nombre d los dibujos en la carpeta
    WALK(1, 2, 1, 3),
    DRAGGING(5, 6, 7, 8, 9, 10),
    JUMP(22),
    FALLING(4),
    CLIMB_WALL(12, 13, 14),
    CLIMB_CEILING(23, 24, 25),
    BOUNCE(18, 19),
    WINK(15, 17),
    SIT(11),
    SIT_DANGLE_LEGS(31, 32),
    SPRAWL(21),
    CREEP(20, 21),
    SIT_LOOK_UP(26),
    TRIP(18, 19, 20);*/