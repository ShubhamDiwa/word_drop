package com.diws.worddrop.ui.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diws.worddrop.data.preferences.UserPreferencesRepository
import com.diws.worddrop.domain.model.UserLevelInfo
import com.diws.worddrop.domain.model.Word
import com.diws.worddrop.domain.model.XpLevelManager
import com.diws.worddrop.domain.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PracticeTab {
    FLASHCARDS,
    QUIZ
}

data class QuizQuestion(
    val id: String,
    val word: String,
    val questionText: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String
)

data class PracticeUiState(
    val isLoading: Boolean = true,
    val selectedTab: PracticeTab = PracticeTab.FLASHCARDS,
    // Flashcard state
    val flashcards: List<Word> = emptyList(),
    val currentCardIndex: Int = 0,
    val isCardFlipped: Boolean = false,
    val isFlashcardsCompleted: Boolean = false,
    val flashcardsMasteredCount: Int = 0,
    val flashcardsXpEarned: Int = 0,
    // Quiz state
    val quizQuestions: List<QuizQuestion> = emptyList(),
    val currentQuizIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerRevealed: Boolean = false,
    val quizScore: Int = 0,
    val isQuizCompleted: Boolean = false,
    val isPerfectQuizScore: Boolean = false,
    val streakShieldEarned: Boolean = false,
    val quizXpEarned: Int = 0,
    // Gamification progress
    val totalUserXp: Int = 0,
    val userLevelInfo: UserLevelInfo = XpLevelManager.getLevelInfo(0),
    val activeStreakShields: Int = 0
)

