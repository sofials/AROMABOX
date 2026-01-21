package com.example.aromabox.viewmodel

import android.content.Intent
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aromabox.data.firebase.AuthManager
import com.example.aromabox.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkCurrentUser()
    }

    fun checkCurrentUser() {
        val user = AuthManager.getCurrentUser()
        if (user != null) {
            _authState.value = AuthState.Success(user)
        } else {
            _authState.value = AuthState.Idle
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    // ========== EMAIL/PASSWORD ==========

    fun registerWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val firebaseUser = AuthManager.registerWithEmail(email, password)
                if (firebaseUser != null) {
                    // Crea utente nel database
                    val user = UserRepository.getOrCreateUser(firebaseUser)
                    if (user != null) {
                        _authState.value = AuthState.Success(firebaseUser)
                    } else {
                        _authState.value = AuthState.Error("Errore nella creazione del profilo")
                    }
                } else {
                    _authState.value = AuthState.Error("Registrazione fallita")
                }
            } catch (e: FirebaseAuthWeakPasswordException) {
                _authState.value = AuthState.Error("La password deve avere almeno 6 caratteri")
            } catch (e: FirebaseAuthUserCollisionException) {
                _authState.value = AuthState.Error("Esiste già un account con questa email")
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _authState.value = AuthState.Error("Email non valida")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Errore: ${e.localizedMessage}")
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val firebaseUser = AuthManager.signInWithEmail(email, password)
                if (firebaseUser != null) {
                    // Assicurati che l'utente esista nel database
                    UserRepository.getOrCreateUser(firebaseUser)
                    _authState.value = AuthState.Success(firebaseUser)
                } else {
                    _authState.value = AuthState.Error("Accesso fallito")
                }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _authState.value = AuthState.Error("Email o password non corretti")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Errore: ${e.localizedMessage}")
            }
        }
    }

    fun sendPasswordReset(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = AuthManager.sendPasswordResetEmail(email)
            onResult(success)
        }
    }

    // ========== GOOGLE ==========

    fun signInWithGoogle(onIntentReceived: (IntentSender) -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val intentSender = AuthManager.beginSignIn()
                if (intentSender != null) {
                    onIntentReceived(intentSender)
                } else {
                    _authState.value = AuthState.Error("Impossibile avviare l'accesso con Google")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Errore: ${e.localizedMessage}")
            }
        }
    }

    fun handleSignInResult(intent: Intent) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val firebaseUser = AuthManager.signInWithIntent(intent)
                if (firebaseUser != null) {
                    val user = UserRepository.getOrCreateUser(firebaseUser)
                    if (user != null) {
                        _authState.value = AuthState.Success(firebaseUser)
                    } else {
                        _authState.value = AuthState.Error("Errore nella creazione del profilo")
                    }
                } else {
                    _authState.value = AuthState.Error("Accesso fallito")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Errore durante l'accesso: ${e.localizedMessage}")
            }
        }
    }

    fun signOut() {
        AuthManager.signOut()
        _authState.value = AuthState.Idle
    }
}