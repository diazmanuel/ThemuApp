package com.gloves.themu.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.navigation.fragment.findNavController
import com.gloves.themu.R
import com.gloves.themu.activitys.MainActivity
import com.gloves.themu.classes.Link
import com.gloves.themu.classes.NativeInterface
import com.gloves.themu.classes.Profile
import com.gloves.themu.databases.ConexionSQLiteHelper
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.imgGesture0
import kotlinx.android.synthetic.main.fragment_home.imgGesture1
import kotlinx.android.synthetic.main.fragment_home.imgGesture2
import kotlinx.android.synthetic.main.fragment_home.imgGesture3
import kotlinx.android.synthetic.main.fragment_home.imgGesture4
import kotlinx.android.synthetic.main.fragment_home.imgGesture5
import kotlinx.android.synthetic.main.fragment_home.imgGesture6
import kotlinx.android.synthetic.main.fragment_home.imgGesture7
import java.util.*
import java.util.function.Predicate

class HomeFragment : Fragment() {
    private var db: ConexionSQLiteHelper? = null
    private var profiles = emptyList<Profile>()
    private var profile: Profile? = null
    private val audioEnable = false
    private var toggleButton = true
    private var leds = mutableListOf<Link>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db= ConexionSQLiteHelper(requireContext())
        profiles = db!!.readProfiles()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().graph.startDestination = R.id.homeFragment
        (activity as MainActivity).myBle.startUpdateSymbol(::updateSymbol)
        menuHome.setAdapter(ArrayAdapter(requireContext(),R.layout.dropdown_menu_popup_item,profiles.map { it.usersName }))
        menuHome.inputType=0
        menuHome.setOnItemClickListener { _, _, position, _ ->  profile = profiles[position]}
        makeSkUnchangeable()
        btnStartSession.setOnClickListener {
            if(toggleButton) {
                profile?.let{
                    loadProfile()
                    btnStartSession.text=resources.getString(R.string.btn_stopSession)
                    toggleButton=false
                    menuHome.isClickable=false
                    chrSession.base=SystemClock.elapsedRealtime()
                    chrSession.start()
                }
            }else{
                profile=null
                btnStartSession.text=resources.getString(R.string.btn_startSession)
                toggleButton=true
                stopSession()
                menuHome.isClickable=true
                menuHome.clearFocus()
                menuHome.text.clear()
                leds.clear()
                chrSession.stop()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        NativeInterface.createAudioEngine()
        NativeInterface.enable(audioEnable)
    }
    override fun onPause() {
        super.onPause()
        (activity as MainActivity).myBle.stopProcess()
        (activity as MainActivity).myBle.stopUpdateSymbol()
        NativeInterface.destroyAudioEngine()
    }

     private fun updateSymbol(flex :IntArray, gestures : BitSet){
        skBarFlex.progress = flex[0]

        for (i in 0..7) {
            if (gestures.get(i)) {
                when (i) {
                    0 -> imgGesture0.setImageResource(R.drawable.gesture_on)
                    1 -> imgGesture1.setImageResource(R.drawable.gesture_on)
                    2 -> imgGesture2.setImageResource(R.drawable.gesture_on)
                    3 -> imgGesture3.setImageResource(R.drawable.gesture_on)
                    4 -> imgGesture4.setImageResource(R.drawable.gesture_on)
                    5 -> imgGesture5.setImageResource(R.drawable.gesture_on)
                    6 -> imgGesture6.setImageResource(R.drawable.gesture_on)
                    7 -> imgGesture7.setImageResource(R.drawable.gesture_on)
                }
            } else {
                when (i) {
                    0 -> imgGesture0.setImageResource(R.drawable.gesture_off)
                    1 -> imgGesture1.setImageResource(R.drawable.gesture_off)
                    2 -> imgGesture2.setImageResource(R.drawable.gesture_off)
                    3 -> imgGesture3.setImageResource(R.drawable.gesture_off)
                    4 -> imgGesture4.setImageResource(R.drawable.gesture_off)
                    5 -> imgGesture5.setImageResource(R.drawable.gesture_off)
                    6 -> imgGesture6.setImageResource(R.drawable.gesture_off)
                    7 -> imgGesture7.setImageResource(R.drawable.gesture_off)
                }
            }
        }
    }

    private fun process(flex :IntArray, gestures : BitSet): Int{

        for ((index, link) in profile!!.links.withIndex()) {
                if ((gestures.get(link.gesture)) && (!link.effect.enable)) {
                    NativeInterface.enableEffectAt(true, index)
                    link.effect.enable = true
                    leds.add(link)
                }
                if ((!gestures.get(link.gesture)) && (link.effect.enable)) {
                    NativeInterface.enableEffectAt(false, index)
                    link.effect.enable = false
                    leds.removeIf { it.gesture == link.gesture }
                }
                if (link.effect.enable && link.dynEffect > 0) {
                    val newValue = (
                            (flex[0] / 100f) *
                                    (link.effect.effectDescription.paramValues[link.dynEffect - 1].maxValue -
                                            link.effect.effectDescription.paramValues[link.dynEffect - 1].minValue) +
                                    link.effect.effectDescription.paramValues[link.dynEffect - 1].minValue)
                    if ((newValue)>(link.effect.paramValues[0]+.20f) || newValue<(link.effect.paramValues[0]-.20f) ) {
                        link.effect.paramValues[0] = newValue
                        NativeInterface.enableEffectAt(false, index)
                        NativeInterface.updateParamsAt(link.effect, index)
                        NativeInterface.enableEffectAt(true, index)

                    }
                }
        }
        return if(leds.isNotEmpty()) leds.last().led else 0
    }

    private fun stopSession(){
        NativeInterface.destroyAudioEngine()
        NativeInterface.createAudioEngine()
        NativeInterface.enable(audioEnable)
        (activity as MainActivity).myBle.setLed(0)
        (activity as MainActivity).myBle.stopProcess()
    }

    private fun loadProfile(){

        for ((index,link) in profile!!.links.withIndex()){
            NativeInterface.addEffect(link.effect)
            NativeInterface.enableEffectAt(link.effect.enable,index)
            NativeInterface.updateParamsAt(link.effect,index)
        }
        (activity as MainActivity).myBle.startProcess(::process)
        NativeInterface.enable(true)
    }

    private fun makeSkUnchangeable() {
        skBarFlex.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var originalProgress = 0
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                //Nothing here..
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                originalProgress = seekBar.progress
            }

            override fun onProgressChanged(
                seekBar: SeekBar,
                arg1: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    seekBar.progress = originalProgress
                }
            }
        })
    }
}