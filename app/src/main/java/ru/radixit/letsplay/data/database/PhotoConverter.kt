package ru.radixit.letsplay.data.database

import androidx.room.TypeConverter
import ru.radixit.letsplay.data.model.PhotoEntity
/*
class PhotoConverter {
    @TypeConverter
    fun teacherToString(photo : PhotoEntity) = "$photo" //Other options are json string, serialized blob

    @TypeConverter
    fun stringToTeacher(value: String): PhotoEntity {
        val id = value.substringAfter(':').toInt()
        val url = value.substringBefore(':')

        return PhotoEntity( id ,url )
    }
}*/
