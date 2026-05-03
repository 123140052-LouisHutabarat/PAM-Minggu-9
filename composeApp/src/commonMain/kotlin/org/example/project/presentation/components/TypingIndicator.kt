package org.example.project.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Animasi "AI sedang mengetik" — 3 titik berkedip dengan efek gelombang.
 * Avatar menggunakan Icon SmartToy 2D (bukan emoji).
 */
@Composable
fun TypingIndicator(modifier: Modifier = Modifier) {
    Row(
        modifier          = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier         = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Filled.SmartToy,
                contentDescription = "AI mengetik",
                tint               = MaterialTheme.colorScheme.onPrimary,
                modifier           = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.size(8.dp))

        Surface(
            shape = RoundedCornerShape(
                topStart    = 16.dp,
                topEnd      = 16.dp,
                bottomStart = 4.dp,
                bottomEnd   = 16.dp
            ),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier              = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                repeat(3) { index -> AnimatedDot(delayMillis = index * 200) }
            }
        }
    }
}

@Composable
private fun AnimatedDot(delayMillis: Int) {
    val transition = rememberInfiniteTransition(label = "dot_$delayMillis")
    val alpha by transition.animateFloat(
        initialValue  = 0.3f,
        targetValue   = 1.0f,
        animationSpec = infiniteRepeatable(
            animation          = tween(600),
            repeatMode         = RepeatMode.Reverse,
            initialStartOffset = StartOffset(delayMillis)
        ),
        label = "alpha_$delayMillis"
    )
    Box(
        modifier = Modifier
            .size(8.dp)
            .alpha(alpha)
            .background(MaterialTheme.colorScheme.primary, CircleShape)
    )
}