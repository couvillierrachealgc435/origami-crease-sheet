package com.crease.origamisheet.presentation.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.crease.origamisheet.core.ui.GlassCard
import com.crease.origamisheet.core.ui.PrimaryButton
import com.crease.origamisheet.core.ui.ScreenBackground
import com.crease.origamisheet.core.ui.SecondaryButton
import com.crease.origamisheet.domain.model.Crease
import com.crease.origamisheet.domain.model.Side
import com.crease.origamisheet.presentation.dialog.PauseDialog

@Composable
fun GameRoute(
    args: GameArgs,
    attempt: Int,
    onMenu: () -> Unit,
    onFinished: (Int, Int) -> Unit
) {
    val key = "game-" + args.setIndex + "-" + args.puzzleIndex + "-" + args.daily + "-" + attempt
    val gameViewModel: GameViewModel = viewModel(key = key, factory = AppViewModelFactory(args))
    val state by gameViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(gameViewModel) {
        gameViewModel.events.collect { event ->
            when (event) {
                is GameEvent.Finished -> onFinished(event.folds, event.par)
            }
        }
    }
    DisposableEffect(gameViewModel) {
        onDispose { gameViewModel.pause() }
    }

    GameScreen(
        state = state,
        onPause = { gameViewModel.onPauseToggle(true) },
        onResume = { gameViewModel.onPauseToggle(false) },
        onRestart = gameViewModel::onRestart,
        onCreaseTap = gameViewModel::onCreaseTap,
        onCreaseFold = gameViewModel::onCreaseDrag,
        onFoldSelected = gameViewModel::onFoldSelected,
        onUndo = gameViewModel::onUndo,
        onHint = gameViewModel::onHint,
        onReset = gameViewModel::onReset,
        onMenu = onMenu
    )
}

@Composable
private fun GameScreen(
    state: GameUiState,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onCreaseTap: (Crease) -> Unit,
    onCreaseFold: (Crease, Side) -> Unit,
    onFoldSelected: () -> Unit,
    onUndo: () -> Unit,
    onHint: () -> Unit,
    onReset: () -> Unit,
    onMenu: () -> Unit
) {
    val overlay = Brush.verticalGradient(
        listOf(
            Color(0xF5F6F4EF),
            Color(0xF7EDEAE2),
            Color(0xFAE4E0D6)
        )
    )
    val settle by animateFloatAsState(
        targetValue = if (state.phase == GamePhase.Solved) 1f else 0f,
        animationSpec = keyframes {
            durationMillis = GameConfig.SETTLE_DURATION_MS.toInt()
            0f at 0
            1f at GameConfig.SETTLE_DURATION_MS.toInt()
        },
        label = "settle"
    )
    val counterPop by animateFloatAsState(
        targetValue = state.foldsUsed.toFloat(),
        label = "counter"
    )
    val overPar = state.foldsUsed > state.par
    val blockedLabel = stringResource(R.string.sd_blocked)
    val selectedLabel = stringResource(R.string.sd_crease_selected)

    ScreenBackground(overlay = overlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onPause, modifier = Modifier.size(48.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.icon_pause),
                        contentDescription = stringResource(R.string.cd_pause),
                        tint = Color.Unspecified,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(
                            if (state.daily) R.string.daily_title else state.setNameRes
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = InkMuted
                    )
                    Text(
                        text = stringResource(R.string.game_puzzle, state.puzzleNumber),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 16.sp,
                        color = Ink
                    )
                }
                Text(
                    text = stringResource(R.string.game_folds, state.foldsUsed, state.par),
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (overPar) Crimson else Ink,
                    modifier = Modifier
                        .width(72.dp)
                        .graphicsLayer {
                            val pop = 1f + (counterPop - state.foldsUsed.toFloat()) * 0.12f
                            scaleX = pop
                            scaleY = pop
                        }
                        .semantics { liveRegion = LiveRegionMode.Polite },
                    textAlign = TextAlign.End
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 20.dp)
                    .background(PaperEdge)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TargetOutline(
                    cells = state.target.cells,
                    cols = state.cols,
                    contrast = state.settings.outlineContrast,
                    pulsing = state.matchPulse
                )
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        text = stringResource(R.string.game_target),
                        style = MaterialTheme.typography.labelSmall,
                        color = InkMuted
                    )
                    Text(
                        text = stringResource(R.string.game_target_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    if (state.target.patterned) {
                        Row(modifier = Modifier.padding(top = 8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(Crimson, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(Teal, CircleShape)
                            )
                        }
                    }
                }
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
                        .coerceAtMost(GameConfig.BOARD_MAX_DP.dp)
                        .coerceAtLeast(140.dp)
                    val frame = (GameConfig.BOARD_PADDING_DP + GameConfig.BOARD_BORDER_DP).dp
                    val cell = (((boardMax - frame * 2).value / state.cols).toInt()).dp
                    val board = cell * state.cols + frame * 2
                    FoldBoard(
                        sheet = state.sheet,
                        cols = state.cols,
                        creases = state.creases,
                        selected = state.selected,
                        hint = state.hint,
                        blocked = state.blocked,
                        animation = state.animation,
                        interactive = state.interactive,
                        crossFade = state.settings.reducedMotion,
                        boardSize = board,
                        cellSize = cell,
                        frameSize = frame,
                        onCreaseTap = onCreaseTap,
                        onCreaseFold = onCreaseFold,
                        modifier = Modifier
                            .graphicsLayer {
                                val factor = 0.96f + settle * 0.04f
                                scaleX = if (state.phase == GamePhase.Solved) factor else 1f
                                scaleY = if (state.phase == GamePhase.Solved) factor else 1f
                            }
                            .semantics {
                                stateDescription = when {
                                    state.blocked.isNotEmpty() -> blockedLabel
                                    state.selected != null -> selectedLabel
                                    else -> ""
                                }
                            }
                    )
                }
            }

            AnimatedVisibility(
                visible = state.selected != null || state.hintOffPath,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.hintOffPath) {
                        Text(
                            text = stringResource(R.string.hint_off_path),
                            style = MaterialTheme.typography.labelSmall,
                            color = Crimson
                        )
                    } else {
                        PrimaryButton(
                            textRes = R.string.fold,
                            onClick = onFoldSelected,
                            modifier = Modifier.width(180.dp)
                        )
                    }
                }
            }

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                radius = 18.dp,
                fibreSeed = 21
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SecondaryButton(
                        text = stringResource(R.string.reset),
                        onClick = onReset,
                        height = 56.dp,
                        enabled = state.canUndo,
                        modifier = Modifier.weight(1f)
                    )
                    if (state.hintsLeft > 0) {
                        SecondaryButton(
                            text = stringResource(R.string.hint, state.hintsLeft),
                            onClick = onHint,
                            height = 56.dp,
                            enabled = state.interactive,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    SecondaryButton(
                        text = stringResource(R.string.undo),
                        onClick = onUndo,
                        height = 56.dp,
                        enabled = state.canUndo,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    if (state.paused) {
        PauseDialog(
            onResume = onResume,
            onRestart = onRestart,
            onMenu = onMenu
        )
    }
}
