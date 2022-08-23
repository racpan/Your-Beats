package com.racpanapps.yourbeats.songsDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
class Song(
        @PrimaryKey @ColumnInfo(name = "listId") val listId : Int,
        @ColumnInfo(name = "songId") val songId : Long,
        @ColumnInfo(name = "uri") val uri : String,
        @ColumnInfo(name = "title") val title : String,
        @ColumnInfo(name = "artist") val artist : String,
        @ColumnInfo(name = "album") val album : String,
        @ColumnInfo(name = "played") val played : Boolean)