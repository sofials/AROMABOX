package com.example.aromabox.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aromabox.data.model.ProfiloOlfattivo
import com.example.aromabox.data.model.User
import com.example.aromabox.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UserState {
    object Loading : UserState()
    data class Success(val user: User) : UserState()
    object NeedsProfileCompletion : UserState()
    data class Error(val message: String) : UserState()
}

class UserViewModel : ViewModel() {
    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    private var currentUser: User? = null

    companion object {
        private const val TAG = "UserViewModel"
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Loading current user...")
                val user = UserRepository.getCurrentUser()
                if (user != null) {
                    Log.d(TAG, "User loaded: ${user.nome} ${user.cognome}, profilo: ${user.profiloOlfattivo}")
                    currentUser = user
                    if (user.nickname.isEmpty()) {
                        _userState.value = UserState.NeedsProfileCompletion
                    } else {
                        _userState.value = UserState.Success(user)
                    }
                } else {
                    Log.e(TAG, "User not found")
                    _userState.value = UserState.Error("Utente non trovato")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user", e)
                _userState.value = UserState.Error("Errore: ${e.localizedMessage}")
            }
        }
    }

    fun completeProfile(nome: String, cognome: String, nickname: String) {
        viewModelScope.launch {
            val user = currentUser ?: return@launch

            val updatedUser = user.copy(
                nome = nome,
                cognome = cognome,
                nickname = nickname
            )
            val success = UserRepository.updateUser(updatedUser)

            if (success) {
                currentUser = updatedUser
                _userState.value = UserState.Success(updatedUser)
            } else {
                _userState.value = UserState.Error("Errore nel salvataggio del profilo")
            }
        }
    }

    fun updateProfiloOlfattivo(profilo: ProfiloOlfattivo) {
        viewModelScope.launch {
            // Se currentUser è null, prova a caricarlo
            if (currentUser == null) {
                Log.d(TAG, "currentUser is null, loading...")
                val user = UserRepository.getCurrentUser()
                if (user != null) {
                    currentUser = user
                } else {
                    Log.e(TAG, "Cannot load user for profilo update")
                    return@launch
                }
            }

            val user = currentUser ?: return@launch
            Log.d(TAG, "Updating profilo olfattivo for user: ${user.userId}")
            Log.d(TAG, "Profilo: $profilo")

            val success = UserRepository.updateProfiloOlfattivo(user.userId, profilo)
            Log.d(TAG, "Update result: $success")

            if (success) {
                val updatedUser = user.copy(profiloOlfattivo = profilo)
                currentUser = updatedUser
                _userState.value = UserState.Success(updatedUser)
                Log.d(TAG, "Profilo olfattivo saved successfully")
            } else {
                Log.e(TAG, "Failed to save profilo olfattivo")
            }
        }
    }

    fun addToFavorites(perfumeId: String) {
        viewModelScope.launch {
            val user = currentUser ?: return@launch
            UserRepository.addToFavorites(user.userId, perfumeId)
            loadCurrentUser()
        }
    }

    fun removeFromFavorites(perfumeId: String) {
        viewModelScope.launch {
            val user = currentUser ?: return@launch
            UserRepository.removeFromFavorites(user.userId, perfumeId)
            loadCurrentUser()
        }
    }

    fun getCurrentUserData(): User? = currentUser
}