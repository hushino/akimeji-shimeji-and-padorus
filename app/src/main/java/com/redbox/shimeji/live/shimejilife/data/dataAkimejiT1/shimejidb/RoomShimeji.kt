package com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1.shimejidb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "shimeji"/*,
        foreignKeys = [ForeignKey(entity = Mascots::class, parentColumns = ["id"], childColumns = ["mascot"], onDelete = CASCADE)],
        indices = [Index("mascot")]*/
)
data class RoomShimeji(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    var id: Int? = null,
    @JvmField
    @ColumnInfo(name = "bitmap", typeAffinity = ColumnInfo.BLOB)
    var bitmap: ByteArray,
    @ColumnInfo(name = "frame", typeAffinity = ColumnInfo.INTEGER)
    var frame: Int? = 0,
    @ColumnInfo(name = "mascot", typeAffinity = ColumnInfo.INTEGER, index = true)
    var mascot: Int? = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoomShimeji

        if (id != other.id) return false
        if (!bitmap.contentEquals(other.bitmap)) return false
        if (frame != other.frame) return false
        if (mascot != other.mascot) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + bitmap.contentHashCode()
        result = 31 * result + (frame ?: 0)
        result = 31 * result + (mascot ?: 0)
        return result
    }
/*

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoomShimeji

        if (!bitmap.contentEquals(other.bitmap)) return false

        return true
    }

    override fun hashCode(): Int {
        return bitmap.contentHashCode()
    }*/

}