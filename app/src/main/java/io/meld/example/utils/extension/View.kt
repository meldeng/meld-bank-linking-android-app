package io.meld.example.utils.extension


import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager


/**
 * Sets the view's visibility to GONE
 */
fun View.hide() {
    visibility = View.GONE
}

/**
 * Sets the view's visibility to VISIBLE
 */
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * Sets the view's visibility to INVISIBLE
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * DEPRECATED
 * Toggle's view's visibility. If View is visible, then sets to gone. Else sets Visible
 */
@Deprecated(
    "Use toggleVisibility() instead",
    ReplaceWith("this.toggleVisibility()", "android.view.View")
)
fun View.toggle() = toggleVisibility()

/**
 * Toggle's view's visibility. If View is visible, then sets to gone. Else sets Visible
 * Previously knows as toggle()
 */
fun View.toggleVisibility() {
    visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
}

/**
 * Hides all the views passed in the arguments
 */
fun hideViews(vararg views: View) = views.forEach { it.visibility = View.GONE }

/**
 * Shows all the views passed in the arguments
 */
fun showViews(vararg views: View) = views.forEach { it.visibility = View.VISIBLE }

fun View?.hideKeyboard() {
    this?.let {
        val inputMethodManager =
            this.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
    }

}

