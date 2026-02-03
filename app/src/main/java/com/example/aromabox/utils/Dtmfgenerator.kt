package com.example.aromabox.utils

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.delay
import kotlin.math.sin

/**
 * Generatore di toni DTMF (Dual-Tone Multi-Frequency)
 * Usato per trasmettere il PIN al distributore tramite audio
 */
object DTMFGenerator {

    // Frequenze DTMF standard (Hz)
    // Ogni tasto è composto da due frequenze: una riga + una colonna
    private val LOW_FREQUENCIES = mapOf(
        '1' to 697, '2' to 697, '3' to 697,
        '4' to 770, '5' to 770, '6' to 770,
        '7' to 852, '8' to 852, '9' to 852,
        '*' to 941, '0' to 941, '#' to 941
    )

    private val HIGH_FREQUENCIES = mapOf(
        '1' to 1209, '2' to 1336, '3' to 1477,
        '4' to 1209, '5' to 1336, '6' to 1477,
        '7' to 1209, '8' to 1336, '9' to 1477,
        '*' to 1209, '0' to 1336, '#' to 1477
    )

    private const val SAMPLE_RATE = 44100
    private const val DEFAULT_TONE_DURATION_MS = 150  // Durata singolo tono
    private const val DEFAULT_DELAY_BETWEEN_TONES_MS = 100L  // Pausa tra toni

    /**
     * Genera e riproduce un singolo tono DTMF
     * @param digit Il carattere da riprodurre (0-9, *, #)
     * @param durationMs Durata del tono in millisecondi
     */
    fun playTone(digit: Char, durationMs: Int = DEFAULT_TONE_DURATION_MS) {
        val lowFreq = LOW_FREQUENCIES[digit] ?: return
        val highFreq = HIGH_FREQUENCIES[digit] ?: return

        val numSamples = (SAMPLE_RATE * durationMs / 1000.0).toInt()
        val samples = ShortArray(numSamples)

        // Genera il segnale combinando le due frequenze
        for (i in 0 until numSamples) {
            val time = i.toDouble() / SAMPLE_RATE
            val lowSample = sin(2 * Math.PI * lowFreq * time)
            val highSample = sin(2 * Math.PI * highFreq * time)
            // Combina le due frequenze e normalizza
            val combined = (lowSample + highSample) / 2.0
            samples[i] = (combined * Short.MAX_VALUE).toInt().toShort()
        }

        // Configura e avvia AudioTrack
        val bufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(maxOf(bufferSize, samples.size * 2))
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(samples, 0, samples.size)
        audioTrack.play()

        // Attendi che il tono finisca
        Thread.sleep(durationMs.toLong())

        audioTrack.stop()
        audioTrack.release()
    }

    /**
     * Riproduce una sequenza di toni DTMF (es. un PIN)
     * @param sequence La sequenza di caratteri da riprodurre
     * @param toneDurationMs Durata di ogni singolo tono in millisecondi
     * @param delayBetweenTonesMs Pausa tra un tono e l'altro in millisecondi
     * @param onDigitPlayed Callback chiamato dopo ogni cifra riprodotta
     */
    suspend fun playSequence(
        sequence: String,
        toneDurationMs: Int = DEFAULT_TONE_DURATION_MS,
        delayBetweenTonesMs: Long = DEFAULT_DELAY_BETWEEN_TONES_MS,
        onDigitPlayed: ((Char, Int) -> Unit)? = null
    ) {
        sequence.forEachIndexed { index, digit ->
            if (digit.isDigit() || digit == '*' || digit == '#') {
                playTone(digit, toneDurationMs)
                onDigitPlayed?.invoke(digit, index)

                // Pausa tra i toni (tranne dopo l'ultimo)
                if (index < sequence.length - 1) {
                    delay(delayBetweenTonesMs)
                }
            }
        }
    }
}