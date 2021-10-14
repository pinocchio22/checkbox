package com.example.checkbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var exitView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val bnv = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        bnv.setOnNavigationItemSelectedListener(this)
        exitView = layoutInflater.inflate(R.layout.exit_layout, null)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        val fm = supportFragmentManager
//        val transaction: FragmentTransaction = fm.beginTransaction()
//
//        when(p0.itemId){
//            R.id.menu_name -> {
//                fm.popBackStackImmediate("name", FragmentManager.POP_BACK_STACK_INCLUSIVE)
//                val fragmentA = NameFragment(appbar)
//                transaction.replace(R.id.frame_layout,fragmentA, "name")
//                transaction.addToBackStack("name")
//            }
//            R.id.menu_tag -> {
//                fm.popBackStackImmediate("tag", FragmentManager.POP_BACK_STACK_INCLUSIVE)
//                val fragmentB = TagFragment(appbar)
//                transaction.replace(R.id.frame_layout,fragmentB, "tag")
//                transaction.addToBackStack("tag")
//            }
//            R.id.menu_cal -> {
//                fm.popBackStackImmediate("cal", FragmentManager.POP_BACK_STACK_INCLUSIVE)
//                val fragmentC = DateFragment(appbar)
//                transaction.replace(R.id.frame_layout,fragmentC, "cal")
//                transaction.addToBackStack("cal")
//            }
//            R.id.menu_location -> {
//                fm.popBackStackImmediate("location", FragmentManager.POP_BACK_STACK_INCLUSIVE)
//                val fragmentD = LocationFragment(appbar)
//                transaction.replace(R.id.frame_layout,fragmentD, "location")
//                transaction.addToBackStack("location")
//            }
//        }
//        //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//        transaction.commit()
//        transaction.isAddToBackStackAllowed

        return true
    }
}