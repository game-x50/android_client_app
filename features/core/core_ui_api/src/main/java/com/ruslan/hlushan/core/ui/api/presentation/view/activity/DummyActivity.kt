package com.ruslan.hlushan.core.ui.api.presentation.view.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class DummyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @SuppressWarnings("MagicNumber")
        val delayMillis = 500L
        Handler(Looper.getMainLooper()).postDelayed({ this.finish() }, delayMillis)
    }
}