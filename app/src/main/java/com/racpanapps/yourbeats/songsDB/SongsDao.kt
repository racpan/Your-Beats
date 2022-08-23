package com.racpanapps.yourbeats.songsDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.racpanapps.yourbeats.musicPlayer.PlaylistItem

@Dao
interface SongsDao {
    @Query("SELECT * FROM songs")
    suspend fun getSongs() : List<Song>

    @Query("SELECT * FROM songs WHERE title LIKE :query OR artist LIKE :query OR album LIKE :query")
    suspend fun searchSongs(query : String) : List<Song>

    @Query("SELECT * FROM songs WHERE songId = :songId")
    suspend fun searchSongId(songId : Long) : Song

    @Query("SELECT * FROM songs WHERE listId = :listId")
    suspend fun searchListId(listId : Long) : Song

    @Query("SELECT * FROM songs WHERE played = 0")
    suspend fun searchUnplayed() : List<Song>

    @Query("SELECT COUNT(listId) FROM songs")
    suspend fun getItemCount() : Long

    @Query("UPDATE songs SET played = :played WHERE listId = :listId")
    suspend fun updatePlayedSong(played : Boolean, listId : Long)

    @Query("UPDATE songs SET played = 0")
    suspend fun setAllSongsUnplayed()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSong(song : Song)

    @Query("DELETE FROM songs")
    suspend fun deleteAllSongs()

    @Query("SELECT * FROM playlists WHERE playlistName = :playlistName")
    suspend fun getPlaylistWithName(playlistName : String) : List<PlaylistItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistItem(playlistItem : PlaylistItem)

    @Query("DELETE FROM playlists WHERE playlistName = :playlistName")
    suspend fun deletePlaylist(playlistName : String)

    @Query("DELETE FROM playlists WHERE playlistName = :playlistName AND songId = :songId")
    suspend fun deleteItemFromPlaylist(playlistName : String, songId : Long)

    @Query("SELECT playlists.songId FROM playlists WHERE playlistName = :playlistName AND songId = :songId")
    suspend fun checkIfInPlaylist(playlistName : String, songId : Long) : List<Long>

    @Query("SELECT songs.listId AS listId, songs.songId AS songId, songs.uri AS uri, songs.title AS title, songs.artist AS artist, songs.album AS album, songs.played AS played FROM songs INNER JOIN playlists ON songs.songId = playlists.songId WHERE playlists.playlistName = :playlistName")
    suspend fun getAllFromPlaylist(playlistName : String) : List<Song>

    @Query("SELECT songs.listId AS listId, songs.songId AS songId, songs.uri AS uri, songs.title AS title, songs.artist AS artist, songs.album AS album, songs.played AS played FROM songs INNER JOIN playlists ON songs.songId = playlists.songId WHERE title LIKE :query OR artist LIKE :query OR album LIKE :query AND playlistName = :playlistName")
    suspend fun searchSongsFromPlaylist(query : String, playlistName : String) : List<Song>

    @Query("SELECT songs.listId AS listId, songs.songId AS songId, songs.uri AS uri, songs.title AS title, songs.artist AS artist, songs.album AS album, songs.played AS played FROM songs INNER JOIN playlists ON songs.songId = playlists.songId WHERE playlists.playlistName = :playlistName AND songs.played = 0")
    suspend fun searchPlaylistUnplayed(playlistName : String) : List<Song>

    @Query("SELECT DISTINCT playlists.playlistName FROM playlists")
    suspend fun getAllPlaylists() : List<String>

    @Query("UPDATE playlists SET playlistName = :newPlaylistName WHERE playlistName = :oldPlaylistName")
    suspend fun updatePlaylistName(newPlaylistName : String, oldPlaylistName : String)

    @Query("SELECT score FROM highScore WHERE highScore = 'highScore'")
    suspend fun getHighScore() : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighScore(highScore : HighScoreDB)
}