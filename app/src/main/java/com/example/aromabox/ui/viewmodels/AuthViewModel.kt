package com.example.aromabox.ui.viewmodels

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aromabox.data.model.User
import com.example.aromabox.data.repository.UserRepository
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {

    // ✅ FIX: Repository creato internamente, non come parametro del costruttore
    private val userRepository = UserRepository()

    private val auth = FirebaseAuth.getInstance()
    private val oneTapClient = Identity.getSignInClient(application)

    // ⚠️ IMPORTANTE: Sostituisci con il tuo Web Client ID da Firebase Console
    private val webClientId = "89037242874-1bm7bblrnhnv8e3031hcmrl0vrvifti6.apps.googleusercontent.com"

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }

    // ✅ Registrazione con Email/Password
    fun registerWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user

                if (firebaseUser != null) {
                    val newUser = User(
                        uid = firebaseUser.uid,
                        email = email,
                        nome = "",  // Verrà compilato in CompleteProfileScreen
                        cognome = "",
                        photoUrl = null,
                        isConnected = false,
                        wallet = 0.0
                    )

                    userRepository.createUser(newUser)
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Errore nella creazione dell'utente")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Errore sconosciuto")
            }
        }
    }

    // ✅ Login con Email/Password
    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Errore nel login")
            }
        }
    }

    // ✅ Login con Google - Avvia il flusso
    fun signInWithGoogle(onLaunchIntent: (android.content.IntentSender) -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                val signInRequest = BeginSignInRequest.builder()
                    .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            .setServerClientId(webClientId)
                            .setFilterByAuthorizedAccounts(false)
                            .build()
                    )
                    .setAutoSelectEnabled(false)
                    .build()

                val result = oneTapClient.beginSignIn(signInRequest).await()
                onLaunchIntent(result.pendingIntent.intentSender)

            } catch (e: Exception) {
                _authState.value = AuthState.Error("Errore Google Sign-In: ${e.message}")
            }
        }
    }

    // ✅ Gestisce il risultato del Google Sign-In
    fun handleSignInResult(intent: Intent) {
        viewModelScope.launch {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(intent)
                val idToken = credential.googleIdToken

                if (idToken != null) {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    val result = auth.signInWithCredential(firebaseCredential).await()
                    val firebaseUser = result.user

                    if (firebaseUser != null) {
                        // Controlla se l'utente esiste già
                        val existingUser = userRepository.getUserByIdOnce(firebaseUser.uid)

                        if (existingUser == null) {
                            // Nuovo utente - Crea profilo
                            val displayNameParts = firebaseUser.displayName?.split(" ") ?: listOf("", "")
                            val newUser = User(
                                uid = firebaseUser.uid,
                                email = firebaseUser.email ?: "",
                                nome = displayNameParts.firstOrNull() ?: "",
                                cognome = displayNameParts.getOrNull(1) ?: "",
                                photoUrl = firebaseUser.photoUrl?.toString(),
                                isConnected = false,
                                wallet = 0.0
                            )

                            userRepository.createUser(newUser)
                        }

                        _authState.value = AuthState.Success
                    } else {
                        _authState.value = AuthState.Error("Errore nel login con Google")
                    }
                } else {
                    _authState.value = AuthState.Error("Token ID Google non valido")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Errore: ${e.message}")
            }
        }
    }

    // ✅ Reset password
    fun sendPasswordReset(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email).await()
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    // ✅ Reset dello stato
    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }
}