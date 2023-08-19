package ru.radixit.letsplay.presentation.global

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.HttpException
import ru.radixit.letsplay.R
import java.io.IOException
import javax.inject.Inject

class ErrorHandler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun proceed(error: Throwable, messageListener: (String) -> Unit) =
        messageListener(error.userMessage(context))
}

fun Throwable.userMessage(context: Context): String {
    return context.getString(
        when (this) {
            is HttpException -> when (this.code()) {
                400, 403, 404, 405, 500 -> R.string.error_server
                else -> R.string.error_unknown
            }
            is IOException -> R.string.error_network
            else -> R.string.error_unknown
        }
    )
}