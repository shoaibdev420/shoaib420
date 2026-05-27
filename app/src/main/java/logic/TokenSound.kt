package com.example.ludostealth.logic
import com.example.ludostealth.dialogs.GameSettingsState
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.ludostealth.R

object TokenSound {

    private var soundPool: SoundPool? = null
    private var soundId: Int = 0
    private var isLoaded = false

    fun init(context: Context) {
        if (soundPool != null) return

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool?.setOnLoadCompleteListener { _, _, status ->
            isLoaded = status == 0
        }

        soundId = soundPool?.load(context, R.raw.token_move, 1) ?: 0
    }

    fun play() {
        if (!GameSettingsState.soundOn) return
        if (!isLoaded) return

        soundPool?.play(
            soundId,
            1f,
            1f,
            1,
            0,
            1f
        )
    }
}