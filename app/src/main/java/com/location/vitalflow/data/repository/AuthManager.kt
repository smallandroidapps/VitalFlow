package com.location.vitalflow.data.repository

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult {
    data class Success(val email: String, val displayName: String?, val photoUrl: String?) : AuthResult()
    data class Error(val message: String, val code: String? = null) : AuthResult()
    object Cancelled : AuthResult()
}

@Singleton
class AuthManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferenceManager: PreferenceManager
) {
    private val credentialManager = CredentialManager.create(context)

    /**
     * Executes the Google Sign-In flow using the modern Credential Manager API.
     * @param activityContext Must be an Activity context to show the bottom sheet.
     */
    suspend fun signIn(activityContext: Context): AuthResult {
        /**
         * CRITICAL: To fix "TYPE_NO_CREDENTIAL", ensure:
         * 1. You have a 'Web application' Client ID in Google Cloud Console.
         * 2. Your app's SHA-1 (from gradle signingReport) is added to the Android client in the console.
         * 3. The serverClientId below matches the 'Web application' Client ID.
         */
        val serverClientId = "729800361f14-example.apps.googleusercontent.com" 
        
        if (serverClientId.contains("example")) {
            return AuthResult.Error("Configuration Error: Please update AuthManager.kt with your Google Cloud Web Client ID.")
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(activityContext, request)
            handleCredential(result.credential)
        } catch (e: GetCredentialException) {
            when (e) {
                is GetCredentialCancellationException -> AuthResult.Cancelled
                else -> {
                    val msg = if (e.type == "android.credentials.GetCredentialException.TYPE_NO_CREDENTIAL") {
                        "No Google Account found or SHA-1/Client ID mismatch. Check Google Cloud Console."
                    } else e.message ?: "Auth failed"
                    AuthResult.Error(msg, e.type)
                }
            }
        } catch (e: Exception) {
            AuthResult.Error("Unexpected error: ${e.message}")
        }
    }

    private suspend fun handleCredential(credential: Credential): AuthResult {
        return when (credential) {
            is GoogleIdTokenCredential -> {
                val email = credential.id
                preferenceManager.setUserEmail(email)
                AuthResult.Success(
                    email = email,
                    displayName = credential.displayName,
                    photoUrl = credential.profilePictureUri?.toString()
                )
            }
            else -> AuthResult.Error("Unsupported account type: ${credential.type}")
        }
    }

    suspend fun signOut() {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            preferenceManager.setUserEmail(null)
        } catch (e: Exception) {
            // Log error
        }
    }
}
