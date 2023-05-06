package com.example.tallerpracticoi_dsm.utils

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.example.tallerpracticoi_dsm.*
import com.google.firebase.auth.FirebaseAuth

open class AppLayout: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Insert fragment menu
        val data = intent.extras
        val itemMenuSelected = data?.getInt("itemMenuSelected")
        val emailUser = data?.getString("email")
        val originType = data?.getString("provider")

        val transaction = supportFragmentManager.beginTransaction()
        val menu = BottomMenu()
        val topMenu = TopMenuFragment()
        if(itemMenuSelected !== null) menu.arguments = bundleOf("active" to itemMenuSelected)

        if(emailUser !== null) topMenu.arguments = bundleOf("email" to emailUser,"origin" to originType)

        transaction.replace(R.id.frameTopMenu, topMenu)
        transaction.replace(R.id.frameBottomMenu, menu)
        transaction.commit()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem):Boolean{
        when(item.itemId){
            R.id.action_sign_out->{
                FirebaseAuth.getInstance().signOut().also {
                    Toast.makeText(this,"Logout",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@AppLayout, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}