package com.crease.origamisheet.presentation.tutorial

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crease.origamisheet.R
import com.crease.origamisheet.core.config.GameConfig
import com.crease.origamisheet.core.di.AppViewModelFactory
import com.crease.origamisheet.core.theme.Crimson
import com.crease.origamisheet.core.theme.Ink
import com.crease.origamisheet.core.theme.InkMuted
import com.crease.origamisheet.core.theme.PaperEdge
import com.crease.origamisheet.core.theme.Teal
import com.crease.origamisheet.core.ui.PrimaryButton
import com.crease.origamisheet.core.ui.ScreenBackground
import com.crease.origamisheet.core.ui.SecondaryButton
import com.crease.origamisheet.data.sample.SampleData
import com.crease.origamisheet.presentation.game.FoldBoard
import com.crease.origamisheet.presentation.game.TargetOutline
import com.crease.origamisheet.domain.model.OutlineContrast

@Composable
fun TutorialRoute(onHome: () -> Unit) {
    val tutorialViewModel: TutorialViewModel =
        viewModel(key = "tutorial", factory = AppViewModelFactory())
    val state by tutorialViewModel.state.collectAsStateWithLifecycle()
    TutorialScreen(
        state = state,
        onHome = onHome,
        onCreaseTap = tutorialViewModel::onCreaseTap,
        onCreaseFold = tutorialViewModel::onCreaseDrag,
        onFoldSelected = tutorialViewModel::onFoldSelected,
        onRetry = tutorialViewModel::onRetryStep,
        onPrevious = tutorialViewModel::onPreviousStep,
        onNext = tutorialViewModel::onNextStep
    )
}

@Composable
private fun TutorialScreen(
    state: TutorialUiState,
    onHome: () -> Unit,
    onCreaseTap: (com.crease.origamisheet.domain.model.Crease) -> Unit,
    onCreaseFold: (com.crease.origamisheet.domain.model.Crease, com.crease.origamisheet.domain.model.Side) -> Unit,
    onFoldSelected: () -> Unit,
    onRetry: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val overlay = Brush.verticalGradient(
        listOf(Color(0xF5F6F4EF), Color(0xFAE9E5DC))
    )
    val stepLabel = stringResource(R.string.sd_step, state.step + 1)

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
                    text = stringResource(R.string.tutorial_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 20.sp,
                    color = Ink
                )
                Box(modifier = Modifier.size(48.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.sprite_5),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(88.dp)
                )
                Column(modifier = Modifier.padding(start = 14.dp)) {
                    Text(
                        text = stringResource(SampleData.tutorialTitles[state.step]),
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink
                    )
                    Text(
                        text = stringResource(SampleData.tutorialBodies[state.step]),
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkMuted,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .semantics { stateDescription = stepLabel },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (index in 0 until GameConfig.TUTORIAL_STEPS) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (index == state.step) Crimson else PaperEdge,
                                CircleShape
                            )
                    )
                }
                Box(modifier = Modifier.weight(1f))
                TargetOutline(
                    cells = state.target.cells,
                    cols = state.cols,
                    contrast = OutlineContrast.NORMAL,
                    pulsing = state.solved,
                    size = 48.dp
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val limit = minOf(maxWidth, maxHeight)
                    val boardMax = (limit - 32.dp)
                        .coerceAtMost(300.dp)
                        .coerceAtLeast(120.dp)
                    val frame = (GameConfig.BOARD_PADDING_DP + GameConfig.BOARD_BORDER_DP).dp
                    val cell = (((boardMax - frame * 2).value / state.cols).toInt()).dp
                    FoldBoard(
                        sheet = state.sheet,
                        cols = state.cols,
                        creases = state.creases,
                        selected = state.selected,
                        hint = null,
                        blocked = state.blocked,
                        animation = null,
                        interactive = !state.solved,
                        crossFade = false,
                        boardSize = cell * state.cols + frame * 2,
                        cellSize = cell,
                        frameSize = frame,
                        onCreaseTap = onCreaseTap,
                        onCreaseFold = onCreaseFold
                    )
                }
            }

            AnimatedVisibility(
                visible = state.selected != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PrimaryButton(
                        textRes = R.string.fold,
                        onClick = onFoldSelected,
                        modifier = Modifier.width(180.dp)
                    )
                }
            }

            AnimatedVisibility(visible = state.solved, enter = fadeIn(), exit = fadeOut()) {
                Text(
                    text = stringResource(R.string.tutorial_done_step),
                    style = MaterialTheme.typography.labelSmall,
                    color = Teal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.back),
                    onClick = onPrevious,
                    enabled = state.step > 0,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.retry),
                    onClick = onRetry,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(
                        if (state.isLastStep) R.string.done else R.string.next
                    ),
                    onClick = { if (state.isLastStep) onHome() else onNext() },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
