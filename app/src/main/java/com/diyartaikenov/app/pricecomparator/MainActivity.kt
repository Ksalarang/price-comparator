package com.diyartaikenov.app.pricecomparator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.diyartaikenov.app.pricecomparator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var bind: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
    }
}
