package com.crease.origamisheet.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crease.origamisheet.R
import com.crease.origamisheet.core.di.AppViewModelFactory
import com.crease.origamisheet.core.theme.Crimson
import com.crease.origamisheet.core.theme.Ink
import com.crease.origamisheet.core.theme.InkMuted
import com.crease.origamisheet.core.theme.PaperEdge
import com.crease.origamisheet.core.theme.PaperSurface
import com.crease.origamisheet.core.theme.Teal
import com.crease.origamisheet.core.ui.ScreenBackground
import com.crease.origamisheet.core.ui.SectionRule
import com.crease.origamisheet.domain.model.OutlineContrast
import com.crease.origamisheet.presentation.dialog.ConfirmResetDialog

@Composable
fun SettingsRoute(onHome: () -> Unit) {
    val settingsViewModel: SettingsViewModel =
        viewModel(key = "settings", factory = AppViewModelFactory())
    val state by settingsViewModel.state.collectAsStateWithLifecycle()
    SettingsScreen(
        state = state,
        onHome = onHome,
        onHaptics = settingsViewModel::onHapticsChange,
        onSounds = settingsViewModel::onSoundsChange,
        onMotion = settingsViewModel::onMotionChange,
        onContrast = settingsViewModel::onContrastChange,
        onResetRequested = settingsViewModel::onResetRequested,
        onResetDismissed = settingsViewModel::onResetDismissed,
        onResetConfirmed = settingsViewModel::onResetConfirmed
    )
}

@Composable
private fun SettingsScreen(
    state: SettingsUiState,
    onHome: () -> Unit,
    onHaptics: (Boolean) -> Unit,
    onSounds: (Boolean) -> Unit,
    onMotion: (Boolean) -> Unit,
    onContrast: (OutlineContrast) -> Unit,
    onResetRequested: () -> Unit,
    onResetDismissed: () -> Unit,
    onResetConfirmed: () -> Unit
) {
    val overlay = Brush.verticalGradient(
        listOf(Color(0xF7FBFAF7), Color(0xFAE9E5DC))
    )
    val onLabel = stringResource(R.string.sd_on)
    val offLabel = stringResource(R.string.sd_off)

    ScreenBackground(overlay = overlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onHome, modifier = Modifier.size(48.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.icon_home),
                        contentDescription = stringResource(R.string.cd_home),
                        tint = Color.Unspecified,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 20.sp,
                    color = Ink
                )
                Box(modifier = Modifier.size(48.dp))
            }

            SectionRule()

            SettingsSwitchRow(
                titleRes = R.string.settings_haptics,
                subtitleRes = R.string.settings_haptics_sub,
                checked = state.settings.haptics,
                stateLabel = if (state.settings.haptics) onLabel else offLabel,
                onChange = onHaptics
            )
            SectionRule()
            SettingsSwitchRow(
                titleRes = R.string.settings_sounds,
                subtitleRes = R.string.settings_sounds_sub,
                checked = state.settings.paperSounds,
                stateLabel = if (state.settings.paperSounds) onLabel else offLabel,
                onChange = onSounds
            )
            SectionRule()
            SettingsSwitchRow(
                titleRes = R.string.settings_motion,
                subtitleRes = R.string.settings_motion_sub,
                checked = state.settings.reducedMotion,
                stateLabel = if (state.settings.reducedMotion) onLabel else offLabel,
                onChange = onMotion
            )
            SectionRule()

            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    text = stringResource(R.string.settings_contrast),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Ink
                )
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ContrastChip(
                        labelRes = R.string.contrast_low,
                        selected = state.settings.outlineContrast == OutlineContrast.LOW,
                        onClick = { onContrast(OutlineContrast.LOW) }
                    )
                    ContrastChip(
                        labelRes = R.string.contrast_normal,
                        selected = state.settings.outlineContrast == OutlineContrast.NORMAL,
                        onClick = { onContrast(OutlineContrast.NORMAL) }
                    )
                    ContrastChip(
                        labelRes = R.string.contrast_high,
                        selected = state.settings.outlineContrast == OutlineContrast.HIGH,
                        onClick = { onContrast(OutlineContrast.HIGH) }
                    )
                }
            }

            SectionRule()

            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.settings_footer, state.solvedTotal),
                    style = MaterialTheme.typography.labelSmall,
                    color = InkMuted,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }

            TextButton(
                onClick = onResetRequested,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.reset_progress),
                    style = MaterialTheme.typography.labelSmall,
                    color = Crimson
                )
            }
        }
    }

    if (state.confirmVisible) {
        ConfirmResetDialog(onConfirm = onResetConfirmed, onDismiss = onResetDismissed)
    }
}

@Composable
private fun SettingsSwitchRow(
    titleRes: Int,
    subtitleRes: Int,
    checked: Boolean,
    stateLabel: String,
    onChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .semantics { stateDescription = stateLabel },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.bodyLarge,
                color = Ink
            )
            Text(
                text = stringResource(subtitleRes),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = InkMuted,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PaperSurface,
                checkedTrackColor = Teal,
                uncheckedThumbColor = PaperSurface,
                uncheckedTrackColor = PaperEdge
            )
        )
    }
}

@Composable
private fun ContrastChip(
    labelRes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(48.dp)
            .background(
                if (selected) Crimson else PaperSurface.copy(alpha = 0.8f),
                RoundedCornerShape(12.dp)
            )
            .border(1.dp, PaperEdge, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) PaperSurface else Ink
        )
    }
}
