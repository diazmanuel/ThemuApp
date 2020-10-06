package com.gloves.themu.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gloves.themu.R
import com.gloves.themu.adapters.GestureRecyclerAdapter
import com.gloves.themu.adapters.ProfileRecyclerAdapter
import com.gloves.themu.adapters.TopSpacingItemDecoration
import com.gloves.themu.classes.Profile
import com.gloves.themu.databases.ConexionSQLiteHelper
import kotlinx.android.synthetic.main.fragment_gesture_list.*
import kotlinx.android.synthetic.main.fragment_profile_list.*

class ProfileListFragment : Fragment() {
    private var db: ConexionSQLiteHelper? = null
    private var profiles = emptyList<Profile>()
    private lateinit var profileAdapter: ProfileRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db= ConexionSQLiteHelper(requireContext())

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_list, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = Bundle()
        data.putString(R.string.key_id.toString(),"0")
        btnAddProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileListFragment_to_profileFragment,data)
        }
    }

    override fun onResume() {
        super.onResume()
        profiles = db!!.readProfiles()

        if(profiles.isEmpty()){
            txtProfileEmpty.visibility = View.VISIBLE
        }else{
            txtProfileEmpty.visibility = View.INVISIBLE
        }

        initRecyclerView()
    }

    private fun initRecyclerView(){
        recyclerViewProfile.apply {
            layoutManager = LinearLayoutManager(requireContext())
            profileAdapter = ProfileRecyclerAdapter()
            addItemDecoration(TopSpacingItemDecoration(30))
            profileAdapter.sumbitList(profiles)
            adapter = profileAdapter
        }
    }
}