package com.crease.origamisheet.presentation.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.crease.origamisheet.R
import com.crease.origamisheet.core.theme.Crimson
import com.crease.origamisheet.core.theme.Ink
import com.crease.origamisheet.core.theme.InkMuted
import com.crease.origamisheet.core.theme.PaperSurface

@Composable
fun ConfirmResetDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PaperSurface,
        titleContentColor = Ink,
        textContentColor = InkMuted,
        title = {
            Text(
                text = stringResource(R.string.confirm_reset_title),
                style = MaterialTheme.typography.headlineMedium
            )
        },
        text = {
            Text(
                text = stringResource(R.string.confirm_reset_body),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.confirm),
                    style = MaterialTheme.typography.labelSmall,
                    color = Crimson
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink
                )
            }
        }
    )
}
