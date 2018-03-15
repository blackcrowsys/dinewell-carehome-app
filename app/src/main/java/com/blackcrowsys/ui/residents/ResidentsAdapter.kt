package com.blackcrowsys.ui.residents

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blackcrowsys.R
import com.blackcrowsys.persistence.entity.Resident
import kotlinx.android.synthetic.main.resident_rv_item.view.*

class ResidentsAdapter(private val residentsList: List<Resident>) :
    RecyclerView.Adapter<ResidentsAdapter.ResidentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResidentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.resident_rv_item, parent, false)
        return ResidentViewHolder(view)
    }

    override fun getItemCount() = residentsList.size

    override fun onBindViewHolder(holder: ResidentViewHolder, position: Int) {
        holder.bindResident(residentsList[position])
    }

    class ResidentViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindResident(resident: Resident) = with(resident) {
            if (imageUrl.isNotBlank()) {
                itemView.ivResidentImage.setImageURI(imageUrl)
            }
            itemView.tvResidentName.text =
                    itemView.context.getString(R.string.name_placeholder, firstName, surname)
        }
    }
}