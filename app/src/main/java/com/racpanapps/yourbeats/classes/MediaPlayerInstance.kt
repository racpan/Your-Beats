package com.racpanapps.yourbeats.classes

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.WorkerThread
import com.racpanapps.yourbeats.R
import com.racpanapps.yourbeats.gameFiles.Game
import com.racpanapps.yourbeats.gameFiles.GameOver
import com.racpanapps.yourbeats.gameFiles.VisualizeAudio
import com.racpanapps.yourbeats.mainMenu.MusicPlayer
import com.racpanapps.yourbeats.notificationControls.Notifications
import com.racpanapps.yourbeats.songsDB.Song
import com.racpanapps.yourbeats.songsDB.SongRoomDatabase
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext


class MediaPlayerInstance : Application(), CoroutineScope {

    companion object {
        private var mpInstance = MediaPlayerInstance()
        private var currentSong : Song? = null
        var songProgress : Int = 0
        var songLength : Int = 0
        var playlistName : String = "null"
        var playedSongs = arrayListOf<Int>()
        var pause = false
        lateinit var songList : ArrayList<Song>
        lateinit var appContext : Context
        var instance : MediaPlayer? = null
        var audioManager : AudioManager? = null
        private var level = ""
        private var gameListId : Long = 0

        @JvmName("getMediaPlayerInstance")
        private fun getInstance(uri: Uri): MediaPlayer {
            if (instance == null) {
                instance = MediaPlayer.create(appContext, uri)
            } else {
                stopSong()
                instance = null
                instance = MediaPlayer.create(appContext, uri)
            }
            instance?.setOnCompletionListener{
                if (Game.songListRunning) {
                    val newIntent = Intent(appContext, GameOver::class.java)
                    newIntent.putExtra("listId", gameListId)
                    newIntent.putExtra("level", level)
                    newIntent.putExtra("points", VisualizeAudio.points)
                    appContext.startActivity(newIntent)
                } else {
                    nextSong()
                }
            }
            return instance as MediaPlayer
        }

        fun startSong(id: Long) {
            try {
                currentSong = mpInstance.getSongFromListId(id)
                AudioManager.STREAM_MUSIC
                getInstance(Uri.parse(currentSong?.uri))
                MusicPlayer.textViewSongTitle.text = currentSong!!.title
                songLength = instance?.duration!!
                MusicPlayer.seekBarSongProgress.progress = 0
                MusicPlayer.setSeekBar()
                addToPlayedSongs(currentSong!!.listId)
                instance?.setOnPreparedListener { it.start() }
                pause = false
                val notifications = Notifications()
                notifications.buildNotification(
                    appContext as Activity,
                    appContext,
                    currentSong!!.title
                )
            } catch (e : Exception) {
                Toast.makeText(appContext, appContext.resources.getString(R.string.song_not_found), Toast.LENGTH_LONG).show()
                nextSong()
            }
         }

        fun startGame(id: Long, screen : RelativeLayout, score : TextView, appContext : Context, level : String) {
            try {
                gameListId = id
                this.level = level
                currentSong = mpInstance.getSongFromListId(id)
                audioManager = appContext.getSystemService(AUDIO_SERVICE) as AudioManager
                getInstance(Uri.parse(currentSong?.uri))
                songLength = instance?.duration!!
                instance?.setOnPreparedListener {
                    it.start()
                    val visualizeAudio = VisualizeAudio()
                    visualizeAudio.startVisualizer(it.audioSessionId, screen, score, appContext, level)
                }
                pause = false
            } catch (e : Exception) {
                Toast.makeText(appContext, appContext.resources.getString(R.string.song_not_found), Toast.LENGTH_LONG).show()
            }
        }

        fun stopSong() {
            instance?.stop()
        }

        fun pause() {
            songProgress = instance?.currentPosition!!
            instance?.pause()
            pause = true
        }

        fun play() {
            instance?.seekTo(songProgress)
            instance?.start()
            pause = false
        }

        fun setSeekBarProgress(progress: Int) {
            instance?.seekTo(progress)
            instance?.start()
        }

        fun nextSong() {
            try {
                if (instance == null) {
                    playFirstSong()
                } else {
                    playNextSong()
                }
            } catch (e: Exception) {
                playFirstSong()
            }
        }

        fun previousSong() {
            try {
                if (instance == null) {
                    playLastSong()
                } else {
                    playPreviousSong()
                }
            } catch (e: Exception) {
                Log.d("TAG", e.toString())
                playLastSong()
            }
        }

        fun randomSong() {
            if (playlistName == "null") {
                val randomNum = (0..mpInstance.getCount().minus(1)).random()
                startSong(randomNum)
            } else {
                val randomNum = (0..songList.count().minus(1)).random()
                startSong(songList[randomNum].listId.toLong())
            }
        }

        private fun playFirstSong() {
            if (!MusicPlayer.shuffle) {
                if (playlistName == "null") {
                    startSong(0)
                } else {
                    startSong(songList[0].listId.toLong())
                }
            } else {
                randomSong()
            }
        }

        private fun playNextSong() {
            if (!MusicPlayer.shuffle) {
                try {
                    startSong(songList[getCurrentIndexInPlaylist().plus(1)].listId.toLong())
                } catch (e: Exception) {
                    playFirstSong()
                }
            } else {
                if (!MusicPlayer.repeat) {
                    startSong(mpInstance.getRandomUnplayedSong().listId.toLong())
                } else {
                    randomSong()
                }
            }
        }
        
        private fun playLastSong() {
            if (!MusicPlayer.shuffle) {
                if (playlistName == "null") {
                    startSong(mpInstance.getCount().minus(1))
                } else {
                    startSong(songList[songList.count().minus(1)].listId.toLong())
                }
            } else {
                randomSong()
            }
        }

        private fun playPreviousSong() {
            val currentIndex = getCurrentIndexInPlaylist()
            if (currentIndex == -1) {
                playLastSong()
            } else {
                if (!MusicPlayer.shuffle) {
                    startSong(currentIndex.minus(1).toLong())
                } else {
                    try {
                        startSong(playedSongs[getCurrentIndexInPlayedSongs().minus(1)].toLong())
                    } catch (e: Exception) {
                        randomSong()
                    }
                }
            }
        }

        private fun getCurrentIndexInPlaylist() : Int{
            var songIndex = 0
            songList.forEachIndexed { index, song ->
                if (currentSong?.songId == song.songId) { songIndex = index }
            }
            return songIndex
        }

        private fun getCurrentIndexInPlayedSongs() : Int {
            var songIndex = -1
            playedSongs.forEachIndexed { index, song ->
                if (currentSong?.listId == song) { songIndex = index }
            }
            return songIndex
        }

        private fun addToPlayedSongs(listId: Int) {
            if (playedSongs.indexOf(listId) == -1) {
                playedSongs.add(listId)
            }
        }
    }

