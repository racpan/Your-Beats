package com.racpanapps.yourbeats.musicPlayer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.annotation.WorkerThread
import androidx.recyclerview.widget.RecyclerView
import com.racpanapps.yourbeats.R
import com.racpanapps.yourbeats.songsDB.Song
import com.racpanapps.yourbeats.songsDB.SongRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EditPlaylistAdapter(private val dataSet : ArrayList<Song>, private val playlistName : String, private val appContext : Context) : RecyclerView.Adapter<EditPlaylistAdapter.ViewHolder>(), CoroutineScope {

    private var job : Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var checkBoxSongName: CheckBox = view.findViewById(R.id.checkBoxSongName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_playlist_song_choices, parent, false)
        return ViewHolder(view)
    }

    @WorkerThread
    override fun onBindViewHolder(holder : ViewHolder, position : Int) {
        val db = SongRoomDatabase.getDatabase(appContext)
        launch {
            val songs = db.songsDao().checkIfInPlaylist(playlistName, dataSet[position].songId)
            holder.checkBoxSongName.text = dataSet[position].title
            if (songs.count() > 0) { holder.checkBoxSongName.isChecked = true }
        }
        holder.checkBoxSongName.setOnCheckedChangeListener { _, isChecked ->
            launch {
                if (isChecked) {
                    val check = db.songsDao().checkIfInPlaylist(playlistName, dataSet[position].songId)
                    if (check.count() == 0) {
                        val playlistItem = PlaylistItem(0, playlistName, dataSet[position].songId)
                        db.songsDao().insertPlaylistItem(playlistItem)
                    }
                } else {
                    db.songsDao().deleteItemFromPlaylist(playlistName, dataSet[position].songId)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}