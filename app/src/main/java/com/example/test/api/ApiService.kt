package com.example.test.api

import com.example.test.pojo.Post
import com.example.test.pojo.PostList
import com.example.test.pojo.Verification
import com.example.test.pojo.User
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Field

interface ApiService {
    @POST("api/login")
    @FormUrlEncoded
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Verification>

    @POST("api/login-confirm")
    @FormUrlEncoded
    fun verifyCode(
        @Field("key") key: String,
        @Field("code") code: String
    ): Call<User>

    @POST("api/session")
    fun session(): Call<User>


    @POST("api/get_user")
    @FormUrlEncoded
    fun getUser(
        @Field("user") user: Int
    ): Call<User>


    @POST("api/get_post")
    @FormUrlEncoded
    fun news(
        @Field("list") list: Int,
        @Field("all") all: Boolean
    ): Call<PostList>

    @POST("api/get_post")
    @FormUrlEncoded
    fun listPost(
        @Field("list") list: Int,
        @Field("user") user: Int
    ): Call<PostList>

    @POST("api/get_post")
    @FormUrlEncoded
    fun post(
        @Field("id") id: Int,
        @Field("user") user: Int
    ): Call<Post>

    @POST("api/delete_post")
    @FormUrlEncoded
    fun geletePost(
        @Field("post") post: Int
    ): Call<Post>

}