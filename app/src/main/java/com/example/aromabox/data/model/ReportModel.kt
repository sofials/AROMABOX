package com.example.aromabox.data.model

import android.os.Build
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Model Singleton per la gestione delle segnalazioni.
 *
 * Segue le linee guida del Prof. Malnati:
 * - Singleton object (una sola istanza nell'intero processo)
 * - Indipendente dal ciclo di vita Android
 * - Nessun riferimento a View o componenti UI
 * - Operazioni asincrone su Dispatchers.IO
 * - Fonte di verità per i dati delle segnalazioni
 */
object ReportModel {

    private val database = FirebaseDatabase.getInstance()
    private val reportsRef = database.getReference("reports")
    private val auth = FirebaseAuth.getInstance()

    /**
     * Invia una segnalazione a Firebase Realtime Database.
     *
     * @param type Tipo di segnalazione: "bug", "suggestion", "other"
     * @param message Testo della segnalazione
     * @return Result<Boolean> - successo o errore
     */
    suspend fun sendReport(type: String, message: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val currentUser = auth.currentUser
                    ?: return@withContext Result.failure(Exception("Utente non autenticato"))

                // Genera chiave univoca con push()
                val reportKey = reportsRef.push().key
                    ?: return@withContext Result.failure(Exception("Errore generazione ID"))

                val report = Report(
                    id = reportKey,
                    uid = currentUser.uid,
                    type = type,
                    message = message.trim(),
                    appVersion = getAppVersion(),
                    device = getDeviceInfo(),
                    createdAt = System.currentTimeMillis(),
                    status = "open"
                )

                // Scrivi su Firebase RTDB
                reportsRef.child(reportKey).setValue(report).await()

                Result.success(true)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Informazioni sul dispositivo per diagnostica.
     */
    private fun getDeviceInfo(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL} - Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
    }

    /**
     * Versione dell'app (placeholder, da collegare a BuildConfig).
     */
    private fun getAppVersion(): String {
        return "1.0.0"
    }
}