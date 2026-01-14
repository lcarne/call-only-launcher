package com.incomingcallonly.launcher.ui.admin.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incomingcallonly.launcher.ui.theme.Spacing

/**
 * Section header with optional accent line for visual hierarchy.
 * Uses uppercase text with bold weight to create clear separation between sections.
 * 
 * @param text The header text to display
 * @param modifier Modifier for customization
 * @param showAccentLine Whether to show the decorative line under the header
 */
@Composable
fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier,
    showAccentLine: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = text.uppercase(),  // All caps for visual distinction
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,  // Extra bold for hierarchy
                letterSpacing = 1.2.sp  // Wider tracking for all-caps readability
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                start = Spacing.listItemHorizontal,
                top = Spacing.sectionHeaderTop,
                bottom = Spacing.sectionHeaderBottom
            )
        )
        
        // Optional decorative accent line
        if (showAccentLine) {
            HorizontalDivider(
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                modifier = Modifier
                    .width(32.dp)
                    .padding(start = Spacing.listItemHorizontal)
            )
        }
    }
}

/**
 * Standard divider between major sections with softer appearance.
 */
@Composable
fun SectionDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(vertical = Spacing.dividerSpacing),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    )
}

/**
 * Inset divider for use within sections, between related items.
 */
@Composable
fun InsetDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier
            .padding(vertical = Spacing.xs)
            .padding(horizontal = Spacing.listItemHorizontal),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    )
}
