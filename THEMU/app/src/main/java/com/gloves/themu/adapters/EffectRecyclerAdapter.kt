package com.gloves.themu.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gloves.themu.R
import com.gloves.themu.classes.Effect
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.layout_effect_list_item.view.*
import kotlinx.android.synthetic.main.layout_gesture_list_item.view.*

class EffectRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var effects : List<Effect> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EffectViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_effect_list_item,parent,false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is EffectViewHolder ->{
                holder.bind(effects[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return effects.size
    }

    fun sumbitList(effectList : List<Effect>){
        effects = effectList
    }

    class EffectViewHolder constructor(effectView: View): RecyclerView.ViewHolder(effectView){
        private val txtEffectItem: MaterialTextView = effectView.txtEffectItem
        fun bind (effectItem : Effect){
            txtEffectItem.text = effectItem.usersName
        }
    }
}