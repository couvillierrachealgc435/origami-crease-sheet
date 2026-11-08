package com.crease.origamisheet.presentation.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.crease.origamisheet.R
import com.crease.origamisheet.core.theme.Crimson
import com.crease.origamisheet.core.theme.Ink
import com.crease.origamisheet.core.theme.InkMuted
import com.crease.origamisheet.core.theme.PaperSurface

@Composable
fun PauseDialog(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onResume,
        containerColor = PaperSurface,
        titleContentColor = Ink,
        textContentColor = InkMuted,
        title = {
            Text(
                text = stringResource(R.string.pause_title),
                style = MaterialTheme.typography.headlineMedium
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.pause_body),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onResume) {
                Text(
                    text = stringResource(R.string.resume),
                    style = MaterialTheme.typography.labelSmall,
                    color = Crimson
                )
            }
        },
        dismissButton = {
            Column {
                TextButton(onClick = onRestart) {
                    Text(
                        text = stringResource(R.string.restart),
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink
                    )
                }
                TextButton(onClick = onMenu) {
                    Text(
                        text = stringResource(R.string.menu),
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink
                    )
                }
            }
        }
    )
}
