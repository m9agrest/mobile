package com.example.test



import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.test.api.RetrofitClient
import com.example.test.pojo.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class VerifyCodeActivity : AppCompatActivity() {

    private lateinit var codeEditText: EditText
    private lateinit var verifyButton: Button
    private lateinit var attemptsErrorTextView: TextView
    private var remainingAttempts = 3 // Максимальное количество попыток


    private var key: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_code)

        attemptsErrorTextView = findViewById(R.id.attemptsErrorTextView)
        codeEditText = findViewById(R.id.codeEditText)
        verifyButton = findViewById(R.id.verifyButton)

        // Получаем ключ, переданный из предыдущего экрана
        key = intent.getStringExtra("KEY")

        // Инициализируем элементы UI

        verifyButton.setOnClickListener {
            val code = codeEditText.text.toString()

            if (code.isEmpty()) {
                Toast.makeText(this, "Please enter the confirmation code", Toast.LENGTH_SHORT).show()
            } else {
                // Отправляем код на сервер для проверки
                verifyCode(code)
            }
        }
    }

    private fun verifyCode(code: String) {
        if (key != null) {
            // Отправляем запрос на сервер
            RetrofitClient.create(applicationContext).verifyCode(key!!, code).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        // Успешный ответ от сервера
                        val userResponse = response.body()
                        userResponse?.let {
                            if (it.response == 0) {
                                /*Toast.makeText(this@VerifyCodeActivity, "Code verified successfully", Toast.LENGTH_SHORT).show()

                                // Здесь можно использовать полученные данные пользователя
                                val userInfo = """
                                    ID: ${it.id}
                                    Name: ${it.name}
                                    Tag: ${it.tag}
                                    Status: ${it.status}
                                    Date of Birth: ${it.dateBirth}
                                """.trimIndent()

                                // Покажем полученную информацию о пользователе
                                Toast.makeText(this@VerifyCodeActivity, userInfo, Toast.LENGTH_LONG).show()
                                // Тут можно сделать переход на главный экран или выполнить другие действия*/

                                val setCookieHeader = response.headers()["Set-Cookie"]
                                val sessionId = setCookieHeader?.split(";") // Разделяем строку по символу `;`
                                    ?.find { it.trim().startsWith("sessionId=") } // Ищем часть, начинающуюся с `sessionId=`
                                    ?.substringAfter("sessionId=")

                                sessionId?.let {
                                    //saveSessionId(sessionId)
                                    saveSessionId(sessionId)
                                }
                                // Переход на следующий экран
                                redirectToMain(userResponse);
                            } else {
                                handleInvalidCode()
                            }
                        }
                    } else {
                        // Ошибка на сервере
                        attemptsErrorTextView.text = "Verification failed. Please try again."
                        attemptsErrorTextView.visibility = TextView.VISIBLE
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    // Ошибка сети или другого типа
                    attemptsErrorTextView.text = "Network error: ${t.message}"
                    attemptsErrorTextView.visibility = TextView.VISIBLE
                }
            })
        }
    }

    private fun handleInvalidCode() {
        // Уменьшаем количество оставшихся попыток
        remainingAttempts--

        // Обновляем текстовое сообщение с количеством оставшихся попыток
        attemptsErrorTextView.text = "Invalid code. You have $remainingAttempts attempts left."
        attemptsErrorTextView.visibility = TextView.VISIBLE

        if (remainingAttempts > 0) {
            // Если попытки остались, показываем сообщение
            Toast.makeText(this@VerifyCodeActivity, "Invalid code. You have $remainingAttempts attempts left.", Toast.LENGTH_SHORT).show()
        } else {
            // Если попытки закончились, показываем сообщение и переходим в LoginActivity
            Toast.makeText(this@VerifyCodeActivity, "No attempts left. Returning to login.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@VerifyCodeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish() // Завершаем текущую активность, чтобы не вернуться к ней с кнопки "Назад"
        }
    }

    private fun saveSessionId(sessionId: String) {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("sessionId", sessionId)
        editor.apply()
    }

    private fun redirectToMain(user: User) {
        // Перенаправляем на экран входа
        Log.d("Debug", "open Main page")
        val intent = Intent(this, MainActivity::class.java)
        GlobalMe.user = user;
        startActivity(intent);
        finish() // Закрываем LoginActivity, чтобы пользователь не вернулся к ней
    }
}