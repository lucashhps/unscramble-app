package com.example.unscramble.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.unscramble.data.allWords
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import kotlinx.coroutines.flow.update


class GameViewModel : ViewModel() {

    // Variables **********************************************************************************************

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState : StateFlow<GameUiState>
        get() = _uiState.asStateFlow()

    // em kotlin var currentWord : String não é permitido, pois:
    // A linguagem Kotlin exige que todas as propriedades de uma classe sejam inicializadas durante
    // instanciação de um novo objeto. Ocasionalmente, uma classe necessitará ser instanciada e o desenvolvedor
    // , nesse momento, não tenha todas as propriedades para usar no construtor.
    // Para não precisar tornar essas propriedades anuláveis, por exemplo, de String para String?,
    // você pode usar o modificador de propriedade lateinit.
    private lateinit var currentWord : String

    private var usedWords : MutableSet<String> = mutableSetOf()

    private var _count = 0

    val count : Int
        get() = _count

    var userGuess by mutableStateOf("")
        private set

    // init **********************************************************************************************

    init {
        resetGame()
    }

    // Helper Methods **********************************************************************************************

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }

    private fun pickRandomWordAndShuffle() : String {
        currentWord = allWords.random()
        if(usedWords.contains(currentWord)){
            return pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
            return shuffleCurrentWord(currentWord)
        }
    }

    private fun shuffleCurrentWord(word : String) : String {
        val tempWord = word.toCharArray()
        tempWord.shuffle()
        while(String(tempWord).equals(word)){
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    // Callback methods ***********************************************************
    fun updateUserGuess(guess : String) {
        userGuess = guess
    }

    fun checkUserGuess() {
        if(userGuess.equals(currentWord, ignoreCase = true)) {
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
        } else {
            _uiState.update {
                currentState ->
                    currentState.copy(isGuessedWordWrong = true)
            }
        }

        updateUserGuess("")
        Log.d("GameViewModel", _uiState.value.isGuessedWordWrong.toString()
        )
    }

    fun updateGameState(updatedScore : Int){
        if(usedWords.size == MAX_NO_OF_WORDS) {
            // last round
            _uiState.update{
                currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
        } else {
            // normal round
            _uiState.update {
                    currentState ->
                currentState.copy(
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    currentWordCount = currentState.currentWordCount.inc()
                )
            }
        }

    }
    fun skipWord(){
        updateGameState(_uiState.value.score)
        updateUserGuess("")
    }

}
