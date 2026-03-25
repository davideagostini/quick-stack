package com.davideagostini.quickstack.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Shared button colors keep action hierarchy consistent across sheets and dialogs.
 *
 * The mapping follows the reference app:
 * - primary actions use filled buttons
 * - secondary actions use neutral outlined buttons
 * - destructive actions use red outlined buttons
 */
object QuickStackButtonDefaults {
    @Composable
    fun neutralOutlinedBorderColor(): Color = MaterialTheme.colorScheme.outline

    @Composable
    fun neutralOutlinedBorder(): BorderStroke = BorderStroke(
        width = 1.dp,
        color = neutralOutlinedBorderColor(),
    )

    @Composable
    fun primaryButtonColors(): ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    )

    @Composable
    fun neutralOutlinedButtonColors(): ButtonColors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.onSurface,
    )

    @Composable
    fun accentOutlinedButtonColors(): ButtonColors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.primary,
    )

    @Composable
    fun tertiaryOutlinedButtonColors(): ButtonColors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.tertiary,
    )

    @Composable
    fun destructiveOutlinedButtonColors(): ButtonColors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.error,
    )
}