@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PracticeUiState())
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()

    private var allWordsCache: List<Word> = emptyList()

    init {
        observeGamificationData()
        loadData()
    }

    private fun observeGamificationData() {
        viewModelScope.launch {
            userPreferencesRepository.userXpFlow.collect { xp ->
                _uiState.update {
                    it.copy(
                        totalUserXp = xp,
                        userLevelInfo = XpLevelManager.getLevelInfo(xp)
                    )
                }
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.streakShieldsFlow.collect { shields ->
                _uiState.update { it.copy(activeStreakShields = shields) }
            }
        }
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val allWords = wordRepository.getAllWords().first()
            allWordsCache = allWords

            // Pick 8 words prioritizing unlearned
            val unlearned = allWords.filter { !it.isLearned }
            val flashcardSelection = if (unlearned.size >= 8) {
                unlearned.shuffled().take(8)
            } else {
                (unlearned + allWords.filter { it.isLearned }.shuffled()).take(8)
            }

            val questions = generateQuizQuestions(allWords, 5)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    flashcards = flashcardSelection,
                    currentCardIndex = 0,
                    isCardFlipped = false,
                    isFlashcardsCompleted = false,
                    flashcardsMasteredCount = 0,
                    flashcardsXpEarned = 0,
                    quizQuestions = questions,
                    currentQuizIndex = 0,
                    selectedOptionIndex = null,
                    isAnswerRevealed = false,
                    quizScore = 0,
                    isQuizCompleted = false,
                    isPerfectQuizScore = false,
                    streakShieldEarned = false,
                    quizXpEarned = 0
                )
            }
        }
    }

    fun setTab(tab: PracticeTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun flipCard() {
        _uiState.update { it.copy(isCardFlipped = !it.isCardFlipped) }
    }

    fun onFlashcardAnswer(mastered: Boolean) {
        val currentState = _uiState.value
        val currentCard = currentState.flashcards.getOrNull(currentState.currentCardIndex)

        if (mastered && currentCard != null) {
            viewModelScope.launch {
                wordRepository.setLearnedStatus(currentCard.id, true)
            }
        }

        val nextIndex = currentState.currentCardIndex + 1
        val isCompleted = nextIndex >= currentState.flashcards.size
        val newMasteredCount = if (mastered) currentState.flashcardsMasteredCount + 1 else currentState.flashcardsMasteredCount

        if (isCompleted) {
            // Reward +25 XP for completing flashcards review session
            viewModelScope.launch {
                userPreferencesRepository.addXp(25)
            }
        }

        _uiState.update {
            it.copy(
                currentCardIndex = if (isCompleted) currentState.currentCardIndex else nextIndex,
                isCardFlipped = false,
                isFlashcardsCompleted = isCompleted,
                flashcardsMasteredCount = newMasteredCount,
                flashcardsXpEarned = if (isCompleted) 25 else 0
            )
        }
    }

    fun restartFlashcards() {
        val unlearned = allWordsCache.filter { !it.isLearned }
        val newCards = if (unlearned.size >= 8) {
            unlearned.shuffled().take(8)
        } else {
            (unlearned + allWordsCache.filter { it.isLearned }.shuffled()).take(8)
        }
        _uiState.update {
            it.copy(
                flashcards = newCards,
                currentCardIndex = 0,
                isCardFlipped = false,
                isFlashcardsCompleted = false,
                flashcardsMasteredCount = 0,
                flashcardsXpEarned = 0
            )
        }
    }

    fun selectQuizOption(optionIndex: Int) {
        val currentState = _uiState.value
        if (currentState.isAnswerRevealed) return

        val currentQuestion = currentState.quizQuestions.getOrNull(currentState.currentQuizIndex) ?: return
        val isCorrect = optionIndex == currentQuestion.correctOptionIndex
        val newScore = if (isCorrect) currentState.quizScore + 1 else currentState.quizScore

        if (isCorrect) {
            // +10 XP for each correct answer
            viewModelScope.launch {
                userPreferencesRepository.addXp(10)
            }
        }

        _uiState.update {
            it.copy(
                selectedOptionIndex = optionIndex,
                isAnswerRevealed = true,
                quizScore = newScore,
                quizXpEarned = it.quizXpEarned + (if (isCorrect) 10 else 0)
            )
        }
    }

    fun nextQuizQuestion() {
        val currentState = _uiState.value
        val nextIndex = currentState.currentQuizIndex + 1
        val isCompleted = nextIndex >= currentState.quizQuestions.size

        var earnedBonusXp = 0
        var isPerfect = false
        var earnedShield = false

        if (isCompleted) {
            val totalQuestions = currentState.quizQuestions.size
            val finalScore = currentState.quizScore
            isPerfect = finalScore == totalQuestions && totalQuestions > 0
            earnedShield = totalQuestions > 0 && (finalScore.toFloat() / totalQuestions.toFloat() >= 0.8f)

            viewModelScope.launch {
                if (isPerfect) {
                    // +50 XP Bonus for Perfect 100% Score
                    userPreferencesRepository.addXp(50)
                }
                if (earnedShield) {
                    // Earn a Streak Shield for 80%+
                    userPreferencesRepository.addStreakShield(1)
                }
            }
            earnedBonusXp = if (isPerfect) 50 else 0
        }

        _uiState.update {
            it.copy(
                currentQuizIndex = if (isCompleted) currentState.currentQuizIndex else nextIndex,
                selectedOptionIndex = null,
                isAnswerRevealed = false,
                isQuizCompleted = isCompleted,
                isPerfectQuizScore = isPerfect,
                streakShieldEarned = earnedShield,
                quizXpEarned = it.quizXpEarned + earnedBonusXp
            )
        }
    }

    fun restartQuiz() {
        val questions = generateQuizQuestions(allWordsCache, 5)
        _uiState.update {
            it.copy(
                quizQuestions = questions,
                currentQuizIndex = 0,
                selectedOptionIndex = null,
                isAnswerRevealed = false,
                quizScore = 0,
                isQuizCompleted = false,
                isPerfectQuizScore = false,
                streakShieldEarned = false,
                quizXpEarned = 0
            )
        }
    }

    private fun generateQuizQuestions(allWords: List<Word>, count: Int): List<QuizQuestion> {
        if (allWords.size < 4) return emptyList()

        val sampleWords = allWords.shuffled().take(count)
        return sampleWords.mapIndexed { idx, targetWord ->
            val wrongCandidates = (allWords - targetWord).shuffled().take(3)
            val isMeaningQuestion = idx % 2 == 0

            if (isMeaningQuestion) {
                val correctMeaning = targetWord.simpleMeaning ?: targetWord.definition
                val wrongMeanings = wrongCandidates.map { it.simpleMeaning ?: it.definition }
                val allOptions = (wrongMeanings + correctMeaning).shuffled()
                val correctIndex = allOptions.indexOf(correctMeaning)

                QuizQuestion(
                    id = "q_$idx",
                    word = targetWord.word,
                    questionText = "What is the meaning of \"${targetWord.word.uppercase()}\"?",
                    options = allOptions,
                    correctOptionIndex = correctIndex,
                    explanation = "${targetWord.word.uppercase()}: $correctMeaning"
                )
            } else {
                val definition = targetWord.simpleMeaning ?: targetWord.definition
                val options = (wrongCandidates.map { it.word.uppercase() } + targetWord.word.uppercase()).shuffled()
                val correctIndex = options.indexOf(targetWord.word.uppercase())

                QuizQuestion(
                    id = "q_$idx",
                    word = targetWord.word,
                    questionText = "Which word matches this meaning?\n\n\"$definition\"",
                    options = options,
                    correctOptionIndex = correctIndex,
                    explanation = "${targetWord.word.uppercase()} means: $definition"
                )
            }
        }
    }
}
