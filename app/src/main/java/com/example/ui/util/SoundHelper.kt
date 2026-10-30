package com.example.ui.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.Executors
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

object SoundHelper {
  private const val PREFS_NAME = "tiktok_games_sound_prefs"
  private const val KEY_MUTED = "is_muted"
  private const val SAMPLE_RATE = 22050

  private val _isMuted = MutableStateFlow(false)
  val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

  private var initialized = false
  private val soundExecutor = Executors.newFixedThreadPool(3)

  fun init(context: Context) {
    if (!initialized) {
      val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
      _isMuted.value = prefs.getBoolean(KEY_MUTED, false)
      initialized = true
    }
  }

  fun toggleMute(context: Context) {
    init(context)
    val newMuted = !_isMuted.value
    _isMuted.value = newMuted
    context.applicationContext
      .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
      .edit()
      .putBoolean(KEY_MUTED, newMuted)
      .apply()
    if (!newMuted) {
      playClick()
    }
  }

  fun playClick() {
    if (_isMuted.value) return
    playSynthesized {
      generateTone(freqStart = 680.0, freqEnd = 880.0, durationMs = 35, volume = 0.28f)
    }
  }

  fun playTap() {
    if (_isMuted.value) return
    playSynthesized {
      generateTone(freqStart = 520.0, freqEnd = 660.0, durationMs = 28, volume = 0.25f)
    }
  }

  fun playTick(urgency: Float = 0f) {
    if (_isMuted.value) return
    val clamped = urgency.coerceIn(0f, 1f)
    val baseFreq = 750.0 + (clamped * 450.0)
    playSynthesized {
      generateTone(freqStart = baseFreq, freqEnd = baseFreq * 0.85, durationMs = 32, volume = 0.35f)
    }
  }

  fun playCountdownBeep(isFinal: Boolean = false) {
    if (_isMuted.value) return
    playSynthesized {
      if (isFinal) {
        generateTone(freqStart = 880.0, freqEnd = 1046.5, durationMs = 160, volume = 0.45f)
      } else {
        generateTone(freqStart = 587.33, freqEnd = 587.33, durationMs = 110, volume = 0.38f)
      }
    }
  }

  fun playHintHigher() {
    if (_isMuted.value) return
    playSynthesized {
      val part1 = generateTone(freqStart = 440.0, freqEnd = 554.37, durationMs = 75, volume = 0.35f)
      val part2 = generateTone(freqStart = 554.37, freqEnd = 659.25, durationMs = 95, volume = 0.40f)
      part1 + part2
    }
  }

  fun playHintLower() {
    if (_isMuted.value) return
    playSynthesized {
      val part1 = generateTone(freqStart = 587.33, freqEnd = 440.0, durationMs = 75, volume = 0.35f)
      val part2 = generateTone(freqStart = 440.0, freqEnd = 329.63, durationMs = 95, volume = 0.40f)
      part1 + part2
    }
  }

  fun playPass() {
    if (_isMuted.value) return
    playSynthesized {
      generateTone(freqStart = 420.0, freqEnd = 280.0, durationMs = 90, volume = 0.32f)
    }
  }

  fun playSuccess() {
    if (_isMuted.value) return
    playSynthesized {
      val n1 = generateTone(523.25, 523.25, 65, 0.38f) // C5
      val n2 = generateTone(659.25, 659.25, 65, 0.40f) // E5
      val n3 = generateTone(783.99, 1046.50, 120, 0.45f) // G5 -> C6
      n1 + n2 + n3
    }
  }

  fun playVictory() {
    if (_isMuted.value) return
    playSynthesized {
      val n1 = generateTone(523.25, 523.25, 80, 0.42f) // C5
      val n2 = generateTone(659.25, 659.25, 80, 0.42f) // E5
      val n3 = generateTone(783.99, 783.99, 80, 0.45f) // G5
      val n4 = generateTone(1046.50, 1046.50, 220, 0.50f) // C6
      n1 + n2 + n3 + n4
    }
  }

  fun playBuzzer() {
    if (_isMuted.value) return
    playSynthesized {
      val n1 = generateTone(240.0, 190.0, 110, 0.42f)
      val n2 = generateTone(190.0, 140.0, 160, 0.45f)
      n1 + n2
    }
  }

  fun playExplosion() {
    if (_isMuted.value) return
    playSynthesized {
      val durationMs = 520
      val numSamples = (SAMPLE_RATE * durationMs) / 1000
      val pcm = ShortArray(numSamples)
      var phase = 0.0
      for (i in 0 until numSamples) {
        val progress = i.toDouble() / numSamples
        val envelope = exp(-3.8 * progress)
        val rumbleFreq = 110.0 * (1.0 - 0.65 * progress)
        phase += 2.0 * PI * rumbleFreq / SAMPLE_RATE
        val rumble = sin(phase) * 0.6
        val noise = (Random.nextDouble(-1.0, 1.0)) * 0.6
        val sample = ((rumble + noise) * envelope * 0.65).coerceIn(-1.0, 1.0)
        pcm[i] = (sample * Short.MAX_VALUE).toInt().toShort()
      }
      pcm
    }
  }

  private fun generateTone(
    freqStart: Double,
    freqEnd: Double,
    durationMs: Int,
    volume: Float
  ): ShortArray {
    val numSamples = ((SAMPLE_RATE * durationMs) / 1000).coerceAtLeast(1)
    val samples = ShortArray(numSamples)
    var phase = 0.0
    val attackSamples = (numSamples * 0.08).toInt().coerceAtLeast(1)
    val releaseSamples = (numSamples * 0.25).toInt().coerceAtLeast(1)

    for (i in 0 until numSamples) {
      val t = i.toDouble() / numSamples
      val freq = freqStart + (freqEnd - freqStart) * t
      phase += 2.0 * PI * freq / SAMPLE_RATE

      val env = when {
        i < attackSamples -> i.toFloat() / attackSamples
        i > numSamples - releaseSamples -> (numSamples - i).toFloat() / releaseSamples
        else -> 1f
      }

      // Add a subtle harmonic for richer arcade sound
      val wave = (sin(phase) * 0.82 + sin(phase * 2.0) * 0.18)
      val value = (wave * volume * env).coerceIn(-1.0, 1.0)
      samples[i] = (value * Short.MAX_VALUE).toInt().toShort()
    }
    return samples
  }

  private fun playSynthesized(generator: () -> ShortArray) {
    try {
      soundExecutor.execute {
        try {
          if (_isMuted.value) return@execute
          val pcm = generator()
          if (pcm.isEmpty()) return@execute
          val byteCount = pcm.size * 2
          val minBuf = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
          )
          val bufferSize = maxOf(byteCount, minBuf)
          val track = AudioTrack.Builder()
            .setAudioAttributes(
              AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            )
            .setAudioFormat(
              AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(SAMPLE_RATE)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

          track.write(pcm, 0, pcm.size)
          track.play()
          val sleepMs = ((pcm.size * 1000L) / SAMPLE_RATE) + 40L
          Thread.sleep(sleepMs)
          track.stop()
          track.release()
        } catch (_: Exception) {
          // Ignore audio hardware exceptions in unit test / headless environments
        }
      }
    } catch (_: Exception) {
    }
  }
}
