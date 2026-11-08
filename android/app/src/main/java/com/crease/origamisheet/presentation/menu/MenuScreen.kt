package com.crease.origamisheet.presentation.menu

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crease.origamisheet.R
import com.crease.origamisheet.core.config.GameConfig
import com.crease.origamisheet.core.di.AppViewModelFactory
import com.crease.origamisheet.core.theme.Crimson
import com.crease.origamisheet.core.theme.Ink
import com.crease.origamisheet.core.theme.InkFaint
import com.crease.origamisheet.core.theme.InkMuted
import com.crease.origamisheet.core.theme.PaperEdge
import com.crease.origamisheet.core.ui.GlassCard
import com.crease.origamisheet.core.ui.PrimaryButton
import com.crease.origamisheet.core.ui.ScreenBackground
import com.crease.origamisheet.core.ui.SecondaryButton
import com.crease.origamisheet.core.ui.SectionRule

@Composable
fun MenuRoute(
    onPlay: (Int, Int) -> Unit,
    onDaily: () -> Unit,
    onMenagerie: () -> Unit,
    onTutorial: () -> Unit,
    onSettings: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(key = "menu", factory = AppViewModelFactory())
    val state by menuViewModel.state.collectAsStateWithLifecycle()
    MenuScreen(
        state = state,
        onPlay = onPlay,
        onDaily = onDaily,
        onMenagerie = onMenagerie,
        onTutorial = onTutorial,
        onSettings = onSettings
    )
}

@Composable
private fun MenuScreen(
    state: MenuUiState,
    onPlay: (Int, Int) -> Unit,
    onDaily: () -> Unit,
    onMenagerie: () -> Unit,
    onTutorial: () -> Unit,
    onSettings: () -> Unit
) {
    val overlay = Brush.verticalGradient(
        listOf(
            Color(0xF2F2F0EB),
            Color(0xFAF6F4EF),
            Color(0xFFF2F0EB)
        )
    )
    val transition = rememberInfiniteTransition(label = "menu")
    val craneFloat by transition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(4200), repeatMode = RepeatMode.Reverse),
        label = "crane"
    )
    val dailyLabel = stringResource(R.string.sd_daily_done)

    ScreenBackground(overlay = overlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = stringResource(R.string.cd_logo),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = stringResource(R.string.app_short),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        letterSpacing = 2.6.sp,
                        color = InkMuted,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
                IconButton(onClick = onSettings, modifier = Modifier.size(48.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = stringResource(R.string.cd_settings),
                        tint = InkMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            SectionRule()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Column(modifier = Modifier.align(Alignment.CenterStart)) {
                    Text(
                        text = stringResource(R.string.menu_hero_line1),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Light,
                        color = Ink
                    )
                    Text(
                        text = stringResource(R.string.menu_hero_line2),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Light,
                        color = Ink
                    )
                    Text(
                        text = stringResource(R.string.menu_hero_line3),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Light,
                        color = Crimson
                    )
                    Text(
                        text = stringResource(R.string.menu_tagline),
                        style = MaterialTheme.typography.labelSmall,
                        color = InkMuted,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                Image(
                    painter = painterResource(R.drawable.sprite_1),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(124.dp)
                        .offset(x = 12.dp, y = craneFloat.dp)
                        .graphicsLayer { rotationZ = 2f }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (state.solvedTotal > 0) {
                Column(modifier = Modifier.padding(bottom = 14.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(PaperEdge, RoundedCornerShape(1.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(
                                    state.solvedTotal.toFloat() / GameConfig.TOTAL_PUZZLES
                                )
                                .height(2.dp)
                                .background(Crimson, RoundedCornerShape(1.dp))
                        )
                    }
                    Text(
                        text = stringResource(R.string.menu_progress, state.solvedTotal),
                        style = MaterialTheme.typography.labelSmall,
                        color = InkMuted,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            PrimaryButton(
                textRes = R.string.play,
                onClick = { onPlay(state.resumeSet, state.resumePuzzle) },
                modifier = Modifier.fillMaxWidth()
            )

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp)
                    .padding(top = 14.dp)
                    .clickable(enabled = !state.dailyDone) { onDaily() }
                    .semantics { stateDescription = if (state.dailyDone) dailyLabel else "" },
                fibreSeed = 13
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.sprite_4),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(48.dp)
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = stringResource(
                                if (state.dailyDone) R.string.daily_done else R.string.daily_title
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (state.dailyDone) InkFaint else Ink
                        )
                        Text(
                            text = stringResource(R.string.daily_sub),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = InkMuted,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    StreakRing(streak = state.streak)
                }
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(state.drawers, key = { it.index }) { drawer ->
                    DrawerCard(drawer = drawer, onOpen = { index -> onPlay(index, 0) })
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.menagerie),
                    onClick = onMenagerie,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.tutorial),
                    onClick = onTutorial,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
