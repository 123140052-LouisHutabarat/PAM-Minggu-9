package org.example.project.presentation.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.data.model.ChatMessage
import org.example.project.presentation.components.TypingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val state     by viewModel.chatState.collectAsState()
    val listState  = rememberLazyListState()
    val snackbar   = remember { SnackbarHostState() }

    LaunchedEffect(state.messages.size, state.isLoading) {
        val target = if (state.isLoading) state.messages.size
        else (state.messages.size - 1).coerceAtLeast(0)
        if (state.messages.isNotEmpty() || state.isLoading)
            listState.animateScrollToItem(target)
    }

    LaunchedEffect(state.error) {
        state.error?.let { snackbar.showSnackbar(it); viewModel.clearError() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "AI Assistant",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Gemini 2.5 Flash",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearChat() }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus chat",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            LazyColumn(
                state               = listState,
                modifier            = Modifier.weight(1f),
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.messages.isEmpty() && !state.isLoading) {
                    item { WelcomeCard() }
                }
                items(state.messages, key = { it.id }) { msg ->
                    ChatBubble(msg)
                }
                if (state.isLoading) {
                    item { TypingIndicator() }
                }
            }

            ChatInputBar(
                inputText     = state.inputText,
                onInputChange = viewModel::updateInput,
                onSend        = { viewModel.sendMessage(state.inputText) },
                isLoading     = state.isLoading
            )
        }

        SnackbarHost(
            hostState = snackbar,
            modifier  = Modifier.align(Alignment.BottomCenter)
        ) { data ->
            Snackbar(
                data,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor   = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape    = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier            = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier         = Modifier.size(72.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Filled.Android,
                    contentDescription = "AI Assistant",
                    tint               = MaterialTheme.colorScheme.onPrimary,
                    modifier           = Modifier.size(40.dp)
                )
            }
            Spacer(Modifier.size(12.dp))
            Text(
                "Halo! Saya AI Assistant ITERA",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.size(4.dp))
            Text(
                "Tanyakan apa saja tentang KMP, Compose, atau topik lainnya.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, modifier: Modifier = Modifier) {
    val isUser = message.isUser
    Row(
        modifier              = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment     = Alignment.Bottom
    ) {
        if (!isUser) {
            Box(
                modifier         = Modifier.size(32.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Filled.Android,
                    contentDescription = "AI",
                    tint               = MaterialTheme.colorScheme.onPrimary,
                    modifier           = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.size(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart    = 16.dp, topEnd      = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd   = if (isUser) 4.dp  else 16.dp
            ),
            color    = if (isUser) MaterialTheme.colorScheme.primary
            else        MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text     = message.text,
                style    = MaterialTheme.typography.bodyMedium,
                color    = if (isUser) MaterialTheme.colorScheme.onPrimary
                else        MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }

        if (isUser) {
            Spacer(Modifier.size(8.dp))
            Box(
                modifier         = Modifier.size(32.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Filled.Person,
                    contentDescription = "User",
                    tint               = MaterialTheme.colorScheme.onSecondary,
                    modifier           = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ← tidak ada parameter bottomPadding sama sekali
@Composable
private fun ChatInputBar(
    inputText     : String,
    onInputChange : (String) -> Unit,
    onSend        : () -> Unit,
    isLoading     : Boolean
) {
    Surface(
        color    = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical   = 8.dp
            ),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value         = inputText,
                onValueChange = onInputChange,
                modifier      = Modifier.weight(1f),
                placeholder   = { Text("Ketik pesan...") },
                shape         = RoundedCornerShape(24.dp),
                maxLines      = 4,
                enabled       = !isLoading
            )
            AnimatedVisibility(
                visible = inputText.isNotBlank(),
                enter   = fadeIn(),
                exit    = fadeOut()
            ) {
                IconButton(
                    onClick  = onSend,
                    enabled  = inputText.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Kirim",
                        tint               = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}