package com.racpanapps.yourbeats.mainMenu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import com.racpanapps.yourbeats.R
import com.racpanapps.yourbeats.classes.InitializeBannerAds
import com.racpanapps.yourbeats.classes.MediaPlayerInstance
import com.racpanapps.yourbeats.gameFiles.SongList
import com.racpanapps.yourbeats.musicPlayer.FragmentPlaylists
import com.racpanapps.yourbeats.musicPlayer.FragmentSongList
import com.racpanapps.yourbeats.musicPlayer.PlaylistAdapter
import java.util.*

class MusicPlayer : AppCompatActivity() {

    private lateinit var textInputSongSearch: TextInputEditText
    private lateinit var buttonShuffle: Button
    private lateinit var buttonPrevious: Button
    private lateinit var buttonNext: Button
    private lateinit var buttonRepeat: Button
    private lateinit var linearLayoutBannerMusicPlayer: LinearLayout
    private lateinit var tabLayoutPages: TabLayout
    private lateinit var constraintLayoutMusicPlayer : ConstraintLayout
    private var fragmentSongList = FragmentSongList()

    companion object {
        private var instance: MusicPlayer? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }

        var musicPlayerRunning: Boolean = false
        var shuffle: Boolean = false
        var repeat: Boolean = false
        private val appContext = applicationContext()
        private lateinit var buttonPlayPause: Button
        lateinit var textViewSongTitle: TextView
        lateinit var seekBarSongProgress: SeekBar
        lateinit var viewPager2Fragments: ViewPager2

        fun playSongButtonText() {
            buttonPlayPause.text = appContext.resources.getString(R.string.pause)
        }

        fun setSeekBar() {
            if (musicPlayerRunning) {
                seekBarSongProgress.max = MediaPlayerInstance.songLength
                seekBarSongProgress.progress = 0
                val timer = Timer()
                val monitor = object : TimerTask() {
                    override fun run() {
                        while (MediaPlayerInstance.instance != null) {
                            val currentPosition = MediaPlayerInstance.instance?.currentPosition!!
                            seekBarSongProgress.progress = currentPosition
                        }
                    }
                }
                timer.schedule(monitor, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.music_player)

        declareVariables()
        initializeFragments()
        loadListeners()
        InitializeBannerAds.loadAds(application, this, linearLayoutBannerMusicPlayer)
        InitializeBannerAds.setBackground(this, constraintLayoutMusicPlayer)
    }

    override fun onStart() {
        super.onStart()
        musicPlayerRunning = true
        SongList.songListRunning = false
    }

    override fun onStop() {
        super.onStop()
        musicPlayerRunning = false
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainMenu::class.java))
    }

    private fun initializeFragments() {
        val viewPagerAdapter = ViewPagerAdapter(this)
        viewPager2Fragments.adapter = viewPagerAdapter
        TabLayoutMediator(tabLayoutPages, viewPager2Fragments) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.music)
                1 -> tab.text = getString(R.string.playlists)
            }
        }.attach()
    }

    inner class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(
        fragmentActivity
    ) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> FragmentSongList()
                1 -> FragmentPlaylists()
                else -> FragmentSongList()
            }
        }

        override fun getItemCount(): Int {
            return 2
        }
    }

    private fun declareVariables() {
        textViewSongTitle = findViewById(R.id.textViewSongTitle)
        textInputSongSearch = findViewById(R.id.textInputSongSearch)
        buttonShuffle = findViewById(R.id.buttonShuffle)
        buttonPrevious = findViewById(R.id.buttonPrevious)
        buttonPlayPause = findViewById(R.id.buttonPlayPause)
        buttonNext = findViewById(R.id.buttonNext)
        buttonRepeat = findViewById(R.id.buttonRepeat)
        seekBarSongProgress = findViewById(R.id.seekBarSongProgress)
        linearLayoutBannerMusicPlayer = findViewById(R.id.linearLayoutBannerMusicPlayer)
        tabLayoutPages = findViewById(R.id.tabLayoutPages)
        viewPager2Fragments = findViewById(R.id.viewPager2Fragments)
        constraintLayoutMusicPlayer = findViewById(R.id.constraintLayoutMusicPlayer)
        MediaPlayerInstance.appContext = this
    }

    @SuppressLint("ResourceType")
    private fun loadListeners() {
        textInputSongSearch.doOnTextChanged { text, _, _, _ ->
            if (text != null && text.length > 3) {
                if (MediaPlayerInstance.playlistName == "null") {
                    fragmentSongList.searchForSong(text.toString())
                } else {
                    fragmentSongList.searchForSongWithPlaylist(text.toString(), MediaPlayerInstance.playlistName)
                }
            } else {
                if (MediaPlayerInstance.playlistName == "null") {
                    fragmentSongList.loadAllSongs()
                } else {
                    var arrayOfPlaylist = ArrayList<String>()
                    arrayOfPlaylist.add(MediaPlayerInstance.playlistName)
                    val playlistAdapter = PlaylistAdapter(arrayOfPlaylist)
                    playlistAdapter.loadAllSongsWithPlaylist(arrayOfPlaylist[0], appContext)
                }
            }
        }
        buttonShuffle.setOnClickListener {
            if (MediaPlayerInstance.instance == null) {
                MediaPlayerInstance.randomSong()
                playSongButtonText()
            }
            if (!shuffle) {
                shuffle = true
                buttonShuffle.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                            R.color.black
                    )
                )
                buttonShuffle.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                            R.color.cyan
                    )
                )
            } else {
                shuffle = false
                buttonShuffle.setTextColor(ContextCompat.getColor(applicationContext, R.color.blue))
                buttonShuffle.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                            R.color.transparent
                    )
                )
            }
        }
        buttonRepeat.setOnClickListener {
            if (!repeat) {
                repeat = true
                buttonRepeat.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
                buttonRepeat.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                            R.color.cyan
                    )
                )
            } else {
                repeat = false
                buttonRepeat.setTextColor(ContextCompat.getColor(applicationContext, R.color.blue))
                buttonRepeat.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                            R.color.transparent
                    )
                )
            }
        }
        buttonPlayPause.setOnClickListener {
            if (MediaPlayerInstance.instance == null) {
                MediaPlayerInstance.nextSong()
            }
            if (buttonPlayPause.text == resources.getString(R.string.play)) {
                playSongButtonText()
                if (MediaPlayerInstance.instance != null) {
                    MediaPlayerInstance.play()
                }
            } else {
                buttonPlayPause.text = resources.getString(R.string.play)
                MediaPlayerInstance.pause()
            }
        }
        buttonNext.setOnClickListener {
            MediaPlayerInstance.nextSong()
            playSongButtonText()
        }
        buttonPrevious.setOnClickListener {
            MediaPlayerInstance.previousSong()
            playSongButtonText()
        }
        seekBarSongProgress.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (MediaPlayerInstance.instance != null && fromUser) {
                    seekBarSongProgress.progress = MediaPlayerInstance.songProgress
                    MediaPlayerInstance.setSeekBarProgress(progress)
                }
            }
        })
    }
}