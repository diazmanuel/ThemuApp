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
import com.gloves.themu.classes.Link
import com.gloves.themu.classes.Profile
import com.gloves.themu.databases.ConexionSQLiteHelper
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    private var id:String = "0"
    private var db: ConexionSQLiteHelper? = null
    private var effects = emptyList<Effect>()
    private var links  = mutableListOf<Link>()
    private var auxGesture : Int? = null
    private var auxEffect : Effect? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString(R.string.key_id.toString()).toString()
        }
        db= ConexionSQLiteHelper(requireContext())
        effects = db!!.readEffects()


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
        var feedbackLed= 0
        if (!id.toBoolean()) {
            btnProfileSave.text= getString(R.string.btn_create)
        }

        imgLed.setOnClickListener {
            feedbackLed++
            feedbackLed = feedbackLed.rem(8)
            when (feedbackLed) {
                0 -> imgLed.setImageResource(R.drawable.feedback_led_a)
                1 -> imgLed.setImageResource(R.drawable.feedback_led_b)
                2 -> imgLed.setImageResource(R.drawable.feedback_led_c)
                3 -> imgLed.setImageResource(R.drawable.feedback_led_d)
                4 -> imgLed.setImageResource(R.drawable.feedback_led_e)
                5 -> imgLed.setImageResource(R.drawable.feedback_led_f)
                6 -> imgLed.setImageResource(R.drawable.feedback_led_g)
                7 -> imgLed.setImageResource(R.drawable.feedback_led_h)
            }
        }
        menuProfileEffect.setAdapter(ArrayAdapter(requireContext(),R.layout.dropdown_menu_popup_item,effects.map { it.usersName }))
        menuProfileEffect.inputType = 0
        menuProfileEffect.setOnItemClickListener { _, _, position, _ -> auxEffect = effects[position] }
        menuProfileGesture.setAdapter(ArrayAdapter(requireContext(),R.layout.dropdown_menu_popup_item,resources.getStringArray(R.array.gestures)))
        menuProfileGesture.inputType = 0
        menuProfileGesture.setOnItemClickListener { _, _, position, _ -> auxGesture= position }
        btnProfileAddLink.setOnClickListener {
            if(auxEffect != null && auxGesture != null){
                links.add(
                        Link(
                            auxGesture!!,
                            auxEffect!!,
                            feedbackLed,
                            0,
                            if(ckbDynParam.isChecked) 1 else 0
                            )
                        )
                ckbDynParam.isChecked=false
                auxEffect= null
                auxGesture= null
                feedbackLed = 0
                imgLed.setImageResource(R.drawable.feedback_led_a)
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
                        if (txtProfileName.text.isEmpty()) "Default" else txtProfileName.text.toString()
                    ,
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