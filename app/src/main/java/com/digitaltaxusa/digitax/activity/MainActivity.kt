package com.digitaltaxusa.digitax.activity

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.drawerlayout.widget.DrawerLayout
import com.digitaltaxusa.digitax.R
import com.digitaltaxusa.digitax.databinding.ActivityMainBinding
import com.digitaltaxusa.digitax.network.NetworkReceiver
import java.util.*

class MainActivity : BaseActivity(), AdapterView.OnItemSelectedListener {

    // view binding and layout widgets
    // this property is only valid between onCreateView and onDestroyView
    private lateinit var binding: ActivityMainBinding

    // views and drawers
    private lateinit var drawerLayoutParent: View
    private var drawerLayout: DrawerLayout? = null

    // network
    private lateinit var networkReceiver: NetworkReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        // instantiate views and listeners
        initializeViews()
        initializeHandlers()
        initializeListeners()
        initializeDrawer()
    }

    /**
     * Method is used to initialize views.
     */
    private fun initializeViews() {
        // initialize views
        networkReceiver = NetworkReceiver()

        // set adapter for arrays
        val months = resources.getStringArray(R.array.array_months)
        val adapterMonths = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            months
        )
        binding.toolbar.spnrMonths.adapter = adapterMonths

        // set labels (sub-toolbar)
        // the values for these labels require API call to retrieve
        binding.milesDriven.tvLabel.text = getString(R.string.miles_driven)
        binding.drives.tvLabel.text = getString(R.string.drives)
        binding.events.tvLabel.text = getString(R.string.events)
        binding.expenses.tvLabel.text = getString(R.string.expenses)
        // set defaults
        binding.milesDriven.tvValue.text = getString(R.string.default_value)
        binding.drives.tvValue.text = getString(R.string.default_value)
        binding.events.tvValue.text = getString(R.string.default_value)
        binding.expenses.tvValue.text = getString(R.string.default_dollar_value)
    }

    /**
     * Method is used to initialize click listeners.
     */
    private fun initializeHandlers() {
        // spinner
        binding.toolbar.spnrMonths.onItemSelectedListener = this
    }

    /**
     * Method is used to initialize listeners and callbacks.
     */
    private fun initializeListeners() {

    }

    /**
     * Method is used to initialize drawer.
     */
    private fun initializeDrawer() {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // TODO("Not yet implemented")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // TODO("Not yet implemented")
    }

}