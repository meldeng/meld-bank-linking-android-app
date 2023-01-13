package io.meld.example.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import io.meld.sdk.logs.logD
import io.meld.sdk.model.ErrorResponse
import io.meld.sdk.model.MeldError

/**
 * Use this method to
 * show alert to user
 */
fun Context.showConnectError(error: MeldError?) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle("Error")
//        builder.setMessage("Error getting connect token $error")
    var message = ""
    error?.let {
        when (it) {
            is MeldError.InternetError -> {
                message = "No Internet Connection"
            }
            is MeldError.TimeOutError -> {
                message = "Request timeout, please try again."
            }
            is MeldError.HTTPError<*> -> {
                val apiError = error as MeldError.HTTPError<ErrorResponse>
                message = if (apiError.statusCode == 204) {
                    "Successfully deleted"
                } else {
                    "${(error as MeldError.HTTPError<ErrorResponse>).errorResponse?.message}"
                }
            }
            else -> {
                message = "Something went wrong!"
            }
        }

    }

    builder.setMessage(message)

    builder.setPositiveButton(
        "OK"
    ) { p0, p1 -> }
    builder.create().show()
}

fun Context.showNoInternetError() {
    val builder = AlertDialog.Builder(this)
    builder.setMessage("No Internet Connection")
    builder.setPositiveButton(
        "OK"
    ) { p0, p1 -> }
    builder.create().show()
}

fun Context.showMessage(message: String) {
    val builder = AlertDialog.Builder(this)
    builder.setMessage(message)
    builder.setPositiveButton(
        "OK"
    ) { p0, p1 -> }
    builder.create().show()
}