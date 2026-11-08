package com.crease.origamisheet.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.crease.origamisheet.core.theme.InkMuted
import com.crease.origamisheet.core.theme.PaperEdge

@Composable
fun SectionRule(modifier: Modifier = Modifier, labelRes: Int? = null) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        if (labelRes != null) {
            Text(
                text = stringResource(labelRes),
                style = MaterialTheme.typography.labelSmall,
                color = InkMuted
            )
        }
        Box(
            modifier = Modifier
                .padding(start = if (labelRes != null) 12.dp else 0.dp)
                .weight(1f)
                .height(1.dp)
                .background(PaperEdge)
        )
    }
}
