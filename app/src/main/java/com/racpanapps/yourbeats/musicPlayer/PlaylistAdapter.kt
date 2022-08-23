package com.racpanapps.yourbeats.musicPlayer

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.WorkerThread
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

class PlaylistAdapter(private val dataSet : ArrayList<String>) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>(),
    CoroutineScope {

    private lateinit var appContext : Context
    private var oldPlaylistName : String = ""
    private var job : Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var linearLayoutNewPlaylist : LinearLayout = view.findViewById(R.id.linearLayoutNewPlaylist)
        var linearLayoutEnterPlaylistName : LinearLayout = view.findViewById(R.id.linearLayoutEnterPlaylistName)
        var linearLayoutEditPlaylist : LinearLayout = view.findViewById(R.id.linearLayoutEditPlaylist)
        var buttonNewPlaylist : Button = view.findViewById(R.id.buttonNewPlaylist)
        var editTextPlaylistName : EditText = view.findViewById(R.id.editTextPlaylistName)
        var buttonSavePlaylistName : Button = view.findViewById(R.id.buttonSavePlaylistName)
        var textViewPlaylistName : TextView = view.findViewById(R.id.textViewPlaylistName)
        var buttonEditPlaylist : Button = view.findViewById(R.id.buttonEditPlaylist)
        var buttonDeletePlaylist : Button = view.findViewById(R.id.buttonDeletePlaylist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_playlists, parent, false)
        appContext = view.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder : ViewHolder, position : Int) {
        if (dataSet[position] == "null") {
            buttonNewPlaylist(holder)
        } else {
            editPlaylist(holder, position)
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    private fun buttonNewPlaylist(holder : ViewHolder) {
        holder.linearLayoutEnterPlaylistName.visibility = View.INVISIBLE
        holder.linearLayoutEditPlaylist.visibility = View.INVISIBLE
        holder.linearLayoutNewPlaylist.visibility = View.VISIBLE
        holder.buttonNewPlaylist.setOnClickListener { enterPlaylistName(holder) }
    }

    @WorkerThread
    private fun editPlaylist(holder : ViewHolder, position : Int) {
        holder.linearLayoutEnterPlaylistName.visibility = View.INVISIBLE
        holder.linearLayoutEditPlaylist.visibility = View.VISIBLE
        holder.linearLayoutNewPlaylist.visibility = View.INVISIBLE
        val textView = holder.textViewPlaylistName
        val playlist = dataSet[position]
        textView.text = playlist
        textView.setOnLongClickListener {
            oldPlaylistName = textView.text.toString()
            alertDialogEditPlaylistName(holder)
            return@setOnLongClickListener true
        }
        textView.setOnClickListener {
            loadAllSongsWithPlaylist(playlist, appContext)
        }
        holder.buttonEditPlaylist.setOnClickListener {
            val intent = Intent(appContext, CreateEditPlaylist::class.java)
            intent.putExtra(appContext.resources.getString(R.string.playlists), textView.text.toString())
            appContext.startActivity(intent)
        }
        holder.buttonDeletePlaylist.setOnClickListener {
            alertDialogDeletePlaylist(holder)
        }
    }

    private fun enterPlaylistName(holder : ViewHolder) {
        holder.linearLayoutEnterPlaylistName.visibility = View.VISIBLE
        holder.linearLayoutEditPlaylist.visibility = View.INVISIBLE
        holder.linearLayoutNewPlaylist.visibility = View.INVISIBLE
        holder.buttonSavePlaylistName.setOnClickListener {
            val textView = holder.editTextPlaylistName
            if (textView.text.isNotEmpty() && textView.text.toString() != "null") {
                launch {
                    val db = SongRoomDatabase.getDatabase(appContext)
                    val playlistName = textView.text.toString()
                    if (db.songsDao().getPlaylistWithName(playlistName).count() < 1) {
                        val intent = Intent(appContext, CreateEditPlaylist::class.java)
                        intent.putExtra(appContext.resources.getString(R.string.playlists), playlistName)
                        appContext.startActivity(intent)
                    }
                }
            }
        }
    }

    @WorkerThread
    fun loadAllSongsWithPlaylist(playlist : String, context : Context) {
        launch {
            val db = SongRoomDatabase.getDatabase(context)
            val songs = ArrayList(db.songsDao().getAllFromPlaylist(playlist))
            MediaPlayerInstance.playedSongs.clear()
            MediaPlayerInstance.songList = songs
            MediaPlayerInstance.playlistName = playlist
            FragmentSongList.setAdapter(songs)
            FragmentSongList.setRecyclerViewCache(songs.count())
            MusicPlayer.viewPager2Fragments.currentItem = 0
        }
    }

    @WorkerThread
    private fun alertDialogEditPlaylistName(holder : ViewHolder) {
        val builder = AlertDialog.Builder(appContext)
        builder.setTitle(appContext.resources.getString(R.string.playlists))
        val input = EditText(appContext)
        val layout = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        input.layoutParams = layout
        builder.setView(input)
        input.setText(oldPlaylistName)
        builder.setMessage(appContext.resources.getString(R.string.enter_playlist_name))
            .setCancelable(false)
            .setPositiveButton(appContext.resources.getString(R.string.save)) { _: DialogInterface, _: Int ->
                launch {
                    val db = SongRoomDatabase.getDatabase(appContext)
                    db.songsDao().updatePlaylistName(input.text.toString(), oldPlaylistName)
                    holder.textViewPlaylistName.text = input.text
                }
            }
            .setNegativeButton(appContext.resources.getString(R.string.cancel)) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    @WorkerThread
    private fun alertDialogDeletePlaylist(holder : ViewHolder) {
        val builder = AlertDialog.Builder(appContext)
        builder.setTitle(appContext.resources.getString(R.string.delete))
        builder.setMessage(appContext.resources.getString(R.string.delete_playlist))
            .setCancelable(false)
            .setPositiveButton(appContext.resources.getString(R.string.delete)) { _: DialogInterface, _: Int ->
                launch {
                    val db = SongRoomDatabase.getDatabase(appContext)
                    db.songsDao().deletePlaylist(holder.textViewPlaylistName.text.toString())
                    reloadPlaylists()
                }
            }
            .setNegativeButton(appContext.resources.getString(R.string.cancel)) { dialog : DialogInterface, _: Int ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun reloadPlaylists() {
        val fragmentPlaylist = FragmentPlaylists()
        fragmentPlaylist.loadPlaylists(appContext)
    }
}