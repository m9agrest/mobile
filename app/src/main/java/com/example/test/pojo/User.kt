package com.example.test.pojo

import java.io.Serializable

data class User(
    val response: Int?,  // Поле ответа от сервера
    val id: Int?,           // Идентификатор пользователя
    val name: String?,      // Имя пользователя
    val tag: String?,       // Тег пользователя
    val status: String?,    // Статус пользователя
    val dateBirth: String?,  // Дата рождения пользователя
    val icon: Photo?,
    val cover: Photo?,
    val verified: Boolean?,
    val group: Boolean?,
    val musician: Boolean?,
    val popular: Boolean?,
    val countFriend: Int?,
    val countSubscribers: Int?,
    val countSubscriptions: Int?,
    val online: Int?
): Serializable