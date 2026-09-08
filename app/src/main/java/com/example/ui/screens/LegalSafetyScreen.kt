package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandGradient
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
import com.example.ui.viewmodel.LegalTab

@Composable
fun LegalSafetyScreen(
    currentTab: LegalTab,
    onSelectTab: (LegalTab) -> Unit,
    onSubmitAbuseReport: (category: String, details: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        LegalTab.TERMS to "Terms of Service",
        LegalTab.PRIVACY to "Privacy Policy",
        LegalTab.COMMUNITY_GUIDELINES to "Community Guidelines",
        LegalTab.SAFETY_RULES to "Safety Rules",
        LegalTab.CONSENT_AGREEMENT to "Consent Agreement",
        LegalTab.AGE_VERIFICATION to "Age Verification (18+)",
        LegalTab.MODERATION to "Moderation Policy",
        LegalTab.DMCA to "DMCA Notice",
        LegalTab.CONTACT to "Contact Support",
        LegalTab.REPORT_ABUSE to "Report Abuse"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Header
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "LEGAL, COMPLIANCE & SAFETY",
                color = ElectricBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Safety & Legal Center",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Horizontal Category Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEach { (tab, label) ->
                val isSelected = currentTab == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) NeonMagenta else DarkSurfaceVariant)
                        .border(1.dp, if (isSelected) NeonCyan else DarkSurfaceHighlight, RoundedCornerShape(20.dp))
                        .clickable { onSelectTab(tab) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Document Content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            when (currentTab) {
                LegalTab.TERMS -> LegalDocumentView(
                    title = "Terms of Service",
                    badge = "Binding Legal Contract",
                    body = """
1. ACCEPTANCE OF TERMS
By accessing or using Blurred Encounters (the "Service"), you acknowledge that you are at least 18 years of age and agree to be bound by these Terms of Service.

2. NATURE OF THE SERVICE
Blurred Encounters is an anonymous, alias-driven social and dating platform providing ephemeral 69-minute encounters with mandatory face blurring technology.

3. ZERO TOLERANCE FOR MINORS
Blurred Encounters is strictly for consenting adults aged 18 and older. Any account suspected of being operated by or depicting a person under 18 will be permanently terminated immediately and reported to relevant legal authorities.

4. MANDATORY ALIAS POLICY
To maintain privacy, members must interact solely through pseudonyms. Sharing real names, home addresses, financial credentials, or external contact coordinates in public lounges is prohibited.

5. SESSIONS & COUNTDOWN PROTOCOLS
Encounters operate on timed 69-minute sessions. At the conclusion of a session, facial shields re-engage automatically to protect member boundaries unless an extension is mutually consented to.

6. TERMINATION & CANCELLATION
We reserve the right to suspend or ban any member who violates mutual consent, takes unauthorized screenshots, or breaches safety rules.
                    """.trimIndent()
                )

                LegalTab.PRIVACY -> LegalDocumentView(
                    title = "Privacy Policy",
                    badge = "Zero Data Selling",
                    body = """
1. PRIVACY BY DESIGN
Blurred Encounters is built from the ground up around identity concealment and ephemeral communication.

2. WHAT WE DO NOT COLLECT
- We do not sell your personal data or advertising identifiers to third parties.
- We do not display your real legal name or facial imagery to other users without your express, revocable double-opt-in consent.

3. DATA WE STORE
- Selected pseudonym / alias.
- Verification hash confirming 18+ age status.
- Encrypted local session data and preference tags.
- Security event logs for anti-abuse and moderation compliance.

4. CAMERA & MICROPHONE PERMISSIONS
Camera and microphone streams are processed client-side with real-time blur shaders. Video streams are peer-to-peer and are never archived or recorded on our servers.

5. SECURITY MEASURES
All transmissions are safeguarded using industry-standard TLS 256-bit encryption. The instant Panic button wipes active local session data immediately.
                    """.trimIndent()
                )

                LegalTab.COMMUNITY_GUIDELINES -> LegalDocumentView(
                    title = "Community Guidelines",
                    badge = "Respect & Boundaries",
                    body = """
1. MUTUAL RESPECT IS PARAMOUNT
Every encounter on Blurred Encounters is founded upon voluntary, adult interaction. Coercion, harassment, hate speech, or pressure to unmask will result in instant account revocation.

2. SCREENSHOT & RECORDING PROHIBITION
Capturing, recording, photographing, or publishing screenshots of blurred encounters, audio snippets, or private messages without mutual notarized consent is strictly prohibited and constitutes a civil and criminal violation.

3. COMMERCIAL SOLICITATION
Blurred Encounters is a social and dating community. Spam, commercial advertising, financial scamming, bot operations, or escort service solicitation are barred.

4. AUTHENTIC EXPERIENCES
Respect the vibe tags and mood parameters established by each lounge room and individual member.
                    """.trimIndent()
                )

                LegalTab.SAFETY_RULES -> LegalDocumentView(
                    title = "Safety Rules",
                    badge = "Zero Harm Protocol",
                    body = """
1. THE 69-MINUTE RULE
Sessions automatically cap at 69 minutes with warnings at 10 minutes and 1 minute. This time limit prevents emotional fatigue and establishes clear boundaries.

2. PANIC BUTTON ACTIVATION
In any 1-on-1 encounter, tapping the PANIC button instantly terminates the call, disconnects camera/mic feeds, and returns you safely to discovery.

3. BLOCK & MUTE IMMEDIATE CONTROL
You possess unconditional power to block or mute any member. Once blocked, they can never view your alias, enter your private chats, or match with you.

4. MEETING IN PERSON CAUTION
Blurred Encounters is designed as an online-first anonymous space. Should you ever choose to meet outside the platform, exercise extreme caution, verify identity, meet in well-lit public venues, and inform a trusted friend.
                    """.trimIndent()
                )

                LegalTab.CONSENT_AGREEMENT -> LegalDocumentView(
                    title = "Consent & Mandatory Privacy Agreement",
                    badge = "Permanent Privacy Blur",
                    body = """
1. ENTHUSIASTIC & CONTINUOUS CONSENT
Consent must be freely given, reversible, informed, enthusiastic, and specific across all anonymous dialogue, audio calls, and encounters.

2. MANDATORY PRIVACY BLUR GUARANTEE
To ensure absolute safety, prevent non-consensual imagery distribution, and protect all members from real-world exposure, facial unmasking and face-reveal features are permanently disabled platform-wide. Faces remain strictly blurred at all times under a cryptographic privacy shield (minimum 60% Frosted Blur).

3. NO PRESSURE OR COERCION
Any attempt to coerce, pressure, or demand that a member unmask, circumvent the blur shield, or disclose personal identifiers constitutes immediate grounds for permanent account suspension.

4. REVOCABILITY & BOUNDARIES
Consent may be withdrawn at any second during an encounter. Tapping the Panic button instantly terminates the call, wipes transient cache, and returns you safely to discovery.
                    """.trimIndent()
                )

                LegalTab.AGE_VERIFICATION -> LegalDocumentView(
                    title = "Age Verification Policy (18+)",
                    badge = "Strict Age Gate",
                    body = """
1. 18+ REQUIREMENT
Blurred Encounters is strictly an adult-only platform. Persons under eighteen (18) years of age are forbidden from creating an account or accessing the service.

2. VERIFICATION PROTOCOL
Users must confirm their birthdate and consent to digital age validation during onboarding.

3. IMMEDIATE PENALTIES
Any profile found or reasonably suspected to represent an underage individual will be permanently banned within 1 hour, and reports forwarded to the National Center for Missing & Exploited Children (NCMEC) where applicable.
                    """.trimIndent()
                )

                LegalTab.MODERATION -> LegalDocumentView(
                    title = "Moderation Policy",
                    badge = "Active Vigilance",
                    body = """
1. HYBRID MODERATION SYSTEM
Our safety ecosystem blends automated text filtering with human reviewer escalation for member reports.

2. RESOLUTION TIMELINE
All member safety reports are triaged within our Admin Dashboard within 24 hours.

3. SANCTION TIERS
- Tier 1: Warning and temporary lounge mute.
- Tier 2: 7-day encounter suspension.
- Tier 3: Permanent hardware and alias blacklist.
                    """.trimIndent()
                )

                LegalTab.DMCA -> LegalDocumentView(
                    title = "DMCA & Copyright Policy",
                    badge = "Intellectual Property",
                    body = """
If you believe that your copyrighted work has been copied in a way that constitutes copyright infringement and is accessible on Blurred Encounters, please notify our designated DMCA agent:

Blurred Encounters Legal Operations
Email: dmca@blurredencounters.com
Address: 100 Privacy Boulevard, Suite 500

Please include:
- A physical or electronic signature of the authorized copyright owner.
- Identification of the copyrighted work claimed to have been infringed.
- Identification of the material claimed to be infringing and its location.
- Your contact information (address, phone number, email).
                    """.trimIndent()
                )

                LegalTab.CONTACT -> LegalDocumentView(
                    title = "Contact Support & Inquiries",
                    badge = "24/7 Response",
                    body = """
We are committed to providing a secure, discreet, and responsive experience for our adult community.

CUSTOMER & TECHNICAL SUPPORT:
support@blurredencounters.com

SAFETY & PRIVACY CONCERNS:
safety@blurredencounters.com

PRESS & PARTNERSHIPS:
press@blurredencounters.com

WEBSITE:
https://blurredencounters.com
https://blurredencounters.github.io/Blurred-Encounters-/
                    """.trimIndent()
                )

                LegalTab.REPORT_ABUSE -> ReportAbuseForm(
                    onSubmit = onSubmitAbuseReport
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun LegalDocumentView(
    title: String,
    badge: String,
    body: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(NeonCyan.copy(alpha = 0.2f))
                    .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(text = badge, color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = body,
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun ReportAbuseForm(
    onSubmit: (category: String, details: String) -> Unit
) {
    val categories = listOf(
        "Underage suspicion (Strict 18+ enforcement)",
        "Non-consensual face reveal or screenshot",
        "Harassment, threats, or boundary violation",
        "Commercial solicitation / Spam",
        "Impersonation or illicit content"
    )

    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var aliasReported by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var submittedToast by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, DangerRed.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = DangerRed, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Report Abuse & Safety Violations",
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Reports are sent directly to the Admin Moderation Queue for urgent review.",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = aliasReported,
            onValueChange = { aliasReported = it },
            label = { Text("Reported Alias (or leave blank if general)", color = TextSecondary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = NeonMagenta,
                unfocusedBorderColor = DarkSurfaceHighlight
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Select Category:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        categories.forEach { cat ->
            val isSelected = selectedCategory == cat
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) DarkSurfaceHighlight else Color.Transparent)
                    .clickable { selectedCategory = cat }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(if (isSelected) DangerRed else DarkSurface)
                        .border(1.dp, DangerRed, RoundedCornerShape(7.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = cat, color = if (isSelected) TextPrimary else TextSecondary, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = details,
            onValueChange = { details = it },
            label = { Text("Describe the Incident & Context", color = TextSecondary) },
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = DangerRed,
                unfocusedBorderColor = DarkSurfaceHighlight
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val fullCat = if (aliasReported.isNotBlank()) "[$aliasReported] $selectedCategory" else selectedCategory
                onSubmit(fullCat, details)
                submittedToast = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().height(46.dp)
        ) {
            Text(
                text = if (submittedToast) "REPORT FILED WITH MODERATORS ✓" else "SUBMIT SAFETY REPORT",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
