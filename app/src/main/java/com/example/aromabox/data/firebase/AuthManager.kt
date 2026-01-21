package com.example.aromabox.data.firebase

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

object AuthManager {
    private lateinit var auth: FirebaseAuth
    private lateinit var signInClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    fun initialize(context: Context) {
        auth = FirebaseAuth.getInstance()
        signInClient = Identity.getSignInClient(context)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId("89037242874-1bm7bblrnhnv8e3031hcmrl0vrvifti6.apps.googleusercontent.com") // Il tuo client ID
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    // ========== EMAIL/PASSWORD ==========

    suspend fun registerWithEmail(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun signInWithEmail(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // ========== GOOGLE ==========

    suspend fun beginSignIn(): IntentSender? {
        return try {
            val result = signInClient.beginSignIn(signInRequest).await()
            result.pendingIntent.intentSender
        } catch (e: Exception) {
            null
        }
    }

    suspend fun signInWithIntent(intent: Intent): FirebaseUser? {
        return try {
            val credential = signInClient.getSignInCredentialFromIntent(intent)
            val googleIdToken = credential.googleIdToken
            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            val result = auth.signInWithCredential(firebaseCredential).await()
            result.user
        } catch (e: Exception) {
            null
        }
    }

    fun signOut() {
        auth.signOut()
        try {
            signInClient.signOut()
        } catch (e: Exception) {
            // Ignora errori di sign out Google
        }
    }
}