package com.gloves.themu.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gloves.themu.R
import com.gloves.themu.adapters.EffectRecyclerAdapter
import com.gloves.themu.adapters.TopSpacingItemDecoration
import com.gloves.themu.classes.Effect
import com.gloves.themu.databases.ConexionSQLiteHelper
import kotlinx.android.synthetic.main.fragment_effect_list.*

class EffectListFragment : Fragment() {
    private var db: ConexionSQLiteHelper? = null
    private var effects = emptyList<Effect>()
    private lateinit var effectAdapter: EffectRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db= ConexionSQLiteHelper(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_effect_list, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = Bundle()
        data.putString(R.string.key_id.toString(),"0")
        btnAddEffect.setOnClickListener {
            findNavController().navigate(R.id.action_effectListFragment_to_effectFragment,data)
        }
    }

    override fun onResume() {
        super.onResume()
        effects = db!!.readEffects()

        if(effects.isEmpty()){
            txtEffectEmpty.visibility = View.VISIBLE
        }else{
            txtEffectEmpty.visibility = View.INVISIBLE
        }

        initRecyclerView()
    }

    private fun initRecyclerView(){
        recyclerViewEffect.apply {
            layoutManager = LinearLayoutManager(requireContext())
            effectAdapter = EffectRecyclerAdapter()
            addItemDecoration(TopSpacingItemDecoration(30))
            effectAdapter.sumbitList(effects)
            adapter = effectAdapter
        }
    }

}