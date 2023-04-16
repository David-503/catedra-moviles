package com.example.tallerpracticoi_dsm

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private var emailTV: EditText? = null
    private var passwordTV: EditText? = null
    private var loginBtn: Button? = null
    private var progressBar: ProgressBar? = null
    private var mAuth: FirebaseAuth? = null
    private var registerQuestion: TextView? = null
    //Escuchador de FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        initializeUI()
        loginBtn!!.setOnClickListener { loginUserAccount() }
        registerQuestion!!.setOnClickListener {redirectRegisterActivity()}
        //Verifica si hay una sesión
        this.checkUser();
    }
    private fun initializeUI() {
        emailTV = findViewById<EditText>(R.id.email)
        passwordTV = findViewById<EditText>(R.id.password)
        loginBtn = findViewById<Button>(R.id.login)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        registerQuestion = findViewById<Button>(R.id.registerQuestionView)

    }
    private fun loginUserAccount() {
        progressBar?.visibility = View.VISIBLE
        val email: String
        val password: String
        email = emailTV?.text.toString()
        password = passwordTV?.text.toString()
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Please enter email...", Toast.LENGTH_LONG)
                .show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Please enter password!", Toast.LENGTH_LONG)
                .show()
            return
        }
        mAuth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Login successful!",
                        Toast.LENGTH_LONG
                    ).show()
                    progressBar?.visibility = View.GONE
                    val intent = Intent(this@LoginActivity, BottomNavigationActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Login failed! Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                    progressBar?.visibility = View.GONE
                }
            }
    }
    private fun redirectRegisterActivity() {
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
    }
    override fun onResume() {
        super.onResume()
        mAuth?.addAuthStateListener(authStateListener)
    }

    override fun onPause() {
        super.onPause()
        mAuth?.removeAuthStateListener(authStateListener)
    }

    private fun checkUser() {
        // Verificacion del usuario
        authStateListener = FirebaseAuth.AuthStateListener { auth ->
            if (auth.currentUser != null) {
                // Cambiando la vista
                val intent = Intent(this@LoginActivity, BottomNavigationActivity::class.java)
                startActivity(intent)
                finish()

            }
        }
    }
}