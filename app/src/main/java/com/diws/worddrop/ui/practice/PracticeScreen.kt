package com.diws.worddrop.ui.practice

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import com.diws.worddrop.ui.components.CelebrationConfetti
import com.diws.worddrop.util.InterstitialAdManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diws.worddrop.domain.model.Word
import com.diws.worddrop.ui.components.AppLoader
import com.diws.worddrop.ui.components.DifficultyChip
import com.diws.worddrop.ui.theme.DarkBackground
import com.diws.worddrop.ui.theme.DarkSurface
import com.diws.worddrop.ui.theme.DarkSurfaceContainer
import com.diws.worddrop.ui.theme.DarkSurfaceHigh
import com.diws.worddrop.ui.theme.PrimaryContainerPurple
import com.diws.worddrop.ui.theme.PrimaryPurple
import com.diws.worddrop.ui.theme.SecondaryTeal
import com.diws.worddrop.ui.theme.TertiarySuccess
import com.diws.worddrop.ui.theme.TextPrimary
import com.diws.worddrop.ui.theme.TextSecondary
import com.diws.worddrop.util.HapticFeedbackHelper

@Composable
fun PracticeScreen(
    onNavigateBack: () -> Unit,
    viewModel: PracticeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val view = LocalView.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        HapticFeedbackHelper.performClick(view, context)
                        onNavigateBack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = "Practice & Quiz",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Selector: [ Flashcards | Daily Quiz ]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceContainer)
                    .padding(4.dp)
            ) {
                TabButton(
                    text = "🃏 Flashcards",
                    selected = state.selectedTab == PracticeTab.FLASHCARDS,
                    onClick = {
                        HapticFeedbackHelper.performClick(view, context)
                        viewModel.setTab(PracticeTab.FLASHCARDS)
                    },
                    modifier = Modifier.weight(1f)
                )

                TabButton(
                    text = "🎯 Daily Quiz",
                    selected = state.selectedTab == PracticeTab.QUIZ,
                    onClick = {
                        HapticFeedbackHelper.performClick(view, context)
                        if (state.selectedTab != PracticeTab.QUIZ) {
                            InterstitialAdManager.showAd(context.findActivity()) {
                                viewModel.setTab(PracticeTab.QUIZ)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                AppLoader(subtitle = "Preparing session...")
            } else {
                when (state.selectedTab) {
                    PracticeTab.FLASHCARDS -> {
                        FlashcardsContent(
                            state = state,
                            onFlip = {
                                HapticFeedbackHelper.performClick(view, context)
                                viewModel.flipCard()
                            },
                            onAnswer = { mastered ->
                                HapticFeedbackHelper.performClick(view, context)
                                viewModel.onFlashcardAnswer(mastered)
                            },
                            onRestart = {
                                HapticFeedbackHelper.performClick(view, context)
                                viewModel.restartFlashcards()
                            },
                            onSwitchToQuiz = {
                                HapticFeedbackHelper.performClick(view, context)
                                InterstitialAdManager.showAd(context.findActivity()) {
                                    viewModel.setTab(PracticeTab.QUIZ)
                                }
                            }
                        )
                    }
                    PracticeTab.QUIZ -> {
                        QuizContent(
                            state = state,
                            onSelectOption = { index ->
                                HapticFeedbackHelper.performClick(view, context)
                                viewModel.selectQuizOption(index)
                            },
                            onNext = {
                                HapticFeedbackHelper.performClick(view, context)
                                viewModel.nextQuizQuestion()
                            },
                            onRestart = {
                                HapticFeedbackHelper.performClick(view, context)
                                InterstitialAdManager.showAd(context.findActivity()) {
                                    viewModel.restartQuiz()
                                }
                            },
                            onSwitchToFlashcards = {
                                HapticFeedbackHelper.performClick(view, context)
                                viewModel.setTab(PracticeTab.FLASHCARDS)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) PrimaryContainerPurple else Color.Transparent,
        animationSpec = tween(250)
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) Color.White else TextSecondary,
        animationSpec = tween(250)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// FLASHCARDS SECTION
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FlashcardsContent(
    state: PracticeUiState,
    onFlip: () -> Unit,
    onAnswer: (Boolean) -> Unit,
    onRestart: () -> Unit,
    onSwitchToQuiz: () -> Unit
) {
    if (state.isFlashcardsCompleted || state.flashcards.isEmpty()) {
        FlashcardsCompletedCard(
            state = state,
            onRestart = onRestart,
            onSwitchToQuiz = onSwitchToQuiz
        )
        return
    }

    val currentCard = state.flashcards.getOrNull(state.currentCardIndex) ?: return
    val progress = (state.currentCardIndex + 1).toFloat() / state.flashcards.size.toFloat()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Progress header
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Card ${state.currentCardIndex + 1} of ${state.flashcards.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = PrimaryPurple,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${state.flashcardsMasteredCount} Mastered",
                    style = MaterialTheme.typography.labelMedium,
                    color = TertiarySuccess
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = PrimaryPurple,
                trackColor = DarkSurfaceHigh
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3D Flippable Flashcard
        val rotation by animateFloatAsState(
            targetValue = if (state.isCardFlipped) 180f else 0f,
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
            label = "CardFlip"
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .clickable(onClick = onFlip),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
            border = BorderStroke(1.5.dp, PrimaryPurple.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                if (rotation <= 90f) {
                    // FRONT OF CARD
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DifficultyChip(difficulty = currentCard.difficulty)
                            currentCard.partOfSpeech?.let {
                                Text(
                                    text = it.lowercase(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondary,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = currentCard.word.uppercase(),
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )

                            currentCard.pronunciation?.let {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = PrimaryPurple
                                )
                            }
                        }

                        Text(
                            text = "Tap to reveal meaning ↻",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    // BACK OF CARD (Mirror back so text isn't reversed)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { rotationY = 180f }
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "MEANING",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryPurple,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val meaning = currentCard.simpleMeaning ?: currentCard.definition
                            Text(
                                text = meaning,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                lineHeight = 24.sp
                            )

                            currentCard.example?.let { example ->
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "EXAMPLE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryTeal,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "\"$example\"",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    fontStyle = FontStyle.Italic,
                                    lineHeight = 20.sp
                                )
                            }

                            if (currentCard.synonyms.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "SYNONYMS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentCard.synonyms.joinToString(", "),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                            }
                        }

                        Text(
                            text = "Tap to flip back ↺",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary.copy(alpha = 0.7f),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons: [ Still Learning | Mastered! ]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { onAnswer(false) },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, Color(0xFFFFB74D).copy(alpha = 0.6f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFB74D))
            ) {
                Icon(Icons.Rounded.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Need Review", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onAnswer(true) },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryTeal)
            ) {
                Icon(Icons.Rounded.Check, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Mastered!", color = DarkBackground, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FlashcardsCompletedCard(
    state: PracticeUiState,
    onRestart: () -> Unit,
    onSwitchToQuiz: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(PrimaryContainerPurple.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.EmojiEvents,
                        contentDescription = null,
                        tint = PrimaryPurple,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Session Complete!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "You practiced ${state.flashcards.size} words and marked ${state.flashcardsMasteredCount} as mastered.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // XP Reward Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryContainerPurple.copy(alpha = 0.15f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Bolt,
                        contentDescription = null,
                        tint = PrimaryPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+${state.flashcardsXpEarned} XP Earned! ⚡",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryPurple
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onSwitchToQuiz,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerPurple)
                ) {
                    Text("Take Daily Quiz 🎯", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onRestart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.4f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryPurple)
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Practice Again", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DAILY QUIZ SECTION
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QuizContent(
    state: PracticeUiState,
    onSelectOption: (Int) -> Unit,
    onNext: () -> Unit,
    onRestart: () -> Unit,
    onSwitchToFlashcards: () -> Unit
) {
    if (state.isQuizCompleted || state.quizQuestions.isEmpty()) {
        QuizCompletedCard(
            state = state,
            onRestart = onRestart,
            onSwitchToFlashcards = onSwitchToFlashcards
        )
        return
    }

    val currentQuestion = state.quizQuestions.getOrNull(state.currentQuizIndex) ?: return
    val progress = (state.currentQuizIndex + 1).toFloat() / state.quizQuestions.size.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Progress Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question ${state.currentQuizIndex + 1} of ${state.quizQuestions.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = PrimaryPurple,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Score: ${state.quizScore}",
                    style = MaterialTheme.typography.labelMedium,
                    color = SecondaryTeal,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = SecondaryTeal,
                trackColor = DarkSurfaceHigh
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Question Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer)
            ) {
                Text(
                    text = currentQuestion.questionText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Options List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                currentQuestion.options.forEachIndexed { index, option ->
                    val isSelected = state.selectedOptionIndex == index
                    val isCorrect = index == currentQuestion.correctOptionIndex

                    val containerColor = when {
                        !state.isAnswerRevealed -> DarkSurfaceContainer
                        isCorrect -> Color(0xFF1B3D34) // Soft green
                        isSelected && !isCorrect -> Color(0xFF4A1E24) // Soft red
                        else -> DarkSurfaceContainer.copy(alpha = 0.5f)
                    }

                    val borderColor = when {
                        !state.isAnswerRevealed && isSelected -> PrimaryPurple
                        state.isAnswerRevealed && isCorrect -> Color(0xFF4DDCC6)
                        state.isAnswerRevealed && isSelected && !isCorrect -> Color(0xFFFF6B8B)
                        else -> Color.Transparent
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !state.isAnswerRevealed) {
                                onSelectOption(index)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = containerColor),
                        border = BorderStroke(1.5.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary,
                                fontWeight = if (isSelected || (state.isAnswerRevealed && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )

                            if (state.isAnswerRevealed) {
                                if (isCorrect) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = "Correct",
                                        tint = Color(0xFF4DDCC6),
                                        modifier = Modifier.size(22.dp)
                                    )
                                } else if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "Wrong",
                                        tint = Color(0xFFFF6B8B),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Explanation box if revealed
            if (state.isAnswerRevealed) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceHigh)
                ) {
                    Text(
                        text = "💡 ${currentQuestion.explanation}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }

        // Next Button
        if (state.isAnswerRevealed) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerPurple)
            ) {
                Text(
                    text = if (state.currentQuizIndex + 1 >= state.quizQuestions.size) "See Results 🏆" else "Next Question →",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun QuizCompletedCard(
    state: PracticeUiState,
    onRestart: () -> Unit,
    onSwitchToFlashcards: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val total = state.quizQuestions.size
    val score = state.quizScore
    val percentage = if (total > 0) (score.toFloat() / total.toFloat() * 100).toInt() else 0
    val isPerfect = state.isPerfectQuizScore || (score == total && total > 0)

    // Trigger celebration fanfare on perfect score
    androidx.compose.runtime.LaunchedEffect(isPerfect) {
        if (isPerfect) {
            HapticFeedbackHelper.performCelebrationFanfare(view, context)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isPerfect) {
            CelebrationConfetti()
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
            border = if (isPerfect) BorderStroke(1.5.dp, Color(0xFFFFD54F)) else null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            if (isPerfect) Color(0xFFFFD54F).copy(alpha = 0.2f)
                            else if (percentage >= 75) Color(0xFF1B3D34)
                            else PrimaryContainerPurple.copy(alpha = 0.25f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPerfect) Icons.Rounded.EmojiEvents else if (percentage >= 75) Icons.Rounded.EmojiEvents else Icons.Rounded.Star,
                        contentDescription = null,
                        tint = if (isPerfect) Color(0xFFFFD54F) else if (percentage >= 75) SecondaryTeal else PrimaryPurple,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isPerfect) "PERFECT SCORE! 🏆" else if (percentage >= 75) "Outstanding! 🌟" else "Good Effort! 👍",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isPerfect) Color(0xFFFFD54F) else TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "You scored $score out of $total ($percentage%)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Rewards Badges (XP + Shield)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // XP Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryContainerPurple.copy(alpha = 0.15f))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Bolt,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+${state.quizXpEarned} XP Earned! ⚡" + (if (isPerfect) " (+50 Bonus!)" else ""),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryPurple
                        )
                    }

                    // Streak Shield Badge
                    if (state.streakShieldEarned) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(SecondaryTeal.copy(alpha = 0.15f))
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Shield,
                                contentDescription = null,
                                tint = SecondaryTeal,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Earned Streak Shield! 🛡️ (Active: ${state.activeStreakShields})",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = SecondaryTeal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Share Score Button
                Button(
                    onClick = {
                        HapticFeedbackHelper.performClick(view, context)
                        val shareText = if (isPerfect) {
                            "🏆 I scored a PERFECT 100% ($score/$total) on today's WordDrop Quiz! 🧠✨ Can you beat my vocabulary score? Download WordDrop to practice daily words!"
                        } else {
                            "🎯 I scored $score/$total ($percentage%) on today's WordDrop Quiz! 📚 Download WordDrop to practice and build your vocabulary!"
                        }
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share your score to:"))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isPerfect) Color(0xFFFFD54F) else SecondaryTeal)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = null,
                        tint = DarkBackground,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share Score to WhatsApp / Story 🚀",
                        color = DarkBackground,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onRestart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerPurple)
                ) {
                    Text("Retake Quiz", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onSwitchToFlashcards,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.4f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryPurple)
                ) {
                    Text("Review Flashcards 🃏", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
