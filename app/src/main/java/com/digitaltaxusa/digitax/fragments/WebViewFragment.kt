package com.digitaltaxusa.digitax.fragments

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import com.digitaltaxusa.digitax.R
import com.digitaltaxusa.digitax.constants.Constants
import com.digitaltaxusa.digitax.databinding.FragmentWebviewBinding
import com.digitaltaxusa.framework.utils.ProgressBarUtils

class WebViewFragment : BaseFragment(), View.OnClickListener {

    // layout widgets
    private var _binding: FragmentWebviewBinding? = null
    private val binding: FragmentWebviewBinding get() = _binding!!
    private var ivBack: ImageView? = null

    // progress bar
    private var progressBar: ProgressBarUtils? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebviewBinding.inflate(inflater, container, false)

        // initialize views, handlers and listeners
        initializeViews()
        initializeHandlers()
        initializeListeners()
        // return root
        return binding.root
    }

    /**
     * Method is used to initialize views.
     */
    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    private fun initializeViews() {
        // instantiate views
        ivBack = binding.root.findViewById(R.id.iv_back)

        // initialize progress bar
        progressBar = activity?.run { ProgressBarUtils(this) }

        val args = arguments
        if (args != null) {
            // show progress dialog
            progressBar?.showLoading(true)

            // webview settings
            binding.webView.settings.javaScriptEnabled = true
            binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true
            binding.webView.settings.domStorageEnabled = true

            // set header
            binding.header.tvHeader.text = args.getString(
                Constants.KEY_WEB_VIEW_HEADER,
                ""
            )

            // load url
            binding.webView.loadUrl(args.getString(Constants.KEY_WEB_VIEW_URL, ""))
        }
    }

    /**
     * Method is used to initialize click listeners.
     */
    private fun initializeHandlers() {
        ivBack?.setOnClickListener(this)
    }

    /**
     * Method is used to initialize listeners and callbacks.
     */
    private fun initializeListeners() {
        // webview listener
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                // dismiss loading dialog
                progressBar?.showLoading(false)
            }

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                // dismiss loading dialog
                progressBar?.showLoading(false)
            }

            @Suppress("DEPRECATION")
            @TargetApi(android.os.Build.VERSION_CODES.M)
            override fun onReceivedError(
                view: WebView,
                req: WebResourceRequest,
                err: WebResourceError
            ) {
                // dismiss loading dialog
                progressBar?.showLoading(false)
                // redirect to deprecated method, so it can be used in all OS versions
                onReceivedError(view, err.errorCode, err.description.toString(), req.url.toString())
            }
        }
        // track back button
        binding.webView.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                // if there is web page history, go back one web page
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                    return@OnKeyListener true
                }
            }
            false
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                // remove fragment
                remove()
            }
        }
    }

    override fun onDestroyView() {
        binding.webView.destroy()
        _binding = null
        super.onDestroyView()
    }
}
