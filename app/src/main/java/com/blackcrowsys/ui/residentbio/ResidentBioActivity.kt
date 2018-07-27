package com.blackcrowsys.ui.residentbio

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.blackcrowsys.R
import com.blackcrowsys.persistence.entity.Resident
import com.blackcrowsys.ui.ViewModelFactory
import com.blackcrowsys.util.Constants.PIN_EXTRA
import com.blackcrowsys.util.Constants.RESIDENT_ID_EXTRA
import com.blackcrowsys.util.ViewState
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_resident_bio.*
import javax.inject.Inject

/**
 * Activity that displays the Resident's Bio info.
 */
class ResidentBioActivity : AppCompatActivity() {

    companion object {
        fun startResidentBioActivity(initialContext: Context, pin: String, residentId: Int) {
            val intent = Intent(initialContext, ResidentBioActivity::class.java)
            intent.putExtra(PIN_EXTRA, pin)
            intent.putExtra(RESIDENT_ID_EXTRA, residentId)
            initialContext.startActivity(intent)
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var bioPagerAdapter: ResidentBioPagerAdapter
    private lateinit var residentBioActivityViewModel: ResidentBioActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resident_bio)

        val allergiesTitle = getString(R.string.allergies)
        val incidentsTitle = getString(R.string.incidents)
        val mealHistoryTitle = getString(R.string.meal_history)

        residentBioActivityViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(ResidentBioActivityViewModel::class.java)

        val residentId = intent.getIntExtra(RESIDENT_ID_EXTRA, 0)

        residentBioActivityViewModel.retrieveResident(residentId)

        residentBioActivityViewModel.retrieveResidentBio(intent.getStringExtra(PIN_EXTRA), residentId)

        bioPagerAdapter = ResidentBioPagerAdapter(supportFragmentManager, arrayOf(allergiesTitle, incidentsTitle, mealHistoryTitle), residentId)
        viewPager.adapter = bioPagerAdapter

        tlResidentTabs.setupWithViewPager(viewPager)
        tlResidentTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, posOffset: Float, posOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                val tab = tlResidentTabs.getTabAt(position)
                tab?.select()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        residentBioActivityViewModel.residentViewState.observe(this, Observer {
            showTopLevelResidentInfo(it)
        })
    }

    private fun showTopLevelResidentInfo(viewState: ViewState?) {
        when (viewState) {
            is ViewState.Success<*> -> {
                val resident = viewState.data as Resident
                ivResidentBioImage.setImageURI(resident.imageUrl)
                tvResidentBioName.text = getString(R.string.name_placeholder, resident.firstName, resident.surname)
                tvResidentBioRoom.text = getString(R.string.room_building_placeholder, resident.room)
            }
            is ViewState.Error -> {
                Log.e("ResidentBioActivity", viewState.throwable.message)
            }
        }
    }
}
