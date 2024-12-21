package com.example.test.pojo

import java.io.Serializable

data class Photo (
    val response: Int?,  // Поле ответа от сервера
    val id: Int?,
    val album: Int?,
    val file: String?,
    val key: String?,
    val rating: Rating?
): Serializable