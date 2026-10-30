package com.example.ui.util

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "AuthHelper"
private const val WEB_CLIENT_ID_RES = "default_web_client_id"

object AuthHelper {
  fun getSignedInAccountLabel(context: Context): String? {
    return try {
      if (FirebaseApp.getApps(context).isEmpty()) return null
      val user = Firebase.auth.currentUser ?: return null
      user.email?.takeIf { it.isNotBlank() }
        ?: user.displayName?.takeIf { it.isNotBlank() }
        ?: "Cuenta de Google conectada"
    } catch (_: Exception) {
      null
    }
  }

  fun onGoogleSignInClicked(
    context: Context,
    scope: CoroutineScope,
    onAuthSuccess: (String) -> Unit,
    onAuthError: (String) -> Unit,
    onAuthCancelled: () -> Unit = {}
  ) {
    val clientId = try {
      val resId = context.resources.getIdentifier(WEB_CLIENT_ID_RES, "string", context.packageName)
      if (resId != 0) context.getString(resId) else ""
    } catch (_: Exception) {
      ""
    }

    if (clientId.isBlank()) {
      onAuthError("No se pudo iniciar sesión con Google en este momento. Tus datos siguen guardados en tu dispositivo.")
      return
    }

    val activity = context as? Activity
    if (activity == null) {
      onAuthError("No se pudo abrir la ventana de inicio de sesión.")
      return
    }

    val credentialManager = CredentialManager.create(context)
    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
    val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

    scope.launch {
      try {
        val result = credentialManager.getCredential(activity, request)
        val credential = result.credential
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
          val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
          val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
          Firebase.auth.signInWithCredential(authCredential).await()
          val label = getSignedInAccountLabel(context) ?: "Cuenta de Google"
          Log.d(TAG, "Inicio de sesión con Google exitoso: $label")
          onAuthSuccess(label)
        } else {
          onAuthError("No se pudo verificar la cuenta seleccionada.")
        }
      } catch (e: GetCredentialCancellationException) {
        Log.w(TAG, "Inicio de sesión cancelado por el usuario", e)
        onAuthCancelled()
      } catch (e: Exception) {
        Log.e(TAG, "Fallo al iniciar sesión con Google", e)
        onAuthError("No se pudo conectar con Google. Tus datos siguen guardados en este dispositivo.")
      }
    }
  }

  fun signOut(
    context: Context,
    scope: CoroutineScope,
    onSignOutComplete: () -> Unit
  ) {
    val credentialManager = CredentialManager.create(context)
    try {
      if (FirebaseApp.getApps(context).isNotEmpty()) {
        Firebase.auth.signOut()
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error al cerrar sesión", e)
    }
    scope.launch {
      try {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
      } catch (e: Exception) {
        Log.e(TAG, "Error al limpiar credenciales", e)
      } finally {
        onSignOutComplete()
      }
    }
  }
}
