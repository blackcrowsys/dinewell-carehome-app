package com.blackcrowsys.ui.residentbio

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.blackcrowsys.ui.residentbio.fragments.ResidentBioAllergyFragment
import com.blackcrowsys.ui.residentbio.fragments.ResidentBioIncidentFragment
import com.blackcrowsys.ui.residentbio.fragments.ResidentBioMealHistoryFragment

class ResidentBioPagerAdapter(
    fragmentManager: FragmentManager,
    private val tabsArray: Array<String>
) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence? {
        return tabsArray[position]
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ResidentBioAllergyFragment.newInstance()
            1 -> ResidentBioIncidentFragment.newInstance()
            2 -> ResidentBioMealHistoryFragment.newInstance()
            else -> throw NotImplementedError()
        }
    }
}