    private var job : Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    @WorkerThread
    fun getCount() : Long = runBlocking {
        val db = SongRoomDatabase.getDatabase(appContext)
        return@runBlocking db.songsDao().getItemCount()
    }

    @WorkerThread
    fun getSongFromListId(id: Long) : Song = runBlocking {
        val db = SongRoomDatabase.getDatabase(appContext)
        var chosenSong : Song
        try {
            chosenSong = db.songsDao().searchListId(id)
            db.songsDao().updatePlayedSong(true, id)
        } catch (e: Exception) {
            chosenSong = db.songsDao().searchListId(0)
        }
        return@runBlocking chosenSong
    }

    @WorkerThread
    fun getRandomUnplayedSong() : Song = runBlocking {
        val db = SongRoomDatabase.getDatabase(appContext)
        val listOfSongs : List<Song> = if (playlistName == "null") {
            db.songsDao().searchUnplayed()
        } else {
            db.songsDao().searchPlaylistUnplayed(playlistName)
        }
        val count = listOfSongs.count()
        if (count < 1) {
            db.songsDao().setAllSongsUnplayed()
            getRandomUnplayedSong()
        } else {
            val randomNum = (0..count.minus(1)).random()
            return@runBlocking db.songsDao().searchListId(listOfSongs[randomNum].listId.toLong())
        }
    }
}