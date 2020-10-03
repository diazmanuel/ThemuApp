package com.gloves.themu.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.gloves.themu.R
import com.gloves.themu.classes.Gesture
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.layout_gesture_list_item.view.*

class GestureRecyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {


        private var gestures : List<Gesture> = ArrayList()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return GestureViewHolder(
                        LayoutInflater.from(parent.context).inflate(R.layout.layout_gesture_list_item,parent,false)
                )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                when(holder){
                        is GestureViewHolder ->{
                                holder.bind(gestures[position])
                        }
                }
        }

        override fun getItemCount(): Int {
                return gestures.size
        }

        fun sumbitList(gestureList : List<Gesture>){
                gestures = gestureList
        }

        class GestureViewHolder constructor(gestureView: View): RecyclerView.ViewHolder(gestureView){
                val txtGestureItem: MaterialTextView = gestureView.txtGestureItem
                fun bind (gestureItem : Gesture){
                        txtGestureItem.text = gestureItem.gestureUsersName
                }
        }


}