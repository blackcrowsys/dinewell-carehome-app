package com.blackcrowsys.ui.residentbio.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.blackcrowsys.R

/**
 * Fragment that displays the resident's meal history.
 */
class ResidentBioMealHistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resident_bio_meal_history, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            ResidentBioMealHistoryFragment()
    }
}
