package com.example.ludostealth.logic
import com.example.ludostealth.dialogs.GameSettingsState
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.ludostealth.R

object DiceSound {

    private var soundPool: SoundPool? = null
    private var diceSoundId: Int = 0
    private var isLoaded = false

    fun init(context: Context) {
        if (soundPool != null) return

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool?.setOnLoadCompleteListener { _, _, status ->
            isLoaded = status == 0
        }

        diceSoundId = soundPool?.load(context, R.raw.dice_roll, 1) ?: 0
    }

    fun play() {
        if (!GameSettingsState.soundOn) return
        if (!isLoaded) return

        soundPool?.play(
            diceSoundId,
            1f,
            1f,
            1,
            0,
            1f
        )
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        diceSoundId = 0
        isLoaded = false
    }
}