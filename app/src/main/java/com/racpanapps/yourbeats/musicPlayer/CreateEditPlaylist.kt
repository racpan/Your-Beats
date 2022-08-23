package com.racpanapps.yourbeats.musicPlayer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.racpanapps.yourbeats.R
import com.racpanapps.yourbeats.classes.InitializeBannerAds
import com.racpanapps.yourbeats.mainMenu.MusicPlayer
import com.racpanapps.yourbeats.songsDB.LoadSongs
import com.racpanapps.yourbeats.songsDB.SongRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CreateEditPlaylist : AppCompatActivity(), CoroutineScope {

    private lateinit var recyclerViewEditPlaylist : RecyclerView
    private lateinit var adapter : EditPlaylistAdapter
    private lateinit var editTextSearchPlaylist : EditText
    private lateinit var linearLayoutBannerCreateEditPlaylist : LinearLayout
    private lateinit var constraintLayoutCreateEditPlaylist : ConstraintLayout
    private lateinit var appIntent : Intent
    private var loadSongs = LoadSongs()

    private var job : Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_edit_playlist)
        appIntent = intent
        recyclerViewEditPlaylist = findViewById(R.id.recyclerViewEditPlaylist)
        editTextSearchPlaylist = findViewById(R.id.editTextSearchPlaylist)
        linearLayoutBannerCreateEditPlaylist = findViewById(R.id.linearLayoutBannerCreateEditPlaylist)
        constraintLayoutCreateEditPlaylist = findViewById(R.id.contraintLayoutCreateEditPlaylist)
        loadSongs()
        InitializeBannerAds.loadAds(application, this, linearLayoutBannerCreateEditPlaylist)
        InitializeBannerAds.setBackground(applicationContext, constraintLayoutCreateEditPlaylist)
        editTextSearchPlaylist.doOnTextChanged { text, _, _, _ ->
            if (text != null && text.length > 3) {
                searchForSong(text.toString(), applicationContext)
            } else {
                loadSongs()
            }
        }
    }

    override fun onBackPressed() {
        backToMusicPlayer()
    }

    private fun backToMusicPlayer() {
        startActivity(Intent(this, MusicPlayer::class.java))
    }

    private fun loadSongs() {
        val songs = loadSongs.listSongs(this)
        adapter = EditPlaylistAdapter(songs, appIntent.getStringExtra(resources.getString(R.string.playlists))!!, applicationContext)
        recyclerViewEditPlaylist.adapter = adapter
        adapter.notifyDataSetChanged()
        recyclerViewEditPlaylist.layoutManager = LinearLayoutManager(this)
    }

    private fun searchForSong(query : String, appContext : Context) {
        launch {
            val db = SongRoomDatabase.getDatabase(appContext)
            val songs = ArrayList(db.songsDao().searchSongs("%$query%"))
            adapter = EditPlaylistAdapter(songs, appIntent.getStringExtra(resources.getString(R.string.playlists))!!, applicationContext)
            recyclerViewEditPlaylist.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
}