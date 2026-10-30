package com.example.ui.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object HapticHelper {
  fun click(context: Context) {
    SoundHelper.init(context)
    SoundHelper.playClick()
    vibrate(context, 20L)
  }

  fun success(context: Context) {
    SoundHelper.init(context)
    SoundHelper.playSuccess()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val vibrator = getVibrator(context)
      val timings = longArrayOf(0, 40, 60, 80)
      val amplitudes = intArrayOf(0, 150, 0, 255)
      vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
    } else {
      vibrate(context, 100L)
    }
  }

  fun buzzer(context: Context) {
    SoundHelper.init(context)
    SoundHelper.playBuzzer()
    vibrate(context, 150L)
  }

  fun tap(context: Context) {
    SoundHelper.init(context)
    SoundHelper.playTap()
    vibrate(context, 12L)
  }

  fun vibrateOnly(context: Context, milliseconds: Long = 15L) {
    vibrate(context, milliseconds)
  }

  private fun vibrate(context: Context, milliseconds: Long) {
    val vibrator = getVibrator(context) ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
      @Suppress("DEPRECATION")
      vibrator.vibrate(milliseconds)
    }
  }

  private fun getVibrator(context: Context): Vibrator? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
      manager?.defaultVibrator
    } else {
      @Suppress("DEPRECATION")
      context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
  }
}
