package ru.radixit.letsplay.presentation.global

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.radixit.letsplay.domain.global.ResourceManager
import javax.inject.Inject

class AndroidResourceManager @Inject constructor(
    @ApplicationContext private val context: Context
) :
    ResourceManager {
    override fun getString(resId: Int) = context.getString(resId)
}