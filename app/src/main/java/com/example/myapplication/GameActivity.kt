package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.ComponentActivity

class GameActivity : ComponentActivity() {
    
    private lateinit var gomokuView: GomokuView
    private lateinit var gameStatusText: TextView
    private lateinit var resetButton: Button
    private lateinit var backButton: Button
    private lateinit var winOverlay: FrameLayout
    private lateinit var winText: TextView
    private lateinit var winResetButton: Button
    
    // 游戏设置
    private var gameMode = 0
    private var difficulty = 1
    private var boardSizeRows = 15
    private var boardSizeCols = 15
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        
        // 获取Intent中的游戏设置
        gameMode = intent.getIntExtra("GAME_MODE", 0)
        difficulty = intent.getIntExtra("DIFFICULTY", 1)
        boardSizeRows = intent.getIntExtra("BOARD_SIZE_ROWS", 15)
        boardSizeCols = intent.getIntExtra("BOARD_SIZE_COLS", 15)
        
        // 初始化视图
        gomokuView = findViewById(R.id.gomoku_view)
        gameStatusText = findViewById(R.id.game_status_text)
        resetButton = findViewById(R.id.reset_button)
        backButton = findViewById(R.id.back_button)
        winOverlay = findViewById(R.id.win_overlay)
        winText = findViewById(R.id.win_text)
        winResetButton = findViewById(R.id.win_reset_button)
        
        // 设置游戏模式、难度和棋盘大小
        gomokuView.setDifficulty(difficulty)
        gomokuView.setBoardSize(boardSizeRows, boardSizeCols)
        gomokuView.setGameMode(gameMode)
        
        // 设置游戏状态变化监听器
        gomokuView.onGameStatusChanged = { winner ->
            val statusText = if (winner == 1) "黑棋胜利!" else "白棋胜利!"
            gameStatusText.text = statusText
            showWinOverlay(winner)
        }
        
        // 设置重置按钮点击事件
        resetButton.setOnClickListener {
            resetGame()
        }
        
        // 设置返回按钮点击事件
        backButton.setOnClickListener {
            finish()
        }
        
        // 设置胜利弹窗重置按钮点击事件
        winResetButton.setOnClickListener {
            hideWinOverlay()
            resetGame()
        }
        
        // 更新游戏状态文本
        updateGameStatus()
    }
    
    private fun showWinOverlay(winner: Int) {
        val winMessage = if (winner == 1) "黑棋胜利！" else "白棋胜利！"
        winText.text = winMessage
        winOverlay.visibility = View.VISIBLE
    }
    
    private fun hideWinOverlay() {
        winOverlay.visibility = View.GONE
    }
    
    private fun resetGame() {
        gomokuView.resetGame()
        updateGameStatus()
        hideWinOverlay()
    }
    
    private fun updateGameStatus() {
        val modeText = when (gameMode) {
            0 -> "双人对战"
            1 -> "人机对战 - 玩家先手"
            2 -> "人机对战 - AI先手"
            else -> "未知模式"
        }
        
        val difficultyText = when (difficulty) {
            0 -> "简单难度"
            1 -> "中等难度"
            2 -> "困难难度"
            else -> ""
        }
        
        val boardSizeText = if (boardSizeRows == boardSizeCols) {
            "${boardSizeRows}x${boardSizeCols}"
        } else {
            "${boardSizeRows}x${boardSizeCols}"
        }
        
        val statusText = if (gameMode == 0) {
            "$modeText - $boardSizeText"
        } else {
            "$modeText - $difficultyText - $boardSizeText"
        }
        
        gameStatusText.text = statusText
    }
} 