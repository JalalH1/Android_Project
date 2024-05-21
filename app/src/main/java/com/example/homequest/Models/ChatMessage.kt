package com.example.homequest.Models

import java.util.Date

data class ChatMessage(
    val senderId: String,
    val message: String,
    val timestamp: Date
)
