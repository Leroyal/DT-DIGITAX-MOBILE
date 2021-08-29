package com.digitaltaxusa.digitax.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.digitaltaxusa.digitax.R
import com.digitaltaxusa.digitax.databinding.FragmentTrackExpensesBinding
import com.digitaltaxusa.framework.utils.DialogUtils
import com.digitaltaxusa.framework.utils.FrameworkUtils

class TrackExpensesFragment : BaseFragment(), View.OnClickListener {

    // layout widgets
    private lateinit var binding: FragmentTrackExpensesBinding

    // dialog
    private var dialog: DialogUtils = DialogUtils()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackExpensesBinding.inflate(inflater, container, false)

        // instantiate views and listeners
        initializeViews()
        initializeHandlers()
        initializeListeners()
        // return root
        return binding.root
    }

    /**
     * Method is used to initialize views.
     */
    private fun initializeViews() {
        // set header
        binding.header.tvHeader.text = resources.getString(R.string.navigation_track_expenses)
    }

    /**
     * Method is used to initialize click listeners.
     */
    private fun initializeHandlers() {
        binding.header.ivBack.setOnClickListener(this)
    }

    /**
     * Method is used to initialize listeners and callbacks.
     */
    private fun initializeListeners() {

    }

    override fun onClick(v: View) {
        if (!FrameworkUtils.isViewClickable) {
            return
        }
        when (v.id) {
            R.id.iv_back -> {
                // remove fragment
                remove()
            }
        }
    }
}