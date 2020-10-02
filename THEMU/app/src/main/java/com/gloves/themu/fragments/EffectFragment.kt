package com.gloves.themu.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.gloves.themu.R
import com.gloves.themu.classes.Effect
import com.gloves.themu.classes.NativeInterface
import com.gloves.themu.databases.ConexionSQLiteHelper
import kotlinx.android.synthetic.main.fragment_effect.*
import kotlinx.android.synthetic.main.fragment_gesture.*
import kotlinx.android.synthetic.main.param_effects.*
import java.util.*
import kotlin.concurrent.timerTask

class EffectFragment : Fragment() {

    private var id:String = "0"
    private val effectType = mutableListOf<String>()
    private var isAudioEnabled = false
    private var first = true
    private val floatFormat = "%4.2f"
    private var db: ConexionSQLiteHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString(R.string.key_id.toString()).toString()
        }
        setHasOptionsMenu(true)
        db= ConexionSQLiteHelper(requireContext())

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_effect, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var effect: Effect? = null

        for (effectName in NativeInterface.effectDescriptionMap.keys) {
            effectType.add(effectName)
        }
        menuEffect.setAdapter(ArrayAdapter(requireContext(),R.layout.dropdown_menu_popup_item,effectType))
        menuEffect.inputType = 0
        if (!id.toBoolean()) {
            btnEffectSave.text= getString(R.string.btn_create)
        }else{
            btnEffectSave.text= getString(R.string.btn_save)
        }
        btnEffectCancel.setOnClickListener {
            findNavController().popBackStack()
        }
        btnEffectSave.setOnClickListener {
            if (effect != null) {
                effect!!.usersName = if (txtEffectName.text.isNotEmpty()) txtEffectName.text.toString() else "Default"
                db?.insertEffect(effect!!)
                }
            findNavController().popBackStack()
        }
        menuEffect.setOnItemClickListener { parent, _, position, _ ->

            NativeInterface.effectDescriptionMap[parent.getItemAtPosition(position).toString()]?.let {
                if (!first) {
                    NativeInterface.removeEffectAt(0)
                    effectContainer.removeAllViews()
                }else{
                    first= false
                }
                effect = Effect(it)
                NativeInterface.addEffect(effect!!)

                for (ind in effect!!.effectDescription.paramValues.withIndex()) {
                    val param = ind.value
                    val counter = ind.index
                    val auxView = View.inflate(context,R.layout.param_effects, null)

                    effectContainer.addView(View.inflate(context,R.layout.spacer, null))
                    effectContainer.addView(auxView)

                    val paramEffectView: LinearLayout = auxView.findViewById(R.id.paramEffect)
                    val paramLabelView: TextView = paramEffectView.findViewById(R.id.paramLabel)
                    val minLabelView: TextView = paramEffectView.findViewById(R.id.minLabel)
                    val maxLabelView: TextView = paramEffectView.findViewById(R.id.maxLabel)
                    val curLabelView: TextView = paramEffectView.findViewById(R.id.curLabel)
                    val seekBarView: SeekBar = paramEffectView.findViewById(R.id.seekBar)
                    paramLabelView.text = param.paramName
                    minLabelView.text = floatFormat.format(param.minValue)
                    maxLabelView.text = floatFormat.format(param.maxValue)
                    seekBarView.progress = ((param.defaultValue - param.minValue) * 100 / (param.maxValue - param.minValue)).toInt()
                    curLabelView.text = floatFormat.format(param.defaultValue)


                    seekBarView.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        var timer : Timer? = null

                        override fun onStartTrackingTouch(p0: SeekBar?) {}

                        override fun onStopTrackingTouch(seekbar: SeekBar?) {}

                        override fun onProgressChanged(
                            seekBarView: SeekBar?, progress: Int, fromUser: Boolean
                        ) {
                            val fracprogress = ((seekBarView!!.progress / 100f) * (param.maxValue - param.minValue) + param.minValue)
                            curLabelView.text = floatFormat.format(fracprogress)

                            timer?.cancel()
                            timer = Timer()
                            timer?.schedule(timerTask { updateEffectParam(fracprogress) }, 100)
                        }

                        fun updateEffectParam(fracprogress : Float){
                            effect!!.paramValues[counter] = fracprogress
                            NativeInterface.updateParamsAt(effect!!, 0)
                        }
                    })

                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_GRANTED
        ) {
            NativeInterface.createAudioEngine()
            NativeInterface.enable(isAudioEnabled)
        }
    }

    override fun onPause() {
        super.onPause()
        NativeInterface.destroyAudioEngine()
    }


    override fun onCreateOptionsMenu(menu: Menu,inflater: MenuInflater) {
        inflater.inflate(R.menu.audio_menu,menu)
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_toggle_audio -> {
            isAudioEnabled = !isAudioEnabled
            NativeInterface.enable(isAudioEnabled)

            if (isAudioEnabled) {
                item.setIcon(R.drawable.ic_baseline_volume_up_24)
            } else {
                item.setIcon(R.drawable.ic_baseline_volume_off_24)
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}