package io.meld

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import io.meld.banklinking.databinding.ActivitySplashBinding
import io.meld.banklinking.ConfigActivity
import io.meld.banklinking.data.MeldData
import io.meld.sdk.enums.Products

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (MeldData.products == null || MeldData.products?.size == 0) {
            MeldData.products = listOf<String>(Products.ACCOUNT_DETAILS.name)
        }
        Handler(Looper.getMainLooper()).postDelayed({
            Intent(this, ConfigActivity::class.java).apply {
                startActivity(this)
            }
            finish()
        }, 2000)
    }
}