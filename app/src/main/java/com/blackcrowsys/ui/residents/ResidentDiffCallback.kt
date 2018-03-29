package com.blackcrowsys.ui.residents

import android.support.v7.util.DiffUtil
import com.blackcrowsys.persistence.entity.Resident

class ResidentDiffCallback : DiffUtil.ItemCallback<Resident>() {
    override fun areItemsTheSame(oldItem: Resident?, newItem: Resident?): Boolean {
        return oldItem?.residentId == newItem?.residentId
    }

    override fun areContentsTheSame(oldItem: Resident?, newItem: Resident?): Boolean {
        return oldItem == newItem
    }
}