package com.gloves.themu.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gloves.themu.R
import com.gloves.themu.classes.Profile
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.layout_profile_list_item.view.*

class ProfileRecyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var profiles : List<Profile> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProfileViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_profile_list_item,parent,false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ProfileViewHolder ->{
                holder.bind(profiles[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return profiles.size
    }

    fun sumbitList(profileList : List<Profile>){
        profiles =profileList
    }

    class ProfileViewHolder constructor(profileView: View): RecyclerView.ViewHolder(profileView){
        private val txtProfileItem: MaterialTextView = profileView.txtProfileItem
        fun bind (profileItem : Profile){
            txtProfileItem.text = profileItem.usersName
        }
    }
}