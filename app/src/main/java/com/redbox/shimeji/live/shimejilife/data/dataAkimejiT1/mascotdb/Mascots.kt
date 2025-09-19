package com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1.mascotdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
@Entity(
    tableName = "mascots", indices = [Index(
        value = ["id"],
        unique = true
    )]
)
data class Mascots constructor(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Int? = null,
    @ColumnInfo(name = "name", index = true)
    val name: String? = null,
    @JvmField
    @ColumnInfo(name = "bitmap", typeAffinity = ColumnInfo.BLOB)
    var bitmap: ByteArray,
    @ColumnInfo(name = "paidEnabled", index = true)
    val paidEnabled: Boolean? = false,
    @ColumnInfo(name = "flingEnabled", index = true)
    val flingEnabled: Boolean? = false,
    @ColumnInfo(name = "version", defaultValue = "1")
    val version: Int? = 1,
    @ColumnInfo(name = "downloaded", defaultValue = "false")
    val downloaded: Boolean? = null,
    @ColumnInfo(name = "status", defaultValue = "1")
    var status: Int? = null,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdTime: String? = System.currentTimeMillis().toString(),
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val lastModifiedTime: String? = System.currentTimeMillis().toString()

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Mascots

        if (id != other.id) return false
        if (name != other.name) return false
        if (!bitmap.contentEquals(other.bitmap)) return false
        if (version != other.version) return false
        if (downloaded != other.downloaded) return false
        if (status != other.status) return false
        if (createdTime != other.createdTime) return false
        if (lastModifiedTime != other.lastModifiedTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + bitmap.contentHashCode()
        result = 31 * result + (version ?: 0)
        result = 31 * result + (downloaded?.hashCode() ?: 0)
        result = 31 * result + (status ?: 0)
        result = 31 * result + (createdTime?.hashCode() ?: 0)
        result = 31 * result + (lastModifiedTime?.hashCode() ?: 0)
        return result
    }
}