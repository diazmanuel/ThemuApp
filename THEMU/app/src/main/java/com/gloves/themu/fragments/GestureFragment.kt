package com.gloves.themu.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gloves.themu.R
import com.gloves.themu.activitys.MainActivity
import com.gloves.themu.classes.Gesture
import com.gloves.themu.databases.ConexionSQLiteHelper
import kotlinx.android.synthetic.main.fragment_gesture.*


class GestureFragment : Fragment() {
    private var id:String = "0"
    private val timeStep :Long = 500
    private val loop = Handler(Looper.getMainLooper())
    private var db: ConexionSQLiteHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            id = it.getString(R.string.key_id.toString()).toString()
        }
        db= ConexionSQLiteHelper(requireContext())

    }

    private fun updateValue(){

        skBarPulgar.progress  = (activity as MainActivity).myBle.fingers[4]
        skBarIndice.progress  = (activity as MainActivity).myBle.fingers[3]
        skBarMedio.progress   = (activity as MainActivity).myBle.fingers[2]
        skBarAnular.progress  = (activity as MainActivity).myBle.fingers[1]
        skBarMenique.progress = (activity as MainActivity).myBle.fingers[0]

        axisX.text = (activity as MainActivity).myBle.newVector[0].toString()
        axisY.text = (activity as MainActivity).myBle.newVector[1].toString()
        axisZ.text = (activity as MainActivity).myBle.newVector[2].toString()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gesture, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).myBle.notify(true)

        if (!id.toBoolean()) {
            btnGestureSave.text= getString(R.string.btn_create)
        }else{
            btnGestureSave.text= getString(R.string.btn_save)
        }
        btnGestureSave.setOnClickListener {
            if(!id.toBoolean()){
                val name = if (txtGestureName.text.isEmpty()) "Default" else txtGestureName.text.toString()
                db?.insertGesture(Gesture(
                    0,
                    name,
                    (activity as MainActivity).myBle.fingers[0],
                    (activity as MainActivity).myBle.fingers[1],
                    (activity as MainActivity).myBle.fingers[2],
                    (activity as MainActivity).myBle.fingers[3],
                    (activity as MainActivity).myBle.fingers[4],
                    (activity as MainActivity).myBle.newVector[0],
                    (activity as MainActivity).myBle.newVector[1],
                    (activity as MainActivity).myBle.newVector[2]
                ))
            }
            findNavController().popBackStack()
        }
        btnGestureCancel.setOnClickListener {
            findNavController().popBackStack()
        }
        makeSkUnchangeable()
        loop.post(object : Runnable {
            override fun run() {
                updateValue()
                loop.postDelayed(this, timeStep)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        loop.removeMessages(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).myBle.notify(false)

    }


    private fun makeSkUnchangeable() {
        skBarMenique.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
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
        skBarAnular.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
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
        skBarMedio.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
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
        skBarIndice.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
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
        skBarPulgar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
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