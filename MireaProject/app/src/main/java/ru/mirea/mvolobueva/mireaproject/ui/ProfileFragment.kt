package ru.mirea.mvolobueva.mireaproject.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import ru.mirea.mvolobueva.mireaproject.LoginActivity
import ru.mirea.mvolobueva.mireaproject.R

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var editTextFullName: EditText
    private lateinit var editTextGroup: EditText
    private lateinit var editTextNumber: EditText
    private lateinit var editTextFavoriteFilm: EditText
    private lateinit var buttonSaveProfile: Button

    private lateinit var textViewEmail: TextView
    private lateinit var textViewVerifyStatus: TextView
    private lateinit var buttonSendVerification: Button
    private lateinit var buttonReloadStatus: Button
    private lateinit var buttonSignOut: Button

    private lateinit var mAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextFullName = view.findViewById(R.id.editTextFullName)
        editTextGroup = view.findViewById(R.id.editTextGroup)
        editTextNumber = view.findViewById(R.id.editTextNumber)
        editTextFavoriteFilm = view.findViewById(R.id.editTextFavoriteFilm)
        buttonSaveProfile = view.findViewById(R.id.buttonSaveProfile)

        textViewEmail = view.findViewById(R.id.textViewEmail)
        textViewVerifyStatus = view.findViewById(R.id.textViewVerifyStatus)
        buttonSendVerification = view.findViewById(R.id.buttonSendVerification)
        buttonReloadStatus = view.findViewById(R.id.buttonReloadStatus)
        buttonSignOut = view.findViewById(R.id.buttonSignOut)

        mAuth = FirebaseAuth.getInstance()

        loadProfile()
        updateFirebaseUserInfo(mAuth.currentUser)

        buttonSaveProfile.setOnClickListener {
            saveProfile()
        }

        buttonSendVerification.setOnClickListener {
            sendVerificationEmail()
        }

        buttonReloadStatus.setOnClickListener {
            reloadUser(true)
        }

        buttonSignOut.setOnClickListener {
            signOut()
        }
    }

    override fun onResume() {
        super.onResume()
        reloadUser(false)
    }

    private fun saveProfile() {
        val preferences = requireActivity().getSharedPreferences(
            "profile_settings",
            Context.MODE_PRIVATE
        )

        preferences.edit()
            .putString("FULL_NAME", editTextFullName.text.toString())
            .putString("GROUP", editTextGroup.text.toString())
            .putString("NUMBER", editTextNumber.text.toString())
            .putString("FAVORITE_FILM", editTextFavoriteFilm.text.toString())
            .apply()

        Toast.makeText(requireContext(), "Профиль сохранён", Toast.LENGTH_SHORT).show()
    }

    private fun loadProfile() {
        val preferences = requireActivity().getSharedPreferences(
            "profile_settings",
            Context.MODE_PRIVATE
        )

        editTextFullName.setText(preferences.getString("FULL_NAME", ""))
        editTextGroup.setText(preferences.getString("GROUP", ""))
        editTextNumber.setText(preferences.getString("NUMBER", ""))
        editTextFavoriteFilm.setText(preferences.getString("FAVORITE_FILM", ""))
    }

    private fun updateFirebaseUserInfo(user: FirebaseUser?) {
        if (user != null) {
            textViewEmail.text = "Email: ${user.email ?: "--"}"
            textViewVerifyStatus.text = if (user.isEmailVerified) {
                "Статус почты: Почта подтверждена"
            } else {
                "Статус почты: Почта не подтверждена"
            }

            buttonSendVerification.isEnabled = !user.isEmailVerified
            buttonReloadStatus.isEnabled = true
            buttonSignOut.isEnabled = true
        } else {
            textViewEmail.text = "Email: пользователь не авторизован"
            textViewVerifyStatus.text = "Статус почты: --"

            buttonSendVerification.isEnabled = false
            buttonReloadStatus.isEnabled = false
            buttonSignOut.isEnabled = false
        }
    }

    private fun sendVerificationEmail() {
        val user = mAuth.currentUser

        if (user == null) {
            Toast.makeText(requireContext(), "Пользователь не авторизован", Toast.LENGTH_SHORT).show()
            return
        }

        buttonSendVerification.isEnabled = false

        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Письмо отправлено на ${user.email}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Не удалось отправить письмо",
                        Toast.LENGTH_SHORT
                    ).show()
                    buttonSendVerification.isEnabled = true
                }
            }
    }

    private fun reloadUser(showToast: Boolean) {
        val user = mAuth.currentUser

        if (user == null) {
            updateFirebaseUserInfo(null)
            return
        }

        buttonReloadStatus.isEnabled = false

        user.reload().addOnCompleteListener { task ->
            buttonReloadStatus.isEnabled = true

            if (task.isSuccessful) {
                val refreshedUser = mAuth.currentUser
                updateFirebaseUserInfo(refreshedUser)

                if (showToast) {
                    Toast.makeText(
                        requireContext(),
                        if (refreshedUser?.isEmailVerified == true)
                            "Почта подтверждена"
                        else
                            "Почта пока не подтверждена",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                updateFirebaseUserInfo(user)
                if (showToast) {
                    Toast.makeText(
                        requireContext(),
                        "Не удалось обновить статус",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun signOut() {
        mAuth.signOut()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}