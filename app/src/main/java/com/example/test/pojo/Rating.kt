package com.example.test.pojo

import java.io.Serializable

data class Rating (
    val like: Int?,
    val dislike: Int?,
    val type: Int?,
    val comment: Int?
): Serializable