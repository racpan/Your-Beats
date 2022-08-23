package com.racpanapps.yourbeats.songsDB

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LoadSongs : Application(), CoroutineScope {
    private val selection = MediaStore.Audio.Media.IS_MUSIC
    private val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST
    )

    private var job : Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    fun listSongs(context : Context) : ArrayList<Song> {
        var songs = arrayListOf<Song>()
        val db = SongRoomDatabase.getDatabase(context)
        launch {
            db.songsDao().deleteAllSongs()
        }
        context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null
        )?.use { cursor : Cursor ->
            val songIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            while(cursor.moveToNext()) {
                val songId = cursor.getLong(songIdColumn)
                val contentUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId.toString())
                val title = cursor.getString(titleColumn)
                val album = cursor.getString(albumColumn)
                val artist = cursor.getString(artistColumn)
                val song = Song(cursor.position, songId, contentUri.toString(), title, artist, album, false)
                insertSong(song, db)
                songs.add(song)
            }
        }
        return songs
    }

    @WorkerThread
    private fun insertSong(song : Song, db : SongRoomDatabase) {
        launch {
            db.songsDao().insertSong(song)
        }
    }
}