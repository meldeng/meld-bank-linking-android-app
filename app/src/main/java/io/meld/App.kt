package io.meld

import android.app.Application
import com.chibatching.kotpref.Kotpref
import com.chibatching.kotpref.gsonpref.gson
import com.google.gson.Gson
import io.meld.example.data.MeldData
import io.meld.sdk.MeldSDK
import io.meld.sdk.env.Environment
import io.meld.sdk.model.MeldConfiguration

class App : Application() {

    private val apiVersion = "2022-09-21"

    override fun onCreate() {
        super.onCreate()

        Kotpref.init(applicationContext)
        Kotpref.gson = Gson()


        val meldConfiguration = MeldConfiguration(
            apiVersion = apiVersion,
            isDebugLogEnabled = true
        )

        MeldSDK.init(
            apiKey = "W2a8zHdgnNoJQVWNyhFhBx:BoyNyR2Vzkuv1Ty6oLxBfvYGgQhhkGQBs4uhB",
            externalCustomerId = MeldData.externalCustomerId,
            environment = Environment.QA,
            meldConfiguration = meldConfiguration
        )
    }
}