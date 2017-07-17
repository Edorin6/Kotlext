package com.cloudstaff.app.stocktrace.utils

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.support.annotation.LayoutRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.DisplayMetrics
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.cloudstaff.app.stocktrace.R
import com.edorin.custom.utils.FontCache
import kotlinx.android.synthetic.main.dlg_alert.view.*
import kotlinx.android.synthetic.main.dlg_yes_no.view.*
import java.net.URLEncoder
import java.util.regex.Pattern

/**
 * Forged by edrynm on 6/30/17.
 */

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun Context.getCompatColor(colorId: Int): Int {
    return ContextCompat.getColor(this, colorId)
}

fun Context.getCompatDrawable(resId: Int): Drawable {
    return ContextCompat.getDrawable(this, resId)
}

fun Context.dpToPx(dp: Int): Int {
    return Math.round(dp * (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}

fun Context.pxToDp(px: Int): Int {
    return Math.round(px / (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}

fun RecyclerView.clear(list: ArrayList<*>) {
    list.clear()
    adapter.notifyItemRangeRemoved(0, adapter.itemCount)
}

fun String.isValidEmail(): Boolean {
    return Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(this).find()
}

fun String.urlEncode(): String {
    return URLEncoder.encode(this, "utf-8")
}

fun EditText.text(): String {
    return text.toString().trim()
}

fun EditText.isBlank(): Boolean {
    return text.toString() == ""
}

fun TextView.setTypeface(font: String) {
    this.typeface = FontCache.getFont(font, this.context)
}

fun Context.getStringResource(stringResource: Int): String {
    return getString(stringResource)
}

fun Context.getColor(colorResource: Int): Int {
    return ContextCompat.getColor(this, colorResource)
}

fun Context.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun Context.showAlertDialog(rootView: ViewGroup? = null, title: String, message: String,
                             button: String? = "OK",
                             clickHandler: (() -> Unit)? = null, dismissHandler: (() -> Unit)? = null) {

    // init dialog and view
    val dialog = AlertDialog.Builder(this).create()
    val dialogView = rootView!!.inflate(R.layout.dlg_alert, false)

    // setup view
    dialogView.apply {
        txvTitle.text = title
        txvMessage.text = message
        btnDone.text = button
        btnDone.setOnClickListener {
            if (clickHandler != null) {
                clickHandler()
            } else {
                dialog.dismiss()
            }
        }
    }

    // setup AlertDialog
    dialog.apply {
        setView(dialogView)
        setOnDismissListener {
            if (dismissHandler != null) { dismissHandler() }
        }
        show()
    }
}

fun Context.showYesNoDialog(rootView: ViewGroup? = null, title: String, message: String, cancelable: Boolean? = true,
                            buttonPositive: String? = "YES", buttonNegative: String? = "NO",
                            yesHandler: (() -> Unit)? = null, noHandler: (() -> Unit)? = null,
                            dismissHandler: (() -> Unit)? = null) {

    // init dialog and view
    val dialog = AlertDialog.Builder(this).create()
    val dialogView = rootView!!.inflate(R.layout.dlg_yes_no, false)

    // setup view
    dialogView.apply {
        txvTitleYN.text = title
        txvMessageYN.text = message
        btnYes.text = buttonPositive
        btnNo.text = buttonNegative
        btnYes.setOnClickListener {
            if (yesHandler != null) {
                yesHandler()
            }
            dialog.dismiss()
        }
        btnNo.setOnClickListener {
            if (noHandler != null) {
                noHandler()
            }
            dialog.dismiss()
        }
    }

    // setup AlertDialog
    dialog.apply {
        setCancelable(cancelable!!)
        setView(dialogView)
        setOnDismissListener {
            if (dismissHandler != null) { dismissHandler() }
        }
        show()
    }

}

fun Context.showSnackbar(rootView: ViewGroup? = null, contentText: String, actionText: String,
                          length: Int? = Snackbar.LENGTH_LONG, actionHandler: () -> Unit) {
    val snackbar = Snackbar.make(rootView!!, contentText, length!!)
            .setAction(actionText, { actionHandler() })
    Utility.overrideFonts(this, snackbar.view, "qanelas_semibold.otf")
    with(snackbar) {
        setActionTextColor(ContextCompat.getColor(this@showSnackbar, R.color.snack_action_text))
        val textView = view.findViewById<TextView>(R.id.snackbar_text)
        view.setBackgroundColor(getCompatColor(R.color.snack_bg))
        Utility.overrideFonts(this@showSnackbar, textView, "qanelas.otf")
        textView.setTextColor(ContextCompat.getColor(this@showSnackbar, R.color.snack_text))
        show()
    }

}

fun Context.showSoftKeyboard(mEtSearch: EditText) {
    mEtSearch.requestFocus()
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
}

fun Context.hideSoftKeyboard(mEtSearch: EditText) {
    mEtSearch.clearFocus()
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(mEtSearch.windowToken, 0)
}

fun Context.hidesSoftInputOnTouch(rootLayout: ViewGroup) {
    rootLayout.apply {
        isClickable = true
        isFocusable = true
        isFocusableInTouchMode = true
        setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.rootView.windowToken, 0)
            }
        }
    }
}

fun ViewGroup.softInputStateChanged(context: Context, shown: () -> Unit, hidden: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener {
        val heightDiff = rootView.height - height
        if (heightDiff > context.dpToPx(200)) {
            shown()
            Log.e("TAG", "shown")
        } else {
            hidden()
            Log.e("TAG", "hidden")
        }
    }
}