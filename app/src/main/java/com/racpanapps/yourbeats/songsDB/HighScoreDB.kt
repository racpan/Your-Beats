package com.racpanapps.yourbeats.songsDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "highScore")
class HighScoreDB(
        @PrimaryKey @ColumnInfo(name = "highScore") val highScore : String,
        @ColumnInfo(name = "score") val score : Long)