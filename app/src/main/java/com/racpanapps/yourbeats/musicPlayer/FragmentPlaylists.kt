package com.racpanapps.yourbeats.musicPlayer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.WorkerThread
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.racpanapps.yourbeats.R
import com.racpanapps.yourbeats.classes.MediaPlayerInstance
import com.racpanapps.yourbeats.mainMenu.MusicPlayer
import com.racpanapps.yourbeats.songsDB.SongRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class FragmentPlaylists : Fragment(), CoroutineScope {

    companion object {
        private lateinit var recyclerView: RecyclerView
    }

    private lateinit var appView: View
    private lateinit var playlistAdapter : PlaylistAdapter
    private lateinit var buttonAllSongs: Button
    private lateinit var appContext : Context
    private var job : Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_playlists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        appContext = view.context
        appView = view
        buttonAllSongs = view.findViewById(R.id.buttonAllSongs)
        buttonAllSongs.setOnClickListener {
            val fragmentSongList = FragmentSongList()
            MusicPlayer.viewPager2Fragments.currentItem = 0
            fragmentSongList.loadAllSongs()
            MediaPlayerInstance.playlistName = "null"
        }
        loadPlaylists(view.context)
    }

    override fun onResume() {
        super.onResume()
        loadPlaylists(appContext)
    }

    @WorkerThread
    fun loadPlaylists(appContext : Context) {
        launch {
            val db = SongRoomDatabase.getDatabase(appContext)
            var playlists = ArrayList<String>()
            try {
                val list = db.songsDao().getAllPlaylists()
                list.forEach { it ->
                    playlists.add(it)
                }
            } catch (e : Exception) {}
            playlists.add("null")
            playlistAdapter = PlaylistAdapter(playlists)
            recyclerView = appView.findViewById(R.id.recyclerViewMusicPlayerPlaylists)
            recyclerView.adapter = playlistAdapter
            playlistAdapter.notifyDataSetChanged()
            recyclerView.layoutManager = LinearLayoutManager(appContext)
        }
    }
}