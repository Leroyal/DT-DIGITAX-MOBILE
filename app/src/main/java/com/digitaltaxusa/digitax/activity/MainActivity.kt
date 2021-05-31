package com.digitaltaxusa.digitax.activity

import android.graphics.RectF
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.digitaltaxusa.digitax.R
import com.digitaltaxusa.digitax.constants.Constants.TAG
import com.digitaltaxusa.digitax.databinding.ActivityMainBinding
import com.digitaltaxusa.digitax.models.calendar.EventInfo
import com.digitaltaxusa.digitax.models.calendar.listeners.EventClickListener
import com.digitaltaxusa.digitax.models.calendar.listeners.MonthChangeListener
import com.digitaltaxusa.digitax.models.calendar.weekview.WeekViewEvent
import com.digitaltaxusa.digitax.network.NetworkReceiver
import com.digitaltaxusa.framework.logger.Logger
import java.time.LocalDate
import java.util.*

class MainActivity : BaseActivity(), AdapterView.OnItemSelectedListener, EventClickListener,
    MonthChangeListener {

    // view binding and layout widgets
    // this property is only valid between onCreateView and onDestroyView
    private lateinit var binding: ActivityMainBinding

    // views and drawers
    private lateinit var drawerLayoutParent: View
    private var drawerLayout: DrawerLayout? = null

    // network
    private lateinit var networkReceiver: NetworkReceiver

    // variables
    private var eventInfoMap: HashMap<LocalDate, EventInfo>? = null

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

    override fun onEventClick(event: WeekViewEvent?, eventRect: RectF?) {
        // TODO("Not yet implemented")
    }

    override fun onMonthChange(newYear: Int, newMonth: Int): List<WeekViewEvent> {
        val initialDate = LocalDate.of(newYear, newMonth, 1)
        val length = initialDate.dayOfMonth

        Logger.i(TAG, "<onMonthChange> size= " + eventInfoMap?.size + ", length= " + length)
        val events: MutableList<WeekViewEvent> = ArrayList()
        for (i in 1..length) {
            val localDate = LocalDate.of(newYear, newMonth, i)
            if (eventInfoMap?.containsKey(localDate) == true) {
                var eventInfo = eventInfoMap?.get(localDate)
                while (eventInfo != null) {
                    val startTime = Calendar.getInstance(TimeZone.getTimeZone(eventInfo.timezone))
                    startTime.timeInMillis = eventInfo.startTime
                    val endTime =
                        Calendar.getInstance(TimeZone.getTimeZone(eventInfo.timezone)) as Calendar
                    endTime.timeInMillis = eventInfo.endTime
                    val event =
                        WeekViewEvent(eventInfo.id.toLong(), eventInfo.title, startTime, endTime)
                    event.isAllDay = eventInfo.isAllDay

                    // set event colors
                    // TODO temporary colors until enumeration of events is completed
                    if (eventInfo.isAllDay) {
                        event.color =
                            ContextCompat.getColor(this, R.color.material_green_300_color_code)
                    } else {
                        event.color =
                            ContextCompat.getColor(this, R.color.material_orange_300_color_code)
                    }
                    // add event
                    events.add(event)
                    eventInfo = eventInfo.nextNode
                }
            }
        }
        return events
    }

}