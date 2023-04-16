package com.example.tallerpracticoi_dsm

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomMenu : Fragment() {
    private var active: Int? = null

    // Declaracion de variables
    private lateinit var bottomNavigationBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            active = it.getInt(ACTIVE)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavigationBar = requireView().findViewById(R.id.bottom_navigation_bar)

        bottomNavigationBar.selectedItemId  = if(arguments != null) active!! else R.id.home
        bottomNavigationBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    startActivity(Intent(activity, BottomNavigationActivity::class.java))
                    activity?.overridePendingTransition(0,0)
                    true
                }

                R.id.cites -> {
                   val i = Intent(requireContext(), CitesList::class.java)
                    i.putExtra("itemMenuSelected", R.id.cites)
                    startActivity(i)
                    activity?.overridePendingTransition(0,0)
                    true
                }

                R.id.setting -> {
                    Toast.makeText(activity, "Setting", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.search -> {
                    Toast.makeText(activity, "Search", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> {
                    Toast.makeText(activity, "Unknown option", Toast.LENGTH_SHORT).show()
                    true
                }
            }
        }

    }

    companion object {
        private val ACTIVE = "active"
        fun newInstance(active: Int) =
            BottomMenu().apply {
                arguments = Bundle().apply {
                    putInt(ACTIVE, active)
                }
            }
    }
}