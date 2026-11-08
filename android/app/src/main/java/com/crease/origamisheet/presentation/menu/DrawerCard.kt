package com.crease.origamisheet.presentation.menu

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crease.origamisheet.R
import com.crease.origamisheet.core.theme.Crimson
import com.crease.origamisheet.core.theme.Gold
import com.crease.origamisheet.core.theme.Ink
import com.crease.origamisheet.core.theme.InkFaint
import com.crease.origamisheet.core.theme.InkMuted
import com.crease.origamisheet.core.theme.PaperEdge
import com.crease.origamisheet.core.theme.PaperSurface
import com.crease.origamisheet.core.theme.PaperSurfaceAlt
import com.crease.origamisheet.core.theme.Teal
import com.crease.origamisheet.core.ui.drawShapeFill
import com.crease.origamisheet.domain.model.SetAccent

@Composable
fun accentColor(accent: SetAccent): Color = when (accent) {
    SetAccent.CRIMSON -> Crimson
    SetAccent.TEAL -> Teal
    SetAccent.GOLD -> Gold
    SetAccent.INK -> Ink
}

@Composable
fun DrawerCard(
    drawer: DrawerUi,
    onOpen: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = accentColor(drawer.accent)
    val lockedLabel = stringResource(R.string.sd_locked)
    val openLabel = stringResource(R.string.sd_unlocked)
    Column(
        modifier = modifier
            .width(108.dp)
            .height(124.dp)
            .background(
                if (drawer.unlocked) PaperSurface.copy(alpha = 0.9f) else PaperSurfaceAlt,
                RoundedCornerShape(14.dp)
            )
            .border(
                1.dp,
                if (drawer.unlocked) PaperEdge else PaperEdge.copy(alpha = 0.6f),
                RoundedCornerShape(14.dp)
            )
            .clickable(enabled = drawer.unlocked) { onOpen(drawer.index) }
            .semantics { stateDescription = if (drawer.unlocked) openLabel else lockedLabel }
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(34.dp)
                .height(2.dp)
                .background(
                    if (drawer.unlocked) accent else PaperEdge,
                    RoundedCornerShape(1.dp)
                )
        )
        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .size(46.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(46.dp)) {
                drawShapeFill(
                    cells = drawer.silhouette,
                    cols = drawer.cols,
                    color = if (drawer.collected) {
                        accent
                    } else if (drawer.unlocked) {
                        accent.copy(alpha = 0.3f)
                    } else {
                        PaperEdge.copy(alpha = 0.45f)
                    },
                    corner = 1f
                )
            }
        }
        Text(
            text = stringResource(drawer.nameRes),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = if (drawer.unlocked) Ink else InkFaint,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )
        Text(
            text = stringResource(R.string.drawer_progress, drawer.solved),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = if (drawer.unlocked) InkMuted else InkFaint,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}
