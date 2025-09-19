package com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1.mascotdb

import androidx.room.Embedded
import androidx.room.Relation
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1.shimejidb.RoomShimeji


class MascotsWithShimeji {
    //deberia funcionar bien
    @Embedded
    var mascots: Mascots? = null
    //id = Mascots.kt
    @Relation(parentColumn = "id", entityColumn = "mascot", entity = RoomShimeji::class)
    var petNames: List<RoomShimeji>? = null
}