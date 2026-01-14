package com.incomingcallonly.launcher.ui.admin.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.incomingcallonly.launcher.ui.theme.IncomingCallOnlyTheme

/**
 * A modern, sexy, and elegant dialog component compliant with 2025-2026 UI trends.
 * Features deep shadows, subtle gradients, and smooth animations.
 */
@Composable
fun ModernSexyDialog(
    onDismissRequest: () -> Unit,
    title: String,
    description: String? = null,
    icon: ImageVector? = null,
    confirmText: String = "Confirm",
    dismissText: String? = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismissRequest) {
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { visible = true }

        IncomingCallOnlyTheme {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)) +
                        slideInVertically(
                            initialOffsetY = { 40 },
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                        )
            ) {
                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = 24.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = Color(0x40000000), // Deep shadow
                            ambientColor = Color(0x20000000)
                        )
                        .background(
                            color = MaterialTheme.colorScheme.surface, // Lighter than app background (#F0F4F8)
                            shape = RoundedCornerShape(24.dp)
                        )
                        // Subtle gradient overlay for texture
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.8f),
                                    if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.02f) else Color.White.copy(alpha = 0.4f)
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header Icon with depth
                        if (icon != null) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .size(64.dp)
                                    .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = MaterialTheme.colorScheme.primary.copy(alpha=0.3f))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        // Title
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (description != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Custom Content
                        if (content != null) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                content()
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (dismissText != null) {
                                ModernSexyButton(
                                    text = dismissText,
                                    onClick = { onDismiss?.invoke() ?: onDismissRequest() },
                                    isPrimary = false,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            
                            ModernSexyButton(
                                text = confirmText,
                                onClick = onConfirm,
                                isPrimary = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * A sexy button with press depth and shadow.
 */
@Composable
fun ModernSexyButton(
    text: String,
    onClick: () -> Unit,
    isPrimary: Boolean = true,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale = if (isPressed) 0.97f else 1f
    
    // Primary Button Style
    if (isPrimary) {
        Button(
            onClick = onClick,
            modifier = modifier
                .scale(scale)
                .height(50.dp)
                .shadow(
                    elevation = if (isPressed) 2.dp else 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            interactionSource = interactionSource
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    } else {
        // Secondary/Ghost Button Style
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
                .scale(scale)
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.secondary
            ),
            interactionSource = interactionSource
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

// ============================================================================
// PREVIEWS
// ============================================================================

@Preview(name = "Elegant - Simple Dialog")
@Composable
fun PreviewSimpleDialog() {
    IncomingCallOnlyTheme {
        Box(modifier = Modifier.background(Color(0xFFF0F4F8)).padding(20.dp)) {
            ModernSexyDialog(
                onDismissRequest = {},
                title = "Confirmer l'action",
                description = "Voulez-vous vraiment appliquer ces changements ? Cette action est irréversible.",
                icon = Icons.Outlined.Info,
                confirmText = "Confirmer",
                dismissText = "Annuler",
                onConfirm = {}
            )
        }
    }
}

@Preview(name = "Elegant - Form Dialog")
@Composable
fun PreviewFormDialog() {
    IncomingCallOnlyTheme {
        Box(modifier = Modifier.background(Color(0xFFF0F4F8)).padding(20.dp)) {
            ModernSexyDialog(
                onDismissRequest = {},
                title = "Modifier le contact",
                onConfirm = {},
                content = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = "Maman",
                            onValueChange = {},
                            label = { Text("Nom") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = "06 12 34 56 78",
                            onValueChange = {},
                            label = { Text("Numéro") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                    }
                }
            )
        }
    }
}

@Preview(name = "Elegant - List Dialog")
@Composable
fun PreviewListDialog() {
    IncomingCallOnlyTheme {
        Box(modifier = Modifier.background(Color(0xFFF0F4F8)).padding(20.dp)) {
            ModernSexyDialog(
                onDismissRequest = {},
                title = "Choisir une action",
                icon = Icons.Default.Check,
                confirmText = "Fermer",
                dismissText = null,
                onConfirm = {},
                content = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ListOption("Appeler", true)
                        ListOption("Envoyer un message", false)
                        ListOption("Voir les détails", false)
                    }
                }
            )
        }
    }
}

@Composable
fun ListOption(text: String, isSelected: Boolean) {
    Surface(
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
        shape = RoundedCornerShape(12.dp),
        onClick = {},
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null)
            }
        }
    }
}
