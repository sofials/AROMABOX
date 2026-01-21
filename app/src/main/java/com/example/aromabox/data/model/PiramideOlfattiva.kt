package com.example.aromabox.data.model

data class PiramideOlfattiva(
    val noteTesta: List<String> = emptyList(),
    val noteCuore: List<String> = emptyList(),
    val noteFondo: List<String> = emptyList()
) {
    constructor() : this(emptyList(), emptyList(), emptyList())
}