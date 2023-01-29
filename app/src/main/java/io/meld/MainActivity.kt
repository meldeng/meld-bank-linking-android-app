package io.meld

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.meld.banklinking.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    /**
     * API key to initialise MeldSDK
     * Please replace this key with your API key
     */

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
}