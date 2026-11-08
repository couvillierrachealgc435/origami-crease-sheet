package com.crease.origamisheet.presentation.menagerie

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crease.origamisheet.R
import com.crease.origamisheet.core.di.AppViewModelFactory
import com.crease.origamisheet.core.theme.Ink
import com.crease.origamisheet.core.theme.InkMuted
import com.crease.origamisheet.core.ui.ScreenBackground
import com.crease.origamisheet.core.ui.SectionRule

@Composable
fun MenagerieRoute(
    onHome: () -> Unit,
    onOpenDrawer: (Int) -> Unit
) {
    val menagerieViewModel: MenagerieViewModel =
        viewModel(key = "menagerie", factory = AppViewModelFactory())
    val state by menagerieViewModel.state.collectAsStateWithLifecycle()
    MenagerieScreen(
        state = state,
        onHome = onHome,
        onTap = menagerieViewModel::onAnimalTap,
        onOpenDrawer = onOpenDrawer
    )
}

@Composable
private fun MenagerieScreen(
    state: MenagerieUiState,
    onHome: () -> Unit,
    onTap: (Int) -> Unit,
    onOpenDrawer: (Int) -> Unit
) {
    val overlay = Brush.verticalGradient(
        listOf(Color(0xF5F2F0EB), Color(0xFAE9E5DC))
    )
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
                    text = stringResource(R.string.menagerie_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 20.sp,
                    color = Ink
                )
                Text(
                    text = stringResource(R.string.menagerie_count, state.collectedCount),
                    style = MaterialTheme.typography.labelSmall,
                    color = InkMuted
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.sprite_3),
                    contentDescription = stringResource(R.string.cd_display_piece),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(112.dp)
                )
                Text(
                    text = stringResource(R.string.menagerie_caption),
                    style = MaterialTheme.typography.labelSmall,
                    color = InkMuted,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            SectionRule()

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.animals, key = { it.index }) { animal ->
                    AnimalCard(
                        animal = animal,
                        expanded = state.expanded == animal.index,
                        onTap = onTap,
                        onOpenDrawer = onOpenDrawer
                    )
                }
            }

            Box(modifier = Modifier.height(12.dp))
        }
    }
}
