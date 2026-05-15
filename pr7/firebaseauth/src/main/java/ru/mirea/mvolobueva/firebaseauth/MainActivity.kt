package ru.mirea.mvolobueva.firebaseauth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "FirebaseAuthApp"
    }

    private lateinit var mAuth: FirebaseAuth

    private lateinit var statusTextView: TextView
    private lateinit var detailTextView: TextView
    private lateinit var fieldEmail: EditText
    private lateinit var fieldPassword: EditText
    private lateinit var emailPasswordButtons: LinearLayout
    private lateinit var emailPasswordFields: LinearLayout
    private lateinit var signedInButtons: LinearLayout
    private lateinit var emailSignInButton: Button
    private lateinit var emailCreateAccountButton: Button
    private lateinit var signOutButton: Button
    private lateinit var verifyEmailButton: Button
    private lateinit var reloadUserButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusTextView = findViewById(R.id.statusTextView)
        detailTextView = findViewById(R.id.detailTextView)
        fieldEmail = findViewById(R.id.fieldEmail)
        fieldPassword = findViewById(R.id.fieldPassword)
        emailPasswordButtons = findViewById(R.id.emailPasswordButtons)
        emailPasswordFields = findViewById(R.id.emailPasswordFields)
        signedInButtons = findViewById(R.id.signedInButtons)
        emailSignInButton = findViewById(R.id.emailSignInButton)
        emailCreateAccountButton = findViewById(R.id.emailCreateAccountButton)
        signOutButton = findViewById(R.id.signOutButton)
        verifyEmailButton = findViewById(R.id.verifyEmailButton)
        reloadUserButton = findViewById(R.id.reloadUserButton)

        mAuth = FirebaseAuth.getInstance()

        emailCreateAccountButton.setOnClickListener {
            createAccount(fieldEmail.text.toString(), fieldPassword.text.toString())
        }

        emailSignInButton.setOnClickListener {
            signIn(fieldEmail.text.toString(), fieldPassword.text.toString())
        }

        signOutButton.setOnClickListener {
            signOut()
        }

        verifyEmailButton.setOnClickListener {
            sendEmailVerification()
        }

        reloadUserButton.setOnClickListener {
            reloadUser(true)
        }
    }

    override fun onStart() {
        super.onStart()
        reloadUser(false)
    }

    override fun onResume() {
        super.onResume()
        reloadUser(false)
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = fieldEmail.text.toString()
        if (email.isEmpty()) {
            fieldEmail.error = "Required"
            valid = false
        } else {
            fieldEmail.error = null
        }

        val password = fieldPassword.text.toString()
        if (password.isEmpty()) {
            fieldPassword.error = "Required"
            valid = false
        } else {
            fieldPassword.error = null
        }

        return valid
    }

    private fun createAccount(email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) return

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    updateUI(mAuth.currentUser)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        if (!validateForm()) return

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    updateUI(mAuth.currentUser)
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    statusTextView.setText(R.string.auth_failed)
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun signOut() {
        mAuth.signOut()
        updateUI(null)
    }

    private fun sendEmailVerification() {
        verifyEmailButton.isEnabled = false

        val user = mAuth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener(this) { task ->
                verifyEmailButton.isEnabled = true
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Verification email sent to ${user.email}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e(TAG, "sendEmailVerification", task.exception)
                    Toast.makeText(
                        this,
                        "Failed to send verification email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun reloadUser(showToast: Boolean) {
        val user = mAuth.currentUser

        if (user == null) {
            updateUI(null)
            return
        }

        reloadUserButton.isEnabled = false
        user.reload().addOnCompleteListener(this) { task ->
            reloadUserButton.isEnabled = true

            if (task.isSuccessful) {
                val refreshedUser = mAuth.currentUser
                updateUI(refreshedUser)

                if (showToast) {
                    val verified = refreshedUser?.isEmailVerified == true
                    Toast.makeText(
                        this,
                        if (verified) "Почта подтверждена" else "Почта пока не подтверждена",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                updateUI(user)
                if (showToast) {
                    Toast.makeText(
                        this,
                        "Не удалось обновить статус",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val verifyStatus = if (user.isEmailVerified) {
                getString(R.string.email_verified)
            } else {
                getString(R.string.email_not_verified)
            }

            statusTextView.text = "Email: ${user.email}\nСтатус: $verifyStatus"
            detailTextView.text = getString(R.string.firebase_status_fmt, user.uid)

            emailPasswordButtons.visibility = View.GONE
            emailPasswordFields.visibility = View.GONE
            signedInButtons.visibility = View.VISIBLE

            verifyEmailButton.isEnabled = !user.isEmailVerified
            reloadUserButton.visibility = View.VISIBLE
        } else {
            statusTextView.setText(R.string.signed_out)
            detailTextView.text = ""

            emailPasswordButtons.visibility = View.VISIBLE
            emailPasswordFields.visibility = View.VISIBLE
            signedInButtons.visibility = View.GONE
        }
    }
}