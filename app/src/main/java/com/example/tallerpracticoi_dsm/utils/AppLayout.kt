package com.example.tallerpracticoi_dsm.utils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.example.tallerpracticoi_dsm.BottomMenu
import com.example.tallerpracticoi_dsm.R

open class AppLayout: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Insert fragment menu
        val data = intent.extras
        val itemMenuSelected = data?.getInt("itemMenuSelected")
        val transaction = supportFragmentManager.beginTransaction()
        val menu = BottomMenu()
        if(itemMenuSelected !== null) menu.arguments = bundleOf("active" to itemMenuSelected)
        transaction.replace(R.id.frameBottomMenu, menu)
        transaction.commit()

    }
}