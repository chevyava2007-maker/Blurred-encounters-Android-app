package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.UserProfile
import com.example.ui.components.BlurredAvatar
import com.example.ui.theme.BrandGradient
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    currentUserId: String,
    partner: UserProfile,
    messages: List<ChatMessage>,
    onSendMessage: (text: String, isBlurredMedia: Boolean) -> Unit,
    onStartEncounter: () -> Unit,
    onBack: () -> Unit,
    onReportUser: () -> Unit,
    onBlockUser: () -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .border(1.dp, DarkSurfaceHighlight)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }

            BlurredAvatar(
                seed = partner.avatarSeed,
                blurIntensity = partner.blurIntensity,
                size = 44.dp,
                showRing = false
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "@${partner.alias}",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${partner.blurIntensity}% Privacy Shield • Permanent Blur",
                        color = NeonCyan,
                        fontSize = 10.sp
                    )
                }
            }

            // Launch 69-Min Encounter shortcut
            IconButton(
                onClick = onStartEncounter,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BrandGradient)
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Start 69-Minute Encounter",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = TextSecondary)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(DarkSurfaceVariant)
                ) {
                    DropdownMenuItem(
                        text = { Text("Clear Chat History", color = TextPrimary) },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = TextMuted) },
                        onClick = {
                            showMenu = false
                            onClearHistory()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Report for Violation", color = DangerRed) },
                        leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null, tint = DangerRed) },
                        onClick = {
                            showMenu = false
                            onReportUser()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Block & Remove", color = DangerRed) },
                        leadingIcon = { Icon(Icons.Default.Block, contentDescription = null, tint = DangerRed) },
                        onClick = {
                            showMenu = false
                            onBlockUser()
                        }
                    )
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Safety Header Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceVariant.copy(alpha = 0.6f))
                        .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ANONYMOUS & ENCRYPTED DIALOGUE",
                                color = NeonCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Never share real names, social handles, or phone numbers. Discretion is strictly enforced.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            BlurredAvatar(
                                seed = partner.avatarSeed,
                                blurIntensity = partner.blurIntensity,
                                size = 64.dp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Start an anonymous spark with @${partner.alias}",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Mood tags: ${partner.moodTags}",
                                color = NeonMagenta,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            items(messages, key = { it.id }) { msg ->
                val isFromMe = msg.senderId == currentUserId
                ChatMessageBubble(
                    message = msg,
                    isFromMe = isFromMe
                )
            }
        }

        // Bottom Input Tray
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .border(1.dp, DarkSurfaceHighlight)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Blurred media polaroid button
            IconButton(
                onClick = {
                    onSendMessage("Sent an anonymous blurred polaroid [Tap to inspect]", true)
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Send Blurred Photo",
                    tint = NeonMagenta,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Anonymous message...", color = TextMuted, fontSize = 14.sp) },
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = DarkSurfaceHighlight
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText.trim(), false)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(BrandGradient)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Message",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    isFromMe: Boolean
) {
    val alignment = if (isFromMe) Alignment.End else Alignment.Start
    val bubbleColor = if (isFromMe) NeonMagenta.copy(alpha = 0.25f) else DarkSurfaceVariant
    val borderColor = if (isFromMe) NeonMagenta.copy(alpha = 0.5f) else DarkSurfaceHighlight
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    var isPeekingMedia by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isFromMe) 16.dp else 4.dp,
                        bottomEnd = if (isFromMe) 4.dp else 16.dp
                    )
                )
                .background(bubbleColor)
                .border(
                    1.dp,
                    borderColor,
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isFromMe) 16.dp else 4.dp,
                        bottomEnd = if (isFromMe) 4.dp else 16.dp
                    )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            if (message.isBlurredMedia) {
                // Blurred Polaroid media bubble (Permanently blurred)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(180.dp, 130.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E1430))
                            .border(1.dp, NeonCyan, RoundedCornerShape(10.dp))
                            .clickable { isPeekingMedia = !isPeekingMedia }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(if (isPeekingMedia) 10.dp else 25.dp)
                                .background(BrandGradient)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x44000000)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isPeekingMedia) "Frosted Peek" else "Tap to Peek",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Encrypted Disappearing Polaroid",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            } else {
                Text(
                    text = message.text,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 19.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = timeFormat.format(Date(message.timestamp)),
            color = TextMuted,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
