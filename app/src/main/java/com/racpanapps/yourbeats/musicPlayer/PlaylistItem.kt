package com.racpanapps.yourbeats.musicPlayer

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
class PlaylistItem(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id : Int,
    @ColumnInfo(name = "playlistName") val playlistName : String,
    @ColumnInfo(name = "songId") val songId : Long
)