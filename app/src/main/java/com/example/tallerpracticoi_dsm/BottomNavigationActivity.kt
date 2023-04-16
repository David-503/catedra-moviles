package com.example.tallerpracticoi_dsm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.tallerpracticoi_dsm.utils.AppLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class BottomNavigationActivity : AppLayout() {

    override fun onCreate(savedInstanceState: Bundle?) {

        setContentView(R.layout.activity_bottom_navigation)
        super.onCreate(savedInstanceState, )

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
                    val intent = Intent(this@BottomNavigationActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}