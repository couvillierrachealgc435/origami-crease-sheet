package com.crease.origamisheet.presentation.menagerie

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
import androidx.compose.ui.text.style.TextAlign
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
import com.crease.origamisheet.core.ui.drawShapeOutline
import com.crease.origamisheet.domain.model.SetAccent

@Composable
fun AnimalCard(
    animal: AnimalUi,
    expanded: Boolean,
    onTap: (Int) -> Unit,
    onOpenDrawer: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val accent: Color = when (animal.accent) {
        SetAccent.CRIMSON -> Crimson
        SetAccent.TEAL -> Teal
        SetAccent.GOLD -> Gold
        SetAccent.INK -> Ink
    }
    val collectedLabel = stringResource(R.string.sd_unlocked)
    val lockedLabel = stringResource(R.string.sd_locked)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(if (expanded) 148.dp else 116.dp)
            .background(
                if (animal.collected) PaperSurface.copy(alpha = 0.9f) else PaperSurfaceAlt,
                RoundedCornerShape(14.dp)
            )
            .border(1.dp, PaperEdge, RoundedCornerShape(14.dp))
            .clickable {
                if (animal.collected) onTap(animal.index) else onOpenDrawer(animal.index)
            }
            .semantics {
                stateDescription = if (animal.collected) collectedLabel else lockedLabel
            }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(52.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(52.dp)) {
                if (animal.collected) {
                    drawShapeFill(animal.silhouette, animal.cols, accent, corner = 1f)
                } else {
                    drawShapeOutline(
                        animal.silhouette,
                        animal.cols,
                        PaperEdge.copy(alpha = 0.9f),
                        1.2f * density
                    )
                }
            }
        }
        Text(
            text = if (animal.collected) {
                stringResource(animal.nameRes)
            } else {
                stringResource(R.string.locked_name)
            },
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = if (animal.collected) Ink else InkFaint,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        Text(
            text = stringResource(R.string.drawer_progress, animal.solved),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = InkMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
        if (expanded && animal.foldedOn.isNotEmpty()) {
            Text(
                text = animal.foldedOn,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = accent,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            )
        }
    }
}
