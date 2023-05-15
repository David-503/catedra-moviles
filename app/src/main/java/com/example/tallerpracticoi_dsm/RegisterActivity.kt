package com.example.tallerpracticoi_dsm

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.telephony.TelephonyCallback.CallStateListener
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.tallerpracticoi_dsm.dto.UserDTO
import com.example.tallerpracticoi_dsm.interfaces.UsersApi
import com.example.tallerpracticoi_dsm.schedules.CitesList
import com.example.tallerpracticoi_dsm.users.CreateUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity() {
    private var emailTV: EditText? = null
    private var passwordTV: EditText? = null
    private var regBtn: Button? = null
    private var progressBar: ProgressBar? = null
    private var mAuth: FirebaseAuth? = null
    private var loginRedirect: TextView? = null

    //Escuchador de FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()
        initializeUI()
        regBtn!!.setOnClickListener { registerNewUser() }
        loginRedirect!!.setOnClickListener { redirectLoginActivity() }
        //Verifica si hay una sesión
        this.checkUser();
    }

    private fun registerNewUser() {
        progressBar!!.visibility = View.VISIBLE
        val email: String
        val password: String
        email = emailTV!!.text.toString()
        password = passwordTV!!.text.toString()
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Please enter email...", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Please enter password!", Toast.LENGTH_LONG).show()
            return
        }
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Registration successful!",
                        Toast.LENGTH_LONG
                    ).show()
                    progressBar!!.visibility = View.GONE
                    Toast.makeText(
                        applicationContext,
                        email,
                        Toast.LENGTH_LONG
                    ).show()
                    Toast.makeText(
                        applicationContext,
                        originType.BASIC.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                    val preps = getSharedPreferences(getString(R.string.preps_file), Context.MODE_PRIVATE).edit()
                    preps.putString("email",email)
                    preps.putString("provider",originType.BASIC.toString())
                    preps.apply()

                    showHome(email,originType.BASIC)

                } else {
                    Toast.makeText(
                        applicationContext,
                        "Registration failed! Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                    progressBar!!.visibility = View.GONE
                }
            }
    }

    private fun initializeUI() {
        emailTV = findViewById(R.id.email)
        passwordTV = findViewById(R.id.password)
        regBtn = findViewById(R.id.register)
        progressBar = findViewById(R.id.progressBar)
        loginRedirect = findViewById<Button>(R.id.loginRedirect)

    }

    private fun redirectLoginActivity() {

        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
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
                showHome(auth.currentUser?.email,originType.BASIC)

                finish()

            }
        }
    }

    private fun showHome(email:String?,provider:originType){
        val usersApi: UsersApi = this.getApi(UsersApi::class.java)
        val userCall = usersApi.getUser(email!!)
        userCall.enqueue(object : Callback<UserDTO> {
            override fun onResponse(
                call: Call<UserDTO>,
                response: Response<UserDTO>
            ) {
                if(response.isSuccessful) {
                    val userData = response.body()
                    val i = Intent(this@RegisterActivity, CitesList::class.java)
                    val preps = getSharedPreferences(getString(R.string.preps_file),Context.MODE_PRIVATE).edit()
                    preps.putString("dui", userData?.dui)
                    preps.apply()
                    i.putExtra("itemMenuSelected", R.id.cites)
                    i.putExtra("email", email)
                    i.putExtra("provider", provider.toString())
                    startActivity(i)
                } else {
                    val i = Intent(this@RegisterActivity, CreateUser::class.java)
                    i.putExtra("email", email)
                    i.putExtra("provider", provider.toString())
                    startActivity(i)
                }
            }

            override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                val i = Intent(this@RegisterActivity, CreateUser::class.java)
                i.putExtra("email", email)
                i.putExtra("provider", provider.toString())
                startActivity(i)
                Log.e("User Api", "error is ${t.message}")
            }
        })
    }

    fun <T> getApi(clazz: Class<T>): T {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", Credentials.basic("prueba", "prueba"))
                    .build()
                chain.proceed(request)
            }
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // Crea una instancia del servicio que utiliza la autenticacion HTTP basica
        return retrofit.create(clazz)
    }
}
