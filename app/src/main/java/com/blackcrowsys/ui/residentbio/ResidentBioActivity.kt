package com.blackcrowsys.ui.residentbio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.blackcrowsys.R
import com.blackcrowsys.ui.ViewModelFactory
import com.blackcrowsys.util.Constants.PIN_EXTRA
import kotlinx.android.synthetic.main.activity_resident_bio.*
import javax.inject.Inject

/**
 * Activity that displays the Resident's Bio info.
 */
class ResidentBioActivity : AppCompatActivity() {

    companion object {
        fun startResidentBioActivity(initialContext: Context, pin: String) {
            val intent = Intent(initialContext, ResidentBioActivity::class.java)
            intent.putExtra(PIN_EXTRA, pin)
            initialContext.startActivity(intent)
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var bioPagerAdapter: ResidentBioPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resident_bio)

        val allergiesTitle = getString(R.string.allergies)
        val incidentsTitle = getString(R.string.incidents)
        val mealHistoryTitle = getString(R.string.meal_history)

        bioPagerAdapter = ResidentBioPagerAdapter(supportFragmentManager, arrayOf(allergiesTitle, incidentsTitle, mealHistoryTitle))
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
    }
}
