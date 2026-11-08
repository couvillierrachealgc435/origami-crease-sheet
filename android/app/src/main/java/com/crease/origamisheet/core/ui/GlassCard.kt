package com.crease.origamisheet.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.crease.origamisheet.core.theme.PaperEdge
import com.crease.origamisheet.core.theme.PaperSurface

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    radius: Dp = 16.dp,
    fibreSeed: Int = 7,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(radius),
        color = PaperSurface.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, PaperEdge),
        shadowElevation = 2.dp
    ) {
        Box(modifier = Modifier.paperFibre(fibreSeed), content = content)
    }
}
