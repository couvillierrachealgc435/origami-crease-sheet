package com.crease.origamisheet.presentation.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crease.origamisheet.R
import com.crease.origamisheet.core.di.AppViewModelFactory
import com.crease.origamisheet.core.theme.Crimson
import com.crease.origamisheet.core.theme.InkFaint
import com.crease.origamisheet.core.theme.PaperBackground
import com.crease.origamisheet.core.theme.SplashBottom
import com.crease.origamisheet.core.theme.SplashMid
import com.crease.origamisheet.core.theme.SplashTop
import com.crease.origamisheet.core.theme.Teal
import com.crease.origamisheet.core.ui.ScreenBackground

@Composable
fun SplashRoute(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(key = "splash", factory = AppViewModelFactory())
    val state by splashViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.finished) {
        if (state.finished) onFinished()
    }
    SplashScreen()
}

@Composable
private fun SplashScreen() {
    val overlay = Brush.verticalGradient(
        listOf(
            SplashTop.copy(alpha = 0.95f),
            SplashMid.copy(alpha = 0.97f),
            SplashBottom.copy(alpha = 0.99f)
        )
    )
    val transition = rememberInfiniteTransition(label = "splash")
    val compass by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing)),
        label = "compass"
    )
    val craneAlpha by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(3200), repeatMode = RepeatMode.Reverse),
        label = "crane"
    )
    val craneFloat by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(3600), repeatMode = RepeatMode.Reverse),
        label = "float"
    )
    val entry = remember { MutableTransitionState(false).apply { targetState = true } }

    ScreenBackground(overlay = overlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(224.dp).rotate(compass)) {
                    val radius = size.minDimension / 2f
                    repeat(3) { index ->
                        val angle = index * 60f
                        val radians = Math.toRadians(angle.toDouble())
                        val dx = (radius * Math.cos(radians)).toFloat()
                        val dy = (radius * Math.sin(radians)).toFloat()
                        drawLine(
                            color = Teal.copy(alpha = 0.28f),
                            start = center + Offset(-dx, -dy),
                            end = center + Offset(dx, dy),
                            strokeWidth = 1f
                        )
                    }
                }
                LogoReveal(entry = entry)
                Image(
                    painter = painterResource(R.drawable.sprite_1),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(96.dp)
                        .offset(x = 86.dp, y = (58 + craneFloat).dp)
                        .alpha(craneAlpha)
                )
            }
            Text(
                text = stringResource(R.string.app_title_line1),
                style = MaterialTheme.typography.displaySmall,
                fontSize = 30.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 5.sp,
                color = PaperBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 36.dp)
            )
            Text(
                text = stringResource(R.string.app_title_line2),
                style = MaterialTheme.typography.displaySmall,
                fontSize = 30.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 5.sp,
                color = PaperBackground,
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .width(56.dp)
                    .height(1.dp)
                    .background(Crimson)
            )
            Text(
                text = stringResource(R.string.splash_tagline),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                letterSpacing = 2.4.sp,
                color = InkFaint,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 20.dp)
            )
            LinearProgressIndicator(
                color = Crimson,
                trackColor = Color(0x33DAD4C8),
                modifier = Modifier
                    .padding(top = 36.dp)
                    .width(148.dp)
                    .height(2.dp)
            )
            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                letterSpacing = 3.sp,
                color = InkFaint,
                modifier = Modifier.padding(top = 14.dp)
            )
        }
    }
}

@Composable
private fun LogoReveal(entry: MutableTransitionState<Boolean>) {
    AnimatedVisibility(
        visibleState = entry,
        enter = fadeIn(tween(480)) + scaleIn(
            initialScale = 0.88f,
            animationSpec = tween(560, easing = FastOutSlowInEasing)
        )
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(132.dp)
        )
    }
}
