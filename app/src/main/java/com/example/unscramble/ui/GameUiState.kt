package com.example.unscramble.ui

data class GameUiState (
    val currentScrambledWord : String = "",
    val currentWordCount : Int = 1,
    var isGuessedWordWrong : Boolean = false,
    var score : Int = 0,
    var isGameOver : Boolean = false
)