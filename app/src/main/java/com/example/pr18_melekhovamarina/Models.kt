package com.example.pr18_melekhovamarina

data class User(
    val name: String,
    val login: String,
    val passwordHash: String
)

data class Disc(
    val id: Long,
    val title: String,
    val artist: String,
    val year: Int,
    val genre: String,
    val tracks: Int,
    val favorite: Boolean
)