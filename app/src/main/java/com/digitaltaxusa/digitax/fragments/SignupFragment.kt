package com.digitaltaxusa.digitax.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.digitaltaxusa.digitax.databinding.FragmentSignupBinding

class SignupFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentSignupBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)

        // instantiate views and listeners
        initializeViews()
        initializeHandlers()
        initializeListeners()

        return binding.root
    }

    /**
     * Method is used to initialize views
     */
    private fun initializeViews() {

    }

    /**
     * Method is used to initialize click listeners
     */
    private fun initializeHandlers() {

    }

    /**
     * Method is used to initialize listeners and callbacks
     */
    private fun initializeListeners() {

    }

    override fun onClick(v: View) {
        TODO("Not yet implemented")
    }


}