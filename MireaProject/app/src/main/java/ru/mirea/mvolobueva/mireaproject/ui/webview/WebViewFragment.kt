package ru.mirea.mvolobueva.mireaproject.ui.webview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import ru.mirea.mvolobueva.mireaproject.R

class WebViewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_web_view, container, false)

        val webView = view.findViewById<WebView>(R.id.webView)

        webView.webViewClient = WebViewClient()

        webView.loadUrl("https://www.google.com")

        return view
    }
}