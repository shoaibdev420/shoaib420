package com.example.ludostealth.logic
import com.example.ludostealth.dialogs.GameSettingsState
import android.content.Context
import android.media.MediaPlayer
import com.example.ludostealth.R

object MenuMusic {

    private var mediaPlayer: MediaPlayer? = null

    fun start(context: Context) {
        if (!GameSettingsState.soundOn) return
        if (mediaPlayer != null) return

        mediaPlayer = MediaPlayer.create(context, R.raw.bg_music)
        mediaPlayer?.isLooping = true
        mediaPlayer?.setVolume(0.5f, 0.5f)
        mediaPlayer?.start()
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}