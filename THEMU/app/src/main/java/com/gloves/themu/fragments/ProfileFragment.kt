package com.gloves.themu.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.gloves.themu.R
import com.gloves.themu.classes.Effect
import com.gloves.themu.classes.Gesture
import com.gloves.themu.classes.Link
import com.gloves.themu.classes.Profile
import com.gloves.themu.databases.ConexionSQLiteHelper
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    private var id:String = "0"
    private var db: ConexionSQLiteHelper? = null
    private var gestures = emptyList<Gesture>()
    private var effects = emptyList<Effect>()
    private var links  = mutableListOf<Link>()
    private var auxGesture : Gesture? = null
    private var auxEffect : Effect? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString(R.string.key_id.toString()).toString()
        }
        db= ConexionSQLiteHelper(requireContext())
        effects = db!!.readEffects()
        gestures = db!!.readGestures()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!id.toBoolean()) {
            btnProfileSave.text= getString(R.string.btn_create)
        }
        menuProfileEffect.setAdapter(ArrayAdapter(requireContext(),R.layout.dropdown_menu_popup_item,effects.map { it.usersName }))
        menuProfileEffect.inputType = 0
        menuProfileEffect.setOnItemClickListener { parent, view, position, id -> auxEffect = effects[position] }
        menuProfileGesture.setAdapter(ArrayAdapter(requireContext(),R.layout.dropdown_menu_popup_item,gestures.map { it.usersName }))
        menuProfileGesture.inputType = 0
        menuProfileGesture.setOnItemClickListener { parent, view, position, id -> auxGesture= gestures[position] }
        btnProfileAddLink.setOnClickListener {
            if(auxEffect != null && auxGesture != null){
                links.add(Link(auxGesture!!, auxEffect!!,0,0))
                auxEffect= null
                auxGesture= null
                menuProfileGesture.text.clear()
                menuProfileGesture.clearFocus()
                menuProfileEffect.text.clear()
                menuProfileEffect.clearFocus()
            }
        }
        btnProfileSave.setOnClickListener {
            if(links.isNotEmpty()){
                db?.insertProfile(
                    Profile(
                        links,
                        txtProfileName.text.toString(),
                        0
                    )
                )
                findNavController().popBackStack()
            }
        }

        btnProfileCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}