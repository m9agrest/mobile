package com.example.test.pojo

import java.io.Serializable

data class Post (
    val response: Int,
    val text: String?,
    val photo: List<Photo>?,
    var user: User?,
    val date: Int?,
    val rating: Rating?,
    val userId: Int?,
    val date_edit: Int?,
    val id: Int?
): Serializable