package com.example.test.pojo

import java.io.Serializable

data class UserList(
    val response: Int,
    val list: Array<User>?
): Serializable
