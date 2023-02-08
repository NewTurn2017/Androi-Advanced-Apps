package com.advanced.chapter1

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.EditText
import android.widget.Toast
import com.advanced.chapter1.databinding.FragmentWebViewBinding

class WebViewFragment(private val position: Int, private val webViewUrl: String) : Fragment() {

    var listener: OnTabLayoutNameChanged? = null

    private lateinit var binding: FragmentWebViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webView = binding.webView
        webView.apply {
            setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_BOUND, true)
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            webViewClient = WebtoonWebViewClient(binding.progressBar) { url ->
                val sharedPreference =
                    activity?.getSharedPreferences("WEB_HISTORY", Context.MODE_PRIVATE)
                sharedPreference?.edit()?.putString("tab${position}", url)?.apply()
            }
            settings.javaScriptEnabled = true
            loadUrl(webViewUrl)
        }

        binding.backToListButton.setOnClickListener {
            val sharedPreference =
                activity?.getSharedPreferences("WEB_HISTORY", Context.MODE_PRIVATE)
            val url = sharedPreference?.getString("tab${position}", "")
            if (url.isNullOrEmpty()) {
                Toast.makeText(activity, "마지막 저장 시점이 없습니다.", Toast.LENGTH_SHORT).show()
            } else {
                webView.loadUrl(url)
            }
        }

        binding.changeTabNameButton.setOnClickListener {
            // show Alert dialog
            val dialog = AlertDialog.Builder(activity)
            val editText = EditText(activity)
            dialog.setTitle("탭 이름 변경")
            dialog.setView(editText)
            dialog.setPositiveButton("확인") { _, _ ->
                val tabName = editText.text.toString()
                val sharedPreference =
                    activity?.getSharedPreferences("WEB_HISTORY", Context.MODE_PRIVATE)
                sharedPreference?.edit()?.putString("tab${position}_name", tabName)?.apply()
                listener?.nameChanged(position, tabName)
            }
            dialog.setNegativeButton("취소") { _, _ -> }
            dialog.show()
        }
    }

    fun canGoBack(): Boolean {
        return binding.webView.canGoBack()
    }


    fun goBack() {
        binding.webView.goBack()
    }
}

interface OnTabLayoutNameChanged {
    fun nameChanged(position: Int, name: String)
}