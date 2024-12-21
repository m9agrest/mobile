package com.example.test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.test.api.RetrofitClient
import com.example.test.pojo.User
import com.example.test.pojo.Verification
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class LoginActivity : AppCompatActivity()  {

    private lateinit var errorMessageTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Debug", "Start app")

        // Проверка сессии при старте
        val sessionId = getSessionId()
        if (sessionId != null) {
            // Сессия активна, продолжаем загрузку активности
            checkSession()
        }else{
            initActivity()
        }
    }
    private fun loginUser(email: String, password: String) {
        // Создаем запрос
        RetrofitClient.create(applicationContext).login(email, password).enqueue(object : Callback<Verification> {
            override fun onResponse(call: Call<Verification>, response: Response<Verification>) {
                if (response.isSuccessful) {
                    // Успешный ответ от сервера
                    val loginResponse = response.body()
                    loginResponse?.let {
                        // Обрабатываем ответ
                        if (it.response == 0) {

                            // Переходим на экран ввода кода
                            val intent = Intent(this@LoginActivity, VerifyCodeActivity::class.java)
                            intent.putExtra("KEY", it.key) // Передаем ключ
                            startActivity(intent)
                        }else{
                            // Неправильный логин или пароль
                            errorMessageTextView.text = "Invalid email or password"
                            errorMessageTextView.visibility = TextView.VISIBLE
                        }
                    }
                } else {
                    // Ошибка на сервере
                    errorMessageTextView.text = "Login failed. Please try again."
                    errorMessageTextView.visibility = TextView.VISIBLE
                }
            }

            override fun onFailure(call: Call<Verification>, t: Throwable) {
                // Ошибка сети или другого типа
                errorMessageTextView.text = "Network error: ${t.message}"
                errorMessageTextView.visibility = TextView.VISIBLE
            }
        })
    }


    private fun getSessionId(): String? {
        // Получаем сохраненный sessionId из SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("sessionId", null)
    }

    private fun checkSession() {
        Log.d("Debug", "check session")
        val apiService = RetrofitClient.create(applicationContext) // Инициализация API-сервиса
        val call = apiService.session() // Вызываем метод session
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                Log.d("Debug", "api request: session")
                if (response.isSuccessful) {
                    Log.d("Debug", "response: successful")
                    val user = response.body()
                    Log.d("Debug", "response json ${user?.response}")
                    if (user?.response == 0) {
                        // Успешно, продолжаем загрузку активности
                        redirectToMain(user);
                    }else{
                        initActivity()
                    }
                }else{
                    Log.d("Debug", "response: error")
                    initActivity()
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                // Ошибка сети или сервера, перенаправляем на экран входа
                Log.d("Debug", "request is error")
                initActivity()
            }
        })
    }

    private fun redirectToMain(user: User) {
        // Перенаправляем на экран входа
        Log.d("Debug", "open Main page")
        GlobalMe.user = user;
        val intent = Intent(this, MainActivity::class.java)
        //intent.putExtra("user", user as Serializable)
        startActivity(intent);
        finish() // Закрываем LoginActivity, чтобы пользователь не вернулся к ней
    }
    private fun initActivity() {
        Log.d("Debug", "open Login page")
        setContentView(R.layout.activity_login)
        errorMessageTextView = findViewById(R.id.errorMessageTextView)

        // Получаем ссылки на элементы UI
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)


        // Обработка нажатия на кнопку "Войти"
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Простая валидация
            if (email.isEmpty() || password.isEmpty()) {
                errorMessageTextView.text = "Please fill in both fields"
                errorMessageTextView.visibility = TextView.VISIBLE
            } else {
                // Тут будет логика отправки данных на сервер
                loginUser(email, password)
            }
        }
    }
}