package com.gloves.themu.fragments

import android.gesture.Gesture
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.gloves.themu.R
import com.gloves.themu.databases.ConexionSQLiteHelper
import kotlinx.android.synthetic.main.fragment_gesture_list.*
import kotlinx.android.synthetic.main.fragment_profile_list.*

class GestureListFragment : Fragment() {
    private var db: ConexionSQLiteHelper? = null
    private var gestures = emptyList<Gesture>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db= ConexionSQLiteHelper(requireContext())
        gestures = db.readGestures(gestures)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gesture_list, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = Bundle()
        data.putString(R.string.key_id.toString(),"0")
        btnAddGesture.setOnClickListener {
            findNavController().navigate(R.id.action_gestureListFragment_to_gestureFragment,data)
        }
    }
}