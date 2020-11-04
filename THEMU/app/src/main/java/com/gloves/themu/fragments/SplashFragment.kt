package com.gloves.themu.fragments

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gloves.themu.R
import com.gloves.themu.activitys.MainActivity
import java.util.*


class SplashFragment : Fragment() {
    val PERMISSION_ALL = 1
    val REQUEST_ENABLE_BT = 2
    val PERMISSIONS = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val reqPermission = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for (permission in PERMISSIONS){
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED){
                reqPermission.add(permission)
            }
        }
        if(reqPermission.isNotEmpty()) {
            requestPermissions(
                reqPermission.toTypedArray(),
                PERMISSION_ALL
            )
        }else {
            if ((activity as MainActivity).myBle.adapter.isEnabled) {
                startBle()
            } else {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_ALL -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }  ) {
                    if ((activity as MainActivity).myBle.adapter.isEnabled) {
                        startBle()
                    } else {
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                    }
                } else {
                    AlertDialog.Builder(requireContext()).apply {
                        setMessage(
                            "Themu requiere permisos para ejecutarse\n" +
                                    "Reinicie la app y habilite los permisos"
                        )
                        setTitle("Error de permisos")
                        setPositiveButton("RESTART"){_,_ ->
                            restartApp()
                        }
                        setCancelable(false)
                    }.create().show()
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            REQUEST_ENABLE_BT -> {
                if (resultCode == Activity.RESULT_OK) {
                    startBle()
                } else {
                    AlertDialog.Builder(requireContext()).apply {
                        setMessage(
                            "Themu requiere el bluetooth encendido \n" +
                                    "Reinicie la app y encienda el bluetooth"
                        )
                        setTitle("Error de bluetooth")
                        setPositiveButton("RESTART"){_,_ ->
                            restartApp()
                        }
                        setCancelable(false)
                    }.create().show()
                }
            }
        }
    }
    private fun startBle(){
        val loop = Handler(Looper.getMainLooper())
        var attempts = 10
        val timeStep: Long = 1000

        (activity as MainActivity).myBle.start()


        loop.post(object : Runnable {
            override fun run() {
                if(attempts==0){
                    AlertDialog.Builder(requireContext()).apply {
                        setMessage(
                            "No se logro conectar con Themu glove\n" +
                                    "Reinicie la aplicacion e intentelo denuevo"
                        )
                        setTitle("Error de Conexion")
                        setPositiveButton("RESTART"){_,_ ->
                            restartApp()
                        }
                        setCancelable(false)
                    }.create().show()
                }else{
                    if ((activity as MainActivity).myBle.isConnected()) {
                        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                    }else {
                        loop.postDelayed(this, timeStep)
                    }
                    attempts = attempts.dec()
                }
            }
        })
    }
    private fun restartApp(){
        val activity = requireActivity()
        val am = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am[AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis + 500] =
            PendingIntent.getActivity(
                activity, 0, activity.intent, PendingIntent.FLAG_ONE_SHOT
                        or PendingIntent.FLAG_CANCEL_CURRENT
            )
        val i = activity.baseContext.packageManager
            .getLaunchIntentForPackage(requireActivity().baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }
}