package com.davideagostini.quickstack.domain.model

/**
 * Records where a quick item originated so future UX or analytics-free heuristics
 * can distinguish tile launches from in-app capture.
 */
enum class QuickItemSource {
    APP,
    TILE,
}
