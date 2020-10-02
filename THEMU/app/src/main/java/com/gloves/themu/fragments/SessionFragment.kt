package com.gloves.themu.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.gloves.themu.R
import com.gloves.themu.classes.Effect
import com.gloves.themu.classes.NativeInterface
import kotlinx.android.synthetic.main.fragment_session.*

class SessionFragment : Fragment() {

    private var id:String? = null
    private var isAudioEnabled: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            id = it.getString(R.string.key_id.toString())
        }

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

}