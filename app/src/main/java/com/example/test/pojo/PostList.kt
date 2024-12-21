package com.example.test.pojo

import java.io.Serializable

data class PostList(
    val response: Int,
    val list: List<Post>?
): Serializable