package com.incomingcallonly.launcher.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object Spacing {
    // Base spacing scale (8dp grid)
    val none: Dp = 0.dp
    val xxs: Dp = 2.dp
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val xl: Dp = 20.dp
    val xxl: Dp = 24.dp
    val xxxl: Dp = 32.dp
    val xxxxl: Dp = 40.dp
    
    // Semantic spacing for specific UI patterns
    val sectionHeaderTop: Dp = xxl        // 24dp - breathing room before sections
    val sectionHeaderBottom: Dp = md      // 12dp - tighter connection to content
    val listItemVertical: Dp = lg         // 16dp - comfortable list spacing
    val listItemHorizontal: Dp = lg       // 16dp - edge margins
    val nestedIndent: Dp = xl             // 20dp - clear visual nesting
    val dialogContentSpacing: Dp = lg     // 16dp - form field spacing
    val dividerSpacing: Dp = md           // 12dp - space around dividers
    
    // Touch targets (minimum 48dp per Material/accessibility guidelines)
    val minTouchTarget: Dp = 48.dp
    val iconButtonSize: Dp = 48.dp
    
    // Icon sizes (consistency)
    val iconSmall: Dp = 16.dp             // Trailing icons, decorative
    val iconMedium: Dp = 20.dp            // Secondary icons
    val iconLarge: Dp = 24.dp             // Primary/leading icons
    val iconExtraLarge: Dp = 32.dp        // Dialog/modal icons
}
