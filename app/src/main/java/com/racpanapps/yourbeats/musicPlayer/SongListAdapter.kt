package com.racpanapps.yourbeats.musicPlayer

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.racpanapps.yourbeats.R
import com.racpanapps.yourbeats.classes.MediaPlayerInstance
import com.racpanapps.yourbeats.gameFiles.GameLevel
import com.racpanapps.yourbeats.mainMenu.MusicPlayer
import com.racpanapps.yourbeats.mainMenu.MusicPlayer.Companion.musicPlayerRunning
import com.racpanapps.yourbeats.songsDB.Song

class SongListAdapter(private val dataSet: ArrayList<Song>) : RecyclerView.Adapter<SongListAdapter.ViewHolder>(){

    private lateinit var context : Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val textView : TextView = view.findViewById(R.id.textViewRecyclerViewSongTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.recycler_view_item_song_list,
            parent,
            false
        )
        context = view.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = dataSet[position].title
        MediaPlayerInstance.appContext = context
        holder.itemView.setOnClickListener {
            if (musicPlayerRunning) {
                MediaPlayerInstance.startSong(dataSet[position].listId.toLong())
                MusicPlayer.playSongButtonText()
            } else {
                val intent = Intent(context, GameLevel::class.java)
                intent.putExtra("listId", dataSet[position].listId)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount() : Int {
        return dataSet.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}