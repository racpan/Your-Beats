package com.racpanapps.yourbeats.musicPlayer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.WorkerThread
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.racpanapps.yourbeats.R
import com.racpanapps.yourbeats.classes.MediaPlayerInstance
import com.racpanapps.yourbeats.songsDB.LoadSongs
import com.racpanapps.yourbeats.songsDB.Song
import com.racpanapps.yourbeats.songsDB.SongRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class FragmentSongList : Fragment(), CoroutineScope {

    companion object {
        private var loadSongs = LoadSongs()
        private lateinit var appContext : Context
        private lateinit var recyclerView : RecyclerView
        private lateinit var adapter : SongListAdapter

        fun setAdapter(songs : ArrayList<Song>) {
            MediaPlayerInstance.playedSongs.clear()
            MediaPlayerInstance.songList = songs
            adapter = SongListAdapter(songs)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            recyclerView.layoutManager = LinearLayoutManager(appContext)
        }

        fun setRecyclerViewCache(count : Int) {
            recyclerView.setItemViewCacheSize(count)
        }
    }

    private var job : Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_song_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        appContext = view.context
        recyclerView = view.findViewById(R.id.recyclerViewMusicPlayerSongList)
        loadAllSongs()
    }

    @WorkerThread
    fun searchForSong(query : String) {
        launch {
            val db = SongRoomDatabase.getDatabase(appContext)
            val songs = ArrayList(db.songsDao().searchSongs("%$query%"))
            adapter = SongListAdapter(songs)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    @WorkerThread
    fun searchForSongWithPlaylist(query : String, playlistName : String) {
        launch {
            val db = SongRoomDatabase.getDatabase(appContext)
            val songs = ArrayList(db.songsDao().searchSongsFromPlaylist("%$query%", playlistName))
            adapter = SongListAdapter(songs)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    fun loadAllSongs() {
        val songs = loadSongs.listSongs(appContext)
        setAdapter(songs)
    }
}
