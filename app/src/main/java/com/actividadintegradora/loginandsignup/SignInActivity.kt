package com.actividadintegradora.loginandsignup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.actividadintegradora.loginandsignup.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                try {
                    firebaseAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish() // Asegúrate de finalizar la actividad actual
                            } else {
                                // Loguea la excepción para obtener más información
                                Log.e("SignInActivity", "Error al iniciar sesión: ${task.exception}")

                                // Maneja la excepción específica
                                when (task.exception) {
                                    is FirebaseAuthInvalidUserException -> {
                                        Toast.makeText(
                                            this,
                                            "El usuario no existe",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    is FirebaseAuthInvalidCredentialsException -> {
                                        Toast.makeText(
                                            this,
                                            "Correo o contraseña incorrectos",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    else -> {
                                        Toast.makeText(
                                            this,

                                            "Usuario o contraseña no incorrectos: ${task.exception?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                } catch (e: FirebaseAuthInvalidUserException) {
            Log.e("SignInActivity", "Error al iniciar sesión - Usuario no existe: $e")
            Toast.makeText(this, "El usuario no existe", Toast.LENGTH_SHORT).show()
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e("SignInActivity", "Error al iniciar sesión - Credenciales incorrectas: $e")
            Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("SignInActivity", "Error inesperado al iniciar sesión: $e")
            Toast.makeText(this, "Unexpected error occurred", Toast.LENGTH_SHORT).show()
        }

        } else {
                Toast.makeText(this, "No se permite espacios vacias", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Asegúrate de finalizar la actividad actual
        }
    }
}
