package com.racpanapps.yourbeats.songsDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.racpanapps.yourbeats.musicPlayer.PlaylistItem

@Database(entities = [Song::class, PlaylistItem::class, HighScoreDB::class], version = 1, exportSchema = false)
abstract class SongRoomDatabase : RoomDatabase() {
    abstract fun songsDao() : SongsDao

    companion object {
        @Volatile
        private var INSTANCE : SongRoomDatabase? = null

        fun getDatabase(context : Context) : SongRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                SongRoomDatabase::class.java,
                                "songs_database").build()
                INSTANCE = instance
                return instance
            }
        }
    }
}