package com.gloves.themu.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.gloves.themu.R
import com.gloves.themu.adapters.GestureRecyclerAdapter
import com.gloves.themu.adapters.TopSpacingItemDecoration
import com.gloves.themu.classes.Gesture
import com.gloves.themu.databases.ConexionSQLiteHelper
import kotlinx.android.synthetic.main.fragment_gesture_list.*

class GestureListFragment : Fragment() {
    private var db: ConexionSQLiteHelper? = null
    private var gestures = emptyList<Gesture>()
    private lateinit var gestureAdapter: GestureRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db= ConexionSQLiteHelper(requireContext())

    }

    override fun onResume() {
        super.onResume()
        gestures = db!!.readGestures()

        if(gestures.isEmpty()){
            txtGestureEmpty.visibility = View.VISIBLE
        }else{
            txtGestureEmpty.visibility = View.INVISIBLE
        }

        initRecyclerView()
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



    private fun initRecyclerView(){
        recyclerViewGesture.apply {
            layoutManager = LinearLayoutManager(requireContext())
            gestureAdapter = GestureRecyclerAdapter()
            addItemDecoration(TopSpacingItemDecoration(30))
            gestureAdapter.sumbitList(gestures)
            adapter = gestureAdapter
        }
    }
}