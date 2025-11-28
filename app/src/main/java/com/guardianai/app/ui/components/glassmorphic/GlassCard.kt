package com.guardianai.app.ui.components.glassmorphic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.guardianai.app.ui.theme.GlassmorphismColors

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    glassType: GlassType = GlassType.DEFAULT,
    cornerRadius: Dp = 16.dp,
    padding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val (backgroundColor, borderColor, shadowColor) = when (glassType) {
        GlassType.DEFAULT -> Triple(
            GlassmorphismColors.Surface,
            GlassmorphismColors.Border,
            GlassmorphismColors.NeonGlow
        )
        GlassType.NEON -> Triple(
            GlassmorphismColors.NeonGlow,
            GlassmorphismColors.NeonBorder,
            NeonAqua.copy(alpha = 0.3f)
        )
        GlassType.EMERGENCY -> Triple(
            GlassmorphismColors.EmergencyGlow,
            GlassmorphismColors.EmergencyBorder,
            EmergencyRed.copy(alpha = 0.3f)
        )
        GlassType.SUCCESS -> Triple(
            GlassmorphismColors.SuccessGlow,
            GlassmorphismColors.SuccessBorder,
            StatusGreen.copy(alpha = 0.3f)
        )
        GlassType.WARNING -> Triple(
            GlassmorphismColors.WarningGlow,
            GlassmorphismColors.WarningBorder,
            StatusYellow.copy(alpha = 0.3f)
        )
        GlassType.ERROR -> Triple(
            GlassmorphismColors.ErrorGlow,
            GlassmorphismColors.ErrorBorder,
            StatusRed.copy(alpha = 0.3f)
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.8f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor,
                        borderColor.copy(alpha = 0.6f),
                        borderColor
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            ),
        shape = RoundedCornerShape(cornerRadius),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    glassType: GlassType = GlassType.DEFAULT,
    cornerRadius: Dp = 12.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val (backgroundColor, borderColor) = when (glassType) {
        GlassType.DEFAULT -> Pair(
            GlassmorphismColors.ContentSurface,
            GlassmorphismColors.ContentBorder
        )
        GlassType.NEON -> Pair(
            GlassmorphismColors.NeonGlow,
            GlassmorphismColors.NeonBorder
        )
        GlassType.EMERGENCY -> Pair(
            GlassmorphismColors.EmergencyGlow,
            GlassmorphismColors.EmergencyBorder
        )
        GlassType.SUCCESS -> Pair(
            GlassmorphismColors.SuccessGlow,
            GlassmorphismColors.SuccessBorder
        )
        GlassType.WARNING -> Pair(
            GlassmorphismColors.WarningGlow,
            GlassmorphismColors.WarningBorder
        )
        GlassType.ERROR -> Pair(
            GlassmorphismColors.ErrorGlow,
            GlassmorphismColors.ErrorBorder
        )
    }

    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .clip(RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun GlassButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    glassType: GlassType = GlassType.NEON,
    cornerRadius: Dp = 12.dp,
    height: Dp = 48.dp,
    content: @Composable RowScope.() -> Unit
) {
    val (backgroundColor, borderColor, textColor) = when (glassType) {
        GlassType.DEFAULT -> Triple(
            GlassmorphismColors.InteractiveSurface,
            GlassmorphismColors.InteractiveBorder,
            White
        )
        GlassType.NEON -> Triple(
            NeonAqua.copy(alpha = 0.2f),
            NeonAqua,
            White
        )
        GlassType.EMERGENCY -> Triple(
            EmergencyRed.copy(alpha = 0.2f),
            EmergencyRed,
            White
        )
        GlassType.SUCCESS -> Triple(
            StatusGreen.copy(alpha = 0.2f),
            StatusGreen,
            White
        )
        GlassType.WARNING -> Triple(
            StatusYellow.copy(alpha = 0.2f),
            StatusYellow,
            White
        )
        GlassType.ERROR -> Triple(
            StatusRed.copy(alpha = 0.2f),
            StatusRed,
            White
        )
    }

    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier
            .height(height)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = borderColor.copy(alpha = 0.5f),
                spotColor = borderColor.copy(alpha = 0.5f)
            ),
        enabled = enabled,
        shape = RoundedCornerShape(cornerRadius),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.5f),
            disabledContentColor = textColor.copy(alpha = 0.5f)
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

enum class GlassType {
    DEFAULT,
    NEON,
    EMERGENCY,
    SUCCESS,
    WARNING,
    ERROR
}