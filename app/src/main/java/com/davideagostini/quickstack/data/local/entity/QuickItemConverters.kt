package com.davideagostini.quickstack.data.local.entity

import androidx.room.TypeConverter
import com.davideagostini.quickstack.domain.model.QuickItemSource
import com.davideagostini.quickstack.domain.model.QuickItemType

/**
 * Stores enum values as stable strings so the schema stays readable and resilient
 * to the addition of new columns later.
 */
class QuickItemConverters {
    @TypeConverter
    fun toType(value: String): QuickItemType = QuickItemType.valueOf(value)

    @TypeConverter
    fun fromType(value: QuickItemType): String = value.name

    @TypeConverter
    fun toSource(value: String): QuickItemSource = QuickItemSource.valueOf(value)

    @TypeConverter
    fun fromSource(value: QuickItemSource): String = value.name
}
