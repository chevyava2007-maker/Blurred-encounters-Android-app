package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SafetyReport
import com.example.data.model.UserProfile
import com.example.ui.components.BlurredAvatar
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    members: List<UserProfile>,
    reports: List<SafetyReport>,
    onResolveReport: (Long) -> Unit,
    onToggleSuspend: (UserProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val pendingReports = reports.filter { it.status == "PENDING" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "PLATFORM MODERATION & SAFETY",
                    color = ElectricBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Admin & Safety Dashboard",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Live surveillance of alias compliance, mandatory face blur integrity, and abuse reports.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        // Metrics Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminMetricCard(
                    title = "Active Aliases",
                    value = "${members.size + 1}",
                    color = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    title = "Pending Reports",
                    value = "${pendingReports.size}",
                    color = if (pendingReports.isEmpty()) SafeGreen else DangerRed,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    title = "Blur Compliance",
                    value = "100%",
                    color = SafeGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section: Safety Reports Queue
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Safety & Abuse Reports Queue",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${pendingReports.size} Pending",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        if (reports.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceVariant)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No reports currently submitted.", color = TextMuted, fontSize = 13.sp)
                }
            }
        } else {
            items(reports, key = { it.id }) { report ->
                AdminReportCard(
                    report = report,
                    onResolve = { onResolveReport(report.id) }
                )
            }
        }

        // Section: Member Management
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.People, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Member Management & Blur Verification",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        items(members, key = { it.id }) { member ->
            AdminMemberCard(
                member = member,
                onToggleSuspend = { onToggleSuspend(member) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AdminMetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = color, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun AdminReportCard(
    report: SafetyReport,
    onResolve: () -> Unit
) {
    val dateStr = SimpleDateFormat("MMM dd • HH:mm", Locale.getDefault()).format(Date(report.timestamp))
    val isPending = report.status == "PENDING"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, if (isPending) WarningOrange.copy(alpha = 0.5f) else DarkSurfaceHighlight, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isPending) WarningOrange.copy(alpha = 0.2f) else SafeGreen.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = report.status,
                        color = if (isPending) WarningOrange else SafeGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Reported: ${report.reportedAlias}",
                    color = DangerRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(text = dateStr, color = TextMuted, fontSize = 10.sp)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Category: ${report.category}",
            color = NeonCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )

        if (report.details.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "\"${report.details}\"",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (isPending) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onResolve,
                    colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Investigate & Mark Resolved", color = Color.White, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun AdminMemberCard(
    member: UserProfile,
    onToggleSuspend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, if (member.isSuspended) DangerRed else DarkSurfaceHighlight, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BlurredAvatar(
                seed = member.avatarSeed,
                blurIntensity = member.blurIntensity,
                size = 44.dp,
                showRing = false
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = member.alias,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${member.age} yo",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "${member.blurIntensity}% Blur • ${member.membershipTier}",
                    color = NeonCyan,
                    fontSize = 11.sp
                )
            }
        }

        Button(
            onClick = onToggleSuspend,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (member.isSuspended) SafeGreen else DangerRed
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Text(
                text = if (member.isSuspended) "Reactivate" else "Suspend",
                color = Color.White,
                fontSize = 11.sp
            )
        }
    }
}
