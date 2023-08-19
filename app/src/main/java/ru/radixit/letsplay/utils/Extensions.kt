package ru.radixit.letsplay.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

fun View.visible(){
    this.visibility = View.VISIBLE
}
fun View.gone(){
    this.visibility = View.GONE
}
fun View.inVisible(){
    this.visibility = View.INVISIBLE
}
fun <T> Flow<T>.launchWhenStartedTest(lifecycleCoroutineScope: LifecycleCoroutineScope){
    lifecycleCoroutineScope.launchWhenStarted {
        this@launchWhenStartedTest.collect()
    }
}
