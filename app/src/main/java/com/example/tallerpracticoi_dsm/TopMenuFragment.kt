package com.example.tallerpracticoi_dsm

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
enum class originType {
    BASIC,
    GOOGLE
}
class TopMenuFragment : Fragment() {
    private var origin: originType? = null
    private var email: String? = null

    lateinit var logoutButton: Button;
    // Declaracion de variables
    private lateinit var bottomNavigationBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            origin = originType.valueOf(it.getString(ORIGIN)?:"")
//            email = it.getString(EMAIL)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logoutButton =requireView().findViewById(R.id.btn_logout)
        logoutButton!!.setOnClickListener { logOut() }


    }
     fun logOut(){

        FirebaseAuth.getInstance().signOut().also {
            Toast.makeText(context,"Logout",Toast.LENGTH_SHORT).show()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }


    }
    companion object {
        private val ORIGIN = "origin"
        private val EMAIL = "email"

        fun newInstance(email:String,origin: String) =
            TopMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ORIGIN, origin)
                    putString(EMAIL, email)

                }
            }
    }
}