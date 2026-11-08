package com.crease.origamisheet.presentation.gameover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.crease.origamisheet.core.theme.Gold
import com.crease.origamisheet.core.theme.Ink
import com.crease.origamisheet.core.theme.InkMuted
import com.crease.origamisheet.core.theme.PaperEdge
import com.crease.origamisheet.core.theme.Teal
import com.crease.origamisheet.core.ui.PrimaryButton
import com.crease.origamisheet.core.ui.ScreenBackground
import com.crease.origamisheet.core.ui.SecondaryButton
import com.crease.origamisheet.core.ui.StatCard
import com.crease.origamisheet.core.ui.drawShapeFill
import com.crease.origamisheet.core.ui.drawShapeOutline

@Composable
fun GameOverRoute(
    args: ResultArgs,
    attempt: Int,
    onNext: (Int, Int) -> Unit,
    onReplayPuzzle: () -> Unit,
    onMenu: () -> Unit
) {
    val key = "result-" + args.setIndex + "-" + args.puzzleIndex + "-" + args.daily + "-" + attempt
    val resultViewModel: GameOverViewModel =
        viewModel(key = key, factory = AppViewModelFactory(args))
    val state by resultViewModel.state.collectAsStateWithLifecycle()
    DisposableEffect(resultViewModel) {
        onDispose { resultViewModel.pause() }
    }
    GameOverScreen(
        state = state,
        onReplayAnimation = resultViewModel::startReplay,
        onNext = {
            if (args.puzzleIndex + 1 < GameConfig.PUZZLES_PER_SET) {
                onNext(args.setIndex, args.puzzleIndex + 1)
            } else {
                onNext(args.setIndex + 1, 0)
            }
        },
        onReplayPuzzle = onReplayPuzzle,
        onMenu = onMenu
    )
}

@Composable
private fun GameOverScreen(
    state: GameOverUiState,
    onReplayAnimation: () -> Unit,
    onNext: () -> Unit,
    onReplayPuzzle: () -> Unit,
    onMenu: () -> Unit
) {
    val overlay = Brush.radialGradient(
        colors = listOf(Color(0xF0FBFAF7), Color(0xFAEDEAE2)),
        center = Offset.Unspecified,
        radius = 900f
    )
    val starsLabel = stringResource(R.string.sd_stars, state.stars)

    ScreenBackground(overlay = overlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(156.dp),
                contentAlignment = Alignment.Center
            ) {
                val frame = state.frames.getOrNull(state.frameIndex)
                Canvas(modifier = Modifier.size(156.dp)) {
                    if (frame != null) {
                        drawShapeFill(frame.occupied, state.cols, PaperEdge, corner = 2f)
                        drawShapeOutline(frame.occupied, state.cols, Ink.copy(alpha = 0.45f), 1.5f * density)
                    }
                }
            }

            TextButton(onClick = onReplayAnimation) {
                Text(
                    text = stringResource(R.string.replay),
                    style = MaterialTheme.typography.labelSmall,
                    color = Teal
                )
            }

            Text(
                text = stringResource(
                    if (state.setCompleted) R.string.result_set_done else R.string.result_solved
                ),
                style = MaterialTheme.typography.displaySmall,
                fontSize = 36.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 4.sp,
                color = if (state.setCompleted) Crimson else Ink,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .width(64.dp)
                    .height(1.dp)
                    .background(Crimson)
            )

            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .semantics { stateDescription = starsLabel },
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (index in 1..3) {
                    Text(
                        text = stringResource(R.string.star_glyph),
                        fontSize = 22.sp,
                        color = if (index <= state.stars) Gold else PaperEdge
                    )
                }
            }

            AnimatedVisibility(
                visible = state.setCompleted,
                enter = fadeIn(tween(320)) + scaleIn(initialScale = 0.8f, animationSpec = tween(320))
            ) {
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(modifier = Modifier.size(96.dp)) {
                        drawShapeFill(state.animalCells, state.cols, Crimson, corner = 2f)
                    }
                    Image(
                        painter = painterResource(R.drawable.sprite_2),
                        contentDescription = stringResource(R.string.cd_menagerie_mark),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(64.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    StatCard(
                        labelRes = R.string.stat_folds,
                        value = state.folds.toString(),
                        accent = Teal
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    StatCard(
                        labelRes = R.string.stat_par,
                        value = state.par.toString(),
                        accent = InkMuted
                    )
                }
                if (state.best > 0) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            labelRes = R.string.stat_best,
                            value = state.best.toString(),
                            accent = Gold
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f))

            if (state.hasNext) {
                PrimaryButton(
                    textRes = R.string.next_puzzle,
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            SecondaryButton(
                text = stringResource(R.string.play_again),
                onClick = onReplayPuzzle,
                height = 52.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
            TextButton(
                onClick = onMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(top = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.menu),
                    style = MaterialTheme.typography.labelSmall,
                    color = InkMuted
                )
            }
            Box(modifier = Modifier.height(16.dp))
        }
    }
}
