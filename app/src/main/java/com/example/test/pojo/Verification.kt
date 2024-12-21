package com.example.test.pojo

data class Verification(
    val response: Int, // Здесь поле, которое ты получаешь от сервера
    val key: String?
)