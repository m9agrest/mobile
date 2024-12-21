package com.example.test.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AddCookiesInterceptor (private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val sessionId = sharedPreferences.getString("sessionId", null)

        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()

        if (sessionId != null) {
            // Добавляем куки в заголовок
            builder.addHeader("Cookie", "sessionId=${sessionId}")
        }

        return chain.proceed(builder.build())
    }
}