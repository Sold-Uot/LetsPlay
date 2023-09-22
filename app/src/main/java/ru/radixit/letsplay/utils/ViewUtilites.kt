package ru.radixit.letsplay.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar


fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun View.showSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}
private const val DEBOUNCE_DELAY = 1000

fun View.setOnSingleClickListener(listener: View.OnClickListener?) {
    var lastClickTime: Long = 0
    setOnClickListener {
        if (System.currentTimeMillis() - lastClickTime < DEBOUNCE_DELAY) return@setOnClickListener
        lastClickTime = System.currentTimeMillis()
        listener?.onClick(this)
    }
}
