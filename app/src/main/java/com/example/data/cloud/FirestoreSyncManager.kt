package com.example.data.cloud

import android.content.Context
import android.util.Log
import com.example.data.local.UserProfileEntity
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

private const val TAG = "FirestoreSync"
private const val FIRESTORE_DB_ID_RES = "firestore_database_id"

class FirestoreSyncManager(private val context: Context) {
  private val db: FirebaseFirestore by lazy {
    val resId = context.resources.getIdentifier(FIRESTORE_DB_ID_RES, "string", context.packageName)
    val dbId = if (resId != 0) context.getString(resId) else ""
    if (dbId.isNotBlank()) {
      FirebaseFirestore.getInstance(dbId)
    } else {
      FirebaseFirestore.getInstance()
    }
  }

  suspend fun syncUserProfileToCloud(profile: UserProfileEntity): Result<Unit> {
    return try {
      if (FirebaseApp.getApps(context).isEmpty()) {
        return Result.failure(IllegalStateException("Servicio en la nube no disponible."))
      }

      val currentUser = Firebase.auth.currentUser ?: return Result.failure(
        IllegalStateException("Sesión no iniciada.")
      )

      val uid = currentUser.uid
      val userDoc = db.collection("users").document(uid)

      val profilePayload = hashMapOf<String, Any>(
        "userId" to uid,
        "nickname" to profile.nickname,
        "avatar" to profile.avatar,
        "level" to profile.level,
        "xp" to profile.xp,
        "diamonds" to profile.diamonds,
        "totalWins" to profile.totalWins,
        "totalLosses" to profile.totalLosses,
        "dailyScore" to profile.dailyScore,
        "streakDays" to profile.streakDays,
        "best10sDiffMs" to profile.best10sDiffMs,
        "bestTapPerSec" to profile.bestTapPerSec.toDouble(),
        "tournamentTrophies" to profile.tournamentTrophies,
        "updatedAt" to FieldValue.serverTimestamp()
      )

      userDoc.set(profilePayload, SetOptions.merge()).await()

      // Update public leaderboard entry
      val leaderboardDoc = db.collection("leaderboard").document(uid)
      val leaderboardPayload = hashMapOf<String, Any>(
        "userId" to uid,
        "nickname" to profile.nickname,
        "avatar" to profile.avatar,
        "score" to profile.dailyScore,
        "countryFlag" to "🌎",
        "badge" to if (profile.dailyScore > 5000) "Campeón" else "Competidor",
        "updatedAt" to FieldValue.serverTimestamp()
      )
      leaderboardDoc.set(leaderboardPayload, SetOptions.merge()).await()

      Log.d(TAG, "Progreso guardado automáticamente en la cuenta del usuario")
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Error al guardar progreso en la nube", e)
      Result.failure(e)
    }
  }
}
