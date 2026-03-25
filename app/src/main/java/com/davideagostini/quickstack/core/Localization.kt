package com.davideagostini.quickstack.core

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

fun applyAppLanguage(languageTag: String?) {
    val locales = if (languageTag.isNullOrBlank()) {
        LocaleListCompat.getEmptyLocaleList()
    } else {
        LocaleListCompat.forLanguageTags(languageTag)
    }
    AppCompatDelegate.setApplicationLocales(locales)
}
