package com.gloves.themu.activitys

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.ParcelUuid
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.gloves.themu.R
import com.gloves.themu.classes.Ble
import com.gloves.themu.databases.ConexionSQLiteHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController:NavController
    private lateinit var appBarConfiguration:AppBarConfiguration


    var myBle = Ble(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNav)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        navController = findNavController(R.id.hostFragment)
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.homeFragment, R.id.effectListFragment,
            R.id.profileListFragment,R.id.splashFragment))
        setupActionBarWithNavController(navController,appBarConfiguration)
        bottomNav.setupWithNavController(navController)
        setVisibility()
    }

    override fun onDestroy() {
        super.onDestroy()
        myBle.close()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.hostFragment).navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setVisibility(){

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.splashFragment -> {
                    hideBottomNav()
                    hideActionBar()
                }
                R.id.homeFragment -> {
                    showActionBar()
                    showBottomNav()
                }
            }
        }
    }

    private fun showBottomNav() {
        bottomNav.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        bottomNav.visibility = View.GONE
    }
    private fun showActionBar(){
        supportActionBar?.show()

    }
    private fun hideActionBar(){
        supportActionBar?.hide()
    }


}