package com.diws.wordzip.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class FaqItem(
    val question: String,
    val answer: String
)

private val faqList = listOf(
    FaqItem(
        question = "How do scheduled Wordzips work?",
        answer = "Wordzip schedules notifications at your chosen times during the day. You receive a new vocabulary word directly on your lock screen or notification bar without needing to open the app!"
    ),
    FaqItem(
        question = "What are Meaning Languages?",
        answer = "You can select one or more languages (such as Hindi or Gujarati) in Settings to view word meanings translated into your preferred languages alongside English."
    ),
    FaqItem(
        question = "How is my Streak calculated?",
        answer = "Learning at least 1 new word or completing a daily practice session maintains your streak. If you miss a day, Streak Shields automatically protect your streak — you won't lose your progress!"
    ),
    FaqItem(
        question = "What are Streak Shields and how do I get them?",
        answer = "Streak Shields protect your streak when you miss a day. You earn them by consistently learning words and leveling up. A maximum of 2 shields can be active at any time. A shield is automatically used the next day you miss your daily goal."
    ),
    FaqItem(
        question = "How do I earn XP and Level Up?",
        answer = "You earn XP by reviewing words, marking words as learned, and completing practice quizzes. The more you engage daily, the faster you level up and unlock new badges!"
    ),
    FaqItem(
        question = "What is the Word of the Day?",
        answer = "Every day, Wordzip selects a featured word just for you. It appears prominently on the Home screen and rotates daily so you're always discovering something new. The app avoids repeating the same word within the last 3 days."
    ),
    FaqItem(
        question = "How does the Practice Quiz work?",
        answer = "The Practice Quiz tests you on words you've been learning. You'll be shown a definition and asked to pick the correct word from multiple options. Completing a quiz session earns you XP and helps reinforce your memory through active recall."
    ),
    FaqItem(
        question = "How do I mark a word as Learned?",
        answer = "On the Home screen or Word Detail screen, tap the bookmark / check button on any word card to mark it as learned. Learned words are tracked in your Vocabulary library and count toward your daily progress."
    ),
    FaqItem(
        question = "Can I change the notification times?",
        answer = "Yes! Go to Settings → Vocabulary Notifications and tap any scheduled time to edit it. You can also add new times (up to 10) or remove existing ones using the × button. Changes take effect immediately."
    ),
    FaqItem(
        question = "Is Wordzip available offline?",
        answer = "Yes! All vocabulary words, pronunciations, meanings, and definitions are stored locally on your device for fast offline access. The app automatically syncs new words from the cloud when you're back online."
    ),
    FaqItem(
        question = "How do I switch between Light and Dark mode?",
        answer = "Go to Settings → App Theme and choose Light, Dark, or System (follows your device's system setting). The change applies instantly without restarting the app."
    ),
    FaqItem(
        question = "Does Wordzip collect my personal data?",
        answer = "No personal data is collected or sold. Your progress, streaks, and settings are stored only on your device. Anonymous usage metrics may be collected by Google AdMob for ad delivery. See our Privacy Policy for full details."
    ),
    FaqItem(
        question = "How do I contact support or report a bug?",
        answer = "Tap the 'Support Us' button at the top of the Settings screen to reach the developer. You can also leave a review on the Play Store — all feedback is personally read and appreciated!"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSideSheet(
    onDismissRequest: () -> Unit,
    initialTab: Int = 0
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
                Column {
                    Text(
                        text = "Frequently Asked Questions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Everything you need to know about Wordzip",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // FAQ list — scrollable accordion
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                faqList.forEach { faq ->
                    FaqAccordionCard(faq = faq)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun FaqAccordionCard(faq: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    text = faq.answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
