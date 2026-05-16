package ru.mirea.mvolobueva.mireaproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
/*

КРАТКОЕ ОПИСАНИЕ:
Экран входа и регистрации пользователя с использованием Firebase Authentication.
Позволяет пользователю войти в существующий аккаунт или создать новый.

ПОДРОБНОЕ ОПИСАНИЕ:
Этот файл реализует активность для аутентификации пользователя через Firebase.
Он предоставляет два основных действия:
1. Вход (Sign In) - для существующих пользователей
2. Регистрация (Create Account) - для новых пользователей

После успешной аутентификации пользователь автоматически перенаправляется
на главный экран (MainActivity). Если пользователь уже был авторизован,
он попадает на главный экран сразу при запуске приложения.

ОСНОВНЫЕ КОМПОНЕНТЫ:
- FirebaseAuth: Основной класс Firebase для работы с аутентификацией
- EditText: Поля ввода email и пароля
- Button: Кнопки для входа и регистрации
- Toast: Всплывающие уведомления о результате операций

*/
class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonSignIn: Button
    private lateinit var buttonCreateAccount: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonSignIn = findViewById(R.id.buttonSignIn)
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount)
// НАСТРОЙКА ОБРАБОТЧИКОВ СОБЫТИЙ
        buttonSignIn.setOnClickListener {
            signIn()
        }

        buttonCreateAccount.setOnClickListener {
            createAccount()
        }
    }
    // onStart - ПРОВЕРКА АВТОРИЗАЦИИ ПРИ ЗАПУСКЕ
    override fun onStart() {
        super.onStart()

        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            openMainScreen()
        }
    }

    private fun createAccount() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Введите email и пароль", Toast.LENGTH_SHORT).show()
            return
        }
//Отправляем запрос в Firebase на создание нового пользователя
        //        createUserWithEmailAndPassword() - асинхронный метод
        mAuth.createUserWithEmailAndPassword(email, password)

            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Аккаунт создан", Toast.LENGTH_SHORT).show()
                    openMainScreen()
                } else {
                    Toast.makeText(
                        this,
                        "Ошибка регистрации: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
    // signIn - ВХОД В СУЩЕСТВУЮЩИЙ АККАУНТ
    private fun signIn() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Введите email и пароль", Toast.LENGTH_SHORT).show()
            return
        }
//Отправляем запрос в Firebase на аутентификацию пользователя
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Вход выполнен", Toast.LENGTH_SHORT).show()
                    openMainScreen()
                } else {
                    Toast.makeText(
                        this,
                        "Ошибка входа: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
    // openMainScreen - ПЕРЕХОД НА ГЛАВНЫЙ ЭКРАН
    private fun openMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}