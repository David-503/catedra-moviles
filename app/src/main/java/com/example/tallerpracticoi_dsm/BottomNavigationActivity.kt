package com.example.tallerpracticoi_dsm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationActivity : AppCompatActivity() {
    private lateinit var bottomNavigationBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation)

        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar)

        bottomNavigationBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    true
                }

                R.id.explore -> {
                    startActivity(Intent(this, OptionTwoActivity::class.java))
                    overridePendingTransition(0,0)
                    true
                }

                R.id.setting -> {
                    Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.search -> {
                    Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> {
                    Toast.makeText(this, "Unknown option", Toast.LENGTH_SHORT).show()
                    true
                }
            }
        }
    }
}