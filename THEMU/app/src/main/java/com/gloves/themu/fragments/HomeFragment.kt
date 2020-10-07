package com.gloves.themu.fragments

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.gloves.themu.R
import com.gloves.themu.classes.Gesture
import com.gloves.themu.classes.Profile
import com.gloves.themu.databases.ConexionSQLiteHelper
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private var db: ConexionSQLiteHelper? = null
    private var profiles = emptyList<Profile>()
    private var profilePK: Int? = null
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
        val data = Bundle()

        findNavController().graph.startDestination = R.id.homeFragment
        menuHome.setAdapter(ArrayAdapter(requireContext(),R.layout.dropdown_menu_popup_item,profiles.map { it.usersName }))
        menuHome.inputType=0
        menuHome.setOnItemClickListener { parent, view, position, id ->  profilePK = profiles[position].profilePK}
        btnStartSession.setOnClickListener {
            profilePK?.let {
                data.putInt(R.string.key_id.toString(), it)
                findNavController().navigate(R.id.action_homeFragment_to_sessionFragment,data)
            }

        }
    }
}