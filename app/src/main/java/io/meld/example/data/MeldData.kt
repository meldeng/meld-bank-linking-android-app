package io.meld.example.data

import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.gsonpref.gsonNullablePref
import io.meld.sdk.model.response.*

object MeldData : KotprefModel() {
    var connect by gsonNullablePref<ConnectResponse>()
    var institutionId by nullableStringPref()
    var institutionName by nullableStringPref()
    var externalCustomerId by stringPref("FooBar")
    var products by gsonNullablePref<List<String>>()
    var environment by nullableStringPref()
    var apiKey by nullableStringPref()
}