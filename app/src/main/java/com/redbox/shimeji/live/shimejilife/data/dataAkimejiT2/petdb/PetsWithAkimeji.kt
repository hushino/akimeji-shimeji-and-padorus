package com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.petdb
import androidx.room.Embedded
import androidx.room.Relation
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.akimejidb.RoomAkimejit2

class PetsWithAkimeji {
    @Embedded
    var mascots: Pets? = null
    @Relation(parentColumn = "id", entityColumn = "mascot", entity = RoomAkimejit2::class)
    var petNames: List<RoomAkimejit2>? = null
}