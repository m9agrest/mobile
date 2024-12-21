package com.example.test.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class NewsAllFragment:NewsFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _all = true
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}