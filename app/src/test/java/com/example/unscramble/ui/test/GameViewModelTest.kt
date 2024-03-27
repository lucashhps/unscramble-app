package com.example.unscramble.ui.test

import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.ui.GameViewModel
import com.example.unscramble.data.getUnscrambledWord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameViewModelTest {
    private val viewModel = GameViewModel()

    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset() {
        var currentGameUiState = viewModel.uiState.value
        val unscrambledWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

        viewModel.updateUserGuess(unscrambledWord)
        viewModel.checkUserGuess()

        currentGameUiState = viewModel.uiState.value
        assertFalse(currentGameUiState.isGuessedWordWrong)
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
    }

    @Test
    fun gameViewModel_IncorrectGuess_ErrorFlagSet() {
        var currentGameStateUi = viewModel.uiState.value
        val scrambledWord = currentGameStateUi.currentScrambledWord

        viewModel.updateUserGuess(scrambledWord)
        viewModel.checkUserGuess()

        currentGameStateUi = viewModel.uiState.value
        assertTrue(currentGameStateUi.isGuessedWordWrong)
        assertEquals(SCORE_AFTER_NO_CORRECT_ANSWER, currentGameStateUi.score)
    }

    @Test
    fun gameViewModel_Initialization_FirstWordLoader() {
        val initializedGameStateUi = viewModel.uiState.value
        val unscrambledWord = getUnscrambledWord(initializedGameStateUi.currentScrambledWord)

        assertNotEquals(unscrambledWord, initializedGameStateUi.currentScrambledWord)
        assertEquals(WORDCOUNT_INITIALIZATION_VALUE, initializedGameStateUi.currentWordCount)
        assertEquals(SCORE_AFTER_NO_CORRECT_ANSWER, initializedGameStateUi.score)
        assertFalse(initializedGameStateUi.isGuessedWordWrong)
        assertFalse(initializedGameStateUi.isGameOver)

    }

    @Test
    fun gameView_AllWordsGuesses_UiStateUpdatedCorrectly() {
        var expectedScore = 0
        var currentGameUiState = viewModel.uiState.value
        var correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
        repeat(MAX_NO_OF_WORDS) {
            expectedScore += SCORE_INCREASE
            viewModel.updateUserGuess(correctPlayerWord)
            viewModel.checkUserGuess()
            currentGameUiState = viewModel.uiState.value
            correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
            assertTrue( expectedScore == currentGameUiState.score )
        }
        assertEquals(MAX_NO_OF_WORDS, currentGameUiState.currentWordCount)
        assertTrue(currentGameUiState.isGameOver)
    }

    @Test
    fun gameView_SkippedWord_ScoreUnchangedWordCountIncreased() {
        var expectedScore = 0
        var lastWordCount = 1
        var currentGameUiState = viewModel.uiState.value
        repeat(MAX_NO_OF_WORDS) {
            viewModel.skipWord()
            currentGameUiState = viewModel.uiState.value
            assertTrue( expectedScore == currentGameUiState.score )
            if (lastWordCount == 10){
                assertEquals(lastWordCount, currentGameUiState.currentWordCount)
            } else {
                assertEquals(lastWordCount + 1, currentGameUiState.currentWordCount)
            }

            lastWordCount = currentGameUiState.currentWordCount
        }
        assertEquals(MAX_NO_OF_WORDS, currentGameUiState.currentWordCount)
        assertTrue(currentGameUiState.isGameOver)
    }

    companion object {
        private const val SCORE_AFTER_FIRST_CORRECT_ANSWER = SCORE_INCREASE
        private const val SCORE_AFTER_NO_CORRECT_ANSWER = 0
        private const val WORDCOUNT_INITIALIZATION_VALUE = 1
    }
}