package com.example.tallerpracticoi_dsm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tallerpracticoi_dsm.dto.ScheduleDTO
import com.example.tallerpracticoi_dsm.dto.UserDTO
import com.example.tallerpracticoi_dsm.interfaces.UsersApi
import com.google.android.gms.tasks.Task
import com.example.tallerpracticoi_dsm.schedules.CitesList
import com.example.tallerpracticoi_dsm.schedules.ScheduleAdapter
import com.example.tallerpracticoi_dsm.users.CreateUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log

class LoginActivity : AppCompatActivity() {
    private var emailTV: EditText? = null
    private var passwordTV: EditText? = null
    private var loginBtn: Button? = null
    private var progressBar: ProgressBar? = null
    private var mAuth: FirebaseAuth? = null
    private var registerQuestion: TextView? = null
    private var btnGoogle: Button? = null
    private val GOOGLE_SIGN_IN = 100
    private  val TAG = "LoginActivityMessage"
    private var btnGitHub: Button? = null

    //Escuchador de FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        initializeUI()
        loginBtn!!.setOnClickListener { loginUserAccount() }
        btnGoogle!!.setOnClickListener { loginGoogleAccount() }
        btnGitHub!!.setOnClickListener { loginGitHubAccount() }

        registerQuestion!!.setOnClickListener {redirectRegisterActivity()}
        //Verifica si hay una sesión
        this.checkUser();
    }
    private fun initializeUI() {
        emailTV = findViewById<EditText>(R.id.email)
        passwordTV = findViewById<EditText>(R.id.password)
        loginBtn = findViewById<Button>(R.id.login)
        btnGoogle = findViewById<Button>(R.id.btnGoogle)
        btnGitHub = findViewById<Button>(R.id.btnGitHub)

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
            Toast.makeText(applicationContext, R.string.email_warning, Toast.LENGTH_LONG)
                .show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, R.string.password_warning, Toast.LENGTH_LONG)
                .show()
            return
        }
        mAuth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        R.string.login_success,
                        Toast.LENGTH_LONG
                    ).show()
                    progressBar?.visibility = View.GONE
                    //Save Data
                    val preps = getSharedPreferences(getString(R.string.preps_file),Context.MODE_PRIVATE).edit()
                    preps.putString("email",email)
                    preps.putString("provider",originType.BASIC.toString())
                    preps.apply()

                    showHome(email,originType.BASIC)

                } else {
                    Toast.makeText(
                        applicationContext,
                        R.string.login_failed,
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
        // Verificacion del usuario por SharedPreferences

        val preps = getSharedPreferences(getString(R.string.preps_file),Context.MODE_PRIVATE)
        val emailProps: String? = preps.getString("email",null)
        val providerProps: String? = preps.getString("provider",null)

        if(emailProps != null && providerProps != null){
            Log.w(TAG, "emailProps=" + emailProps);
            Log.w(TAG, "providerProps" + providerProps);

            showHome(emailProps,originType.valueOf(providerProps))
            finish()
        }
        // Verificacion del por firebase  si es necesario
        authStateListener = FirebaseAuth.AuthStateListener { auth ->
            if (auth.currentUser != null) {
                // Cambiando la vista
//                val intent = Intent(this@LoginActivity, BottomNavigationActivity::class.java)
//                startActivity(intent)
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
                    val i = Intent(this@LoginActivity, CitesList::class.java)
                    val preps = getSharedPreferences(getString(R.string.preps_file),Context.MODE_PRIVATE).edit()
                    preps.putString("dui", userData?.dui)
                    preps.apply()
                    i.putExtra("itemMenuSelected", R.id.cites)
                    i.putExtra("email", email)
                    i.putExtra("provider", provider.toString())
                    startActivity(i)
                } else {
                    val i = Intent(this@LoginActivity, CreateUser::class.java)
                    i.putExtra("email", email)
                    i.putExtra("provider", provider.toString())
                    startActivity(i)
                }
            }

            override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                val i = Intent(this@LoginActivity, CreateUser::class.java)
                i.putExtra("email", email)
                i.putExtra("provider", provider.toString())
                startActivity(i)
                Log.e("User Api", "error is ${t.message}")
            }
        })
    }
    private  fun loginGoogleAccount(){
        val webID ="443330130053-gigiqskgb77gkhtcfbimk3d2qj8nhnqh.apps.googleusercontent.com";
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(webID)
            .requestEmail()
            .build()
        val googleCliente = GoogleSignIn.getClient(this, googleConf)
        googleCliente.signOut()
        startActivityForResult(googleCliente.signInIntent,GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                if(account!= null){
                    val credential = GoogleAuthProvider.getCredential(account.idToken,null);
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                applicationContext,
                                R.string.login_success,
                                Toast.LENGTH_LONG
                            ).show()
                            progressBar?.visibility = View.GONE
                            val preps = getSharedPreferences(getString(R.string.preps_file),Context.MODE_PRIVATE).edit()
                            preps.putString("email",account.email?:"")
                            preps.putString("provider",originType.GOOGLE.toString())
                            preps.apply()
                            showHome(account.email?:"",originType.GOOGLE)

                        } else {
                            Toast.makeText(
                                applicationContext,
                                R.string.google_login_failed,
                                Toast.LENGTH_LONG
                            ).show()
                            progressBar?.visibility = View.GONE
                        }
                    }


                }
            }catch (e:ApiException){
                Toast.makeText(
                    applicationContext,
                    "Google Api login failed! Please try again later",
                    Toast.LENGTH_LONG
                ).show()

                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

            }
        }
    }
    private  fun loginGitHubAccount(){
        val i = Intent(this@LoginActivity, gitHubLogin::class.java)
        startActivity(i)
        val intent = Intent(this@LoginActivity, gitHubLogin::class.java)
        startActivity(intent)

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