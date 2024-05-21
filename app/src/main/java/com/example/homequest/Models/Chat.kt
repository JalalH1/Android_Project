package com.example.homequest.Models

import java.util.Date

data class Chat(
    val chatId: String = "",
    val participants: List<String> = listOf(),
    val lastMessage: String = "",
    val timestamp: Date? = null,
    val listingId: String = "",
    val listingName: String = ""
)
