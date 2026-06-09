package com.example.ludostealth.chat

data class Message(
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),

    val status: String = "sent"
)