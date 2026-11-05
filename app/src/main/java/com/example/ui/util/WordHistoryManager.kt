package com.example.ui.util

import android.content.Context
import org.json.JSONArray
import kotlin.random.Random

object WordHistoryManager {
  private const val PREFS_NAME = "tiktok_word_history_prefs"
  private const val MAX_HISTORY_PER_KEY = 120

  fun getHistory(context: Context, categoryKey: String): List<String> {
    val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val raw = prefs.getString("hist_$categoryKey", null) ?: return emptyList()
    return try {
      val arr = JSONArray(raw)
      val list = ArrayList<String>(arr.length())
      for (i in 0 until arr.length()) {
        val w = arr.optString(i)
        if (w.isNotEmpty()) list.add(w)
      }
      list
    } catch (_: Exception) {
      emptyList()
    }
  }

  fun markWordUsed(context: Context, categoryKey: String, word: String) {
    val current = getHistory(context, categoryKey).toMutableList()
    current.remove(word)
    current.add(word) // Most recently used at the end
    val trimmed = if (current.size > MAX_HISTORY_PER_KEY) {
      current.takeLast(MAX_HISTORY_PER_KEY)
    } else {
      current
    }
    saveHistory(context, categoryKey, trimmed)
  }

  private fun saveHistory(context: Context, categoryKey: String, list: List<String>) {
    val arr = JSONArray()
    list.forEach { arr.put(it) }
    context.applicationContext
      .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
      .edit()
      .putString("hist_$categoryKey", arr.toString())
      .apply()
  }

  /**
   * Returns a deck of words where unseen words are prioritized first, and recently
   * seen words have a very low probability of appearing early (pushed to the end of the queue).
   */
  fun getSmartShuffledWords(
    context: Context,
    categoryKey: String,
    allWords: List<String>
  ): List<String> {
    if (allWords.isEmpty()) return emptyList()
    var history = getHistory(context, categoryKey)

    // If over 80% of the category has been seen, forget the oldest half so the cycle stays fresh
    if (history.size >= (allWords.size * 0.8f).toInt().coerceAtLeast(1)) {
      history = history.takeLast((allWords.size / 3).coerceAtLeast(1))
      saveHistory(context, categoryKey, history)
    }

    val historySet = history.toSet()
    val unseen = allWords.filter { it !in historySet }.shuffled().toMutableList()
    // Seen words ordered from oldest seen to most recently seen
    val seenOrdered = history.filter { it in allWords }.toMutableList()

    // Give older seen words a small (12%) chance of sneaking into the second half of unseen words
    val result = ArrayList<String>(allWords.size)
    result.addAll(unseen)
    for (seenWord in seenOrdered) {
      if (result.size > 6 && Random.nextFloat() < 0.12f) {
        val insertIdx = Random.nextInt(result.size / 2, result.size)
        result.add(insertIdx, seenWord)
      } else {
        result.add(seenWord)
      }
    }
    return result
  }

  /**
   * Picks a single item with anti-repeat history weighting (ideal for El Impostor).
   */
  fun <T> pickSmartItem(
    context: Context,
    categoryKey: String,
    items: List<T>,
    wordSelector: (T) -> String
  ): T {
    val orderedWords = getSmartShuffledWords(
      context = context,
      categoryKey = categoryKey,
      allWords = items.map(wordSelector)
    )
    val chosenWord = orderedWords.firstOrNull() ?: wordSelector(items.random())
    markWordUsed(context, categoryKey, chosenWord)
    return items.firstOrNull { wordSelector(it) == chosenWord } ?: items.random()
  }
}
