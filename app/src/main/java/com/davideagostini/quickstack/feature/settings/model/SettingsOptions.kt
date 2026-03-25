package com.davideagostini.quickstack.feature.settings.model

data class LanguageOption(val tag: String?, val label: String)
data class ChoiceOption<T>(val value: T, val label: String)

fun languageOptions(systemLabel: String): List<LanguageOption> = listOf(
    LanguageOption(null, systemLabel),
    LanguageOption("it", "Italiano"),
    LanguageOption("fr", "Français"),
    LanguageOption("de", "Deutsch"),
    LanguageOption("es", "Español"),
    LanguageOption("nl", "Nederlands"),
    LanguageOption("ru", "Русский"),
    LanguageOption("zh-CN", "简体中文"),
    LanguageOption("ar", "العربية"),
)

fun reminderOffsetOptions(): List<ChoiceOption<Int>> = listOf(
    ChoiceOption(30, "30 min"),
    ChoiceOption(60, "1 hour"),
    ChoiceOption(120, "2 hours"),
)

fun tonightHourOptions(): List<ChoiceOption<Int>> = listOf(
    ChoiceOption(19, "19:00"),
    ChoiceOption(20, "20:00"),
    ChoiceOption(21, "21:00"),
    ChoiceOption(22, "22:00"),
)

fun timerOffsetOptions(): List<ChoiceOption<Int>> = listOf(
    ChoiceOption(5, "5 min"),
    ChoiceOption(10, "10 min"),
    ChoiceOption(15, "15 min"),
    ChoiceOption(30, "30 min"),
)

fun languageLabelForTag(tag: String): String = when (tag) {
    "it" -> "Italiano"
    "fr" -> "Français"
    "de" -> "Deutsch"
    "es" -> "Español"
    "nl" -> "Nederlands"
    "ru" -> "Русский"
    "zh-CN" -> "简体中文"
    "ar" -> "العربية"
    else -> tag
}
