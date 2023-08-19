package ru.radixit.letsplay.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.widget.doAfterTextChanged
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.PhoneNumberUnderscoreSlotsParser
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun EditText.maskForNumberPhone() {
    val slots = PhoneNumberUnderscoreSlotsParser().parseSlots("+7 (___) ___-__-__")
    val mask = MaskImpl.createTerminated(slots)
    mask.isHideHardcodedHead = true
    mask.toUnformattedString()
    val formatWatcher: FormatWatcher = MaskFormatWatcher(
        mask
    )
    this.doAfterTextChanged {
        if (it!!.length != 18) {
            error = "Количество цифр номера меньше 11"
        }
    }
    formatWatcher.installOn(this)
}

fun TextView.maskForNumberPhone() {
    val slots = PhoneNumberUnderscoreSlotsParser().parseSlots("+7 (___) ___-__-__")
    val mask = MaskImpl.createTerminated(slots)
    mask.isHideHardcodedHead = true
    mask.toUnformattedString()
    val formatWatcher: FormatWatcher = MaskFormatWatcher(
        mask
    )
    this.doAfterTextChanged {
        if (it!!.length != 18) {
            error = "Количество цифр номера меньше 11"
        }
    }
    formatWatcher.installOn(this)
}

@RequiresApi(Build.VERSION_CODES.O)
fun EditText.maskForBirthDate() {
    val slots = UnderscoreDigitSlotsParser().parseSlots("__-__-____")
    val mask = MaskImpl.createTerminated(slots)
    mask.isHideHardcodedHead = true
    val formatWatcher: FormatWatcher = MaskFormatWatcher(
        mask
    )
    formatWatcher.installOn(this)
    doAfterTextChanged {
        if (it.toString().length != 10) {
            error = "Дата не действительна"
        } else {
            try {
                val sdf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                LocalDate.parse(it.toString(), sdf)
            } catch (e: DateTimeParseException) {
                error = "Дата не действительна"
            }
        }
    }
}

@SuppressLint("Range")
fun ContentResolver.getFileName(uri: Uri): String {
    var name = ""
    val cursor = query(uri, null, null, null, null)
    cursor?.use {
        it.moveToFirst()
        name = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
    }
    return name
}

fun Context.hideKeyboardOnScroll(view: View): Boolean {
    val imm: InputMethodManager? =
        this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    return imm!!.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Int.years(): String {
    return if (this == 11 || this == 12 || this == 13 || this == 14 || this % 10 == 5 || this % 10 == 6 || this % 10 == 7 || this % 10 == 8 || this % 10 == 9) {
        "$this лет"
    } else if (this % 10 == 1) {
        "$this год"
    } else {
        "$this года"
    }
}