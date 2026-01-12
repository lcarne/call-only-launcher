package com.incomingcallonly.launcher.data.local

import androidx.room.TypeConverter
import com.incomingcallonly.launcher.data.model.CallLogType

class Converters {
    @TypeConverter
    fun fromCallLogType(value: CallLogType): String {
        return value.name
    }

    @TypeConverter
    fun toCallLogType(value: String): CallLogType {
        return CallLogType.valueOf(value)
    }
}
