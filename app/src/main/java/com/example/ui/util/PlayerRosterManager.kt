package com.example.ui.util

import android.content.Context
import org.json.JSONArray

object PlayerRosterManager {
  private const val PREFS_NAME = "tiktok_party_roster_prefs"
  private const val KEY_PARTY_PLAYERS = "party_players_json"
  private const val KEY_DUEL_P1 = "duel_p1_name"
  private const val KEY_DUEL_P2 = "duel_p2_name"

  fun getPartyPlayers(context: Context, defaultFirstPlayer: String = "Jugador 1"): List<String> {
    val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val raw = prefs.getString(KEY_PARTY_PLAYERS, null)
    if (!raw.isNullOrBlank()) {
      try {
        val arr = JSONArray(raw)
        val result = mutableListOf<String>()
        for (i in 0 until arr.length()) {
          val name = arr.optString(i).trim()
          if (name.isNotEmpty()) {
            result.add(name)
          }
        }
        if (result.size >= 2) {
          return result
        }
      } catch (_: Exception) {
      }
    }
    val first = defaultFirstPlayer.ifBlank { "Jugador 1" }
    return listOf(first, "Amigo 1", "Amigo 2")
  }

  fun savePartyPlayers(context: Context, names: List<String>) {
    val cleaned = names.map { it.trim() }.filter { it.isNotEmpty() }
    if (cleaned.isEmpty()) return
    val arr = JSONArray()
    cleaned.forEach { arr.put(it) }
    context.applicationContext
      .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
      .edit()
      .putString(KEY_PARTY_PLAYERS, arr.toString())
      .apply()
  }

  fun getDuelPlayers(context: Context, defaultP1: String = "Tú"): Pair<String, String> {
    val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val p1 = prefs.getString(KEY_DUEL_P1, null)?.takeIf { it.isNotBlank() } ?: defaultP1.ifBlank { "Tú" }
    val p2 = prefs.getString(KEY_DUEL_P2, null)?.takeIf { it.isNotBlank() } ?: "Amigo"
    return p1 to p2
  }

  fun saveDuelPlayers(context: Context, p1: String, p2: String) {
    val cleanP1 = p1.trim().ifEmpty { "Jugador 1" }
    val cleanP2 = p2.trim().ifEmpty { "Jugador 2" }
    context.applicationContext
      .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
      .edit()
      .putString(KEY_DUEL_P1, cleanP1)
      .putString(KEY_DUEL_P2, cleanP2)
      .apply()
  }
}
