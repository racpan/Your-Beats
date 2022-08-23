package com.racpanapps.yourbeats.gameFiles

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.racpanapps.yourbeats.R
import com.racpanapps.yourbeats.classes.InitializeBannerAds
import com.racpanapps.yourbeats.classes.MediaPlayerInstance
import com.racpanapps.yourbeats.mainMenu.MainMenu
import com.racpanapps.yourbeats.mainMenu.MusicPlayer
import com.racpanapps.yourbeats.musicPlayer.SongListAdapter
import com.racpanapps.yourbeats.songsDB.LoadSongs
import com.racpanapps.yourbeats.songsDB.Song
import com.racpanapps.yourbeats.songsDB.SongRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SongList : AppCompatActivity(), CoroutineScope {

    private lateinit var constraintLayoutSongList : ConstraintLayout
    private lateinit var linearLayoutBannerSongList : LinearLayout
    private lateinit var editTextSongSearchSongList : EditText
    private lateinit var recyclerViewSongList : RecyclerView
    private lateinit var adapter : SongListAdapter
    private var loadSongs = LoadSongs()
    private lateinit var appContext : Context

    companion object {
        var songListRunning: Boolean = true
    }

    private var job : Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.song_list)
        MediaPlayerInstance.pause = false
        appContext = applicationContext
        initializeControls()
        setListeners()
        InitializeBannerAds.loadAds(application, this, linearLayoutBannerSongList)
        InitializeBannerAds.setBackground(this, constraintLayoutSongList)
        loadAllSongs()
        askPermission()
    }

    override fun onStart() {
        super.onStart()
        MusicPlayer.musicPlayerRunning = false
        songListRunning = true
    }

    override fun onStop() {
        super.onStop()
        songListRunning = false
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainMenu::class.java))
    }

    private fun initializeControls() {
        constraintLayoutSongList = findViewById(R.id.constraintLayoutSongList)
        linearLayoutBannerSongList = findViewById(R.id.linearLayoutBannerSongList)
        editTextSongSearchSongList = findViewById(R.id.editTextSongSearchSongList)
        recyclerViewSongList = findViewById(R.id.recyclerViewSongList)
    }

    private fun setListeners() {
        editTextSongSearchSongList.doOnTextChanged { text, _, _, count ->
            if (count > 3) {
                searchForSong(text.toString())
            } else {
                loadAllSongs()
            }
        }
    }

    @WorkerThread
    private fun loadAllSongs() {
        launch {
            val songs = loadSongs.listSongs(appContext)
            MediaPlayerInstance.playedSongs.clear()
            MediaPlayerInstance.songList = songs
            setAdapter(songs)
        }
    }

    @WorkerThread
    fun searchForSong(query : String) {
        launch {
            val db = SongRoomDatabase.getDatabase(appContext)
            val songs = ArrayList(db.songsDao().searchSongs("%$query%"))
            setAdapter(songs)
        }
    }

    private fun setAdapter(songs : ArrayList<Song>) {
        adapter = SongListAdapter(songs)
        recyclerViewSongList.adapter = adapter
        adapter.notifyDataSetChanged()
        recyclerViewSongList.layoutManager = LinearLayoutManager(appContext)
    }

    private fun askPermission() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        val permissionGranted = PackageManager.PERMISSION_GRANTED
        if (permission != permissionGranted) {
            val builder = AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle(resources.getString(R.string.record_audio_permission_necessary))
                    .setMessage(resources.getString(R.string.permission_explanation))
                    .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { _, _ ->
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 101)
                    })
                    .create()
            builder.show()
        }
    }
}