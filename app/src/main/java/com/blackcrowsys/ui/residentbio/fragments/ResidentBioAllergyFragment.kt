package com.blackcrowsys.ui.residentbio.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.blackcrowsys.R
import com.blackcrowsys.api.models.ResidentBioResponse
import com.blackcrowsys.persistence.datamodel.ResidentAllergyHolder
import com.blackcrowsys.ui.residentbio.ResidentBioActivityViewModel
import com.blackcrowsys.util.Constants.RESIDENT_ID_EXTRA
import com.blackcrowsys.util.ViewState
import kotlinx.android.synthetic.main.fragment_resident_bio_allergy.*

/**
 * Fragment that displays the Resident's Allergy information.
 */
class ResidentBioAllergyFragment : Fragment() {

    private lateinit var viewModel: ResidentBioActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resident_bio_allergy, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(ResidentBioActivityViewModel::class.java)

        viewModel.residentBioViewState.observe(this, Observer {
            handleInitialResidentBioState(it)
        })

        viewModel.residentAllergiesViewState.observe(this, Observer {
            handleAllergiesStateFromDb(it)
        })
    }

    private fun handleAllergiesStateFromDb(viewState: ViewState?) {
        when (viewState) {
            is ViewState.Success<*> -> {
                val residentAllergy = viewState.data as ResidentAllergyHolder
                tvAllergies.append(getString(R.string.allergies_placeholder,
                    residentAllergy.allergy.allergen, residentAllergy.residentAllergy.severity))
            }
            is ViewState.Error -> {
                Log.e("ResidentBioAllergyFR", viewState.throwable.message)
            }
        }
    }

    private fun handleInitialResidentBioState(viewState: ViewState?) {
        when (viewState) {
            is ViewState.Success<*> -> {
                val residentBioResponse = viewState.data as ResidentBioResponse
                residentBioResponse.allergies.forEach {
                    tvAllergies.append(
                        getString(
                            R.string.allergies_placeholder,
                            it.allergen,
                            it.severity
                        )
                    )
                }
            }
            is ViewState.Error -> {
                viewModel.retrieveResidentAllergiesFromDb(arguments!!.getInt(RESIDENT_ID_EXTRA))
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(residentId: Int): ResidentBioAllergyFragment {
            val fragment = ResidentBioAllergyFragment()

            val args = Bundle()
            args.putInt(RESIDENT_ID_EXTRA, residentId)
            fragment.arguments = args

            return fragment
        }
    }
}
