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
  fun onGoogleSignInClicked(
    context: Context,
    scope: CoroutineScope,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit,
    onAuthCancelled: () -> Unit = {}
  ) {
    val clientId = try {
      val resId = context.resources.getIdentifier(WEB_CLIENT_ID_RES, "string", context.packageName)
      if (resId != 0) context.getString(resId) else ""
    } catch (e: Exception) {
      ""
    }

    if (clientId.isBlank()) {
      onAuthError("Configuración de Google no encontrada: default_web_client_id")
      return
    }

    val credentialManager = CredentialManager.create(context)
    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
    val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

    scope.launch {
      try {
        val result = credentialManager.getCredential(context as Activity, request)
        val credential = result.credential
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
          val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
          val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
          Firebase.auth.signInWithCredential(authCredential).await()
          Log.d(TAG, "Inicio de sesión con Google exitoso: ${Firebase.auth.currentUser?.email}")
          onAuthSuccess()
        } else {
          onAuthError("Tipo de credencial no reconocido")
        }
      } catch (e: GetCredentialCancellationException) {
        Log.w(TAG, "Inicio de sesión con Google cancelado por el usuario", e)
        onAuthCancelled()
      } catch (e: Exception) {
        Log.e(TAG, "Fallo al iniciar sesión con Google", e)
        onAuthError(e.localizedMessage ?: "Error al conectar con Google")
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
      Log.e(TAG, "Error al cerrar sesión en Firebase", e)
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
