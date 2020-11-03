package com.gloves.themu.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.gloves.themu.R
import com.gloves.themu.activitys.MainActivity
import com.gloves.themu.classes.Effect
import com.gloves.themu.classes.NativeInterface
import com.gloves.themu.classes.Profile
import com.gloves.themu.databases.ConexionSQLiteHelper
import kotlinx.android.synthetic.main.fragment_session.*

class SessionFragment : Fragment() {

    private var id:Int? = null
    private val TAG = "BLE"
    private val range = 20
    private var isAudioEnabled: Boolean = false
    private var db: ConexionSQLiteHelper? = null

    private lateinit var profile : Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            id = it.getInt(R.string.key_id.toString())
        }
        setHasOptionsMenu(true)

        db= ConexionSQLiteHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_session, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as MainActivity).myBle.notify(true)
        Handler().postDelayed({

            loadProfile()
            btnSession.setOnClickListener {
                //findNavController().popBackStack()
                isAudioEnabled = !isAudioEnabled
                NativeInterface.enable(isAudioEnabled)
                if (isAudioEnabled){
                    btnSession.text = "STOP"
                }else{
                    btnSession.text = "START"
                }
            }
        }, 500)
    }

    override fun onResume() {
        super.onResume()
            NativeInterface.createAudioEngine()
            NativeInterface.enable(isAudioEnabled)
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).myBle.notify(false)
        NativeInterface.destroyAudioEngine()
        (activity as MainActivity).myBle.closeSession()
    }

    private fun process(fingers :IntArray, vector : FloatArray): Int{
        var led = 0
        var enable: Boolean
        for((index,link) in profile.links.withIndex()){
            enable = link.effect.enable
            link.effect.enable =
                                fingers[0] < link.gesture.littleFinger + range/2 &&
                                fingers[0] > link.gesture.littleFinger - range/2 &&
                                fingers[1] < link.gesture.ringFinger + range/2  &&
                                fingers[1] > link.gesture.ringFinger - range/2  &&
                                fingers[2] < link.gesture.middleFinger + range/2  &&
                                fingers[2] > link.gesture.middleFinger - range/2 &&
                                fingers[3] < link.gesture.indexFinger + range/2  &&
                                fingers[3] > link.gesture.indexFinger - range/2 &&
                                fingers[4] < link.gesture.thumbFinger + range/2  &&
                                fingers[4] > link.gesture.thumbFinger - range/2
            if(enable != link.effect.enable) NativeInterface.enableEffectAt(link.effect.enable,index)
            if (link.effect.enable) led = link.led
        }
        return led
    }
    private fun loadProfile(){

        profile = db?.readProfiles()?.find { it.profilePK == id  }!!
        for ((index,link) in profile.links.withIndex()){
            NativeInterface.addEffect(link.effect)
            NativeInterface.enableEffectAt(link.effect.enable,index)
            NativeInterface.updateParamsAt(link.effect,index)
        }
        (activity as MainActivity).myBle.openSession(::process)
    }
}