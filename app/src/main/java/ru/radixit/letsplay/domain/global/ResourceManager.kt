package ru.radixit.letsplay.domain.global

interface ResourceManager {
    fun getString(resId: Int): String
}